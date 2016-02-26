using UnityEngine;
using System.Collections;

public class FoetusScript : MonoBehaviour {

	int bouncesLeft = 1;
	Transform camera; 
	GameScript gameScript;

	// Use this for initialization
	void Start () {
		camera = Camera.main.transform;
		gameScript = (GameScript)camera.GetComponent<GameScript> ();
	}
	
	// Update is called once per frame
	void Update () {
	}

	void OnCollisionEnter2D(Collision2D collision){
		if (collision.collider.transform.tag.Equals ("Player")) {
			if(bouncesLeft <= 0){
				this.gameObject.SetActive(false);
				gameScript.points += 1;
			}
		} else {
			bouncesLeft--;
			if (bouncesLeft <= 0) {
				transform.GetComponent<Rigidbody2D> ().drag = 2;
			}
		}
	}

}
