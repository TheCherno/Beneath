package com.thecherno.ld29.resources;

import java.io.File;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Sound {

	static {
		new JFXPanel();
	}

	private volatile MediaPlayer player;

	public Sound(String file) {
		create(file);
	}

	private void create(String file) {
		final File soundFile = new File(file);
		new Thread() {
			public void run() {
				try {
					player = new MediaPlayer(new Media(soundFile.toURI().toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void play() {
		new Thread() {
			public void run() {
				while (player == null) {
				}
				player.stop();
				player.setCycleCount(MediaPlayer.INDEFINITE);
				player.play();
			}
		}.start();
	}

	public void dispose() {
		if (player != null) {
			player.stop();
			player.dispose();
		}

	}

	public void stop() {
		if (player != null) player.stop();
	}
}
