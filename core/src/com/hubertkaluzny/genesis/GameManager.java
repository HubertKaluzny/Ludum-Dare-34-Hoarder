package com.hubertkaluzny.genesis;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.hubertkaluzny.genesis.entities.*;
import com.hubertkaluzny.genesis.listeners.PlayerPhysicsListener;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.print.attribute.HashAttributeSet;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 13/12/2015.
 */
public class GameManager {
	public World world = new World(new Vector2(0, 0), true);
	public OrthographicCamera cam;
	public List<Entity> entityList = new ArrayList<Entity>();
	public GUIManager guiManager;
	public City city;
	public List<Bullet> bullets = new ArrayList<Bullet>();
	public List<Bullet> bulletsToRemove = new ArrayList<Bullet>();



	public void init(){
		city = new City();
		Box2D.init();
		city.generate();
		guiManager = new GUIManager();
		world.setContactListener(new PlayerPhysicsListener());
	}

	public void render(SpriteBatch batch){
		city.render(batch);
		List<Entity> toTurn = new ArrayList<Entity>();
		List<Entity> toDestroy = new ArrayList<Entity>();
		for(Entity e : entityList){
			if(e.ID != 0){
				if(e instanceof Civilian){
					((Civilian) e).render(batch);
				}else if(e instanceof Police){
					((Police) e).render(batch);
				}else if(e instanceof Zombie){
					((Zombie) e).render(batch);
				}
				e.update();
				if(e.health <= 0 && (e instanceof Civilian || e instanceof Police)){
					toTurn.add(e);
				}else if(e.health <= 0 && e instanceof Zombie){
					toDestroy.add(e);
				}
			}
		}
		for(Bullet b : bullets){
			if(b.position.dst(b.origin) > 100){
				bulletsToRemove.add(b);
			}else{
				b.render(batch);
			}
		}
		List<Integer> removed = new ArrayList<Integer>();
		for(Bullet b : bulletsToRemove){
			if(b.body != null && !removed.contains(b.ID)) {
				world.destroyBody(b.body);
				removed.add(b.ID);
			}
		}
		bullets.removeAll(bulletsToRemove);
		bulletsToRemove.clear();
		Player p = getPlayer();
		for(Entity e : toDestroy){
			if(e instanceof Zombie){
				world.destroyBody(((Zombie) e).body);
				p.zombies--;
			}else if(e instanceof Civilian){
				world.destroyBody(((Civilian) e).body);
			}else if(e instanceof Police){
				world.destroyBody(((Police) e).body);
			}
		}
		entityList.removeAll(toDestroy);
		for(Entity e : toTurn){
			turnToZombie(e.ID);
		}
		for(Entity e : entityList){
			if(e.ID == 0){
				((Player) e).render(batch);
				break;
			}
		}
		guiManager.render(batch);

		int civilians = 0;
		List<Entity> toRemove = new ArrayList<Entity>();
		for(Entity e : entityList){
			if(e.position != null) {
				if (e.position.dst(p.position) < 150) {
					if (e instanceof Civilian || e instanceof Police) {
						civilians++;
					}
				}else if(e.position.dst(p.position) > 350){
					toRemove.add(e);
				}
			}
		}
		entityList.removeAll(toRemove);
		Random random = new Random();
		while(civilians < 50){
			if(random.nextInt(3) == 1){
				Police popo = new Police(findSpawnPositionNear(p.position));
				entityList.add(popo);
			}else{
				Civilian c = new Civilian(findSpawnPositionNear(p.position));
				entityList.add(c);
			}
			civilians++;
		}

		updateEntity(p);

		world.step(1/60f, 6, 2);
	}

	public Vector2 findSpawnPositionNear(Vector2 spawnPos){
		List<Vector2> nearestAvailableTiles = new ArrayList<Vector2>();
		HashMap<Vector2, Boolean> filledTiles = (HashMap<Vector2, Boolean>)city.filledTiles.clone();
		for(Vector2 v : filledTiles.keySet()){
			if(v != null && filledTiles.containsKey(v)) {
				if (filledTiles.get(v)) {
					if (v.dst(spawnPos) < 150 && v.dst(spawnPos) > 50) {
						nearestAvailableTiles.add(v);
					}
				}
			}
		}
		Random rand = new Random();
		if(nearestAvailableTiles.size() > 0) {
			int element = rand.nextInt(nearestAvailableTiles.size());
			return nearestAvailableTiles.get(element).cpy().add(rand.nextInt(10), rand.nextInt(10));
		}else{
			return new Vector2(20, 20);
		}
	}

	public Player getPlayer(){
		for(Entity e : entityList){
			if(e.ID == 0){
				return (Player)e;
			}
		}
		Random random = new Random();
		Player player = new Player(findSpawnPositionNear(new Vector2(random.nextInt(1280), random.nextInt(720))));
		player.ID = 0;
		entityList.add(player);
		return player;
	}

	public void turnToZombie(int entityID){
		Entity toRemove = getEntityInstance(entityID);
		Zombie zombie = new Zombie(toRemove.position);
		entityList.add(zombie);
		if(toRemove instanceof Civilian) {
			world.destroyBody(((Civilian) toRemove).body);
		}else if(toRemove instanceof Police){
			world.destroyBody(((Police) toRemove).body);
		}
		entityList.remove(toRemove);
		Player p = getPlayer();
		p.humanFlesh++;
		p.zombies++;
		p.totalZombies++;
		p.score = p.score.add(new BigInteger(((Integer)(int)(p.timePlayedMultiplier * 20 * ((p.humanFlesh / 10f) + 1) * ((p.zombies / 10) + 1))).toString()));
		p.timePlayedMultiplier += 0.1f;
		updateEntity(p);
	}

	public void updateEntity(Entity entity){
		Entity toRemove = null;
		for(Entity e : entityList){
			if(e.ID == entity.ID){
				toRemove = e;
				break;
			}
		}
		entityList.remove(toRemove);
		entityList.add(entity);
	}

	public int getNewBulletID(){
		int ID = bullets.size();

		boolean identical = true;
		while(identical){
			identical = false;
			for(Bullet b : bullets){
				if(b.ID == ID){
					identical = true;
				}
			}
			if(identical){
				ID++;
			}
		}

		return ID;
	}

	public int getNewEntityID(){
		int ID = entityList.size();

		boolean identical = true;
		while(identical){
			identical = false;
			for(Entity e : entityList){
				if(e.ID == ID){
					identical = true;
				}
			}
			if(identical){
				ID++;
			}
		}

		return ID;
	}

	public Entity getEntityInstance(int ID){
		Entity e = null;
		for(Entity er : entityList){
			if(er.ID == ID){
				e = er;
			}
		}
		return e;
	}

}
