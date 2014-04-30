package com.thecherno.ld29.graphics;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector2f;

public class Camera {

	private static Vector2f position = new Vector2f();

	public static void move(float x, float y) {
		position.x = x;
		position.y = y;
	}

	public static void render() {
		glLoadIdentity();
		glTranslatef(position.x, position.y, 0);

	}

}
