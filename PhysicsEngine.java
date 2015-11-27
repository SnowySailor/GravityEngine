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
		points.add(new Point(1,200,200,20,0,0,0,0,800000000));
		points.add(new Point(2,600,600,20,0,0,0,0,800000000));
		points.add(new Point(3,300,700,20,0,0,0,0,800000000));
		points.add(new Point(4,700,100,20,0,0,0,0,800000000));
		points.add(new Point(5,1000,0,20,0,0,0,0,800000000));

		DrawPanel panel = new DrawPanel(this.points);
		JFrame app = new JFrame();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.add(panel);
		app.setSize(1000,800);
		app.setVisible(true);
		int time = 0;
		while(time < 5000) {
			panel.repaint();
			time++;
			System.out.println("painted");
		}

		System.out.println(points.getPoint(1).x);

		// int runs = 0;
		// while(runs < 100) {
		// 	handleGravity();
		// 	runs += 1;
		// }
	}

	public String getGravity() {
		long start = System.nanoTime();
		String build = null;
		String inputString = this.points.getPoints();

		if(inputString == null)
			inputString = points.toString();

		try {
			String[] command = {"/bin/processGravity", inputString, ""+points.length};
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

	public SimplePoint[] handleGravity() {
		long start = System.nanoTime();
		String[] gravityTotal = getGravity().split(" aaa ");
		String gravity = gravityTotal[0];
		//System.out.println("Gravity : " + gravity);
		//String sCollisions = gravityTotal[1];
		SimplePoint[] p = fromString(gravity);
		//int[][] collisions = parseCollisions(sCollisions);
		//for(int[] a : collisions)
		//	for(int b : a)
		//		System.out.println(b);
		//p = handleCollisions(collisions);
		String build = "[";
		for(SimplePoint a : p)
			build += (a.toString() + ", ");

		build = build.substring(0,build.length()-2);
		build += "]";
		this.points.setPoints(build);
		return p;
		// System.out.println(build);System.out.println();
		// long end = System.nanoTime();
		// System.out.println("Real:"+((double)end-(double)start)/1000000000.0);
	}

	public int[][] parseCollisions(String collisions) {
		collisions = collisions.substring(1,collisions.length()-1);
		if(collisions.length() < 3) {
			return new int[0][0];
		}
		String[] aCollisions = collisions.split(", ");
		int[][] ret = new int[aCollisions.length][2];
		for(int i = 0; i < aCollisions.length; i++) {
			String a = aCollisions[i];
			String pointsAndComma = a.substring(1,a.length()-1);
			String[] splitString = pointsAndComma.split(",");
			for(int j = 0; j < 2; j++) {
				ret[i][j] = Integer.parseInt(splitString[j]);
			}
		}	
		return ret;
	}

	public SimplePoint[] fromString(String output) {
		long start = System.nanoTime();
		output = output.substring(1,output.length()-1);
		output = output.replace("},", "}");
		String[] points = output.split("ComplexPoint");

		int len = points.length;

		SimplePoint[] simplePoints = new SimplePoint[len-1];
		//System.out.println(len);
		String current; int key,keyPos,keyEnd,xEnd,yEnd,rEnd; double x,y,r;
		for(int i = 0; i < len; i++) {
			if(!(points[i].length() < 10)) {
				current = points[i];
				//System.out.println(current);
				keyPos = current.indexOf("key = ");
				keyEnd = current.indexOf(", x = ");
				key = Integer.parseInt(current.substring(keyPos+6, keyEnd));
				xEnd = current.indexOf(", y = ");
				x = Double.parseDouble(current.substring(keyEnd+6, xEnd));
				yEnd = current.indexOf(", r = ");
				y = Double.parseDouble(current.substring(xEnd+6, yEnd));
				rEnd = current.indexOf(", xVel = ");
				r = Double.parseDouble(current.substring(yEnd+6, rEnd));
				simplePoints[i-1] = new SimplePoint(current, x, y, r, key);
			}
		}
		this.points.length = len-1;
		//System.out.println(simplePoints.length);
		long end = System.nanoTime();
		return simplePoints;
	}

	public SimplePoint[] handleCollisions(int[][] collisions) {
		ArrayList<Integer[]> netCollisions = new ArrayList<Integer[]>();
		for(int i = 0; i < collisions.length; i++) {
			for(int j = 0; j < 2; j++) {
				int key = collisions[i][j];
				int in = isIn(collisions, netCollisions, i, key);
				if(in == 0) {
					int other;
					if(j == 0)
						other = collisions[i][1];
					else
						other = collisions[i][0];
					netCollisions.add(new Integer[]{key,other});
				} else {
					Integer[] newArray = addTo(netCollisions.get(in), key);
					netCollisions.set(in,newArray);
				}

			}
		}
		return null;
	}

	public Integer[] addTo(Integer[] array, int key) {
		Integer[] n = Arrays.copyOf(array, array.length+1);
		n[array.length] = key;
		return n;
	}

	public int isIn(int[][] collisions, ArrayList<Integer[]> netCollisions, int i, int key) {
		if(netCollisions.size() == 0)
			return 0;
		int isIn = 0;
		return 0;
	}

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
			SimplePoint[] pts = handleGravity();
			for(SimplePoint p : pts) {
				if(p.key == 1) {g.setColor(Color.GREEN);}else if(p.key == 2){g.setColor(Color.BLUE);}else if(p.key==3){g.setColor(Color.BLACK);}else if(p.key==4){g.setColor(Color.PINK);}else{g.setColor(Color.RED);}
				g.fillOval((int)p.x, (int)p.y, 20,20);
			}

			g.dispose();
		}
	}
}