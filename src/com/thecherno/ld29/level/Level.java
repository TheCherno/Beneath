package com.thecherno.ld29.level;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;

import com.thecherno.ld29.Main;
import com.thecherno.ld29.State;
import com.thecherno.ld29.entity.Entity;
import com.thecherno.ld29.entity.mob.Player;
import com.thecherno.ld29.graphics.Camera;
import com.thecherno.ld29.graphics.Font;
import com.thecherno.ld29.graphics.Light;
import com.thecherno.ld29.graphics.Shader;
import com.thecherno.ld29.level.tile.DirtTile;
import com.thecherno.ld29.level.tile.ForeTile;
import com.thecherno.ld29.level.tile.GrassTile;
import com.thecherno.ld29.level.tile.Tile;
import com.thecherno.ld29.level.tile.WallTile;
import com.thecherno.ld29.level.tile.WaterTile;

public class Level {

	protected int width, height;
	private Shader lightShader;
	private int xOffset, yOffset;

	public final static int BACKGROUND = 0x0;
	public final static int FOREGROUND = 0x1;
	private Font font;

	private int[] backgroundTiles, foregroundTiles;
	private Random random = new Random();
	private Vector2f[][] foregroundVertices;
	private List<Light> lights = new ArrayList<Light>();
	private List<Entity> entities = new ArrayList<Entity>();

	private float[] waterLevels, waterIncreaseRate;

	private float waterLevel = 0.0f;
	private int time = 0;

