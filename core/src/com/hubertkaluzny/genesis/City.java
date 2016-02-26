package com.hubertkaluzny.genesis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.hubertkaluzny.genesis.entities.Player;

import java.util.*;

/**
 * Created by Hubert Kaluzny
 * http://hubertkaluzny.com
 * on 12/12/2015.
 */
public class City {

	public HashMap<Vector2, RoadType> roads = new HashMap<Vector2, RoadType>();
	public HashMap<Vector2, BuildingType> buildings = new HashMap<Vector2, BuildingType>();
	public HashMap<Vector2, Boolean> filledTiles = new HashMap<Vector2, Boolean>();
	public HashMap<Vector2, Boolean> canPlace = new HashMap<Vector2, Boolean>();

	public void render(Batch batch){
		batch.begin();
		for(Vector2 v : roads.keySet()){
			if(roads.containsKey(v)) {
				batch.draw(roads.get(v).texture, v.x, v.y);
			}
		}
		for(Vector2 vector : buildings.keySet()){
			if(buildings.containsKey(vector)) {
				batch.draw(buildings.get(vector).texture, vector.x, vector.y);
			}
		}
		batch.end();
	}

	public void generate(){

		//generate roads
		Random random  = new Random();
		for(int y = 0; y <= 720 / 30; y++) {
			if((y % 30) % 3 == 0) {
				y += random.nextInt(2);
				for (int x = 0; x <= 1280 / 30; x++) {
					roads.put(new Vector2(x * 30, y * 30), RoadType.horizontal_A);
					filledTiles.put(new Vector2(x * 30, y * 30), true);
				}
			}
		}
		for(int x = 0; x <= (1280 / 30) + 1; x++){
			if((x % 30) % 3 == 0){
				x += random.nextInt(2);
				for(int y = 0; y <= 720 / 30; y++){
					if(roads.containsKey(new Vector2(x * 30, y * 30))){
						int a = random.nextInt(2);
						if(a == 1){
							roads.put(new Vector2(x * 30, y * 30), RoadType.intersection_A);
							filledTiles.put(new Vector2(x * 30, y * 30), true);
						}else{
							roads.put(new Vector2(x * 30, y * 30), RoadType.intersection_B);

							filledTiles.put(new Vector2(x * 30, y * 30), true);
						}
					}else {
						roads.put(new Vector2(x * 30, y * 30), RoadType.vertical_A);
						filledTiles.put(new Vector2(x * 30, y * 30), true);
					}

				}
			}
		}

		//generate buildings
		for(int x = 0; x <= 1280 / 30; x++){
			for(int y = 0; y <= 720 / 30; y++) {
				//Origin Tile
				Vector2 origin = new Vector2(x * 30, y * 30);
				boolean cantPlaceOrigin = false;
				int badTiles = 0;
				BuildingType b = BuildingType.Building_A;
				do {
					int randomBuildingType = random.nextInt(11);
					b = BuildingType.Building_A;
					if (randomBuildingType == 0) {
						b = BuildingType.Building_A;
					} else if (randomBuildingType == 1) {
						b = BuildingType.Building_B;
					} else if (randomBuildingType == 2) {
						b = BuildingType.Building_C;
					} else if (randomBuildingType == 3) {
						b = BuildingType.Building_D;
					} else if (randomBuildingType == 4) {
						b = BuildingType.Building_E;
					} else if (randomBuildingType == 5) {
						b = BuildingType.PARK_1;
					} else if (randomBuildingType == 6){
						b = BuildingType.PARK_2;
					} else if (randomBuildingType == 7){
						b = BuildingType.Building_F;
					} else if (randomBuildingType == 8){
						b = BuildingType.Building_G;
					} else if (randomBuildingType == 9){
						b = BuildingType.Building_H;
					} else if (randomBuildingType == 10){
						b = BuildingType.SQUARE_1;
					}

					for (int wx = 0; wx < b.widthX; wx++) {
						for (int wy = 0; wy < b.widthY; wy++) {
							Vector2 vector = new Vector2(origin.x + (wx * 30), origin.y + (wy * 30));

							for(Vector2 tile : filledTiles.keySet()){
								if(vector.x == tile.x && vector.y == tile.y) {
									badTiles++;
								}
							}
							if(vector.equals(origin) && (badTiles > 0)){
								cantPlaceOrigin = true;
							}
						}
					}
				}while(badTiles > 0 && !cantPlaceOrigin);

				if(!cantPlaceOrigin) {
					buildings.put(origin, b);
					for(int wx = 0; wx < b.widthX; wx++){
						for(int wy = 0; wy < b.widthY; wy++){
							filledTiles.put(new Vector2(origin.x + (wx * 30), origin.y + (wy * 30)), b.traversable);
						}
					}
				}
			}
		}

		//second pass
		for(int x = 0; x <= 1280 / 30; x++) {
			for (int y = 0; y <= 720 / 30; y++) {
				Vector2 v = new Vector2(x * 30, y * 30);
				boolean filled = false;
				for(Vector2 vec : filledTiles.keySet()){
					if(vec.x == v.x && vec.y == v.y){
						filled = true;
						break;
					}
				}
				if(!filled){
					int buildingInt = random.nextInt(6);
					BuildingType b = BuildingType.Building_A;
					if (buildingInt == 0) {
						b = BuildingType.Building_A;
					} else if (buildingInt== 1) {
						b = BuildingType.Building_B;
					} else if (buildingInt == 2) {
						b = BuildingType.Building_C;
					} else if (buildingInt == 3) {
						b = BuildingType.Building_D;
					} else if (buildingInt == 4) {
						b = BuildingType.Building_E;
					} else if (buildingInt == 5) {
						b = BuildingType.PARK_1;
					}
					buildings.put(v, b);
					filledTiles.put(v, b.traversable);
				}
			}
		}

		//Initialise Bodies
		for(Vector2 tile : filledTiles.keySet()){
			if(!filledTiles.get(tile)){
				BodyDef tileBodyDef = new BodyDef();
				tileBodyDef.position.set(new Vector2(tile.x + 15, tile.y + 15));
				Body tileBody = StateManager.getInstance().getGM().world.createBody(tileBodyDef);
				PolygonShape tileShape = new PolygonShape();
				tileShape.setAsBox(15, 15);
				tileBody.createFixture(tileShape, 0.0f);
			}
		}
		//Borders
		BodyDef leftSide = new BodyDef();
		leftSide.position.set(-15, 360);
		Body leftBody = StateManager.getInstance().getGM().world.createBody(leftSide);
		PolygonShape leftSideShape = new PolygonShape();
		leftSideShape.setAsBox(15, 380);
		leftBody.createFixture(leftSideShape, 0.0f);

		BodyDef rightSide = new BodyDef();
		rightSide.position.set(1320, 360);
		Body rightBody = StateManager.getInstance().getGM().world.createBody(rightSide);
		PolygonShape rightSideShape = new PolygonShape();
		rightSideShape.setAsBox(15, 390);
		rightBody.createFixture(rightSideShape, 0.0f);

		BodyDef topSide = new BodyDef();
		topSide.position.set(660, 770);
		Body topBody = StateManager.getInstance().getGM().world.createBody(topSide);
		PolygonShape topShape = new PolygonShape();
		topShape.setAsBox(660, 15);
		topBody.createFixture(topShape, 0.0f);

		BodyDef bottomSide = new BodyDef();
		bottomSide.position.set(660, -15);
		Body bottomBody = StateManager.getInstance().getGM().world.createBody(bottomSide);
		PolygonShape bottomShape = new PolygonShape();
		bottomShape.setAsBox(670, 15);
		bottomBody.createFixture(bottomShape, 0.0f);

	}

}

