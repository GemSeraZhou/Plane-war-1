package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import drawing.GameCanvas;
import input.InputUtility;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import sharedObject.RenderableHolder;
import window.SceneManager;

public class GameLogic {
	private List<Entity> gameObjectContainer; //ALL enemy bullet boss laser item
	private List<Entity> graveYard;
	private List<Enemy> enemys;
	private GameCanvas canvas;
	private Tank tank;
	private Boss boss;
	private Laser ai;
	/*private Enemy enemy;
	private Item item;*/
	private Bullet aBullet;
	private int upgrade = 0;
	private int count = 0;
	private int lastCount = 0;
	//private int count1;
	//private int count2;
	private int tickSpawn=0;
	private int lastTickSpawn=480;
	boolean otk = false;
	boolean start3 = true;
	private int countEnemy = 0;
	public static int killEnemy = 0;
	public int killEnemyForItem=0;
	private boolean bossOnce = true;
	private boolean bossDead=false;
	private int bossCount=1;

	public GameLogic(GameCanvas canvas) {
		this.gameObjectContainer = new ArrayList<Entity>();
		this.graveYard = new ArrayList<Entity>();
		this.enemys = new ArrayList<Enemy>();
		this.canvas = canvas;
		tank = new Tank(380, 300);
		ai = new Laser(300,300,2,1);
		addNewObject(tank);
		addNewAi();

	}
	public void logicUpdate() {
		tickSpawn++;
		if(this.countEnemy<4)
			addNewAi();
		RenderableHolder.getInstance().update();
		checkEntityDead();
		itemSpawn();
		bossSpawn();
		if(!bossOnce)
			phaseBoss();
		secretKey();
		tankFire();
		enemyFire();
		count++;
		canvas.paintComponent();

	}
	protected void addNewAi() {
		if(this.tickSpawn>=this.lastTickSpawn) {
			for (int i = 0; i < 4; i++) {
				if(this.countEnemy>4) break;
				Enemy enemy = new Enemy(200 + i * 40, 0 * i * 80);
				if(this.bossDead) {
					enemy.levelEnemy=this.bossCount;
					enemy.setImage(new Image("res/bot"+this.bossCount+".png"));
				}
				addNewObject(enemy);
				enemys.add(enemy);
				this.countEnemy++;
			}
			this.lastTickSpawn+=480;
		}
		else if(this.tickSpawn==0) {
			for (int i = 0; i < 4; i++) {
				Enemy enemy = new Enemy(200 + i * 40, 0 * i * 80);
				addNewObject(enemy);
				enemys.add(enemy);
				this.countEnemy++;
			}
		}
	}

	protected void addNewObject(Entity entity) {
		gameObjectContainer.add(entity);
		RenderableHolder.getInstance().add(entity);
	}

	protected void removeObject(Entity entity) {
		gameObjectContainer.remove(entity);
		RenderableHolder.getInstance().remove(entity);
	}

	private void enemyFire() {
		for (Enemy enemy1 : enemys) {
			if (enemy1.fire == false)
				continue;
			Random rand = new Random();
			int randDirect = rand.nextInt(2);
			if (randDirect == 1) {
				Bullet aBullet = new Bullet(enemy1.getX() + 20, enemy1.getY() + 15, enemy1.direction, true);
				addNewObject(aBullet);
			}
		}
	}
	private void itemSpawn() {
		if(this.killEnemyForItem%2==0&&this.killEnemyForItem!=0) {
			Item item = new Item();
			addNewObject(item);
			this.killEnemyForItem=0;
		}
	}
	private void bossSpawn() {
		if (killEnemy == 4 &&this.killEnemy!=0) {
			if(bossOnce) {
			    boss = new Boss(355, 0);
				addNewObject(boss);
				bossOnce=false;
				this.bossDead=false;
			}
			if(boss.destroyed) {
				this.bossDead=true;
				this.countEnemy=0;
				this.bossOnce=true;
				this.killEnemy=0;
				this.bossCount++;
			}
			
		}
	}
	private void checkEntityDead() {
		for (Entity e : gameObjectContainer) {
			if (e.destroyed != true) {
				e.update();
			} else {
				if (e instanceof Enemy) {
					killEnemy++;
					this.killEnemyForItem++;
					enemys.remove(e);
				}
				this.graveYard.add(e);
				// System.out.println(e);
			}
		}
		for (Entity e : graveYard) {
			removeObject(e);
			if (this.enemys.contains(e)) {
				this.enemys.remove(e);
			}

		}
		graveYard.clear();
	}

