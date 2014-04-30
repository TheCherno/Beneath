package com.thecherno.ld29;

import static org.lwjgl.opengl.GL11.*;
import javafx.application.Platform;

import com.thecherno.ld29.graphics.Display;
import com.thecherno.ld29.graphics.Shader;
import com.thecherno.ld29.input.Keyboard;
import com.thecherno.ld29.level.Level;
import com.thecherno.ld29.menu.AboutMenu;
import com.thecherno.ld29.menu.LoseMenu;
import com.thecherno.ld29.menu.Menu;
import com.thecherno.ld29.menu.WinMenu;
import com.thecherno.ld29.resources.Sound;
import com.thecherno.ld29.resources.Texture;

public class Main implements Runnable {

	private int width = 960;
	private int height = 540;

	private boolean running = false;
	private Thread thread;

	private Level level;

	private static Menu menu;
	public static Menu main, about, win, gameover;

	public static Sound piano, music;

	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		Display.create("Beneath the Surface", width, height);
		Display.initGL();
		Texture.load();
		Shader.loadAll();
		level = new Level("levels/prelude.png", "levels/lightmaps/prelude_light.png");
		long lastTime = System.nanoTime();
		double delta = 0;
		double ns = 1000000000.0 / 60.0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		main = new Menu();
		menu = main;
		about = new AboutMenu();
		win = new WinMenu();
		gameover = new LoseMenu();
		piano = new Sound("res/music/piano.mp3");
		music = new Sound("res/music/music.mp3");
		State.setState(State.MENU);
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				update();
				updates++;
				render();
				frames++;
				delta--;
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println(updates + " ups, " + frames + " fps");
				updates = 0;
				frames = 0;
			}
			Display.update();
			if (Display.close()) running = false;
		}
		piano.dispose();
		music.dispose();
		Platform.exit();
		Display.destroy();
		System.exit(0);
	}

	public static void setMenu(Menu menu) {
		Main.menu = menu;
	}

	private void update() {
		Keyboard.update();
		if (State.getState() == State.GAME) level.update();
		else if (State.getState() == State.MENU) menu.update();
		if (Keyboard.keyTyped(Keyboard.VK_ESCAPE)) {
			level.reset();
			State.setState(State.MENU);
		}
	}

	private void drawTestQuad() {
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex3f(50.0f, 50.0f, 0.0f);
		glTexCoord2f(1, 0);
		glVertex3f(350.0f, 50.0f, 0.0f);
		glTexCoord2f(1, 1);
		glVertex3f(350.0f, 350.0f, 0.0f);
		glTexCoord2f(0, 1);
		glVertex3f(50.0f, 350.0f, 0.0f);
		glEnd();
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		if (State.getState() == State.GAME) {
			level.render();
		} else if (State.getState() == State.MENU) {
			menu.render();
		}
		// drawTestQuad();
	}

	public static void main(String[] args) {
		new Main().start();
	}

}
