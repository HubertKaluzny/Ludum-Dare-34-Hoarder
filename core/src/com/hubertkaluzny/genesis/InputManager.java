package com.hubertkaluzny.genesis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.hubertkaluzny.genesis.entities.Player;

import javax.swing.plaf.nimbus.State;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 12/12/2015.
 */
public class InputManager implements InputProcessor {

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(StateManager.getInstance().gameState.equals(GameState.IN_PROGRESS)) {
			if (button == 0) {
				Vector2 actVect = new Vector2(screenX, screenY);
				actVect.y = 720 - actVect.y;
				actVect.x *= 0.075f;
				actVect.y *= 0.075f;
				actVect.y += StateManager.getInstance().getGM().cam.position.y;
				actVect.x += StateManager.getInstance().getGM().cam.position.x;
				actVect.y -= (720f * 0.075f) / 2f;
				actVect.x -= (1280f * 0.075f) / 2f;
				StateManager.getInstance().getGM().getPlayer().setTarget(actVect);
			}
		}else if(StateManager.getInstance().gameState.equals(GameState.START_SCREEN)){
			if(button == 0){
				Vector2 posVect = new Vector2(screenX, 720 - screenY);
				if(StateManager.getInstance().s_play.contains(posVect)){
					StateManager.getInstance().newGame();
					SoundEffect.SELECT.sound.play(1f);
				}else if(StateManager.getInstance().s_help.contains(posVect)){
					StateManager.getInstance().goToState(GameState.HELP_SCREEN);
					SoundEffect.SELECT.sound.play(1f);
				}else if(StateManager.getInstance().s_quit.contains(posVect)){
					SoundEffect.SELECT.sound.play(1f);
					StateManager.getInstance().quit();
				}
			}
		}else if(StateManager.getInstance().gameState.equals(GameState.HELP_SCREEN)){
			if(button == 0){
				Vector2 posVect = new Vector2(screenX, 720 - screenY);
				if(StateManager.getInstance().h_back.contains(posVect)){
					StateManager.getInstance().goToState(GameState.START_SCREEN);
					SoundEffect.SELECT.sound.play(1f);
				}
			}
		}else if(StateManager.getInstance().gameState.equals(GameState.GAME_OVER_SCREEN)){
			if(button == 0){
				Vector2 posVect = new Vector2(screenX, 720 - screenY);
				if(StateManager.getInstance().g_retry.contains(posVect)){
					StateManager.getInstance().newGame();
					SoundEffect.SELECT.sound.play(1f);
				}else if(StateManager.getInstance().g_quit.contains(posVect)){
					SoundEffect.SELECT.sound.play(1f);
					StateManager.getInstance().quit();
				}
			}
		}else if(StateManager.getInstance().gameState.equals(GameState.PAUSE_SCREEN)){
			if(button == 0){
				Vector2 posVect = new Vector2(screenX, 720 - screenY);
				if(StateManager.getInstance().p_back.contains(posVect)){
					StateManager.getInstance().goToState(GameState.IN_PROGRESS);
					SoundEffect.SELECT.sound.play(1f);
				}else if(StateManager.getInstance().p_quit.contains(posVect)){
					SoundEffect.SELECT.sound.play(1f);
					StateManager.getInstance().quit();
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(StateManager.getInstance().gameState.equals(GameState.IN_PROGRESS)) {
			if (keycode == Input.Keys.Q) {
				StateManager.getInstance().getGM().getPlayer().executeAbility('Q');
			} else if (keycode == Input.Keys.W) {
				StateManager.getInstance().getGM().getPlayer().executeAbility('W');
			} else if (keycode == Input.Keys.E) {
				StateManager.getInstance().getGM().getPlayer().executeAbility('E');
			} else if (keycode == Input.Keys.R) {
				StateManager.getInstance().getGM().getPlayer().executeAbility('R');
			} else if (keycode == Input.Keys.ESCAPE){
				SoundEffect.SELECT.sound.play(1f);
				StateManager.getInstance().goToState(GameState.PAUSE_SCREEN);
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
