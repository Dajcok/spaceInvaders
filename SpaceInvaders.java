package com.spaceinvaders;
import org.jsfml.graphics.*;
import org.jsfml.system.Vector2f;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.Random;

class projectile {
	RectangleShape projectile = new RectangleShape();
	private Vector2f size;
	private Vector2f position;
	private Color color = Color.WHITE;

	projectile(Vector2f size, Vector2f position) {
		this.position = position;
		this.size = size;

		this.projectile.setSize(this.size);
		this.projectile.setPosition(this.position);
		this.projectile.setOrigin(new Vector2f(this.size.x / 2, this.size.y));
		this.projectile.setFillColor(this.color);
	}

	void updatePosition(Vector2f position) {
		this.position = position;
		this.projectile.setPosition(this.position);
	}

	void setColor(Color color) {
		this.color = color;
		this.projectile.setFillColor(this.color);
	}

	Vector2f getPosition() {
		return this.position;
	}
}

class texturesLoader {
	private Texture texture = new Texture();
	Sprite sprite = null;
	private boolean textureLoaded;
	private Vector2f textureScale;
	private Vector2f spritePos;

	texturesLoader(String filePath, Vector2f textureScale) {
		this.textureScale = textureScale;

		try {
			this.texture.loadFromFile(Paths.get(filePath));
			System.out.println("Textúra " + filePath + " úspešne načítaná");

			this.textureLoaded = true;
		} catch(IOException ex) {
			ex.printStackTrace();
			this.textureLoaded = false;
		}
	}

	Sprite createSprite() {
		if(this.textureLoaded) {
			Sprite sprite = new Sprite(this.texture);
			sprite.setScale(this.textureScale);
			return sprite;
		} else {
			System.out.println("Sprite z textúry nemohol byť vytvorený");
			return null;
		}
	}

	boolean updatePosition(Vector2f position) {
		this.spritePos = position;

		if(this.sprite == null) {
			this.sprite = createSprite();
			this.sprite.setOrigin(Vector2f.div(new Vector2f(texture.getSize()), 2));
			return false;
		}

		else {
			this.sprite.setPosition(this.spritePos);
			return true;
		}
	}

	Vector2f getSpritePos() {
		return this.spritePos;
	}
}

class targetWindow {
	private boolean changeDir = false;
	private boolean win = false;

	private projectile enemyProjectile = null;
	private boolean eProjectileFired = false;

