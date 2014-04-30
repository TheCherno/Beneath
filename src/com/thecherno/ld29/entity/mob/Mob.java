package com.thecherno.ld29.entity.mob;

import java.util.List;

import com.thecherno.ld29.entity.Entity;
import com.thecherno.ld29.graphics.Light;
import com.thecherno.ld29.level.Level;
import com.thecherno.ld29.level.tile.Tile;

public abstract class Mob extends Entity {

	private int dir = -1;

	public void move(int xa, int ya) {
		if (xa != 0 && ya != 0) {
			move(xa, 0);
			move(0, ya);
			return;
		}
		if (xa < 0) dir = 3;
		if (ya < 0) dir = 0;
		if (xa > 0) dir = 1;
		if (ya > 0) dir = 2;

		if (light(xa, ya, dir)) {
			xa /= 2;
			ya /= 2;
		}
		if (!collision(xa, ya)) {
			x += xa;
			y += ya;
		}

	}

	private boolean light(int xa, int ya, int dir) {
		List<Light> lights = level.getLights(x, y, dir);
		if (lights.size() <= 0) return false;
		Light light = lights.get(0);
		if (light.intensity > 30) return false;
		if (light.dir == 3) {
			light.x--;
		} else if (light.dir == 1) {
			light.x++;
		} else if (light.dir == 0) {
			light.y--;
		} else if (light.dir == 2) {
			light.y++;
		}

		return true;
		/*		for (int i = 0; i < lights.size(); i++) {
					if (light)
				}*/
	}

	public boolean collision(int xa, int ya) {
		boolean solid = false;
		for (int i = 0; i < 4; i++) {
			int xt = ((x + xa) + i % 2 * 48 + 8) >> 6;
			int yt = ((y + ya) + i / 2 * 48 + 8) >> 6;
			Tile tile = level.getTile(xt, yt, Level.FOREGROUND);
			if (tile == null) continue;
			if (tile.solid()) solid = true;
		}
		return solid;
	}
}
