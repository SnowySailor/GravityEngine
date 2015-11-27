public class SimplePoint {
	int key;
	double x, y, r;
	String values;

	public SimplePoint(String values, double x, double y, double r, int key) {
		this.values = values;
		this.key = key;
		this.x = x;
		this.y = y;
		this.r = r;
	}

	public String toString() {
		return "ComplexPoint " + values;
	}
}