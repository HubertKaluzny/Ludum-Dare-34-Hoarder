using UnityEngine;
using System.Collections;

public class CameraFollow : MonoBehaviour {
	
	GameScript gameScript; 
	Transform player;

	// Use this for initialization
	void Start () {
		gameScript = (GameScript)this.gameObject.GetComponent<GameScript> ();
		player = GameObject.FindGameObjectWithTag ("Player").transform;
	}
	
	// Update is called once per frame
	void Update () {
		if(gameScript.gameStatus.Equals(GameStatus.RUNNING)){
			transform.position = new Vector3(player.position.x, player.position.y, this.transform.position.z);
		}
	}
}
