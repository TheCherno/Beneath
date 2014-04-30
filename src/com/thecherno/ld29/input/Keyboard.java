package com.thecherno.ld29.input;

import java.util.ArrayList;
import java.util.List;

public class Keyboard {

	public static final int VK_UP = org.lwjgl.input.Keyboard.KEY_UP;
	public static final int VK_DOWN = org.lwjgl.input.Keyboard.KEY_DOWN;
	public static final int VK_LEFT = org.lwjgl.input.Keyboard.KEY_LEFT;
	public static final int VK_RIGHT = org.lwjgl.input.Keyboard.KEY_RIGHT;
	public static final int VK_ENTER = org.lwjgl.input.Keyboard.KEY_RETURN;
	public static final int VK_SPACE = org.lwjgl.input.Keyboard.KEY_SPACE;
	public static final int VK_ESCAPE = org.lwjgl.input.Keyboard.KEY_ESCAPE;

	private static List<Integer> pressed = new ArrayList<Integer>();

	public static boolean keyPressed(int key) {
		return org.lwjgl.input.Keyboard.isKeyDown(key);
	}

	public static void update() {
		for (int i = 0; i < pressed.size(); i++) {
			if (!keyPressed(pressed.get(i))) pressed.remove(new Integer(pressed.get(i)));
		}
	}

	public static boolean keyTyped(int key) {
		if (!keyPressed(key)) return false;
		if (pressed.contains(key)) return false;
		pressed.add(key);

		return org.lwjgl.input.Keyboard.isKeyDown(key);
	}

}
