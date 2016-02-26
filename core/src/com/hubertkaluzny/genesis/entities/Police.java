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

import javax.xml.soap.Text;
import java.util.Random;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 12/12/2015.
 */
public class Police extends Entity {

	public boolean isSWAT = false;

	PoliceType pt = PoliceType.getRandom();

	public Police(Vector2 spawnPos){
		position = spawnPos;
		Random random = new Random();
		ID = StateManager.getInstance().getGM().getNewEntityID();
		if(random.nextInt(20) == 3){
			pt = PoliceType.SWAT;
			fireRate = 1f;
		}else{
			fireRate = 0.8f;
		}


		sprite = new Sprite(pt.texture);
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
		if(pt.equals(PoliceType.SWAT)){
			health = 1200;
			maxHealth = 1200;
		}else{
			health = 800;
			maxHealth = 800;
		}

	}

	public Body body;
	float fireCooldown, fireRate;

	public void render(SpriteBatch batch){
		batch.begin();
		if(fireCooldown > 0){
			fireCooldown -= Gdx.graphics.getDeltaTime();
		}
		Player p = StateManager.getInstance().getGM().getPlayer();
		if(position.dst(p.position) < 50){
			Vector2 target = p.body.getWorldCenter();
			float rotation = (float) Math.toDegrees(Math.atan2((target.x - body.getPosition().x), body.getPosition().y - target.y)) + 180;
			if (rotation < 0) {
				rotation = 360 + rotation;
			}
			if(fireCooldown <= 0){
				fireCooldown = (1 / fireRate);
				Bullet b = new Bullet(position.add(4f * (float)Math.cos(rotation), 4f * (float)Math.sin(rotation)), rotation);
				StateManager.getInstance().gameManager.bullets.add(b);
			}
			sprite.setRotation(rotation);
			body.setTransform(body.getPosition(), (float) Math.toRadians(rotation));
		}
		body.setLinearVelocity(0, 0);
		position = body.getPosition();
		sprite.setPosition(position.x - (sprite.getWidth() / 2), position.y - (sprite.getHeight() / 2));
		sprite.draw(batch);
		batch.end();
	}


}

enum PoliceType{
    A(new Texture(Gdx.files.internal("Police/1.png")), 0),
	B(new Texture(Gdx.files.internal("Police/2.png")), 1),
	C(new Texture(Gdx.files.internal("Police/3.png")), 2),
	D(new Texture(Gdx.files.internal("Police/4.png")), 3),
	E(new Texture(Gdx.files.internal("Police/5.png")), 4),
	SWAT(new Texture(Gdx.files.internal("Police/1.png")), 5);

	public Texture texture;
	public int ID;

	PoliceType(Texture texture, int ID){
		this.texture = texture;
		this.ID = ID;
	}

	public static PoliceType getRandom(){
		Random random = new Random();
		return  getByID(random.nextInt(5));
	}

	public static PoliceType getByID(int ID){
		for(PoliceType type : PoliceType.values()){
			if(type.ID == ID){
				return type;
			}
		}
		return A;
	}
}