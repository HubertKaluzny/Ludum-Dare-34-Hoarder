using UnityEngine;
using System.Collections;

public enum GameStatus{
	STARTMENU, INFOBAR, PAUSED, RUNNING,
}

public class GameScript : MonoBehaviour {

	public GameObject[] enemyFetusPool;
	int wave = 1;
	public GameStatus gameStatus = GameStatus.PAUSED;
	public int health = 20, enemyFeotusesLeft = 0;
	public long points = 0;
	public Texture2D popupBox, mainMenu, cross, fullHeart, emptyHeart;
	public GUISkin skin;
	public Vector3[] FetusSpawns;
	public AudioClip mainMenuSound, mainGame, groan1, groan2, groan3;
	AudioSource audioSource;

	// Use this for initialization
	void Start () {
		gameStatus = GameStatus.STARTMENU;

		AudioSource[] audioSources = (AudioSource[])this.gameObject.GetComponents<AudioSource> ();
		audioSource = audioSources[1];
		enemyFetusPool = GameObject.FindGameObjectsWithTag("AbortedFoetus");
		for (int i = 0; i < enemyFetusPool.Length; i++) {
			enemyFetusPool[i].SetActive(false);
		}
	}

	public void decreaseEnemyFoetuses(){
		enemyFeotusesLeft--;
		if (enemyFeotusesLeft <= 0) {
			wave++;
			spawnNewEnemies();
		}
	}

	public void Update(){
		if (gameStatus.Equals (GameStatus.RUNNING)) {
			int ActRandom = Random.Range(0, 100000);
			if(ActRandom > 100 && ActRandom < 600){
			int random = Random.Range(0, 3);
				if(random == 0){
					audioSource.clip = groan1;
					audioSource.Play();
				}else if(random == 1){
					audioSource.clip = groan2;
					audioSource.Play();
				}else if(random == 2){
					audioSource.clip = groan3;
					audioSource.Play();
				}
			}
		}
	}

	void spawnNewEnemies(){
		int numberToSpawn = wave * 2;
		for (int i = 0; i < numberToSpawn; i++) {
			int spawnNo = Random.Range(0, FetusSpawns.Length - 1);
			enemyFetusPool[i].SetActive(true);
			enemyFetusPool[i].transform.position = FetusSpawns[spawnNo];
		}
		enemyFeotusesLeft = numberToSpawn;
	}

	void OnGUI(){
		if (skin != null) {
			GUI.skin = skin;
		}
		if (gameStatus.Equals (GameStatus.STARTMENU)) {
			this.transform.position = new Vector3 (100, 100, -1);
			int textWidth = Screen.height, textHeight = Screen.height;
			GUI.DrawTexture (new Rect ((Screen.width / 2) - (textWidth / 2), (Screen.height / 2) - (textHeight / 2), textWidth, textHeight), mainMenu); 

			//Play
			if(GUI.Button(new Rect ((Screen.width / 2) - 200 , (Screen.height / 2) - 64, 120, 64), " ")){
				gameStatus = GameStatus.RUNNING;
				GameObject player = (GameObject)GameObject.FindGameObjectWithTag("Player");
				this.transform.position = new Vector3(player.transform.position.x, player.transform.position.y, this.transform.position.z);
				spawnNewEnemies();
			}

			//Info
			if(GUI.Button(new Rect ((Screen.width / 2) - 55 , (Screen.height / 2) - 64, 120, 60), " ")){
				gameStatus = GameStatus.INFOBAR;
			}

			//Quit
			if(GUI.Button(new Rect ((Screen.width / 2) + 112 , (Screen.height / 2) - 64, 120, 60), " ")){
				Application.Quit();
			}

		} else if (gameStatus.Equals (GameStatus.INFOBAR)) {
			this.transform.position = new Vector3 (100, 100);
			GUI.Label(new Rect (10, 10, 1000, 30), "This game was created for Ludum Dare 32 Jam, for the weekend of 18/04/2015");
			GUI.Label(new Rect (10, 40, 1000, 30), "Game by Hubert Kaluzny (http://www.hubertkaluzny.com)");
			GUI.Label(new Rect (10, 70, 1000, 30), "Art by Matthew Seredyn");
			GUI.Label(new Rect (10, 110, 1000, 30), "Royalty free music by Kevin MacLeod http://incompetech.com/");
			int boxHeight = 20, boxWidth = 100;
			if(GUI.Button( new Rect ((Screen.width / 2) - (boxWidth / 2), (Screen.height / 2) - (boxHeight / 2), boxWidth, boxHeight), "Back")){
				gameStatus = GameStatus.STARTMENU;
			}
		}else if (gameStatus.Equals (GameStatus.RUNNING)) {
			if(GUI.Button(new Rect(5, 5, 40, 40), cross)){
				gameStatus = GameStatus.PAUSED;
				Time.timeScale = 0;
			}
			 
			//Also other shit down here
			int width = 20, height  = 20;
			for(int i = 1; i <= 20; i++){
				if(health >= i){
					GUI.DrawTexture(new Rect(10 + (i * (width + 5)), Screen.height - (10 +  height), width, height), fullHeart);
				}else if(health < i){
					GUI.DrawTexture(new Rect(10 + (i * (width + 5)), Screen.height - (10 +  height), width, height), emptyHeart);
				}
			}

			GUI.color = Color.cyan;
			GUI.Label(new Rect(60, 15, 1000, 40), points.ToString() + " points");
			GUI.color = Color.red;
			GUI.Label(new Rect(Screen.width - 100, Screen.height - 30, 100, 30), "Wave " + wave);

		} else if (gameStatus.Equals (GameStatus.PAUSED)) {

			int textWidth = 400, textHeight = 400;
			GUI.DrawTexture(new Rect((Screen.width / 2)-(textWidth / 2), (Screen.height / 2) - (textHeight /2 ), textWidth, textHeight), popupBox); 

			if(GUI.Button(new Rect((Screen.width / 2)-(textWidth / 2) + 68, (Screen.height / 2) - (textHeight /2) + 175, 100, 60), "")){
				Application.Quit();
			}

			if(GUI.Button(new Rect((Screen.width / 2)-(textWidth / 2) + 178, (Screen.height / 2) - (textHeight /2) + 145, 150, 90), "")){
				gameStatus = GameStatus.RUNNING;
				Time.timeScale = 1;
			}

		}
	}


}
