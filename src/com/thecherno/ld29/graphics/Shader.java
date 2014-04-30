package com.thecherno.ld29.graphics;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import com.thecherno.ld29.util.FileUtils;

public class Shader {

	private int shader;
	private String vertexPath, fragmentPath;

	public static Shader TILE, FORETILE, WATER, LIGHT, FONT, MOB;

	public Shader(String vertPath, String fragPath) {
		this.vertexPath = vertPath;
		this.fragmentPath = fragPath;
		String vertexSource = FileUtils.loadAsString(vertPath);
		String fragSource = FileUtils.loadAsString(fragPath);
		System.out.println("Loading shader: " + vertPath + ", " + fragPath);
		shader = create(vertexSource, fragSource);
	}

	public static void loadAll() {
		TILE = new Shader("shaders/tile.vert", "shaders/tile.frag");
		FORETILE = new Shader("shaders/tile.vert", "shaders/foretile.frag");
		WATER = new Shader("shaders/tile.vert", "shaders/water.frag");
		LIGHT = new Shader("shaders/light.vert", "shaders/light.frag");
		FONT = new Shader("shaders/font.vert", "shaders/font.frag");
		MOB = new Shader("shaders/tile.vert", "shaders/mob.frag");
	}

	public static int create(String vertexSource, String fragSource) {
		int program = glCreateProgram();
		int vertID = glCreateShader(GL_VERTEX_SHADER);
		int fragID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(vertID, vertexSource);
		glShaderSource(fragID, fragSource);
		glCompileShader(vertID);
		if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile vertex shader!");
			System.err.print(glGetShaderInfoLog(vertID, 2048));
			return -1;
		}
		glCompileShader(fragID);
		if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failed to compile fragment shader!");
			System.err.print(glGetShaderInfoLog(fragID, 2048));
			return -1;
		}
		glAttachShader(program, vertID);
		glAttachShader(program, fragID);
		glLinkProgram(program);
		glValidateProgram(program);
		return program;
	}

	public void recompile() {
		String vertexSource = FileUtils.loadAsString(vertexPath);
		String fragSource = FileUtils.loadAsString(fragmentPath);
		int shader = create(vertexSource, fragSource);
		if (shader == -1) return;
		this.shader = shader;
	}

	public void bind() {
		glUseProgram(shader);
	}

	public void release() {
		glUseProgram(0);
	}

	public int getID() {
		return shader;
	}

	public String toString() {
		return "Shader ID: " + getID() + "; " + vertexPath + ", " + fragmentPath;
	}

	public int getUniform(String name) {
		return glGetUniformLocation(shader, name);
	}

	public void setUniform1f(String name, float value) {
		bind();
		glUniform1f(getUniform(name), value);
		release();
	}

	public void setUniform2f(String name, float x, float y) {
		bind();
		glUniform2f(getUniform(name), x, y);
		release();
	}

	public void setUniform3f(String name, float x, float y, float z) {
		bind();
		glUniform3f(getUniform(name), x, y, z);
		release();
	}

	public void setUniformf(int location, float value) {
		glUniform1f(location, value);
	}

	public boolean equals(Object object) {
		if (!(object instanceof Shader)) return false;
		Shader shader = (Shader) object;
		return vertexPath.equals(shader.vertexPath) && fragmentPath.equals(shader.fragmentPath);
	}
}
