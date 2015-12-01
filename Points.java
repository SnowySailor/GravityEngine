import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

public class Points {
	Point[] points;
	public int length;
	public int nextKey;
	String sPoints;

	public Points() {
		this.points = new Point[0];
		this.length = 0;
		this.nextKey = 0;
	}

	public void add(Point a) {
		this.points = Arrays.copyOf(this.points, this.length+1);
		this.points[length] = a;
		this.length += 1;
	}

	public Point getPoint(int key) {
		for(Point a : this.points)
			if(a.key == key)
				return a;
		return null;
	}

	public void setPoints(String points) {
		this.sPoints = points;
	}

	public String getPoints() {
		return this.sPoints;
	}

	public int getMaxKey() {
		int max = this.points[0].key;
		for(Point a : this.points)
			if(a.key > max)
				max = a.key;
		return max;
	}

	public String toString() {
		String build =  "[";
		for(Point a : this.points) {
			if(a.sValues == null)
				build += "ComplexPoint {key= "+a.key+", x= "+a.x+", y= "+a.y+", r= "+a.r+", xVel= "+a.xVel+", yVel= "+a.yVel+", xAcc= "+a.xAcc+", yAcc= "+a.yAcc+", mass= "+a.mass+"}, ";
			else
				build += a.toString() + ", ";
		}
		build = build.substring(0,build.length()-2);
		build += "]";
		return build;
	}
}