	targetWindow(int W, int H, int frameLimit) {

		texturesLoader mainMenu = new texturesLoader("menu.png", new Vector2f(1.f, 1.f));
		while(!mainMenu.updatePosition(new Vector2f(1920 / 2, 1080 / 2)));

		RenderWindow window = new RenderWindow();
		window.create(new VideoMode(W, H), "Space Invaders", WindowStyle.FULLSCREEN);
		window.setFramerateLimit(frameLimit);

		while(true) {

			int enemies1PosX = 0;
			int enemies2PosX = 0;
			int enemies3PosX = 0;
			int clock = 0;
			int livesNum = 3;

			Vector2f difficulty = new Vector2f(60.f,1.f);

			boolean projectileFired = false;
			boolean newGameHover = true;
			boolean newGame = false;
			boolean diffBool = false;

			projectile myProjectile = null;

			texturesLoader bckg = new texturesLoader("background.png", new Vector2f(1.f, 1.f));
			while (!bckg.updatePosition(new Vector2f(1920 / 2, 1080 / 2))) ;

			texturesLoader ship = new texturesLoader("ship.png", new Vector2f(0.2f, 0.2f));
			while (!ship.updatePosition(new Vector2f(1150.f, 490.f))) ;

			Vector<texturesLoader> lives = new Vector<>();

			for (int i = 0; i < 3; i++) {
				lives.addElement(new texturesLoader("ship.png", new Vector2f(0.1f, 0.1f)));
				while (!lives.elementAt(i).updatePosition(new Vector2f(100.f + (i * 100), 100.f))) ;
			}

			Vector<texturesLoader> enemies1 = new Vector<>();

			for (int i = 0; i < 18; i++) {
				enemies1.addElement(new texturesLoader("enemy1.png", new Vector2f(0.2f, 0.2f)));
				while (!enemies1.elementAt(i).updatePosition(new Vector2f(100.f + enemies1PosX, 400.f))) ;
				enemies1PosX += 100;
			}

			Vector<texturesLoader> enemies2 = new Vector<>();

			for (int i = 0; i < 18; i++) {
				enemies2.addElement(new texturesLoader("enemy2.png", new Vector2f(0.2f, 0.2f)));
				while (!enemies2.elementAt(i).updatePosition(new Vector2f(100.f + enemies2PosX, 300.f))) ;
				enemies2PosX += 100;
			}

			Vector<texturesLoader> enemies3 = new Vector<>();

			for (int i = 0; i < 18; i++) {
				enemies3.addElement(new texturesLoader("enemy3.png", new Vector2f(0.2f, 0.2f)));
				while (!enemies3.elementAt(i).updatePosition(new Vector2f(100.f + enemies3PosX, 200.f))) ;
				enemies3PosX += 100;
			}

			Vector<texturesLoader> allEnemies = new Vector<>();

			for (int i = 0; i < 18; i++) {
				allEnemies.addElement(enemies1.elementAt(i));
				allEnemies.addElement(enemies2.elementAt(i));
				allEnemies.addElement(enemies3.elementAt(i));
			}

			MAINMENUSCENE:
			while (window.isOpen()) {
				if(newGame){
					mainMenu = new texturesLoader("diff.png", new Vector2f(1.f, 1.f));
					while(!mainMenu.updatePosition(new Vector2f(1920 / 2, 1080 / 2)));
					ship.updatePosition(new Vector2f(1100, 450));
					newGame = false;
					diffBool = true;
				}

				window.clear();
				window.draw(mainMenu.sprite);
				window.draw(ship.sprite);
				window.display();

				for (Event event : window.pollEvents()) {
					switch (event.type) {
						case KEY_PRESSED:
							KeyEvent keyEvent = event.asKeyEvent();
							if(!diffBool) {
								if (keyEvent.key == Keyboard.Key.UP && ship.getSpritePos().y != 490) {
									ship.updatePosition(new Vector2f(1150, 490));
									newGameHover = true;
								} else if (keyEvent.key == Keyboard.Key.DOWN && ship.getSpritePos().y != 590) {
									ship.updatePosition(new Vector2f(1200, 590));
									newGameHover = false;
								} else if (keyEvent.key == Keyboard.Key.ESCAPE) {
									System.exit(0);
								}
							} else {
								if (keyEvent.key == Keyboard.Key.UP && ship.getSpritePos().y == 540) {
									ship = new texturesLoader("ship.png", new Vector2f(0.2f, 0.2f));
									while(!ship.updatePosition(new Vector2f(1100, 450)));
									difficulty = new Vector2f(60, 1);//difficulty 1
								} else if (keyEvent.key == Keyboard.Key.UP && ship.getSpritePos().y == 630) {
									ship = new texturesLoader("ship_diff1.png", new Vector2f(0.2f, 0.2f));
									while(!ship.updatePosition(new Vector2f(1400, 540)));
									difficulty = new Vector2f(10, 1);//difficulty 2
								} else if (keyEvent.key == Keyboard.Key.DOWN && ship.getSpritePos().y == 540) {
									ship = new texturesLoader("ship_diff2.png", new Vector2f(0.2f, 0.2f));
									while(!ship.updatePosition(new Vector2f(1150, 630)));
									difficulty = new Vector2f(10, 2);//difficulty 3
								} else if (keyEvent.key == Keyboard.Key.DOWN && ship.getSpritePos().y == 450) {
									ship = new texturesLoader("ship_diff1.png", new Vector2f(0.2f, 0.2f));
									while(!ship.updatePosition(new Vector2f(1400, 540)));
									difficulty = new Vector2f(10, 1);//difficulty 2
								} else if (keyEvent.key == Keyboard.Key.ESCAPE) {
									System.exit(0);
								} else if (keyEvent.key == Keyboard.Key.RETURN) {
									ship = new texturesLoader("ship.png", new Vector2f(0.2f, 0.2f));
									while (!ship.updatePosition(new Vector2f(100.f, 1000.f))) ;
									break MAINMENUSCENE;
								}
							}
							break;

						case CLOSED:
							window.close();
							System.exit(0);
					}
					if (event.type == Event.Type.CLOSED) {
						window.close();
					}
				}

				if(newGameHover) {
					if(Keyboard.isKeyPressed(Keyboard.Key.RETURN)) {
						newGame = true;
					}
				} else
					if(Keyboard.isKeyPressed(Keyboard.Key.RETURN)) {
						System.exit(0);
					}
			}


			MAINSCENE:
			while (window.isOpen()) {
				window.clear(Color.BLACK);
				window.draw(bckg.sprite);
				window.draw(ship.sprite);
				for (int i = 0; i < allEnemies.size(); i++)
					window.draw(allEnemies.elementAt(i).sprite);
				if (myProjectile != null)
					window.draw(myProjectile.projectile);
				if (this.enemyProjectile != null)
					window.draw(this.enemyProjectile.projectile);
				if (lives != null)
					for (int i = 0; i < lives.size(); i++)
						window.draw(lives.elementAt(i).sprite);
				window.display();


				if (projectileFired && myProjectile != null) {
					myProjectile.updatePosition(new Vector2f(myProjectile.getPosition().x, myProjectile.getPosition().y - 50));
					if (myProjectile.getPosition().y < 0)
						projectileFired = false;
				}

				if (eProjectileFired && enemyProjectile != null) {
					this.enemyProjectile.updatePosition(new Vector2f(this.enemyProjectile.getPosition().x, this.enemyProjectile.getPosition().y + 50 * difficulty.y));
					if (this.enemyProjectile.getPosition().y > 1200)
						this.eProjectileFired = false;
				}

				if (clock % difficulty.x == 0) {
					if (!this.eProjectileFired) {
						try {
							for(int i = 0; i < 1; i++) {
								Random rand = new Random();
								int random = rand.nextInt(allEnemies.size());
								this.enemyFire(allEnemies, random);
							}
						} catch (Exception e) {
							this.win = true;
							break MAINSCENE;
						}
					}
				}


				if (myProjectile != null) {
					A:
					for (int i = 0; i < allEnemies.size() + 1; i++) {
						if (allEnemies.size() != i && hitCheck(myProjectile.getPosition(), allEnemies.elementAt(i).getSpritePos(), 30)) {
							myProjectile = null;
							projectileFired = false;
							allEnemies.remove(i);
							break A;
						}
					}
				}

				if (this.enemyProjectile != null) {
					if (hitCheck(this.enemyProjectile.getPosition(), ship.getSpritePos(), 100)) {
						this.enemyProjectile = null;
						this.eProjectileFired = false;
						if (lives != null) {
							livesNum--;
							if (livesNum == 0) {
								break MAINSCENE;
							}
							lives.remove(0);
						}
					}
				}

				enemyMovement(allEnemies);
				if(Keyboard.isKeyPressed(Keyboard.Key.ESCAPE)) {
					System.exit(0);
				}

				if (Keyboard.isKeyPressed(Keyboard.Key.RIGHT) && ship.getSpritePos().x < 1850)
					ship.updatePosition(new Vector2f(ship.getSpritePos().x + 20.f, ship.getSpritePos().y));
				else if (Keyboard.isKeyPressed(Keyboard.Key.LEFT) && ship.getSpritePos().x > 100)
					ship.updatePosition(new Vector2f(ship.getSpritePos().x - 20.f, ship.getSpritePos().y));
				else if (Keyboard.isKeyPressed(Keyboard.Key.UP) && !projectileFired) {
					myProjectile = new projectile(new Vector2f(5, 50), ship.getSpritePos());
					projectileFired = true;
				}

				for (Event event : window.pollEvents()) {
					switch (event.type) {
						case CLOSED:
							window.close();
							System.exit(0);
					}
					if (event.type == Event.Type.CLOSED) {
						window.close();
					}
				}
				clock++;
			}

			if(!this.win) {
				mainMenu = new texturesLoader("gameOver.png", new Vector2f(1.f, 1.f));
				while (!mainMenu.updatePosition(new Vector2f(1920 / 2, 1080 / 2))) ;
			} else {
				mainMenu = new texturesLoader("gameOverWin.png", new Vector2f(1.f, 1.f));
				while (!mainMenu.updatePosition(new Vector2f(1920 / 2, 1080 / 2))) ;
			}
		}
	}

