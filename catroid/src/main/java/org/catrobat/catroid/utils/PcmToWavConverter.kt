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

package org.catrobat.catroid.utils

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.RandomAccessFile

private const val TRANSFER_BUFFER_SIZE = 10 * 1024
private const val CHUNK_SIZE = 36
private const val SUB_CHUNK_SIZE = 16
private const val PCM_AUDIO_FORMAT = 1
private const val POSITION_OF_BYTE_ZERO = 0
private const val POSITION_OF_BYTE_ONE = 8
private const val POSITION_OF_BYTE_TWO = 16
private const val POSITION_OF_BYTE_THREE = 24
private const val CHUNK_ID = "RIFF"
private const val FILE_FORMAT = "WAVE"
private const val FIRST_SUBCHUNK_ID = "fmt "
private const val SECOND_SUBCHUNK_ID = "data"

object PcmToWavConverter {
    private val TAG = PcmToWavConverter::class.simpleName

    fun convertPcmToWav(
        input: File,
        output: File?,
        channelCount: Int,
        sampleRate: Int,
        bitsPerSample: Int
    ) {
        val inputSize = input.length().toInt()
        FileOutputStream(output).close()
        try {
            FileOutputStream(output).use { encoded ->
                writeToOutput(encoded, CHUNK_ID)
                writeToOutput(encoded, CHUNK_SIZE + inputSize)
                writeToOutput(encoded, FILE_FORMAT)

                writeToOutput(encoded, FIRST_SUBCHUNK_ID)
                writeToOutput(encoded, SUB_CHUNK_SIZE)
                writeToOutput(encoded, PCM_AUDIO_FORMAT.toShort())
                writeToOutput(encoded, channelCount.toShort())
                writeToOutput(encoded, sampleRate)
                writeToOutput(encoded, sampleRate * channelCount * bitsPerSample / Byte.SIZE_BITS)
                writeToOutput(encoded, (channelCount * bitsPerSample / Byte.SIZE_BITS).toShort())
                writeToOutput(encoded, bitsPerSample.toShort())

                writeToOutput(encoded, SECOND_SUBCHUNK_ID)
                writeToOutput(encoded, inputSize)
                copy(FileInputStream(input), encoded)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to convert file ${input.name} to WAV for Huawei text-to-speech", e)
        }
    }

    @JvmOverloads
    fun copy(
        source: InputStream,
        output: OutputStream,
        bufferSize: Int = TRANSFER_BUFFER_SIZE
    ): Long {
        var read = 0L
        val buffer = ByteArray(bufferSize)
        var n: Int
        while (source.read(buffer).also { n = it } != -1) {
            output.write(buffer, 0, n)
            read += n.toLong()
        }
        return read
    }

    fun writePcmToFile(buffer: ByteArray?, file: File, append: Boolean) {
        var randomAccessFile: RandomAccessFile? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            if (append) {
                randomAccessFile = RandomAccessFile(file, "rw")
                file.length().let { randomAccessFile.seek(it) }
                randomAccessFile.write(buffer)
            } else {
                fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(buffer)
                fileOutputStream.flush()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to write data ${file.name} to WAV file for Huawei " +
                "text-to-speech", e)
        } finally {
            try {
                randomAccessFile?.close()
                fileOutputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Failed to write data ${file.name} to WAV file for Huawei " +
                    "text-to-speech", e)
            }
        }
    }

    private fun writeToOutput(output: OutputStream, data: String) {
        for (element in data) output.write(element.toInt())
    }

    private fun writeToOutput(output: OutputStream, data: Int) {
        output.write(data shr POSITION_OF_BYTE_ZERO)
        output.write(data shr POSITION_OF_BYTE_ONE)
        output.write(data shr POSITION_OF_BYTE_TWO)
        output.write(data shr POSITION_OF_BYTE_THREE)
    }

    private fun writeToOutput(output: OutputStream, data: Short) {
        output.write(data.toInt() shr POSITION_OF_BYTE_ZERO)
        output.write(data.toInt() shr POSITION_OF_BYTE_ONE)
    }
}
