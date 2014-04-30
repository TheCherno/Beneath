package com.thecherno.ld29.level.tile;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.List;

import com.thecherno.ld29.graphics.Light;
import com.thecherno.ld29.graphics.Shader;
import com.thecherno.ld29.input.Keyboard;
import com.thecherno.ld29.resources.Texture;
import com.thecherno.ld29.util.Buffer;

public class Tile {

	public static final byte GROUND = 0x0;
	public static final byte GRASS = 0x2;
	public static final byte DIRT = 0x3;
	public static final float SIZE = 64.0f;
	// vao = entire array
	// vbo = vertex buffer
	// vio = index buffer
	// vto = texCoord buffer
	protected int vao, vbo, vio, vto;
	protected Shader shader;
	protected int texture;

	protected float waterLevel = 0.0f;
	private float time = 0.0f;

	protected float[] vertices = new float[] {
			0.0f, 0.0f, 0.0f,//
			SIZE, 0.0f, 0.0f, //
			SIZE, SIZE, 0.0f,//
			0, SIZE, 0.0f //
	};

	protected byte[] indices = new byte[] {
			0, 1, 2, //
			2, 3, 0 //
	};

	protected byte[] texCoords = new byte[] {
			0, 0,//
			1, 0,//
			1, 1, //
			1, 1, //
			0, 1, //
			0, 0 //
	};

	public Tile() {
		texture = Texture.GROUND;
		createShader(Shader.TILE);
		compile();
	}

	public Tile(int texture) {
		this.texture = texture;
		createShader(Shader.TILE);
		compile();
	}

	protected void compile() {
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		{
			vbo = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			{
				glBufferData(GL_ARRAY_BUFFER, Buffer.createFloatBuffer(vertices), GL_STATIC_DRAW);
				glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			}
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			vio = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vio);
			{
				glBufferData(GL_ARRAY_BUFFER, Buffer.createByteBuffer(indices), GL_STATIC_DRAW);
			}
			glBindBuffer(GL_ARRAY_BUFFER, 0);

			vto = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, vto);
			{
				glBufferData(GL_ARRAY_BUFFER, Buffer.createByteBuffer(texCoords), GL_STATIC_DRAW);
				glVertexAttribPointer(1, 3, GL_UNSIGNED_BYTE, false, 0, 1);
			}
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
		glBindVertexArray(0);
	}

	protected void createShader(Shader shader) {
		this.shader = shader;
		glActiveTexture(GL_TEXTURE1);
		shader.bind();
		int uniform = glGetUniformLocation(shader.getID(), "texture");
		glUniform1i(uniform, 1);

		glActiveTexture(GL_TEXTURE2);
		uniform = glGetUniformLocation(shader.getID(), "waterTexture");
		glUniform1i(uniform, 2);
		shader.release();
	}

	public void render(int x, int y, float water) {
		setWaterLevel(water);
		glPushMatrix();
		shader.bind();
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		{
			glTranslatef(x, y, 0);
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, texture);
			glActiveTexture(GL_TEXTURE2);
			glBindTexture(GL_TEXTURE_2D, Texture.WATER);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vio);
			glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, 0);
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shader.release();
		glPopMatrix();
	}

	public void update() {
		time += 0.005f;
		shader.bind();
		int uniform = shader.getUniform("time");
		shader.setUniformf(uniform, time);
		shader.release();
	}

	public void bindUniforms(Light light) {
		shader.bind();
		light.bindUniforms(shader.getID());
		shader.setUniform1f("waterLevel", waterLevel);
		shader.release();
	}

	public void bindUniforms(List<Light> lights) {
		if (lights.size() > 10) {
			System.err.println("Too many lights!");
			return;
		}
		float[] positions = new float[10 * 2];
		float[] colors = new float[10 * 3];
		float[] intensities = new float[10];
		for (int i = 0; i < lights.size() * 2; i += 2) {
			positions[i] = lights.get(i >> 1).x - lights.get(i >> 1).getXOffset();
			positions[i + 1] = 540.0f - lights.get(i >> 1).y + lights.get(i >> 1).getYOffset();
		}

		for (int i = 0; i < lights.size(); i++) {
			intensities[i] = lights.get(i).intensity;
		}

		for (int i = 0; i < lights.size() * 3; i += 3) {
			colors[i] = lights.get(i / 3).vc.x;
			colors[i + 1] = lights.get(i / 3).vc.y;
			colors[i + 2] = lights.get(i / 3).vc.z;
		}

		shader.bind();
		int uniform = glGetUniformLocation(shader.getID(), "lightPosition");
		glUniform2(uniform, Buffer.createFloatBuffer(positions));

		uniform = glGetUniformLocation(shader.getID(), "lightColor");
		glUniform3(uniform, Buffer.createFloatBuffer(colors));

		uniform = glGetUniformLocation(shader.getID(), "lightIntensity");
		glUniform1(uniform, Buffer.createFloatBuffer(intensities));
		shader.release();
	}

	public void setWaterLevel(float waterLevel) {
		this.waterLevel = waterLevel;
	}

	public boolean solid() {
		return false;
	}

	public void increaseWaterLevel(float amount) {
		this.waterLevel += amount;
	}
}
