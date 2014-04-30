package com.thecherno.ld29.menu;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import org.lwjgl.opengl.Display;

import com.thecherno.ld29.Main;
import com.thecherno.ld29.State;
import com.thecherno.ld29.graphics.Font;
import com.thecherno.ld29.input.Keyboard;
import com.thecherno.ld29.resources.Texture;

public class Menu {

	protected static final int MAX_SELECTED = 2;
	protected int texture;
	protected static Font font;
	protected int pointer, background;
	private int selected = 0;
	protected int x = 700, y = 384;

	public Menu() {
		glActiveTexture(GL_TEXTURE0);
		texture = Texture.MENU;
		create();
	}

	protected void create() {
		font = new Font();
		background = glGenLists(1);
		glNewList(background, GL_COMPILE);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2f(0, 0);
		glTexCoord2f(960.0f / 1024.0f, 0);
		glVertex2f(960, 0);
		glTexCoord2f(960.0f / 1024.0f, 540.0f / 4096.0f);
		glVertex2f(960, 540);
		glTexCoord2f(0, 540.0f / 4096.0f);
		glVertex2f(0, 540);
		glEnd();
		glEndList();

		pointer = glGenLists(1);
		glNewList(pointer, GL_COMPILE);
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(30, 0);
		glVertex2f(30, 10);
		glVertex2f(0, 10);
		glEnd();
		glEndList();
	}

	public void update() {
		if (Keyboard.keyTyped(Keyboard.VK_DOWN) && selected < MAX_SELECTED) selected++;
		if (Keyboard.keyTyped(Keyboard.VK_UP) && selected > 0) selected--;
		if (selected < 0) selected = 0;
		if (selected > MAX_SELECTED) selected = MAX_SELECTED;

		if (selected == 0 && (Keyboard.keyTyped(Keyboard.VK_ENTER) || Keyboard.keyTyped(Keyboard.VK_SPACE))) {
			State.setState(State.GAME);
		}
		if (selected == 1 && (Keyboard.keyTyped(Keyboard.VK_ENTER) || Keyboard.keyTyped(Keyboard.VK_SPACE))) {
			Main.setMenu(Main.about);
		}
		if (selected == 2 && (Keyboard.keyTyped(Keyboard.VK_ENTER) || Keyboard.keyTyped(Keyboard.VK_SPACE))) {
			Display.destroy();
			System.exit(0);
		}
	}

	public void render() {
		if (pointer != 0) {
			glLoadIdentity();
			glTranslatef(x, y, 0);
			glCallList(pointer);
			if (selected == 0) {
				x = 740;
				y = 384;
			} else if (selected == 1) {
				x = 720;
				y = 438;
			} else if (selected == 2) {
				x = 750;
				y = 484;
			}
		}

		glLoadIdentity();
		glEnable(GL_TEXTURE_2D);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		glCallList(background);
		glBindTexture(GL_TEXTURE_2D, 0);
		glDisable(GL_TEXTURE_2D);
	}
}
