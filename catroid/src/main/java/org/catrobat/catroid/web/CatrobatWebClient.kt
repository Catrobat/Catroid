/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.web

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.retrofit.ErrorInterceptor
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Locale
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object CatrobatWebClient {
    val client: OkHttpClient = setUpClient()
}

const val BEGIN_CERT = "-----BEGIN CERTIFICATE-----"
const val END_CERT = "-----END CERTIFICATE-----"
const val SECRET = BuildConfig.CLOUDFLARE_SECRET
const val LINE_LENGTH = 64

private fun setUpClient(): OkHttpClient {
    val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
    val keySpec = getPrivateKey()

    val certificate: Certificate? = getCertificate(certificateFactory)

    val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
    val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, SECRET.toCharArray())
    keyStore.setKeyEntry(
        "client",
        keyFactory.generatePrivate(keySpec),
        SECRET.toCharArray(),
        arrayOf(certificate)
    )

    val trustManagers: Array<TrustManager> = setUpTrustManagers()
    val keyManagers: Array<KeyManager> = setUpKeyManagers(keyStore)
    val sslSocketFactory: SSLSocketFactory = obtainSSLSocketFactory(keyManagers, trustManagers)
    return obtainOkHttpClient(sslSocketFactory, trustManagers)
}

@Throws(WebconnectionException::class)
fun OkHttpClient.performCallWith(request: Request): String {
    var message = "Bad Connection"
    var statusCode = WebconnectionException.ERROR_NETWORK
    try {
        val response = this.newCall(request).execute()
        response.body()?.let {
            return it.string()
        }
        statusCode = response.code()
        message = response.message()
    } catch (e: IOException) {
        e.message?.let {
            message = it
        }
    }
    throw WebconnectionException(statusCode, message)
}
fun Map<String, String>.createFormEncodedRequest(url: String): Request {
    val formEncodingBuilder = FormBody.Builder()
    for ((key, value) in this) {
        formEncodingBuilder.add(key, value)
    }
    return Request.Builder()
        .url(url)
        .post(formEncodingBuilder.build())
        .build()
}

private fun getPrivateKey(): PKCS8EncodedKeySpec {
    val privateKeyInputStream: InputStream = BuildConfig.CLOUDFLARE_PRIVATE_KEY.byteInputStream()
    val privateKeyByteArray =
        ByteArray(privateKeyInputStream.available())
    privateKeyInputStream.read(privateKeyByteArray)
    val privateKeyContent = String(privateKeyByteArray, Charset.defaultCharset())
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")

    val rawPrivateKeyByteArray: ByteArray? =
        android.util.Base64.decode(privateKeyContent, android.util.Base64.DEFAULT)

    return PKCS8EncodedKeySpec(rawPrivateKeyByteArray)
}

private fun setUpTrustManagers(): Array<TrustManager> {
    val trustManagerFactory: TrustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    return trustManagerFactory.trustManagers
}

private fun setUpKeyManagers(keyStore: KeyStore): Array<KeyManager> {
    val keyManagerFactory: KeyManagerFactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
    keyManagerFactory.init(keyStore, SECRET.toCharArray())
    return keyManagerFactory.keyManagers
}

private fun obtainSSLSocketFactory(
    keyManagers: Array<KeyManager>,
    trustManagers: Array<TrustManager>
): SSLSocketFactory {
    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagers, trustManagers, SecureRandom())
    return sslContext.socketFactory
}

private fun obtainOkHttpClient(
    sslSocketFactory: SSLSocketFactory,
    trustManagers: Array<TrustManager>
): OkHttpClient {
    return OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustManagers[0] as X509TrustManager)
        .addInterceptor { chain ->
            val lang = Locale.getDefault().language
            val request = chain.request()
                .newBuilder()
                .addHeader("Accept-Language", lang)
                .build()
            chain.proceed(request)
        }
        .addInterceptor(ErrorInterceptor())
        .build()
}

private fun getCertificate(certificateFactory: CertificateFactory): Certificate? {
    val certificateString = BuildConfig.CLOUDFLARE_CERTIFICATE
    val correctCertificateFormat = certificateString.chunked(LINE_LENGTH).joinToString("\n")
        .replace(END_CERT, "\n" + END_CERT)
        .replace(BEGIN_CERT, BEGIN_CERT + "\n")
    return certificateFactory.generateCertificate(correctCertificateFormat.byteInputStream())
}
