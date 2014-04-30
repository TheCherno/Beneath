package com.thecherno.ld29.level.tile;

import org.lwjgl.util.vector.Vector3f;

import com.thecherno.ld29.graphics.Shader;
import com.thecherno.ld29.resources.Texture;

public class ForeTile extends Tile {

	public static final byte WALL = 0x1;
	public static final byte WATER = 0x4;

	public ForeTile(int texture) {
		this.texture = texture;
		createShader(Shader.FORETILE);
		compile();
	}

	public Vector3f[] getVertexPositions() {
		Vector3f[] result = new Vector3f[4];
		for (int i = 0; i < result.length; i++) {
			result[i] = new Vector3f(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]);
		}
		return result;
	}

	public boolean solid() {
		return true;
	}
}
