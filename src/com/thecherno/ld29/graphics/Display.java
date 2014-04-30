package com.thecherno.ld29.graphics;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public class Display {

	private static int width, height;

	public static void create(String title, int width, int height) {
		Display.width = width;
		Display.height = height;
		try {
			org.lwjgl.opengl.Display.setDisplayMode(new DisplayMode(width, height));
			org.lwjgl.opengl.Display.setTitle(title);
			org.lwjgl.opengl.Display.create(new PixelFormat(0, 16, 1));
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public static void setOrtho(float left, float right, float bottom, float top, float near, float far) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(left, right, bottom, top, near, far);
		glMatrixMode(GL_MODELVIEW);
	}

	public static void initGL() {
		setOrtho(0, width, height, 0, -1.0f, 1.0f);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glEnable(GL_STENCIL_TEST);
		glFrontFace(GL_CW);
	}

	public static void update() {
		org.lwjgl.opengl.Display.update();
	}

	public static boolean close() {
		return org.lwjgl.opengl.Display.isCloseRequested();
	}

	public static void destroy() {
		org.lwjgl.opengl.Display.destroy();
	}

}
