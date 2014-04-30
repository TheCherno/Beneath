package com.thecherno.ld29.menu;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import com.thecherno.ld29.Main;
import com.thecherno.ld29.graphics.Font;
import com.thecherno.ld29.input.Keyboard;
import com.thecherno.ld29.resources.Texture;

public class LoseMenu extends Menu {

	public LoseMenu() {
		glActiveTexture(GL_TEXTURE0);
		texture = Texture.MENU;
		create();
	}

	public void update() {
		if (Keyboard.keyTyped(Keyboard.VK_ENTER) || Keyboard.keyTyped(Keyboard.VK_SPACE) || Keyboard.keyTyped(Keyboard.VK_ESCAPE)) {
			Main.setMenu(Main.main);
		}
	}

	protected void create() {
		background = glGenLists(1);
		glNewList(background, GL_COMPILE);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 540.0f * 2.0f / 4096.0f);
		glVertex2f(0, 0);
		glTexCoord2f(960.0f / 1024.0f, 540.0f * 2.0f / 4096.0f);
		glVertex2f(960, 0);
		glTexCoord2f(960.0f / 1024.0f, 540.0f * 3.0f / 4096.0f);
		glVertex2f(960, 540);
		glTexCoord2f(0, 540.0f * 3.0f / 4096.0f);
		glVertex2f(0, 540);
		glEnd();
		glEndList();
	}

}
