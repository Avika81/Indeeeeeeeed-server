package Network;
public class Vector2 {
	public float x,y;
	
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static float magnitude(Vector2 vec) {
		return (float)Math.sqrt(Math.pow(vec.x, 2) + Math.pow(vec.y, 2));
	}
	
	public static float distance(Vector2 vec1, Vector2 vec2) {
		return Vector2.magnitude(Vector2.substract(vec1, vec2));
	}
	
	public static Vector2 normalize(Vector2 vec) {
		return Vector2.multiplyByScalar(vec, 1/(Vector2.magnitude(vec))); 		
	}
	
	public static Vector2 multiplyByScalar(Vector2 vec, float scalar) {
		return new Vector2(vec.x * scalar, vec.y * scalar);		
	}
	
	public static float scalarMultiplication(Vector2 vec1, Vector2 vec2) {
		return (vec1.x * vec2.x + vec1.y * vec2.y);		
	}
	
	public static Vector2 add(Vector2 vec1, Vector2 vec2) {
		return new Vector2(vec1.x + vec2.x , vec1.y + vec2.y);		
	}
	
	public static Vector2 substract(Vector2 vec1, Vector2 vec2) {
		return new Vector2(vec1.x - vec2.x , vec1.y - vec2.y);		
	}
	
	public static float dotProduct(Vector2 vec1, Vector2 vec2) {
		return (vec1.x * vec2.x + vec1.y * vec2.y);		
	}
	
	// projection of vec1 onto vec2
	public static Vector2 parallel(Vector2 vec1, Vector2 vec2) {
		return (Vector2.multiplyByScalar(Vector2.normalize(vec2), dotProduct(vec1, Vector2.normalize(vec2))));		
	}
	
	public static Vector2 tangential(Vector2 vec1, Vector2 vec2) {
		return (Vector2.substract(vec1, parallel(vec1, vec2)));	
	}
	
	public static Vector2 right() {
		return new Vector2(1,0);	
	}
	
	public static Vector2 left() {
		return new Vector2(-1,0);	
	}
	
	public static Vector2 forward() {
		return new Vector2(0,1);	
	}

	public static Vector2 backward() {
		return new Vector2(0,-1);	
	}
	
	public static Vector2 zero() {
		return new Vector2(0,0);	
	}
	
	public static Vector2 Average(Vector2 vector1, Vector2 vector2) {
		Vector2 average = new Vector2(0f, 0f);
		average.y = (vector1.y + vector2.y)/2;
		average.x = (vector1.x + vector2.x)/2;
		return average;
	}
	
	public static void print(String msg, Vector2 vec) {
		System.out.println(msg + ": (" + vec.x + "," + vec.y + ")");
	}
}
