package com.thecherno.ld29.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.thecherno.ld29.entity.Entity;

public class Light {

	public int x, y;
	private int xOffset, yOffset;
	public Vector3f vc;
	private int color;
	public float intensity = 1.0f;
	private float radi = 0.4f;

	public float dir = 0;

	public Light(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.vc = new Vector3f(((color & 0xff0000) >> 16) / 255.0f + radi, ((color & 0xff00) >> 8) / 255.0f + radi, (color & 0xff) / 255.0f + radi);
	}

	public Light(int x, int y, int color, float intensity) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.intensity = intensity;
		this.vc = new Vector3f(((color & 0xff0000) >> 16) / 255.0f + radi, ((color & 0xff00) >> 8) / 255.0f + radi, (color & 0xff) / 255.0f + radi);
	}

	public void bindUniforms(int shader) {
		int uniform = glGetUniformLocation(shader, "lightPosition");
		glUniform2f(uniform, x - xOffset, 540.0f - y + yOffset);

		uniform = glGetUniformLocation(shader, "lightColor");
		glUniform3f(uniform, vc.x, vc.y, vc.z);

		uniform = glGetUniformLocation(shader, "lightIntensity");
		glUniform1f(uniform, intensity);
	}

	public void setWhiteness(float whiteness) {
		vc = new Vector3f(((color & 0xff0000) >> 16) / 255.0f + whiteness, ((color & 0xff00) >> 8) / 255.0f + whiteness, (color & 0xff) / 255.0f + whiteness);
	}

	public void setColor(int color) {
		vc = new Vector3f(((color & 0xff0000) >> 16) / 255.0f + radi, ((color & 0xff00) >> 8) / 255.0f + radi, (color & 0xff) / 255.0f + radi);
	}

	public void shadows(Vector2f[][] foregrounds, List<Entity> entities, int width, int height) {
		glColorMask(false, false, false, false);
		glStencilFunc(GL_ALWAYS, 1, 1);
		glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
		Vector2f lightpos = new Vector2f(x, y);
		int x0 = xOffset >> 6;
		int x1 = (xOffset >> 6) + 16;
		int y0 = yOffset >> 6;
		int y1 = (yOffset >> 6) + 10;
		for (int y = y0 - 20; y < y1 + 20; y++) {
			for (int x = x0 - 20; x < x1 + 20; x++) {
				if (x < 0 || x >= width || y < 0 || y >= height) continue;
				Vector2f[] vertices = foregrounds[x + y * width];
				for (int i = 0; i < vertices.length; i++) {
					Vector2f current = vertices[i];
					if (current == null) break;
					Vector2f next = vertices[(i + 1) % vertices.length];
					Vector2f edge = Vector2f.sub(next, current, null);
					Vector2f normal = new Vector2f(edge.y, -edge.x);
					Vector2f dir = Vector2f.sub(current, lightpos, null);
					if (Vector2f.dot(normal, dir) > 0) {
						Vector2f point0 = Vector2f.add(current, (Vector2f) Vector2f.sub(current, lightpos, null).scale(960.0f), null);
						Vector2f point1 = Vector2f.add(next, (Vector2f) Vector2f.sub(next, lightpos, null).scale(960.0f), null);
						float z = 0.5f;
						// TODO: Turn into VAO!
						glBegin(GL_QUADS);
						{
							glVertex3f(current.x, current.y, z);
							glVertex3f(point0.x, point0.y, z);
							glVertex3f(point1.x, point1.y, z);
							glVertex3f(next.x, next.y, z);
						}
						glEnd();
					}
				}
			}
		}

		for (int j = 0; j < entities.size(); j++) {
			Entity e = entities.get(j);
			Vector2f[] vertices = new Vector2f[] {
					new Vector2f(e.getX(), e.getY()), //
					new Vector2f(e.getX() + 64.0f, e.getY()), //
					new Vector2f(e.getX() + 64.0f, e.getY() + 64.0f), //
					new Vector2f(e.getX(), e.getY() + 64.0f) //
			};
			for (int i = 0; i < vertices.length; i++) {
				Vector2f current = vertices[i];
				if (current == null) break;
				Vector2f next = vertices[(i + 1) % vertices.length];
				Vector2f edge = Vector2f.sub(next, current, null);
				Vector2f normal = new Vector2f(edge.y, -edge.x);
				Vector2f dir = Vector2f.sub(current, lightpos, null);
				if (Vector2f.dot(normal, dir) > 0) {
					Vector2f point0 = Vector2f.add(current, (Vector2f) Vector2f.sub(current, lightpos, null).scale(960.0f), null);
					Vector2f point1 = Vector2f.add(next, (Vector2f) Vector2f.sub(next, lightpos, null).scale(960.0f), null);
					float z = 0.5f;
					// TODO: Turn into VAO!
					glBegin(GL_QUADS);
					{
						glVertex3f(current.x, current.y, z);
						glVertex3f(point0.x, point0.y, z);
						glVertex3f(point1.x, point1.y, z);
						glVertex3f(next.x, next.y, z);
					}
					glEnd();
				}
			}
		}

		glColorMask(true, true, true, true);
		glStencilFunc(GL_EQUAL, 0, 1);
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
	}

	public void setOffset(int xo, int yo) {
		this.xOffset = xo;
		this.yOffset = yo;
	}

	public void render(int shader) {
		glUseProgram(shader);
		glColorMask(true, true, true, true);
		bindUniforms(shader);

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		// TODO: Turn into VAO!
		glBegin(GL_QUADS);
		glVertex3f(0 + xOffset, 0 + yOffset, 0.0f);
		glVertex3f(960 + xOffset, 0 + yOffset, 0.0f);
		glVertex3f(960 + xOffset, 540 + yOffset, 0.0f);
		glVertex3f(0 + xOffset, 540 + yOffset, 0.0f);
		glEnd();
		glDisable(GL_BLEND);
		glUseProgram(0);
		glClear(GL_STENCIL_BUFFER_BIT);
	}

	public int getXOffset() {
		return xOffset;
	}

	public int getYOffset() {
		return yOffset;
	}
}
