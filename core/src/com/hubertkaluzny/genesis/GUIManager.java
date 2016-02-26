package com.hubertkaluzny.genesis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.hubertkaluzny.genesis.entities.Civilian;
import com.hubertkaluzny.genesis.entities.Entity;
import com.hubertkaluzny.genesis.entities.Police;
import com.hubertkaluzny.genesis.entities.Zombie;

import java.util.HashMap;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 13/12/2015.
 */
public class GUIManager {

	public HashMap<Vector2, HashMap<Boolean, Float>> targetClickFades = new HashMap<Vector2, HashMap<Boolean, Float>>();

	Texture move_to = new Texture(Gdx.files.internal("GUI/move_to_target.png")), attack_target = new Texture(Gdx.files.internal("GUI/attack_target.png"));
	Texture emptyBar = new Texture(Gdx.files.internal("GUI/healthbar_empty.png")), fullBar = new Texture(Gdx.files.internal("GUI/healthbar_full.png")), zombieBar = new Texture(Gdx.files.internal("GUI/zombiebar_full.png"));
	Texture shieldBar = new Texture(Gdx.files.internal("GUI/shieldbar_full.png"));

	public void render(SpriteBatch batch){
		batch.begin();

		for(Entity e : StateManager.getInstance().getGM().entityList){
			if(e.ID != 0){
				if(e.position != null) {
					Vector2 pos = e.position.cpy();
					if (e instanceof Civilian || e instanceof Police) {
						batch.draw(emptyBar, pos.x - 2.5f, pos.y, 5f, 0.4f);
						TextureRegion reg = new TextureRegion(fullBar, 0, 0, (int) (fullBar.getWidth() * (e.health / (e.maxHealth + e.shield))), fullBar.getHeight());
						batch.draw(reg, pos.x - 2.5f, pos.y, 5f * (e.health / (e.maxHealth + e.shield)), 0.4f);
						if(e.shield > 0) {
							batch.draw(shieldBar, (pos.x - 2.5f) + (5f * (e.health / (e.maxHealth + e.shield))), pos.y, 5f * (e.shield / (e.maxHealth + e.shield)), 0.4f);
						}
					} else if (e instanceof Zombie) {
						batch.draw(emptyBar, pos.x - 2.5f, pos.y, 5f, 0.4f);
						TextureRegion reg = new TextureRegion(zombieBar, 0, 0, (int) (zombieBar.getWidth() * (e.health / (e.maxHealth + e.shield))), zombieBar.getHeight());
						batch.draw(reg, pos.x - 2.5f, pos.y, 5f * (e.health / (e.maxHealth + e.shield)), 0.4f);
						if(e.shield > 0) {
							batch.draw(shieldBar, (pos.x - 2.5f) + (5f * (e.health / (e.maxHealth + e.shield))), pos.y, 5f * (e.shield / (e.maxHealth + e.shield)), 0.4f);
						}
					}
				}
			}
		}

		for(Vector2 v : targetClickFades.keySet()) {
			HashMap<Boolean, Float> fades = targetClickFades.get(v);
			if (fades != null) {
				if (fades.containsKey(true)) {
					float newTime = fades.get(true) - Gdx.graphics.getDeltaTime();
					if (newTime <= 0) {
						fades.remove(true);
					} else {
						Sprite s = new Sprite(attack_target);
						s.setScale(0.1f);
						s.setAlpha(newTime / 0.75f);
						s.setPosition(v.x - (s.getWidth() / 2), v.y - (s.getHeight() / 2));
						s.draw(batch);
						fades.put(true, newTime);
					}
				} else if (fades.containsKey(false)) {
					float newTime = fades.get(false) - Gdx.graphics.getDeltaTime();
					if (newTime <= 0) {
						fades.remove(false);
					} else {
						Sprite s = new Sprite(move_to);
						s.setScale(0.1f);
						s.setAlpha(newTime / 0.75f);
						s.setPosition(v.x - (s.getWidth() / 2), v.y - (s.getHeight() / 2));
						s.draw(batch);
						fades.put(false, newTime);
					}
				}
			}
		}

		batch.end();

	}
}
