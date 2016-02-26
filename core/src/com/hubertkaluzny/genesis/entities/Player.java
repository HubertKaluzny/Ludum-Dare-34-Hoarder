package com.hubertkaluzny.genesis.entities;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import com.hubertkaluzny.genesis.GameManager;
import com.hubertkaluzny.genesis.GameState;
import com.hubertkaluzny.genesis.SoundEffect;
import com.hubertkaluzny.genesis.StateManager;

import java.math.BigInteger;
import java.util.HashMap;


/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 12/12/2015.
 */
public class Player extends Entity {
	BitmapFont bp;

	public BigInteger score = new BigInteger("0");
	public float timePlayedMultiplier = 1;
	public int zombies = 0, totalZombies;
	float extraDamage;

	public Player(Vector2 position){
		this.position = position;
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
		p.size = 35;
		p.color = Color.WHITE;
		bp = generator.generateFont(p);
		sprite = new Sprite(new Texture(Gdx.files.internal("MainZombie.png")));
		sprite.setScale(0.3f);
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);


		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(new Vector2(position.x, position.y));
		body = StateManager.getInstance().getGM().world.createBody(bodyDef);
		PolygonShape bodyBox = new PolygonShape();
		bodyBox.setAsBox(10 * 0.3f, 7 * 0.3f);
		body.createFixture(bodyBox, 0.1f);
		body.getFixtureList().get(0).setUserData(0);
		body.setActive(true);
		body.setSleepingAllowed(false);
		body.setFixedRotation(true);

		Q_active = new Texture(Gdx.files.internal("Abilities/get_turnd_active.png"));
		Q_inactive = new Texture(Gdx.files.internal("Abilities/get_turnd_inactive.png"));

		W_active = new Texture(Gdx.files.internal("Abilities/thick_skin_active.png"));
		W_inactive = new Texture(Gdx.files.internal("Abilities/thick_skin_inactive.png"));

		E_active = new Texture(Gdx.files.internal("Abilities/feeding_time_active.png"));
		E_inactive = new Texture(Gdx.files.internal("Abilities/feeding_time_inactive.png"));

		R_active = new Texture(Gdx.files.internal("Abilities/sns_active.png"));
		R_inactive = new Texture(Gdx.files.internal("Abilities/sns_inactive.png"));

		playerBarFull = new Texture(Gdx.files.internal("GUI/playerbar_full.png"));
		playerBarOverlay = new Texture(Gdx.files.internal("GUI/playerbar_overlay.png"));
		playerBarUnder = new Texture(Gdx.files.internal("GUI/playerbar_empty.png"));
		meatDisplay = new Texture(Gdx.files.internal("GUI/meat_display.png"));
		playerShieldBarFull = new Texture(Gdx.files.internal("GUI/playershieldbar_full.png"));

