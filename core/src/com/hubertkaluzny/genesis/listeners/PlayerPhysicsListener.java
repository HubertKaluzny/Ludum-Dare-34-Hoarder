package com.hubertkaluzny.genesis.listeners;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.hubertkaluzny.genesis.GameManager;
import com.hubertkaluzny.genesis.SoundEffect;
import com.hubertkaluzny.genesis.StateManager;
import com.hubertkaluzny.genesis.entities.Bullet;
import com.hubertkaluzny.genesis.entities.Civilian;
import com.hubertkaluzny.genesis.entities.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 13/12/2015.
 */
public class PlayerPhysicsListener implements ContactListener {

	@Override
	public void endContact(Contact contact){

	}

	@Override
	public void beginContact(Contact contact) {
		if (contact.getFixtureA().getUserData() != null) {
			int a = (Integer) contact.getFixtureA().getUserData();
			if (a == 0) {
				StateManager.getInstance().getGM().getPlayer().setTarget(StateManager.getInstance().getGM().getPlayer().position);
			}
		}
		if (contact.getFixtureB().getUserData() != null) {
			int b = (Integer) contact.getFixtureB().getUserData();
			if (b == 0) {
				StateManager.getInstance().getGM().getPlayer().setTarget(StateManager.getInstance().getGM().getPlayer().position);
			}
		}
		List<Entity> updateEntities = new ArrayList<Entity>();
		boolean aIsBullet = false;
		boolean bIsBullet = false;
		if(contact.getFixtureA().getUserData() != null){
			if(-1 >= (Integer)contact.getFixtureA().getUserData()){
				aIsBullet = true;
			}
		}
		if(contact.getFixtureB().getUserData() != null){
			if(-1 >= (Integer)contact.getFixtureB().getUserData()){
				bIsBullet = true;
			}
		}
		for(Entity e : StateManager.getInstance().getGM().entityList) {
				if (contact.getFixtureA().getUserData() != null) {
					if (e.ID == (Integer) contact.getFixtureA().getUserData()) {
						if (e instanceof Civilian) {
							((Civilian) e).findNewTarget();
						}
						if (bIsBullet) {
							e.dealDamage(30f);
						}
						updateEntities.add(e);
					}
			}
		}
		for(Entity e : updateEntities){
			StateManager.getInstance().getGM().updateEntity(e);
		}
		updateEntities.clear();
		for(Entity e : StateManager.getInstance().getGM().entityList) {
				if (contact.getFixtureA().getUserData() != null) {
					if (contact.getFixtureB().getUserData() != null) {
						if (e.ID == (Integer) contact.getFixtureB().getUserData()) {
							if (e instanceof Civilian) {
								((Civilian) e).findNewTarget();
							}
							if (aIsBullet) {
								e.dealDamage(30f);
							}
							updateEntities.add(e);
						}
					}
			}
		}
		for(Entity e : updateEntities){
			StateManager.getInstance().getGM().updateEntity(e);
		}
		updateEntities.clear();
		List<Bullet> toRemove = new ArrayList<Bullet>(0);
		for(Bullet b : StateManager.getInstance().gameManager.bullets){
			if(aIsBullet){
				if(b.ID * -1 == (Integer)contact.getFixtureA().getUserData()){
					SoundEffect.EXPLOSION.sound.play(1f, new Random().nextFloat() + 0.5f, 0.5f);
					toRemove.add(b);
				}
			}
			if(bIsBullet){
				if(b.ID * -1 == (Integer)contact.getFixtureB().getUserData()){
					SoundEffect.EXPLOSION.sound.play(1f, new Random().nextFloat() + 0.5f, 0.5f);
					toRemove.add(b);
				}
			}
		}
		StateManager.getInstance().gameManager.bulletsToRemove.addAll(toRemove);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}
}