	private Tile[] tileIDs = new Tile[5];
	private String path, lightPath;

	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		backgroundTiles = new int[width * height];
		foregroundTiles = new int[width * height];
		foregroundVertices = new Vector2f[width * height][4];
		init();
		genRandom();
	}

	public Level(String path, String lightPath) {
		this.path = path;
		this.lightPath = lightPath;
		initOnce();
		createLevel();
	}

	private void createLevel() {
		load(path);
		loadLights(lightPath);
		init();
	}

	private void load(String path) {
		int[] pixels = null;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(path));
			this.width = image.getWidth();
			this.height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		backgroundTiles = new int[width * height];
		for (int i = 0; i < backgroundTiles.length; i++) {
			backgroundTiles[i] = -1;
		}
		foregroundTiles = new int[width * height];
		for (int i = 0; i < foregroundTiles.length; i++) {
			foregroundTiles[i] = -1;
		}
		foregroundVertices = new Vector2f[width * height][4];

		for (int i = 0; i < width * height; i++) {
			if (pixels[i] == 0xff000000) createForegroundTile(i % width, i / width, ForeTile.WALL);
			else if (pixels[i] == 0xffffffff) createBackgroundTile(i % width, i / width, Tile.GROUND);
			else if (pixels[i] == 0xff00ff00) createBackgroundTile(i % width, i / width, ForeTile.GRASS);
			else if (pixels[i] == 0xffffff00) createBackgroundTile(i % width, i / width, ForeTile.DIRT);
			else if (pixels[i] == 0xff0000ff) createForegroundTile(i % width, i / width, ForeTile.WATER);
		}
	}

	private void loadLights(String lightPath) {
		int[] pixels = null;
		try {
			BufferedImage image = ImageIO.read(new FileInputStream(lightPath));
			this.width = image.getWidth();
			this.height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < width * height; i++) {
			if (pixels[i] != 0xff000000) {
				float intensity = (pixels[i] & 0xff000000) >> 24;
				if (intensity < 0) intensity += 0x7f;
				lights.add(new Light(((i % width) << 6) + (64 >> 1), ((i / width) << 6) + (64 >> 1), pixels[i], intensity / 12));
			}
		}
		lights.add(new Light((106 << 6) + 32, (71 << 6) + 32, 0xffffff, 50.0f));
	}

	private void initOnce() {
		tileIDs[Tile.GROUND] = new Tile();
		tileIDs[ForeTile.WALL] = new WallTile();
		tileIDs[Tile.DIRT] = new DirtTile();
		tileIDs[Tile.GRASS] = new GrassTile();
		tileIDs[ForeTile.WATER] = new WaterTile();
		lightShader = Shader.LIGHT;
		waterLevels = new float[width * height];
		waterIncreaseRate = new float[width * height];
		font = new Font();
	}

	private void init() {
		add(new Player(11, 5));
		for (int i = 0; i < waterIncreaseRate.length; i++) {
			waterIncreaseRate[i] = random.nextFloat();
		}
	}

	private void add(Entity e) {
		entities.add(e);
		e.init(this);
	}

	public List<Light> getLights(int x, int y, int dira) {
		List<Light> lights = new ArrayList<Light>();
		int dir = 0;
		for (int i = 0; i < this.lights.size(); i++) {
			Light light = this.lights.get(i);
			float e0 = light.y - 20;
			float e1 = light.x + 20;
			float e2 = light.y + 20;
			float e3 = light.x - 20;
			if (e3 < x + 64) {
				if (e1 > x) {
					if (e0 < y + 64) {
						if (e2 > y) {
							lights.add(light);
						}
					}
				}
			}
			Vector2f lightPos = new Vector2f(light.x, light.y);

			float distance = 20.0f;
			// Edge 0
			if (distance(lightPos, new Vector2f(x + 32.0f, y + 64.0f)) < distance) {
				distance = distance(lightPos, new Vector2f(x + 32.0f, y + 64.0f));
				dir = 2;
			}
			// Edge 2
			if (distance(lightPos, new Vector2f(x + 32.0f, y)) < distance) {
				distance = distance(lightPos, new Vector2f(x + 32.0f, y));
				dir = 0;
			}
			// Edge 1
			if (distance(lightPos, new Vector2f(x, y + 32.0f)) < distance) {
				distance = distance(lightPos, new Vector2f(x, y + 32.0f));
				dir = 3;
			}
			// Edge 3
			if (distance(lightPos, new Vector2f(x + 64.0f, y + 32.0f)) < distance) {
				dir = 1;
			}
			light.dir = dir;
		}
		return lights;
	}

	private float distance(Vector2f a, Vector2f b) {
		float x = a.x - b.x;
		float y = a.y - b.y;
		return (float) Math.sqrt(x * x + y * y);
	}

	private void genRandom() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int tile = random.nextInt(5);
				if (tile < 4) backgroundTiles[x + y * width] = 1;
				else createForegroundTile(x, y, ForeTile.WALL);
			}
		}
	}

	private void createBackgroundTile(int x, int y, int type) {
		backgroundTiles[x + y * width] = type;
	}

	private void createForegroundTile(int x, int y, int type) {
		foregroundTiles[x + y * width] = type;
		Vector2f[] vertices = new Vector2f[4];
		vertices[0] = new Vector2f(x * Tile.SIZE, y * Tile.SIZE);
		vertices[1] = new Vector2f(x * Tile.SIZE + Tile.SIZE, y * Tile.SIZE);
		vertices[2] = new Vector2f(x * Tile.SIZE + Tile.SIZE, y * Tile.SIZE + Tile.SIZE);
		vertices[3] = new Vector2f(x * Tile.SIZE, y * Tile.SIZE + Tile.SIZE);
		foregroundVertices[x + y * width] = vertices;
	}

	public void update() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).update();
			checkWin(entities.get(i));
		}
		tileIDs[ForeTile.WATER].update();
		time++;
		for (int i = 0; i < tileIDs.length; i++) {
			tileIDs[i].update();
		}
		if (waterLevel / 100 > 100) {
			reset();
			Main.setMenu(Main.gameover);
			State.setState(State.MENU);
		}
		increaseWaterLevels();
	}

	private void checkWin(Entity entity) {
		if (entity instanceof Player) {
			if (entity.getX() + 128 < 106 * 64 + 192 && entity.getX() - 64 > 106 * 64 - 128) {
				if (entity.getY() + 128 < 71 * 64 + 192 && entity.getY() - 64 > 71 * 64 - 128) {
					reset();
					Main.setMenu(Main.win);
					State.setState(State.MENU);
				}
			}
		}
	}

	private void increaseWaterLevels() {
		float speed = 3.0f;
		if (waterLevel / 100 > 40 && waterLevel / 100 < 60) speed = 0.2f;
		waterLevel += random.nextFloat() * speed;
	}

	public void render() {
		Camera.move(-xOffset, -yOffset);
		Camera.render();
		glDepthMask(false);
		for (int i = 0; i < lights.size(); i++) {
			Light light = lights.get(i);
			light.setOffset(xOffset, yOffset);
			light.shadows(foregroundVertices, entities, width, height);
			light.render(lightShader.getID());
			renderBackground(light); // Could be optimized...
		}
		renderForeground(lights);
		glDepthMask(true);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		for (int j = 0; j < lights.size(); j++) {
			for (int i = 0; i < entities.size(); i++) {
				entities.get(i).bindUniforms(lights.get(j));
				entities.get(i).render();
			}
		}
		glDisable(GL_BLEND);
		font.drawString((int) waterLevel / 100 + "%", 20, 20, 6, -5);
	}

	public void renderBackground(Light light) {
		glEnable(GL_BLEND);
		int x0 = xOffset >> 6;
		int x1 = (xOffset >> 6) + 16;
		int y0 = yOffset >> 6;
		int y1 = (yOffset >> 6) + 10;
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				Tile tile = getTile(x, y, BACKGROUND);
				if (tile != null) {
					tile.bindUniforms(light);
					tile.render(x << 6, y << 6, waterLevel);
				}
			}
		}
		glDisable(GL_BLEND);
	}

	public void renderForeground(List<Light> lights) {
		int x0 = xOffset >> 6;
		int x1 = (xOffset >> 6) + 16;
		int y0 = yOffset >> 6;
		int y1 = (yOffset >> 6) + 10;
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				Tile tile = getTile(x, y, FOREGROUND);
				if (tile != null) {
					tile.bindUniforms(lights);
					tile.render(x << 6, y << 6, 0.0f);
				}
			}
		}
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	public int getXOffset() {
		return xOffset;
	}

	public int getYOffset() {
		return yOffset;
	}

	public Tile getTile(int x, int y, int level) {
		if (x < 0 || x >= width || y < 0 || y >= height) return null;
		int id = 0;
		if (level == BACKGROUND) {
			id = backgroundTiles[x + y * width];
			if (id == -1) return null;
			return tileIDs[id];
		}
		id = foregroundTiles[x + y * width];
		if (id == -1) return null;
		return tileIDs[id];
	}

	public void reset() {
		waterLevel = 0.0f;
		lights.clear();
		entities.clear();
		createLevel();
	}

	/*public int getTile(int x, int y, int level) {
		if (x < 0 || x >= width || y < 0 || y >= height) return 0;
		return level == BACKGROUND ? backgroundTiles[x + y * width] : foregroundTiles[x + y * width];
	}*/

}
