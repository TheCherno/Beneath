package com.thecherno.ld29.level.tile;

import com.thecherno.ld29.graphics.Shader;
import com.thecherno.ld29.resources.Texture;

public class WaterTile extends ForeTile {

	public WaterTile() {
		super(Texture.WATER);
		createShader(Shader.WATER);
	}

}
