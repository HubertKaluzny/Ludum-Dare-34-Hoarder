package com.hubertkaluzny.genesis.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.hubertkaluzny.genesis.SoundEffect;
import com.hubertkaluzny.genesis.StateManager;

import java.util.Random;

public class Bullet {

    public Body body;
    public Texture texture;
    public Vector2 position, origin;
    public float direction;
    public int ID;

    public Bullet(Vector2 spawnPos, float direction){
        Random random = new Random();
        SoundEffect.SHOOT.sound.play(1f, random.nextFloat() + 0.5f, 0.5f);
        texture = new Texture(Gdx.files.internal("bullet.png"));
        ID = StateManager.getInstance().gameManager.getNewBulletID();
        this.position = spawnPos;
        this.origin = spawnPos;
        this.direction = direction;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(position.x, position.y));
        body = StateManager.getInstance().getGM().world.createBody(bodyDef);
        PolygonShape bodyBox = new PolygonShape();
        bodyBox.setAsBox(3 * 0.2f, 3 * 0.2f);
        body.createFixture(bodyBox, 0.1f);
        body.getFixtureList().get(0).setUserData(-1 * ID);
        body.setActive(true);
        body.setSleepingAllowed(false);
        body.setFixedRotation(true);
        body.setTransform(body.getPosition().x, body.getPosition().y, direction);
        body.applyLinearImpulse(3f * (float)Math.cos(Math.toRadians(direction + 90)), 3f * (float)Math.sin(Math.toRadians(direction + 90)), body.getWorldCenter().x, body.getWorldCenter().y, true);

    }

    public void render(SpriteBatch batch){
        position = body.getPosition();
        batch.begin();
        batch.draw(texture, position.x, position.y, 0.6f, 0.6f);
        batch.end();
    }
}
