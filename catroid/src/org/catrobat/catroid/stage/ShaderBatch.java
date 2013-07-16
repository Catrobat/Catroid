/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderBatch extends SpriteBatch {

	static final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
			+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
			+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
			+ "uniform mat4 u_projTrans;\n" //
			+ "varying vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "\n" //
			+ "void main()\n" //
			+ "{\n" //
			+ " v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
			+ " v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
			+ " gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
			+ "}\n";
	static final String fragmentShader = "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n" //
			+ "varying LOWP vec4 v_color;\n" //
			+ "varying vec2 v_texCoords;\n" //
			+ "uniform sampler2D u_texture;\n" //
			+ "uniform float brightness;\n" //
			+ "uniform float contrast;\n" //
			+ "void main()\n"//
			+ "{\n" //
			+ " vec4 color = v_color * texture2D(u_texture, v_texCoords);\n" + " color.rgb /= color.a;\n" //ignore alpha
			+ " color.rgb = ((color.rgb - 0.5) * max(contrast, 0.0)) + 0.5;\n" //apply contrast
			+ " color.rgb += brightness;\n" //apply brightness
			+ " color.rgb *= color.a;\n" //return alpha
			+ " gl_FragColor = color;\n" + "}";

	ShaderProgram shader;
	public final boolean isCompiled;
	public final String log;
	protected int brightnessLoc = -1, contrastLoc = -1;
	//ideally use getters/setters here...
	public float brightness = 0f;
	private float lastBrightness = 0f;
	public float contrast = 1f;

	public ShaderBatch(int size) {
		super(size);
		if (Gdx.graphics.isGL20Available()) {
			ShaderProgram.pedantic = false;
			shader = new ShaderProgram(vertexShader, fragmentShader);
			isCompiled = shader.isCompiled();
			log = shader.getLog();
			if (isCompiled) {
				setShader(shader);
				shader.begin();
				brightnessLoc = shader.getUniformLocation("brightness");
				contrastLoc = shader.getUniformLocation("contrast");
				shader.end();
			}
		} else {
			isCompiled = false;
			log = "Shaders not supported in GL11";
		}
	}

	@Override
	public void begin() {
		super.begin();
		if (brightnessLoc != -1 && shader != null) {
			shader.setUniformf(brightnessLoc, brightness);
		}
		if (contrastLoc != -1 && shader != null) {
			shader.setUniformf(contrastLoc, contrast);
		}
	}

	public void refreshShader() {
		if (brightnessLoc != -1 && shader != null) {
			shader.setUniformf(brightnessLoc, brightness);
		}
		if (contrastLoc != -1 && shader != null) {
			shader.setUniformf(contrastLoc, contrast);
		}
	}

	public void resetShader() {
		brightness = 0f;
		contrast = 1f;
	}

	@Override
	public void end() {
		super.end();
		brightness = 0f;
		contrast = 1f;
	}
}
