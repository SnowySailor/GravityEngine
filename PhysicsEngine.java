import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

public class PhysicsEngine {
	public Points points = new Points();
	public static final int XSIZE = 1430;
	public static final int YSIZE = 850;
	public Dimension size = new Dimension(XSIZE,YSIZE);
	private Process proc;
	private BufferedReader stdInput;

	public static void main(String[] args) {
		PhysicsEngine run = new PhysicsEngine();
	}

	public PhysicsEngine() {
		try {
			String command = "pgrep processGravityTest | xargs kill -9";
			proc = Runtime.getRuntime().exec(command);
		} catch(Exception e) {
			e.printStackTrace();
		}
		// // points.add(new Point(1,500,200,0.01,0,0,0,8000000));
		// // points.add(new Point(2,500,350,-0.006,-0.001,0,0,99999999));
		points.add(new Point(1,500,100,0.005,0,0,0,8000000)); // Planet
		points.add(new Point(2,500,420,0.0,0,0,0,99999999)); // Star
		points.add(new Point(3,500,130,0.002,-0.00,0,0,80000)); // Moon
		// points.add(new Point(3,300,700,0,0,-0.01,0,80000000));
		// points.add(new Point(4,700,100,0,0,0,0,80000000));
		// points.add(new Point(5,1400,0,0,0,0,0,80000000));
		// points.add(new Point(6,1000,800,0,0,0,0,40000000));
		//this.points = generateNValues(10, 8000000, 90000000);
		points.nextKey = points.getMaxKey();

		DrawPanel panel = new DrawPanel();
		JFrame app = new JFrame();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.add(panel);
		app.setSize(this.size);
		app.setVisible(true);
		int time = 0;
		while(time < 20000) {
			panel.repaint();
			time++;
			System.out.println();
		}
	}

	public String getGravity() {
		String build = null;
		String inputString = this.points.toString();
		inputString = points.toString();

		try {
			String[] command = {"/bin/processGravityTest", inputString, ""+points.length, ""+points.nextKey};
			proc = Runtime.getRuntime().exec(command);

			stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
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
		}
		this.points = newPointType;
		this.points.nextKey = futureNextKey;
		return p;
	}

	public Point[] fromString(String output) {
		long start = System.nanoTime();
		output = output.substring(1,output.length()-1);
		output = output.replace("},", "}");
		output = output.replace("]","").replace("[","");
		String[] points = output.split("ComplexPoint");

		int len = points.length;

		Point[] simplePoints = new Point[len-1];
		String current; int key,keyPos,keyEnd,xEnd,yEnd,rEnd,xVelEnd,yVelEnd,xAccEnd,yAccEnd,massEnd; double x,y,xVel,yVel,xAcc,yAcc,mass;
		for(int i = 0; i < len; i++) {
			if(!(points[i].length() < 10)) {
				current = points[i];
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
				xVel = Double.parseDouble(current.substring(rEnd+9, xVelEnd));
				yVel = Double.parseDouble(current.substring(xVelEnd+9, yVelEnd));
				xAcc = Double.parseDouble(current.substring(yVelEnd+9, xAccEnd));
				yAcc = Double.parseDouble(current.substring(xAccEnd+9, yAccEnd));
				mass = Double.parseDouble(current.substring(yAccEnd+9, massEnd));

				simplePoints[i-1] = new Point(key, x, y, xVel, yVel, xAcc, yAcc, mass, current);
			}
		}
		this.points.length = len-1;
		long end = System.nanoTime();
		return simplePoints;
	}

	public Points generateNValues(int n, int massMin, int massMax) {
		Random r = new Random();
		double rangeMin = -0;
		double rangeMax = 0;
		int xMax = XSIZE;
		int yMax = YSIZE;
		Points nValues = new Points();
		for(int i = 1; i <= n; i++) {
			int x, y, mass; double vX, vY;
			x =  Math.round((float) Math.random() * xMax);
			y =  Math.round((float) Math.random() * yMax);
			mass = 1000000 +  Math.round((float) (Math.random() * (massMax - 1000000)));
			vX = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			vY = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
			nValues.add(new Point(i, x, y, Math.round(vX),  Math.round(vY), 0, 0, mass));
		}
		return nValues;
	}

	public class DrawPanel extends JPanel {
		public DrawPanel() {
			super();
			setBackground(Color.WHITE);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.setColor(Color.RED);
			Point[] pts = handleGravity();
			Color[] colors = new Color[]{Color.GREEN, Color.BLUE, Color.BLACK, Color.PINK, Color.RED, Color.GRAY, Color.MAGENTA, Color.ORANGE, Color.YELLOW, Color.CYAN, Color.LIGHT_GRAY, Color.DARK_GRAY};
			for(Point p : pts) {
				g.setColor(colors[p.key%12]);
				g.fillOval((int)p.x - (int)p.r, (int)p.y - (int)p.r, (int)p.r*2,(int)p.r*2);
			}

			g.dispose();
		}
	}
}