	private void phaseBoss() {
		if (boss.phase1) {
			if (boss.b1) {
				ai.playerPos(tank.x + tank.width / 2, tank.y + tank.height / 2);
				aBullet = new Bullet(boss.getX() + boss.width, boss.getY() + boss.height, boss.direction, true);
				addNewObject(aBullet);
			}
		}
		if (boss.phase2) {
			if (boss.b2) {
				ai.playerPos(tank.x + tank.width / 2, tank.y + tank.height / 2);
				boss.playerPos(tank.x, tank.y);
				int count1 = new Random().nextInt(8);
				int count2 = new Random().nextInt(8);
				// System.out.println(count1);
				if (boss.barrier) {
					Barrier barrier = new Barrier(boss);
					addNewObject(barrier);
				}
				if (count1 == 0 || count1 == 1 || count2 == 6 || count2 == 1) {
					Laser laser = new Laser(boss.getX() + (boss.width / 2), boss.getY() + boss.height, 3, 1);
					addNewObject(laser);
				}
				if (count1 == 0 || count1 == 2 || count2 == 6 || count2 == 2) {
					Laser laser1 = new Laser(boss.getX() + (boss.width / 2), boss.getY(), 3, 0);
					addNewObject(laser1);
				}
				if (count1 == 0 || count1 == 3 || count2 == 6 || count2 == 3) {
					Laser laser2 = new Laser(boss.getX() + (97), boss.getY() + 74, 2, 0);
					addNewObject(laser2);
				}
				if (count1 == 0 || count1 == 4 || count2 == 6 || count2 == 4) {
					Laser laser3 = new Laser(boss.getX() + (97), boss.getY() + 27, 2, 1);
					addNewObject(laser3);
				}
				if (count1 == 0 || count1 == 5 || count2 == 6 || count2 == 5) {
					Laser laser4 = new Laser(boss.getX() + (2), boss.getY() + 76, 2, 2);
					addNewObject(laser4);
				}
				if (count1 == 0 || count1 == 6 || count2 == 6 || count2 == 6) {
					Laser laser5 = new Laser(boss.getX() + (2), boss.getY() + (26), 2, 3);
					addNewObject(laser5);
				}

			}
			if (boss.b3) {
				ai.setDirection(3);
				ai.playerPos(tank.x + tank.width / 2, tank.y + tank.height / 2);
				if (boss.barrier) {
					Barrier barrier = new Barrier(boss);
					addNewObject(barrier);
				}
				Bomb bomb = new Bomb(boss.getX(), boss.getY(), boss.getHp());
				addNewObject(bomb);
			}
		}
		if (boss.phase3) {
			if (start3) {
				
				Fang fang1 = new Fang(boss.x + (boss.width / 2) - (16), boss.y - 40, 1, new Image("res/fang1.png"), boss);
				addNewObject(fang1);

				Fang fang2 = new Fang(boss.x - (32), boss.y, 2, new Image("res/fang2.png"), boss);
				addNewObject(fang2);

				Fang fang3 = new Fang(boss.x - (32), boss.y + (boss.height / 2) + (16), 3, new Image("fang3.png"),
						boss);
				addNewObject(fang3);

				Fang fang4 = new Fang(boss.x + (boss.width / 2) - (16), boss.y + 40 + boss.getHeight() - (32), 4,
						new Image("fang4.png"), boss);
				addNewObject(fang4);

				Fang fang5 = new Fang(boss.x + (boss.width), boss.y, 5, new Image("res/fang5.png"), boss);
				addNewObject(fang5);

				Fang fang6 = new Fang(boss.x + (boss.width), boss.y + (boss.height / 2) + (16), 6,
						new Image("res/fang6.png"), boss);
				addNewObject(fang6);
				
				boss.setFang(fang1, fang2, fang3, fang4, fang5, fang6);

				start3 = false;
			}
			
			if(boss.b5) {
				Laser l1 = new Laser(boss.fang1.x+20, boss.fang1.y+20, 6, 10, tank.x+20, tank.y+15,1);
				addNewObject(l1);
				
				Laser l4 = new Laser(boss.fang4.x+20, boss.fang4.y+20, 6, 10, tank.x+20, tank.y+15,4);
				addNewObject(l4);
				
			}
			
			if (boss.barrier) {
				Barrier barrier = new Barrier(boss);
				addNewObject(barrier);
			}

		}
	}
	private void secretKey() {
		if (InputUtility.getKeyPressed(KeyCode.U) && count - lastCount > 50) {
			System.out.println("x");
			this.upgrade++;
			lastCount = count;
		}
		if (InputUtility.getKeyPressed(KeyCode.O)) {
			otk = true;
		}
		if (InputUtility.getKeyPressed(KeyCode.R)) {
			tank = new Tank(380, 450);
			addNewObject(tank);
		}
	}
	private void tankFire() {
		if (tank.fire) {
			aBullet = new Bullet(tank.getX() + 20, tank.getY() + 15, tank.direction, false);
			for (int i = 0; i < upgrade; i++) {
				aBullet.upgrade();
			}
			if (otk) {
				aBullet.otk();
			}
			addNewObject(aBullet);
		}
	}
}
