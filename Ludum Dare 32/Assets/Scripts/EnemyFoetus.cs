using UnityEngine;
using System.Collections;

public enum AiStatus{
	SEARCHING,
	ATTACKING,
}

public class Action{

	public Transform target;
	public bool completed;

}

public class EnemyFoetus : MonoBehaviour {

	AiStatus status;
	Transform camera;
	GameScript gameScript;
	Action action;
	Transform player;
	GameObject[] waypoints;
	Transform walls;
	float attackCooldown = 0;

	// Use this for initialization
	void Start () {
		status = AiStatus.SEARCHING;
		camera = Camera.main.transform;
		gameScript = (GameScript)camera.gameObject.GetComponent<GameScript> ();
		player = GameObject.FindGameObjectWithTag ("Player").transform;
		waypoints = GameObject.FindGameObjectsWithTag ("Waypoint");
		walls = GameObject.FindGameObjectWithTag ("Walls").transform;
	}
	
	// Update is called once per frame
	void Update () {
		if (gameScript.gameStatus.Equals (GameStatus.RUNNING)) {

			if(attackCooldown > 0){
				attackCooldown -= Time.deltaTime;
			}

			if (status.Equals (AiStatus.ATTACKING)) {

				float angle = Mathf.Atan2 (player.position.y, player.position.x) * Mathf.Rad2Deg;
				transform.rotation = Quaternion.AngleAxis (angle, Vector3.forward);

				if (Vector3.Distance (this.transform.position, player.transform.position) > 0.5 && Vector3.Distance (this.transform.position, player.transform.position) < 3) {
					//this.transform.Translate(Time.deltaTime * 50, 0, 0);
					this.transform.position = Vector3.MoveTowards (this.transform.position, player.position, 1.5f * Time.deltaTime);
					//this.gameObject.GetComponent<Rigidbody2D>().AddRelativeForce(Vector3.forward * 500);
				} else if (Vector3.Distance (this.transform.position, player.transform.position) >= 3 || !canGetTo (this.transform.position, player.position)) {
					status = AiStatus.SEARCHING;
				}else{
					if(attackCooldown <= 0){
						gameScript.health -= 1;
						attackCooldown = 3;
					}
				}
				//Deal Damage
			} else {
				if (action == null || action.completed) {
					Transform target = findNextWaypoint ();
					if (target.gameObject.name.Equals (player.gameObject.name)) {
						status = AiStatus.ATTACKING;
					} else {
						float angle = Mathf.Atan2 (target.position.y, target.position.x) * Mathf.Rad2Deg;
						transform.rotation = Quaternion.AngleAxis (angle, Vector3.forward);
						//this.transform.Translate(Time.deltaTime * 50, 0, 0);
						this.transform.position = Vector3.MoveTowards (this.transform.position, target.position, 1.5f * Time.deltaTime);
						//this.gameObject.GetComponent<Rigidbody2D>().AddRelativeForce(Vector3.forward * 500);
						action = new Action ();
						action.target = target;
						action.completed = false;
					}
				} else {
					float angle = Mathf.Atan2 (action.target.position.y, action.target.position.x) * Mathf.Rad2Deg;
					transform.rotation = Quaternion.AngleAxis (angle, Vector3.forward);
					//this.transform.Translate(Time.deltaTime * 50, 0, 0);
					this.transform.position = Vector3.MoveTowards (this.transform.position, action.target.position, 1.5f * Time.deltaTime);
					//this.gameObject.GetComponent<Rigidbody2D>().AddRelativeForce(Vector3.forward * 500);
					if (Vector3.Distance (action.target.position, this.transform.position) <= 0.1f) {
						action.completed = true;
					}
				}
			}
		}
	}

	Transform findNextWaypoint(){
		GameObject[] notRuledOutWaypoints = (GameObject[])waypoints.Clone();
		for(int i = 0; i < waypoints.Length; i++){
			if(waypoints[i] != null){
				if(!canGetTo(this.transform.position, waypoints[i].transform.position)){
					notRuledOutWaypoints.SetValue(null, i);
				}
			}
		}

		if (canGetTo (this.transform.position, player.position)) {
			if(Vector3.Distance(this.transform.position, player.position) < 3){
				return player;
			}
		}

		int nonNull = 0;
		for (int i = 0; i < notRuledOutWaypoints.Length; i++) {
			if(notRuledOutWaypoints[i] != null){
				nonNull++;
			}
		}
		GameObject[] actArray = new GameObject[nonNull];
		int actI = 0;
		for (int i = 0; i < notRuledOutWaypoints.Length; i++) {
			if(notRuledOutWaypoints[i] != null){
				actArray[actI] = notRuledOutWaypoints[i];
				actI++;
			}
		}
		notRuledOutWaypoints = (GameObject[])actArray.Clone();
		int number = Random.Range (0, actArray.Length);
		return notRuledOutWaypoints [number].transform;
	}

	bool canGetTo(Vector2 Position, Vector2 Target){
		bool result = !Physics2D.Linecast (Position, Target, 1 << 8);
		return result;
	}

	void OnCollisionEnter2D(Collision2D collision){
		if (collision.collider.tag.Equals ("ZombieFoetus")) {
			this.gameObject.SetActive(false);
			gameScript.points += 10;
			gameScript.decreaseEnemyFoetuses();
		}
	}
}
