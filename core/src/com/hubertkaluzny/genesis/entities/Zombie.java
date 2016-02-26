package com.hubertkaluzny.genesis.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.hubertkaluzny.genesis.GameManager;
import com.hubertkaluzny.genesis.StateManager;

import java.util.Random;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 12/12/2015.
 */
public class Zombie extends Entity{

	public int attackDamage;
	public ZombieType type = ZombieType.getRandom();

	public Zombie(Vector2 spawnPos){
		this.position = spawnPos;
		this.maxHealth = 50;
		this.health = 50;
		ID = StateManager.getInstance().getGM().getNewEntityID();
		sprite = new Sprite(type.texture);
		sprite.setScale(0.2f);
		sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(new Vector2(position.x, position.y));
		body = StateManager.getInstance().getGM().world.createBody(bodyDef);
		PolygonShape bodyBox = new PolygonShape();
		bodyBox.setAsBox(10 * 0.2f, 7 * 0.2f);
		body.createFixture(bodyBox, 0.1f);
		body.getFixtureList().get(0).setUserData(ID);
		body.setActive(true);
		body.setSleepingAllowed(false);
		body.setFixedRotation(true);
	}

	public Body body;

	public void render(SpriteBatch batch){
		batch.begin();
		Player p = StateManager.getInstance().getGM().getPlayer();
		Vector2 target = p.body.getWorldCenter();
		float rotation = (float) Math.toDegrees(Math.atan2((target.x - body.getPosition().x), body.getPosition().y - target.y)) + 180;
		if (rotation < 0) {
			rotation = 360 + rotation;
		}
		sprite.setRotation(rotation);
		body.setTransform(body.getPosition(), (float) Math.toRadians(rotation));
		position = body.getPosition();
		if(target.dst(body.getPosition()) > (10f + (p.zombies / 3.5f))){
			Vector2 vel = (new Vector2(target.x, target.y)).sub(body.getPosition());
			float length = (float) Math.sqrt((vel.x * vel.x) + (vel.y * vel.y));
			vel.x /= length;
			vel.y /= length;
			vel.mulAdd(vel, 15);
			body.setLinearVelocity(vel);
		}else if(target.dst(body.getPosition()) < 8f){
			Vector2 vel = (new Vector2(target.x, target.y)).sub(body.getPosition());
			float length = (float) Math.sqrt((vel.x * vel.x) + (vel.y * vel.y));
			vel.x /= length;
			vel.y /= length;
			vel.mulAdd(vel, -17);
			body.setLinearVelocity(vel);
		} else{
			body.setLinearVelocity(0, 0);
		}
		sprite.setPosition(position.x - (sprite.getWidth() / 2), position.y - (sprite.getHeight() / 2));
		sprite.draw(batch);
		batch.end();
	}
}

enum ZombieType {

	A(new Texture(Gdx.files.internal("Zombies/1.png")), 0),
	B(new Texture(Gdx.files.internal("Zombies/2.png")), 1),
	C(new Texture(Gdx.files.internal("Zombies/3.png")), 2),
	D(new Texture(Gdx.files.internal("Zombies/4.png")), 3),
	E(new Texture(Gdx.files.internal("Zombies/5.png")), 4),
	F(new Texture(Gdx.files.internal("Zombies/6.png")), 5),
	G(new Texture(Gdx.files.internal("Zombies/7.png")), 6),
	H(new Texture(Gdx.files.internal("Zombies/8.png")), 7),
	I(new Texture(Gdx.files.internal("Zombies/9.png")), 8),
	J(new Texture(Gdx.files.internal("Zombies/10.png")), 9);

	public Texture texture;
	public int ID;

	ZombieType(Texture texture, int ID) {
		this.texture = texture;
		this.ID = ID;
	}

	public static ZombieType getByID(int ID) {
		for(ZombieType zt : ZombieType.values()){
			if(zt.ID == ID){
				return zt;
			}
		}
		return A;
	}

	public static ZombieType getRandom() {
		Random random = new Random();
		int ID = random.nextInt(10);
		return getByID(ID);
	}
}
