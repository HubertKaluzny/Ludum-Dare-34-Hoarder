package com.hubertkaluzny.genesis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 13/12/2015.
 */

public enum RoadType{
	horizontal_A(new Texture(Gdx.files.internal("Road Tiles/horizontal_1.png"))),
	vertical_A(new Texture(Gdx.files.internal("Road Tiles/vertical_1.png"))),
	intersection_A(new Texture(Gdx.files.internal("Road Tiles/intersection_1.png"))),
	intersection_B(new Texture(Gdx.files.internal("Road Tiles/intersection_2.png")));

	public Texture texture;

	RoadType(Texture texture){
		this.texture = texture;
	}
}
