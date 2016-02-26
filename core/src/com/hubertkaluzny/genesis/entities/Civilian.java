package com.hubertkaluzny.genesis.entities;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.hubertkaluzny.genesis.City;
import com.hubertkaluzny.genesis.GameManager;
import com.hubertkaluzny.genesis.RoadType;
import com.hubertkaluzny.genesis.StateManager;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 12/12/2015.
 */
public class Civilian extends Entity {

	public CivilianType type = CivilianType.getRandom();

	public Civilian(Vector2 pos){
		this.position = pos;
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

	public Vector2 target = null;

	public void render(SpriteBatch batch){
		if(target == null){
			findNewTarget();
		}
		batch.begin();
		float rotation = (float) Math.toDegrees(Math.atan2((target.x - body.getPosition().x), body.getPosition().y - target.y)) + 180;
		if (rotation < 0) {
			rotation = 360 + rotation;
		}
		sprite.setRotation(rotation);
		body.setTransform(body.getPosition(), (float) Math.toRadians(rotation));
		if(target.dst(body.getPosition()) > 10f) {
			Vector2 vel = (new Vector2(target.x, target.y)).sub(body.getPosition());
			float length = (float) Math.sqrt((vel.x * vel.x) + (vel.y * vel.y));
			vel.x /= length;
			vel.y /= length;
			vel.mulAdd(vel, 15);
			body.setLinearVelocity(vel);
		}else{
			body.setLinearVelocity(0, 0);
			findNewTarget();
		}
		position = body.getPosition();
		sprite.setPosition(position.x - (sprite.getWidth() / 2), position.y - (sprite.getHeight() / 2));
		sprite.draw(batch);
		batch.end();
	}

	public void findNewTarget(){
		Player p = StateManager.getInstance().getGM().getPlayer();
		if(position.dst(p.position) < 150){
			List<Vector2> intersections = new ArrayList<Vector2>();
			City city = StateManager.getInstance().getGM().city;
			HashMap<Vector2, RoadType> roads = (HashMap<Vector2, RoadType>)city.roads.clone();
			for(Vector2 v : roads.keySet()){
				if(roads.containsKey(v)){
					if(roads.get(v).equals(RoadType.intersection_A) || roads.get(v).equals(RoadType.intersection_B)){
						intersections.add(v);
					}
				}
			}

			List<Vector2> closestIntersecions = new ArrayList<Vector2>();
			for(Vector2 inter : intersections){
				if(inter.dst(position) < 150){
					closestIntersecions.add(inter);
				}
			}
			Random rand = new Random();
			if(closestIntersecions.size() > 0) {
				Vector2 newVec = closestIntersecions.get(rand.nextInt(closestIntersecions.size())).cpy();
				target = newVec.add(15, 15);
			}else{
				target = new Vector2(500, 500);
			}
		}else{
			target = position;
		}
	}

}

enum CivilianType{

	A(new Texture(Gdx.files.internal("Civilians/1.png")), 0),
	B(new Texture(Gdx.files.internal("Civilians/2.png")), 1),
	C(new Texture(Gdx.files.internal("Civilians/3.png")), 2),
	D(new Texture(Gdx.files.internal("Civilians/4.png")), 3),
	E(new Texture(Gdx.files.internal("Civilians/5.png")), 4),
	F(new Texture(Gdx.files.internal("Civilians/6.png")), 5),
	G(new Texture(Gdx.files.internal("Civilians/7.png")), 6),
	H(new Texture(Gdx.files.internal("Civilians/8.png")), 7),
	I(new Texture(Gdx.files.internal("Civilians/9.png")), 8),
	J(new Texture(Gdx.files.internal("Civilians/10.png")), 9),
	K(new Texture(Gdx.files.internal("Civilians/11.png")), 10),
	L(new Texture(Gdx.files.internal("Civilians/12.png")), 11),
	M(new Texture(Gdx.files.internal("Civilians/13.png")), 12),
	N(new Texture(Gdx.files.internal("Civilians/14.png")), 13),
	O(new Texture(Gdx.files.internal("Civilians/15.png")), 14),
	P(new Texture(Gdx.files.internal("Civilians/16.png")), 15),
	Q(new Texture(Gdx.files.internal("Civilians/17.png")), 16),
	R(new Texture(Gdx.files.internal("Civilians/18.png")), 17),
	S(new Texture(Gdx.files.internal("Civilians/19.png")), 18),
	T(new Texture(Gdx.files.internal("Civilians/20.png")), 19);

	public Texture texture;
	public int ID;

	CivilianType(Texture texture, int ID){
		this.texture = texture;
		this.ID = ID;
	}

	public static CivilianType getByID(int ID){
		for(CivilianType ct : CivilianType.values()){
			if(ct.ID == ID){
				return ct;
			}
		}
		return A;
	}

	public static CivilianType getRandom(){
		Random random = new Random();
		int ID = random.nextInt(20);
		return getByID(ID);
	}

}
