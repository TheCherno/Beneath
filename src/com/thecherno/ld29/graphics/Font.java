package com.thecherno.ld29.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import com.thecherno.ld29.resources.Texture;
import com.thecherno.ld29.util.Buffer;

public class Font {

	protected int vao, vbo, vio, vto;
	private int list = 0;
	private int[] texIDs;
	private int size = 128;
	private static Shader shader;
	private String chars = "ABCDEFGHIJKLM" + //
			"NOPQRSTUVWXYZ" + //
			"abcdefghijklm" + //
			"nopqrstuvwxyz" + //
			"0123456789!,%";

	protected float[] vertices = new float[] {
			0.0f, 0.0f, 0.0f,//
			size, 0.0f, 0.0f, //
			size, size, 0.0f,//
			0, size, 0.0f //
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

	public Font() {
		texIDs = Texture.loadFont("res/font.png", 13, 5, size);
		compile();
		if (shader == null) shader = Shader.FONT;
		shader.bind();
		int uniform = shader.getUniform("texture");
		glUniform1i(uniform, 3);
		shader.release();
	}

	/*	private void createList() {
			list = glGenLists(1);
			glNewList(list, GL_COMPILE);
			glBegin(GL_TRIANGLES);
			glTexCoord2f(0.0f, 0.0f);
			glVertex2f(0.0f, 0.0f);
			glTexCoord2f(1.0f, 0.0f);
			glVertex2f(size, 0);
			glTexCoord2f(1.0f, 1.0f);
			glVertex2f(size, size);

			glTexCoord2f(1.0f, 1.0f);
			glVertex2f(size, size);
			glTexCoord2f(0.0f, 1.0f);
			glVertex2f(0.0f, size);
			glTexCoord2f(0.0f, 0.0f);
			glVertex2f(0.0f, 0.0f);

			glEnd();
			glEndList();
		}*/
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

	public void drawString(String text, int x, int y, int size, int spacing) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glActiveTexture(GL_TEXTURE3);
		shader.bind();
		float scale = size / 20.0f;
		int xx = x;
		int yy = y;
		for (int i = 0; i < text.length(); i++) {
			float xOffset = xx / scale;
			float yOffset = yy / scale;
			int currentChar = text.charAt(i);
			int index = chars.indexOf(currentChar);
			if (index >= 0 && currentChar != ' ') {
				if (currentChar == 'p' || currentChar == 'g' || currentChar == 'q' || currentChar == 'y' || currentChar == ',') yOffset += 40;
				glPushMatrix();
				glLoadIdentity();
				glScalef(scale, scale, 0);
				glTranslatef(xOffset, yOffset, 0);
				glBindTexture(GL_TEXTURE_2D, texIDs[index]);

				glBindVertexArray(vao);
				glEnableVertexAttribArray(0);
				glEnableVertexAttribArray(1);
				{
					glTranslatef(x, y, 0);
					glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vio);
					glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, 0);
					glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
				}
				glDisableVertexAttribArray(1);
				glDisableVertexAttribArray(0);
				glBindVertexArray(0);

				glBindTexture(GL_TEXTURE_2D, 0);
				glPopMatrix();
			}
			xx += (this.size + spacing) * scale;
		}
		shader.release();
		glDisable(GL_BLEND);
	}
}
