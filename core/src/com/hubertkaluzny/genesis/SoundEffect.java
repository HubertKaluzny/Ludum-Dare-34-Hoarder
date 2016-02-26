package com.hubertkaluzny.genesis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 19/12/2015.
 */
public enum SoundEffect {

	HURT(Gdx.audio.newSound(Gdx.files.internal("Sounds/Hurt.wav"))),
	ABILITY(Gdx.audio.newSound(Gdx.files.internal("Sounds/Ability.wav"))),
	SHOOT(Gdx.audio.newSound(Gdx.files.internal("Sounds/Shoot.wav"))),
	SELECT(Gdx.audio.newSound(Gdx.files.internal("Sounds/Select.wav"))),
	EXPLOSION(Gdx.audio.newSound(Gdx.files.internal("Sounds/Explosion.wav")));

	public Sound sound;

	SoundEffect(Sound sound){
		this.sound = sound;
	}
}
