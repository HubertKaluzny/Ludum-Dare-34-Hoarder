using UnityEngine;
using System.Collections;

public class PlayerScript : MonoBehaviour {

	Transform mainCamera;
	GameScript gameScript; 
	Rigidbody2D thisRigid;
	public Transform[] foetusPool;
	float headCooldown = 0;
	public AudioClip shoot;

	// Use this for initialization
	void Start () {
		mainCamera = Camera.main.transform;
		thisRigid = (Rigidbody2D)this.gameObject.GetComponent<Rigidbody2D> ();
	}
	
	// Update is called once per frame
	void Update () {
		gameScript = (GameScript)mainCamera.GetComponent<GameScript> ();
		if (gameScript.gameStatus.Equals (GameStatus.RUNNING)) {
			if(headCooldown > 0){
				headCooldown -= Time.deltaTime;
			}
			if(Input.GetKey(KeyCode.A)){
				thisRigid.AddRelativeForce(new Vector2(-500, 0));
				//this.transform.Translate(-Time.deltaTime * 2, 0, 0);
			}else{
				thisRigid.inertia = 0f;
			}
			if(Input.GetKey(KeyCode.D)){
				thisRigid.AddRelativeForce(new Vector2(500, 0));
				//this.transform.Translate(-Time.deltaTime * 2, 0, 0);
			}else{
				thisRigid.inertia = 0f;
			}
			if(Input.GetKey(KeyCode.W)){
				thisRigid.AddRelativeForce(new Vector2(0, 500));
				//this.transform.Translate(0, Time.deltaTime * 2, 0);
			}else{
				thisRigid.inertia = 0f;
			}
			if(Input.GetKey(KeyCode.S)){
				//this.transform.Translate(0, -Time.deltaTime * 2, 0);
				thisRigid.AddRelativeForce(new Vector2(0, -500));
			}else{
				thisRigid.inertia = 0f;
			}
			if(Input.GetKeyDown(KeyCode.Space)){
				//Fire foetus
				if(headCooldown <= 0){
					for(int i = 0; i < foetusPool.Length; i++){
						if(!foetusPool[i].gameObject.activeInHierarchy){
							foetusPool[i].gameObject.SetActive(true);
							Vector3 position = this.transform.position;
							foetusPool[i].transform.position = position;
							foetusPool[i].transform.rotation = transform.rotation;
							foetusPool[i].transform.Rotate(new Vector3(0, 0, 90));
							foetusPool[i].transform.Translate(new Vector3(0.3f, 0));
							foetusPool[i].transform.GetComponent<Rigidbody2D> ().AddRelativeForce(new Vector2(150, 0));
							headCooldown = 0;
							break;
						}
					}
				}
			}

			Vector3 pos = Camera.main.WorldToScreenPoint(transform.position);
			pos.z = transform.position.z;
			Vector3 dir = Input.mousePosition - pos;
			if(Vector2.Distance(pos, this.transform.position) > 10){
				float angle = Mathf.Atan2(dir.y, dir.x) * Mathf.Rad2Deg;
				transform.rotation = Quaternion.AngleAxis(angle - 90, Vector3.forward); 
			}
		}
	}
}
