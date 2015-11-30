import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.*;
import java.awt.*;
//import java.awt.image.*;
import java.util.Arrays;
import java.util.ArrayList;

public class PhysicsEngine {
	public Points points = new Points();
	public Dimension size = new Dimension(1000,800);

	public static void main(String[] args) {
		PhysicsEngine run = new PhysicsEngine();
		//ExecuteShellComand obj = new ExecuteShellComand();
	}

	public PhysicsEngine() {
		points.add(new Point(1,500,200,10,-0.25,0,0,0,80000));
		points.add(new Point(2,500,350,25,0,0,0,0,999999999));
		points.add(new Point(3,300,700,20,0,0,0,0,80000000));
		points.add(new Point(4,700,100,20,0,0,0,0,80000000));
		points.add(new Point(5,1000,0,20,0,0,0,0,80000000));
		points.nextKey = 6;
		System.out.println(points.getPoint(1).toString());

		DrawPanel panel = new DrawPanel(this.points);
		JFrame app = new JFrame();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.add(panel);
		app.setSize(1000,800);
		app.setVisible(true);
		int time = 0;
		while(time < 500) {
			panel.repaint();
			time++;
			System.out.println("painted");
		}

		//System.out.println(points.getPoint(1).x);

		// int runs = 0;
		// while(runs < 100) {
		// 	handleGravity();
		// 	runs += 1;
		// }
	}

	public String getGravity() {
		long start = System.nanoTime();
		String build = null;
		String inputString = this.points.toString();

		if(inputString == null)
			inputString = points.toString();
		//points.setPoints(inputString);
		//System.out.println("Input string: " + inputString);

		try {
			String[] command = {"/bin/processPointsNew", inputString, ""+points.length, ""+points.nextKey};
			Process proc = Runtime.getRuntime().exec(command);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			// read the output from the command
			String s;
			while ((s = stdInput.readLine()) != null) {
			    build = s;
			    break;
			}
			proc.destroy();
		} catch(Exception e) {
			e.printStackTrace();
		}
		long stop = System.nanoTime();
		System.out.println((stop - start)/(double)1000000000);
		return build;
	}

	public Point[] handleGravity() {
		long start = System.nanoTime();
		String[] gravityTotal = getGravity().split(" aaa ");
		String gravity = gravityTotal[0];

		int futureNextKey = Integer.parseInt(gravityTotal[1]);

		Point[] p = fromString(gravity);
		Points newPointType = new Points();
		for(Point a : p) {
			newPointType.add(a);
			//System.out.println("Point added: " + a.toString());
		}
		this.points = newPointType;
		this.points.nextKey = futureNextKey;
		return p;
	}

	public Point[] fromString(String output) {
		//System.out.println("Returned: " + output);
		long start = System.nanoTime();
		output = output.substring(1,output.length()-1);
		output = output.replace("},", "}");
		output = output.replace("]","").replace("[","");
		String[] points = output.split("ComplexPoint");

		int len = points.length;

		Point[] simplePoints = new Point[len-1];
		//System.out.println(len);
		String current; int key,keyPos,keyEnd,xEnd,yEnd,rEnd,xVelEnd,yVelEnd,xAccEnd,yAccEnd,massEnd; double x,y,r,xVel,yVel,xAcc,yAcc,mass;
		for(int i = 0; i < len; i++) {
			if(!(points[i].length() < 10)) {
				current = points[i];
				//System.out.println(current);
				keyPos = current.indexOf("key = ");
				keyEnd = current.indexOf(", x = ");
				xEnd = current.indexOf(", y = ");
				yEnd = current.indexOf(", r = ");
				rEnd = current.indexOf(", xVel = ");
				xVelEnd = current.indexOf(", yVel = ");
				yVelEnd = current.indexOf(", xAcc = ");
				xAccEnd = current.indexOf(", yAcc = ");
				yAccEnd = current.indexOf(", mass = ");
				massEnd = current.indexOf("}");

				key = Integer.parseInt(current.substring(keyPos+6, keyEnd));
				x = Double.parseDouble(current.substring(keyEnd+6, xEnd));
				y = Double.parseDouble(current.substring(xEnd+6, yEnd));
				r = Double.parseDouble(current.substring(yEnd+6, rEnd));
				xVel = Double.parseDouble(current.substring(rEnd+9, xVelEnd));
				yVel = Double.parseDouble(current.substring(xVelEnd+9, yVelEnd));
				xAcc = Double.parseDouble(current.substring(yVelEnd+9, xAccEnd));
				yAcc = Double.parseDouble(current.substring(xAccEnd+9, yAccEnd));
				mass = Double.parseDouble(current.substring(yAccEnd+9, massEnd));

				//simplePoints[i-1] = new SimplePoint(current, x,y,r,key);
				simplePoints[i-1] = new Point(key, x, y, r, xVel, yVel, xAcc, yAcc, mass, current);
			}
		}
		this.points.length = len-1;
		//System.out.println(simplePoints.length);
		long end = System.nanoTime();
		return simplePoints;
	}

	// public Integer[] addTo(Integer[] array, int key) {
	// 	Integer[] n = Arrays.copyOf(array, array.length+1);
	// 	n[array.length] = key;
	// 	return n;
	// }

	// public void handleCollisions(String newPoints, String removeElems) {
	// 	if(newPoints.length() < 10 || removeElems.length() < 4)
	// 		return;
	// 	removeElems = removeElems.substring(1,removeElems.length()-1);
	// 	String[] keysAsString = removeElems.split(",");
	// 	//System.out.println("wwkrwrgwrgwrgwrgr" + Arrays.toString(keysAsString));
	// 	int[] keysToRemove = new int[keysAsString.length];
	// 	for(int i = 0; i < keysAsString.length; i++)
	// 		keysToRemove[i] = Integer.parseInt(keysAsString[i]);
	// 	for(int k : keysToRemove) {
	// 		this.points.removePoint(k);
	// 		System.out.println("Removed : " + k);
	// 	}
	// 	String newString = points.toString();
	// 	System.out.println("Doing fromString: " + newString);
	// 	Point[] newPointsToAdd = fromString(newPoints);
	// 	this.points.nextKey += newPointsToAdd.length;
	// 	for(Point a : newPointsToAdd)
	// 		this.points.add(a);
	// 	//this.points.setPoints(newString.substring(0,newString.length()-1) + newPoints + "]");
	// }

	public class DrawPanel extends JPanel {
		Points points = new Points();
		public DrawPanel(Points p) {
			super();
			this.points = p;
			setBackground(Color.WHITE);
		}

		public Points getPoints() {
			return this.points;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.setColor(Color.RED);
			Point[] pts = handleGravity();
			for(Point p : pts) {
				if(p.key == 1) {g.setColor(Color.GREEN);}else if(p.key == 2){g.setColor(Color.BLUE);}else if(p.key==3){g.setColor(Color.BLACK);}else if(p.key==4){g.setColor(Color.PINK);}else{g.setColor(Color.RED);}
				g.fillOval((int)p.x, (int)p.y, (int)p.r*2,(int)p.r*2);
			}

			g.dispose();
		}
	}
}