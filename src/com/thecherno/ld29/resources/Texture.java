package com.thecherno.ld29.resources;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.thecherno.ld29.util.Buffer;

public class Texture {

	public static int MENU = 0;
	public static int WALL = 0;
	public static int GROUND = 0;
	public static int PLAYER = 0;
	public static int WATER = 0;
	public static int GRASS = 0;
	public static int DIRT = 0;

	private static List<Integer> textures = new ArrayList<Integer>();

	public static void load() {
		MENU = loadTexture("res/menu.png", false);
		GROUND = loadTexture("res/tex/ground.png", false);
		WALL = loadTexture("res/tex/wall.png", false);
		PLAYER = loadTexture("res/player.png", true);
		WATER = loadTexture("res/tex/water.png", false);
		GRASS = loadTexture("res/tex/grass.png", false);
		DIRT = loadTexture("res/tex/dirt.png", false);
	}

	private static int loadTexture(String path, boolean antialiase) {
		BufferedImage image;
		int width = 0;
		int height = 0;
		int[] pixels = null;
		try {
			image = ImageIO.read(new FileInputStream(path));
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < width * height; i++) {
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);
			pixels[i] = a << 24 | b << 16 | g << 8 | r;
		}

		IntBuffer buffer = Buffer.createIntBuffer(pixels);
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, 3, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		int ps = GL_NEAREST;
		if (antialiase) ps = GL_LINEAR;
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, ps);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, ps);
		glBindTexture(GL_TEXTURE_2D, 0);
		return texture;
	}

	public static int[] loadFont(String path, int hLength, int vLength, int size) {
		int width = 0;
		int height = 0;
		int index = 0;
		int[] ids = new int[hLength * vLength];
		int[] sheet = null;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(path));
			width = image.getWidth();
			height = image.getHeight();
			sheet = new int[width * height];
			image.getRGB(0, 0, width, height, sheet, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int y0 = 0; y0 < vLength; y0++) {
			for (int x0 = 0; x0 < hLength; x0++) {
				int[] letter = new int[size * size];
				for (int y = 0; y < size; y++) {
					for (int x = 0; x < size; x++) {
						letter[x + y * size] = sheet[(x + x0 * size) + (y + y0 * size) * width];
					}
				}

				ByteBuffer buffer = BufferUtils.createByteBuffer(size * size * 4);
				for (int y = 0; y < size; y++) {
					for (int x = 0; x < size; x++) {
						byte a = (byte) ((letter[x + y * size] & 0xff000000) >> 24);
						byte r = (byte) ((letter[x + y * size] & 0xff0000) >> 16);
						byte g = (byte) ((letter[x + y * size] & 0xff00) >> 8);
						byte b = (byte) (letter[x + y * size] & 0xff);
						buffer.put(r).put(g).put(b).put(a);
					}
				}
				buffer.flip();
				int texID = glGenTextures();
				glBindTexture(GL_TEXTURE_2D, texID);
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
				textures.add(texID);
				ids[index++] = texID;
				glBindTexture(GL_TEXTURE_2D, 0);
			}
		}
		return ids;
	}

	public static int get(int texture) {
		if (texture < 0 || texture >= textures.size()) return 0;
		return textures.get(texture);
	}
}
