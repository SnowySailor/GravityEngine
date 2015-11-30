import java.lang.Math.*;

public class Point {
	public int key;
	public double x, y, r, xVel, yVel, xAcc, yAcc, mass;
	public String sValues;
	public static final double DENSITY = 2400;

	public Point(int key, double x, double y, double xVel, double yVel, double xAcc, double yAcc, double mass, String values) {
		this.key = key;
		this.x = x;
		this.y = y;
		this.xVel = xVel;
		this.yVel = yVel;
		this.xAcc = xAcc;
		this.yAcc = yAcc;
		this.mass = mass;
		this.sValues = values;
		double rCubed = ((mass*3.0)/(4.0 * Math.PI * DENSITY));
		this.r = Math.cbrt(rCubed);
		//System.out.println("R: "+this.r);
	}

	public Point(int key, double x, double y, double xVel, double yVel, double xAcc, double yAcc, double mass) {
		this.key = key;
		this.x = x;
		this.y = y;
		this.xVel = xVel;
		this.yVel = yVel;
		this.xAcc = xAcc;
		this.yAcc = yAcc;
		this.mass = mass;
		double rCubed = ((mass*3.0)/(4.0 * Math.PI * DENSITY));
		this.r = Math.cbrt(rCubed);
		//System.out.println("R: "+this.r);
	}

	public String toString() {
		return "ComplexPoint " + this.sValues;
	}
}