	boolean hitCheck(Vector2f bulletPos, Vector2f targetPos, int halfSize) {
		if((bulletPos.x > targetPos.x - halfSize && bulletPos.y > targetPos.y - halfSize) && (bulletPos.x < targetPos.x + halfSize && bulletPos.y < targetPos.y + halfSize))
			return true;
		else
			return false;
	}
	void enemyFire(Vector<texturesLoader> enemies, int random) {
		this.enemyProjectile = new projectile(new Vector2f(5, 50), enemies.elementAt(random).getSpritePos());
		this.enemyProjectile.setColor(Color.RED);
		this.eProjectileFired = true;
	}

	void enemyMovement(Vector<texturesLoader> enemies) {
		OUT:
		for(int i = 0; i < enemies.size(); i++) {
			if(enemies.elementAt(i).getSpritePos().x > 1870) {
				enemyMovement_direction(i, enemies);
				for(int j = 0; j < enemies.size() ; j++)
					enemies.elementAt(j).updatePosition(new Vector2f(enemies.elementAt(j).getSpritePos().x - (27.f / enemies.size()), enemies.elementAt(j).getSpritePos().y + 10));

				this.changeDir = true;

				break OUT;
			}

			else if(enemies.elementAt(i).getSpritePos().x < 30) {
				enemyMovement_direction(i, enemies);
				for(int j = 0; j < enemies.size(); j++)
					enemies.elementAt(j).updatePosition(new Vector2f(enemies.elementAt(j).getSpritePos().x + (27.f / enemies.size()), enemies.elementAt(j).getSpritePos().y + 10));
				this.changeDir = false;

				break OUT;
			}
			else
				enemyMovement_direction(i, enemies);
		}
	}

	void enemyMovement_direction(int i, Vector<texturesLoader> enemies) {
		if(this.changeDir) {
			enemies.elementAt(i).updatePosition(new Vector2f (enemies.elementAt(i).getSpritePos().x - (27.f / enemies.size()), enemies.elementAt(i).getSpritePos().y));
		}

		else {
			enemies.elementAt(i).updatePosition(new Vector2f (enemies.elementAt(i).getSpritePos().x + (27.f / enemies.size()), enemies.elementAt(i).getSpritePos().y));
		}
	}
}

public class SpaceInvaders {
	public static void main(String[] args) {
		targetWindow window = new targetWindow(1920, 1080, 60);
	}
}
