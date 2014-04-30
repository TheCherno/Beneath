package com.thecherno.ld29.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

	private FileUtils() {
	}

	public static String loadAsString(String file) {
		String result = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				result += buffer + "\n";
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
