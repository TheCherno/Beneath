package com.thecherno.ld29;

public class State {

	public static final int MENU = 0x0;
	public static final int GAME = 0x1;

	private static int current = MENU;

	public static int getState() {
		return current;
	}

	public static void setState(int state) {
		if (state == MENU) {
			Main.music.stop();
			Main.piano.play();
		} else if (state == GAME) {
			Main.piano.stop();
			Main.music.play();
		}
		State.current = state;
	}

}