enum BuildingType {
	Building_A(new Texture(Gdx.files.internal("Building Tiles/building_1.png")), false, 1, 1),
	Building_B(new Texture(Gdx.files.internal("Building Tiles/building_2.png")), false, 1, 1),
	Building_C(new Texture(Gdx.files.internal("Building Tiles/building_3.png")), false, 1, 1),
	Building_D(new Texture(Gdx.files.internal("Building Tiles/building_4.png")), false, 1, 1),
	Building_E(new Texture(Gdx.files.internal("Building Tiles/building_5.png")), false, 1, 1),
	Building_F(new Texture(Gdx.files.internal("Building Tiles/building_6.png")), false, 2, 2),
	Building_G(new Texture(Gdx.files.internal("Building Tiles/building_7.png")), false, 2, 3),
	Building_H(new Texture(Gdx.files.internal("Building Tiles/building_8.png")), false, 2, 2),
	PARK_1(new Texture(Gdx.files.internal("Building Tiles/park_1.png")), true, 1, 1),
	PARK_2(new Texture(Gdx.files.internal("Building Tiles/park_2.png")), true, 3, 3),
	SQUARE_1(new Texture(Gdx.files.internal("Building Tiles/square_1.png")), true, 2, 2);

	public Texture texture;
	public Boolean traversable;
	public int widthX, widthY;

	BuildingType(Texture texture, boolean traversable, int widthX, int widthY) {
		this.texture = texture;
		this.traversable = traversable;
		this.widthX = widthX;
		this.widthY = widthY;
	}
}
