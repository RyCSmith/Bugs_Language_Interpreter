package bugs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.Timer;
import javax.swing.JPanel;

/**
 * @author Ryan Smith
 * @version March 2015
 * View class for Bugs Interpreter GUI.
 * @author ryancsmith
 *
 */
public class View extends JPanel implements ActionListener{
	Interpreter interpreter;
	Timer timer=new Timer(40, this);
	
	/**
	 * No argument constructor. The View will be created and added to the GUI before a Bugs
	 * program is loaded. For that reason it begins without an Interpreter and checks to see
	 * if an Interpreter is present in the paint() method.
	 */
	public View(){
		timer.start();// Start the timer here.
	}
	
	/**
	 * Adds an Interpreter to this View.
	 * @param interpreter - Interpreter to be displayed.
	 */
	synchronized public void addInterpreter(Interpreter interpreter){
		this.interpreter = interpreter;
		timer.stop();
		timer.start();
	}
	
	/**
	 * Method is listening for an ActionEvent from the Swing Timer. 
	 * Calls repaint() each time such an event is observed.
	 */
	public void actionPerformed(ActionEvent ev){
		if(ev.getSource()==timer){
			repaint();
		}
	}
	
	/**
	 * Draws the graphics in the View.
	 */
	@Override
	 public void paint(Graphics g){
		 //draw all the Bugs and and lines
		if (interpreter == null){
			g.setColor(Color.BLUE);
			g.drawString("Select a Bugs program to load from the File menu.", 140, 250);
		}
		else{
			//draw all Bugs
			for (int f = 0; f < interpreter.getBugs().size(); f++){
				Bug bug = interpreter.getBugs().get(f);
				if (bug.getColor() == null) 
					continue;
			    g.setColor(bug.getColor());
			    
			    int x1 = (int) (scaleX(bug.getX()) + computeDeltaX(12, (int)bug.getAngle()));
			    int x2 = (int) (scaleX(bug.getX() + computeDeltaX(6, (int)bug.getAngle() - 135)));
			    int x3 = (int) (scaleX(bug.getX() + computeDeltaX(6, (int)bug.getAngle() + 135)));
			    
			    int y1 = (int) (scaleY(bug.getY() + computeDeltaY(12, (int)bug.getAngle())));
			    int y2 = (int) (scaleY(bug.getY()) + computeDeltaY(6, (int)bug.getAngle() - 135));
			    int y3 = (int) (scaleY(bug.getY()) + computeDeltaY(6, (int)bug.getAngle() + 135));
			    g.fillPolygon(new int[] { x1, x2, x3 }, new int[] { y1, y2, y3 }, 3);
			}
			//draw all lines that have been created
			for (int s = 0; s < interpreter.getLines().size(); s++){
				g.setColor(interpreter.getLines().get(s).getColor());
				Graphics2D g2 = (Graphics2D) g;
				g2.draw(new Line2D.Double(scaleX(interpreter.getLines().get(s).getX1()), scaleY(interpreter.getLines().get(s).getY1()), scaleX(interpreter.getLines().get(s).getX2()), scaleY(interpreter.getLines().get(s).getY2())));
			}
		}
	 }
	
	/**
	 * Scales the x position of each Bug and each Command so that to View is limited to a 100x100 unit area.
	 * @param d
	 * @return
	 */
	private double scaleX(double d) {
		Dimension dimension = this.getSize();
		return d * (dimension.getWidth() / 100);
	}
		
	/**
	 * Scales the y position of each Bug and each Command so that to View is limited to a 100x100 unit area.
	 * @param y
	 * @return
	 */
	private double scaleY(double y) {
		Dimension dimension = this.getSize();
		return y * (dimension.getHeight() / 100);
	}

	/**
	 * Computes how much to move to add to this Bug's x-coordinate,
	 * in order to displace the Bug by "distance" pixels in 
	 * direction "degrees".
	 * 
	 * @param distance The distance to move.
	 * @param degrees The direction in which to move.
	 * @return The amount to be added to the x-coordinate.
	 */
	private static double computeDeltaX(int distance, int degrees) {
	    double radians = Math.toRadians(degrees);
	    return distance * Math.cos(radians);
	}

	/**
	 * Computes how much to move to add to this Bug's y-coordinate,
	 * in order to displace the Bug by "distance" pixels in 
	 * direction "degrees.
	 * 
	 * @param distance The distance to move.
	 * @param degrees The direction in which to move.
	 * @return The amount to be added to the y-coordinate.
	 */
	private static double computeDeltaY(int distance, int degrees) {
	    double radians = Math.toRadians(degrees);
	    return distance * Math.sin(-radians);
	}
}
