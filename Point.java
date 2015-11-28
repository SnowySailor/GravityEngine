public class Point {
	public int key;
	public double x, y, r, xVel, yVel, xAcc, yAcc, mass;
	public String sValues;

	public Point(int key, double x, double y, double r, double xVel, double yVel, double xAcc, double yAcc, double mass, String values) {
		this.key = key;
		this.x = x;
		this.y = y;
		this.r = r;
		this.xVel = xVel;
		this.yVel = yVel;
		this.xAcc = xAcc;
		this.yAcc = yAcc;
		this.mass = mass;
		this.sValues = values;
	}

	public Point(int key, double x, double y, double r, double xVel, double yVel, double xAcc, double yAcc, double mass) {
		this.key = key;
		this.x = x;
		this.y = y;
		this.r = r;
		this.xVel = xVel;
		this.yVel = yVel;
		this.xAcc = xAcc;
		this.yAcc = yAcc;
		this.mass = mass;
	}

	public String toString() {
		return "ComplexPoint " + this.sValues;
	}
}