		cooldownFull = new Texture(Gdx.files.internal("GUI/cooldownbar_full.png"));
		cooldownEmpty = new Texture(Gdx.files.internal("GUI/cooldownbar_empty.png"));
	}

	public Body body;
	Vector2 target = null;
	Entity target_E = null;

	public void render(SpriteBatch batch){
		if(extraDamage > 0){
			extraDamage -= Gdx.graphics.getDeltaTime();
		}
		timePlayedMultiplier += (Gdx.graphics.getDeltaTime() * 0.01f);
		update();
		if(Qcooldown > 0){
			Qcooldown -= Gdx.graphics.getDeltaTime();
		}
		if(Ecooldown > 0){
			Ecooldown -= Gdx.graphics.getDeltaTime();
		}
		if(Wcooldown > 0){
			Wcooldown -= Gdx.graphics.getDeltaTime();
		}
		if(Rcooldown > 0){
			Rcooldown -= Gdx.graphics.getDeltaTime();
		}
		batch.begin();
		int inpX = Gdx.input.getX();
		int inpY = Gdx.input.getY();
		inpX *= 0.075f;
		inpY *= 0.075f;
		inpY += StateManager.getInstance().getGM().cam.position.y;
		inpX += StateManager.getInstance().getGM().cam.position.x;
		inpY -= (720f * 0.075f) / 2f;
		inpX -= (1280f * 0.075f) / 2f;
		float rotation = (float) Math.toDegrees(Math.atan2((inpX - body.getPosition().x), inpY- body.getPosition().y)) + 180;
		if (rotation < 0) {
			rotation = 360 + rotation;
		}
		sprite.setRotation(rotation);
		body.setTransform(body.getPosition(), (float) Math.toRadians(rotation));
		if(target != null) {
			if(target.dst(body.getPosition()) > 2f) {
				Vector2 vel = (new Vector2(target.x, target.y)).sub(body.getPosition());
				float length = (float) Math.sqrt((vel.x * vel.x) + (vel.y * vel.y));
				vel.x /= length;
				vel.y /= length;
				vel.mulAdd(vel, 15);
				body.setLinearVelocity(vel);
			}else{
				if(target_E != null){
					Entity e = StateManager.getInstance().getGM().getEntityInstance(target_E.ID);
					if(e != null) {
						if (position.dst(e.position) < 10f) {
							if(extraDamage > 0){
								e.dealDamage((200 * Gdx.graphics.getDeltaTime()) + extraDamage + (zombies * 5 * Gdx.graphics.getDeltaTime()));
							}else {
								e.dealDamage(200 * Gdx.graphics.getDeltaTime()+ (zombies * 5 * Gdx.graphics.getDeltaTime()));
							}
							StateManager.getInstance().getGM().updateEntity(e);
						}
					}
				}
				body.setLinearVelocity(0, 0);
			}
		}
		body.setAwake(true);
		sprite.setPosition(body.getPosition().x - 10, body.getPosition().y - 7);
		position.set(body.getPosition().x, body.getPosition().y);
		sprite.draw(batch);

		float camX = StateManager.getInstance().getGM().cam.position.x;
		float camY = StateManager.getInstance().getGM().cam.position.y;
		if(Qcooldown > 0) {
			batch.draw(Q_inactive, camX - 10, camY - 21, 5, 5);
			batch.draw(cooldownEmpty, camX - 9.5f, camY - 21, 4f, 0.4f);
			TextureRegion reg = new TextureRegion(cooldownFull, 0, 0, (int)(cooldownFull.getWidth() * (Qcooldown / 5f)), cooldownFull.getHeight());
			batch.draw(reg, camX - 9.5f, camY - 21, 4f * (Qcooldown / 5f), 0.4f);
		}else{
			batch.draw(Q_active, camX - 10, camY - 21, 5, 5);
		}
		if(Wcooldown > 0) {
			batch.draw(W_inactive, camX - 5, camY - 21, 5, 5);

			batch.draw(cooldownEmpty, camX - 4.5f, camY - 21, 4f, 0.4f);
			TextureRegion reg = new TextureRegion(cooldownFull, 0, 0, (int)(cooldownFull.getWidth() * (Wcooldown / 15f)), cooldownFull.getHeight());
			batch.draw(reg, camX - 4.5f, camY - 21, 4f * (Wcooldown / 15f), 0.4f);

		}else{
			batch.draw(W_active, camX - 5, camY - 21, 5, 5);
		}
		if(Ecooldown > 0) {
			batch.draw(E_inactive, camX, camY - 21, 5, 5);

			batch.draw(cooldownEmpty, camX + 0.5f, camY - 21, 4f, 0.4f);
			TextureRegion reg = new TextureRegion(cooldownFull, 0, 0, (int)(cooldownFull.getWidth() * (Ecooldown / 10f)), cooldownFull.getHeight());
			batch.draw(reg, camX + 0.5f, camY - 21, 4f * (Ecooldown / 10f), 0.4f);


		}else{
			batch.draw(E_active, camX, camY - 21, 5, 5);
		}
		if(Rcooldown > 0) {
			batch.draw(R_inactive, camX + 5, camY - 21, 5, 5);

			batch.draw(cooldownEmpty, camX + 5.5f, camY - 21, 4f, 0.4f);
			TextureRegion reg = new TextureRegion(cooldownFull, 0, 0, (int)(cooldownFull.getWidth() * (Rcooldown / 2f)), cooldownFull.getHeight());
			batch.draw(reg, camX + 5.5f, camY - 21, 4f * (Rcooldown / 2f), 0.4f);


		}else{
			batch.draw(R_active, camX + 5, camY - 21, 5, 5);
		}

		batch.draw(playerBarUnder, camX - 15, camY - 25.5f, 30, 4);
		TextureRegion reg = new TextureRegion(playerBarFull, 0, 0, (int)(playerBarFull.getWidth() * (health / (maxHealth + shield))), playerBarFull.getHeight());
		batch.draw(reg, camX - 15, camY - 25.5f, 30 * (health / (maxHealth + shield)), 4);
		if(shield > 0){
			TextureRegion shieldReg = new TextureRegion(playerShieldBarFull, (int)(playerShieldBarFull.getWidth() * (shield / (maxHealth + shield))), 0, playerShieldBarFull.getWidth() - 37, playerShieldBarFull.getHeight());
			batch.draw(shieldReg, (camX - 15) + ((30 * (health / (maxHealth + shield))) - (30f * (shield / (maxHealth + shield)))), camY - 25.5f, 30 * (shield / (maxHealth + shield)), 4);
		}
		batch.draw(playerBarOverlay, camX - 15, camY - 25.5f, 30, 4);
		batch.draw(meatDisplay, camX + 15, camY - 25.5f, 10, 4);
		bp.getData().setScale(0.1f, 0.1f);
		bp.draw(batch, humanFlesh + "", camX + 19f, camY - 22f);
		bp.getData().setScale(0.05f, 0.05f);
		bp.draw(batch, "Score: " + score, camX - 44f, camY + 25f);

		batch.end();
		if(health <= 0){
			StateManager.getInstance().goToState(GameState.GAME_OVER_SCREEN);
		}
	}

	public void setTarget(Vector2 vector){
		target = vector;
		boolean placed = false;
		for(Entity e : StateManager.getInstance().getGM().entityList){
			if(e.ID != ID){
				if(e.sprite != null) {
					if (e.sprite.getBoundingRectangle().contains(vector)) {
						if (!(e instanceof Zombie)) {
							HashMap<Boolean, Float> map = new HashMap<Boolean, Float>();
							map.put(true, 0.75f);
							StateManager.getInstance().getGM().guiManager.targetClickFades.put(vector, map);
							placed = true;
							target_E = e;
						}
					}
				}
			}
		}
		if(!placed){
			HashMap<Boolean, Float> map = new HashMap<Boolean, Float>();
			map.put(false, 0.75f);
			StateManager.getInstance().getGM().guiManager.targetClickFades.put(vector, map);
		}
	}

	/**
	 * Player abilities:
	 * Q - (Get Turn'd)Instantly turn citizen into zombie
	 * W - (Thick Skin)Apply shield to all your zombies
	 * E - (Feeding Time!)Feed your zombies brains, all gain attack
	 * R - (Sorry not sorry!)Destroy a zombie to gain 10% of max health
	 *
	 * Cooldowns:
	 * Q - 5s
	 * W - 15s
	 * E - 10s
	 * R - 2s
	 *
	 * Human flesh is consumed for abilities
	 * Ability costs:
	 * Q - 3hf
	 * W - 2hf
	 * E - 2hf
	 * R - 1hf
	 */

	public void executeAbility(char ability){
		if(ability == 'Q'){
			if(Qcooldown <= 0) {
				if (humanFlesh >= 3) {
					Entity toTurn = null;
					for(Entity e : StateManager.getInstance().getGM().entityList){
						if(e instanceof Civilian || e instanceof Police){
							Vector2 victim = new Vector2(e.sprite.getX(), e.sprite.getY());
							if(victim.dst(position) < 15f){
								toTurn = e;
								break;
							}
						}
					}
					if(toTurn != null){
						humanFlesh -= 3;
						Qcooldown = 5;
						StateManager.getInstance().getGM().turnToZombie(toTurn.ID);
						SoundEffect.ABILITY.sound.play(1.2f);
					}
				}
			}
		}else if(ability == 'W'){
			if(Wcooldown <= 0){
				if(humanFlesh >= 2) {
					Wcooldown = 15;
					humanFlesh -= 2;
					SoundEffect.ABILITY.sound.play(1.2f);
					shield = 100f;
					for(Entity e : StateManager.getInstance().getGM().entityList){
						if(e instanceof Zombie){
							e.shield = 100f;
						}
					}
				}
			}
		}else if(ability == 'E'){
			if(Ecooldown <= 0){
				if(humanFlesh >= 2) {
					Ecooldown = 10;
					humanFlesh -= 2;
					SoundEffect.ABILITY.sound.play(1.2f);
					for(Entity e : StateManager.getInstance().getGM().entityList){
						if(e instanceof Zombie){
							((Zombie) e).attackDamage *= 1.5f;
						}
					}
					extraDamage += 5f;
				}
			}
		}else if(ability == 'R'){
			if(Rcooldown <= 0){
				if(humanFlesh >= 1) {
					Zombie zombie = null;
					for(Entity e : StateManager.getInstance().getGM().entityList){
						if(e instanceof Zombie){
							zombie = (Zombie)e;
							break;
						}
					}
					if(zombie != null) {
						StateManager.getInstance().getGM().world.destroyBody(zombie.body);
						StateManager.getInstance().getGM().entityList.remove(zombie);
						Rcooldown = 2;
						humanFlesh -= 1;
						health += (maxHealth / 10);
						health = MathUtils.clamp(health, 0, maxHealth);
						zombies--;
						SoundEffect.ABILITY.sound.play(1.2f);
					}
				}
			}
		}
	}

	public Vector2 position = new Vector2(720, 360);

	Texture Q_active, W_active, E_active, R_active, Q_inactive, W_inactive, E_inactive, R_inactive;
	Texture playerBarUnder, playerBarOverlay, playerBarFull, playerShieldBarFull;
	Texture meatDisplay;
	Texture cooldownFull, cooldownEmpty;
	float Qcooldown = 0, Wcooldown = 0, Ecooldown = 0, Rcooldown = 0;
	public int humanFlesh = 25;

}
