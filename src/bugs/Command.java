package bugs;

import java.awt.Color;

/**
 * Holds coordinates and color of a single line that has been created and should be drawn on the View.
 * @author Ryan Smith
 * @version March 2015
 *
 */
public class Command {
	private double x1, y1, x2, y2;
	private Color color;
	private int id;
	private static int idCounter = 0; //static variable used to give a unique ID to each Command 
	
	/**
	 * Constructor for Command. Takes the x/y start/end points and the color of this line.
	 * @param x
	 * @param y
	 * @param color
	 */
	Command(double x1, double y1, double x2, double y2, Color color){
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color = color;
		this.id = idCounter;
		idCounter++;
	}
	
	/**
	 * Getter method for x1 coordinate of this line.
	 * @return x1
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * Getter method for y1 coordinate of this line.
	 * @return y1
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * Getter method for x2 coordinate of this line.
	 * @return x2
	 */
	public double getX2() {
		return x2;
	}

	/**
	 * Getter method for y2 coordinate of this line.
	 * @return y2
	 */
	public double getY2() {
		return y2;
	}

	/**
	 * Getter method for color of this line.
	 * @return color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * String representation of this line.
	 * @return String representing this line.
	 */
	public String toString(){
		return "Command " + id + ": " + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ".";
	}
}
