package com.hubertkaluzny.genesis.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.hubertkaluzny.genesis.GameManager;
import com.hubertkaluzny.genesis.SoundEffect;

import java.util.Random;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 12/12/2015.
 */
public class Entity {

	public float maxHealth = 500;
	public float health = 500;
	public float regenPerSec = 5f;
	public float shield = 0;
	public Sprite sprite;
	public Vector2 position;
	public int ID;

	public void update(){
		health += regenPerSec * Gdx.graphics.getDeltaTime();
		health = MathUtils.clamp(health, -maxHealth, maxHealth);
		shield -= 10 * Gdx.graphics.getDeltaTime();
		shield = MathUtils.clamp(shield, 0, 100);
	}

	public void dealDamage(float damage){
		if(shield <= 0) {
			health -= damage;
		}else{
			shield -= damage;
		}
		Random random = new Random();
		SoundEffect.HURT.sound.play(1f, random.nextFloat() + 0.5f, 0.5f);
	}
}
