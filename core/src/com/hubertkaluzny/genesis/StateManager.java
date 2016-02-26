package com.hubertkaluzny.genesis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;


import javax.xml.soap.Text;
import java.math.BigInteger;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 14/12/2015.
 */
public class StateManager {
	private static StateManager ourInstance = new StateManager();

	public static StateManager getInstance() {
		return ourInstance;
	}

	private StateManager() {
		startScreen = new Texture(Gdx.files.internal("GUI/mainmenu.png"));
		helpScreen = new Texture(Gdx.files.internal("GUI/helpscreen.png"));
		gameOverScreen = new Texture(Gdx.files.internal("GUI/gameover.png"));
		pauseScreen = new Texture(Gdx.files.internal("GUI/pausescreen.png"));
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Square.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter p = new FreeTypeFontGenerator.FreeTypeFontParameter();
		p.size = 35;
		p.color = Color.WHITE;
		bp = generator.generateFont(p);
		defCam = new OrthographicCamera(1280, 720);
		defCam.translate(640, 360);
		defCam.update();
	}

	OrthographicCamera defCam;
	public GameManager gameManager;
	public GameState gameState = GameState.START_SCREEN;

	public Texture startScreen, helpScreen, gameOverScreen, pauseScreen;

	public Rectangle s_play = new Rectangle(425, 430, 425, 100),
			s_help = new Rectangle(425, 310, 425, 100),
			s_quit = new Rectangle(425, 190, 425, 100),
			h_back = new Rectangle(0, 660, 190, 70),
			g_retry = new Rectangle(505, 210, 270, 95),
			g_quit = new Rectangle(505, 70, 270, 95),
			p_back = new Rectangle(475, 210, 330, 95),
			p_quit = new Rectangle(475, 70, 330, 95);

	BitmapFont bp;

	public void render(SpriteBatch batch){
		if(gameState.equals(GameState.START_SCREEN)){
			batch.begin();
			batch.draw(startScreen, 0, 0);
			batch.end();
		}else if(gameState.equals(GameState.HELP_SCREEN)){
			batch.begin();
			batch.draw(helpScreen, 0, 0);
			batch.end();
		}else if(gameState.equals(GameState.IN_PROGRESS)){
			gameManager.cam.zoom = 0.07f;
			gameManager.cam.position.set(StateManager.getInstance().getGM().getPlayer().position, 0);
			gameManager.cam.update();
			batch.setProjectionMatrix(gameManager.cam.combined);
			gameManager.render(batch);
		}else if(gameState.equals(GameState.GAME_OVER_SCREEN)){
			batch.setProjectionMatrix(defCam.combined);
			batch.begin();
			batch.draw(gameOverScreen, 0, 0);
			BigInteger score = gameManager.getPlayer().score;
			bp.getData().setScale(2f);
			GlyphLayout layout = new GlyphLayout(bp, score.toString());
			bp.draw(batch, score.toString(), 640 - (layout.width / 2), 400);
			batch.end();
		}else if(gameState.equals(GameState.PAUSE_SCREEN)){
			batch.setProjectionMatrix(defCam.combined);
			batch.begin();
			batch.draw(pauseScreen, 0, 0);
			batch.end();
		}
	}

	public GameManager getGM(){
		return gameManager;
	}

	public void newGame(){
		goToState(GameState.IN_PROGRESS);
		gameManager = new GameManager();

		gameManager.cam = new OrthographicCamera(1280, 720);
		gameManager.cam.update();
		gameManager.init();
	}

	public void goToState(GameState state){
		gameState = state;
	}

	public void quit(){
		Gdx.app.exit();
	}
}
