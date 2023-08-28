/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.content

import com.badlogic.gdx.graphics.glutils.ShaderProgram

class BrightnessContrastHueShaderProgram : ShaderProgram(
    VERTEX_SHADER, FRAGMENT_SHADER
) {
    init {
        pedantic = false
        if (isCompiled) {
            begin()
            setUniformf(BRIGHTNESS_STRING_IN_SHADER, 0.0f)
            setUniformf(CONTRAST_STRING_IN_SHADER, 1.0f)
            setUniformf(HUE_STRING_IN_SHADER, 0.0f)
            end()
        }
    }

    fun setBrightness(brightness: Float) {
        begin()
        setUniformf(BRIGHTNESS_STRING_IN_SHADER, brightness - 1f)
        end()
    }

    fun setHue(hue: Float) {
        begin()
        setUniformf(HUE_STRING_IN_SHADER, hue)
        end()
    }

    private companion object {
        private const val VERTEX_SHADER = "attribute vec4 " + POSITION_ATTRIBUTE + ";\n" +
            "attribute vec4 " + COLOR_ATTRIBUTE + ";\n" + "attribute vec2 " +
            TEXCOORD_ATTRIBUTE + "0;\n" + "uniform mat4 u_projTrans;\n" + "varying vec4 " +
            "v_color;\n" +
            "varying vec2 v_texCoords;\n" + "\n" + "void main()\n" + "{\n" + " v_color = " +
            COLOR_ATTRIBUTE + ";\n" + " v_texCoords = " + TEXCOORD_ATTRIBUTE + "0;\n" +
            " gl_Position = u_projTrans * " + POSITION_ATTRIBUTE + ";\n" + "}\n"
        private const val FRAGMENT_SHADER = "#ifdef GL_ES\n" +
            "    #define LOWP lowp\n" +
            "    precision mediump float;\n" +
            "#else\n" +
            "    #define LOWP\n" +
            "#endif\n" +
            "varying LOWP vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "uniform float brightness;\n" +
            "uniform float contrast;\n" +
            "uniform float hue;\n" +
            "vec3 rgb2hsv(vec3 c)\n" +
            "{\n" +
            "    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);\n" +
            "    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));\n" +
            "    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));\n" +
            "    float d = q.x - min(q.w, q.y);\n" +
            "    float e = 1.0e-10;\n" +
            "    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);\n" +
            "}\n" +
            "vec3 hsv2rgb(vec3 c)\n" +
            "{\n" +
            "    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);\n" +
            "    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);\n" +
            "    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);\n" +
            "}\n" +
            "void main()\n" +
            "{\n" +
            "    vec4 color = v_color * texture2D(u_texture, v_texCoords);\n" +
            "    color.rgb /= color.a;\n" +
            "    color.rgb = ((color.rgb - 0.5) * max(contrast, 0.0)) + 0.5;\n" +
            "    color.rgb += brightness;\n" +
            "    color.rgb *= color.a;\n" +
            "    vec3 hsv = rgb2hsv(color" +
            ".rgb);\n" +
            "    hsv.x += hue;\n" +
            "    vec3 rgb = hsv2rgb(hsv);\n" +
            "    gl_FragColor = vec4(rgb.r, rgb.g, rgb.b, color.a);\n" +
            " }"
        private const val BRIGHTNESS_STRING_IN_SHADER = "brightness"
        private const val CONTRAST_STRING_IN_SHADER = "contrast"
        private const val HUE_STRING_IN_SHADER = "hue"
    }
}
