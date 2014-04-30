package com.thecherno.ld29.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Buffer {

	public static FloatBuffer createFloatBuffer(float[] array) {
		FloatBuffer result = BufferUtils.createFloatBuffer(array.length);
		result.put(array);
		result.flip();
		return result;
	}

	public static ByteBuffer createByteBuffer(byte[] array) {
		ByteBuffer result = BufferUtils.createByteBuffer(array.length);
		result.put(array);
		result.flip();
		return result;
	}

	public static IntBuffer createIntBuffer(int[] array) {
		IntBuffer result = BufferUtils.createIntBuffer(array.length);
		result.put(array);
		result.flip();
		return result;
	}
}
