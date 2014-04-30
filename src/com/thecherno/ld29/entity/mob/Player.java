package com.thecherno.ld29.entity.mob;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import com.thecherno.ld29.graphics.Light;
import com.thecherno.ld29.graphics.Shader;
import com.thecherno.ld29.input.Keyboard;
import com.thecherno.ld29.resources.Texture;
import com.thecherno.ld29.util.Buffer;

public class Player extends Mob {

	public static final float SIZE = 64.0f;

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

	public Player(int x, int y) {
		this.x = x << 6;
		this.y = y << 6;
		texture = Texture.PLAYER;
		shader = Shader.MOB;
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

		glActiveTexture(GL_TEXTURE1);
		shader.bind();
		int uniform = glGetUniformLocation(shader.getID(), "texture");
		glUniform1i(uniform, 1);
		shader.release();
	}

	public void update() {
		int xa = 0;
		int ya = 0;
		if (Keyboard.keyPressed(Keyboard.VK_UP)) ya--;
		if (Keyboard.keyPressed(Keyboard.VK_DOWN)) ya++;
		if (Keyboard.keyPressed(Keyboard.VK_LEFT)) xa--;
		if (Keyboard.keyPressed(Keyboard.VK_RIGHT)) xa++;
		if (xa != 0 || ya != 0) move(xa * 3, ya * 3);
		level.setOffset((int) (x - 960.0f / 2.0f), (int) (y - 540.0f / 2.0f));
	}

	public void bindUniforms(Light light) {
		shader.bind();
		light.bindUniforms(shader.getID());
		shader.release();
	}

	public void render() {
		glPushMatrix();
		// x=104*64;
		// y=68*64;
		shader.bind();
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		{
			glTranslatef(x, y, 0);
			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, Texture.PLAYER);
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
}
