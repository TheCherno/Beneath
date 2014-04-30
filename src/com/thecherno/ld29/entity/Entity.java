package com.thecherno.ld29.entity;

import com.thecherno.ld29.graphics.Light;
import com.thecherno.ld29.graphics.Shader;
import com.thecherno.ld29.level.Level;

public abstract class Entity {

	protected int x, y;
	protected int vao, vbo, vio, vto;
	protected int texture;
	protected Shader shader;
	protected Level level;
	private boolean removed = false;

	public void remove() {
		removed = true;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void bindUniforms(Light light) {
		shader.bind();
		light.bindUniforms(shader.getID());
		shader.release();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public abstract void update();

	public abstract void render();

	public void init(Level level) {
		this.level = level;
	}

}
