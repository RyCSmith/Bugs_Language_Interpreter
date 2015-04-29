package bugs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import tree.Tree;

/**
 * Interpreter class for Bug Interpreter program. Runs as an independent Thread, begins and 
 * controls the progress of all Bug Threads in the current program. Interprets and holds 
 * Allbugs variables and functions for current program as well as list of lines drawn so far by Bugs.
 * @author Ryan Smith
 * @version March 2015
 *
 */
public class Interpreter  extends Thread{
	
	private Tree<Token> programTree; //most recently loaded program Tree
	private Vector<Bug> bugs; //Bugs currently being managed by this Interpreter
	private HashMap<String, Double> allbugsVariables; //Allbugs variables from the current program
	private HashMap<String, Tree<Token>> allbugsFunctions; //Allbugs functions from the current program
	private Vector<Command> lines; //all Commands that have been drawn by the Bugs in the current program
	private boolean permissionToGrant; //whether or not the Interpreter is currently allowed to hand out work permits
	private boolean oneTimePermission; //signal that permission to issue work permits is for a single step only
	private int pauseTime; //the amount of time the Interpreter should pause between issuing each set of work permits
	private boolean die; //if flagged this Thread should notify all Bugs it's managin and attempt to terminate as soon as possible
	
	/**
	 * No argument constructor. Used only for unit testing
	 */
	protected Interpreter(){
		//initialize data structures
		bugs = new Vector<Bug>();
		allbugsVariables = new HashMap<String, Double>();
		allbugsFunctions = new HashMap<String, Tree<Token>>();
		lines = new Vector<Command>();
		permissionToGrant = false;
		oneTimePermission = false;
		pauseTime = 100;
		die = false;
	}
	
	/**
	 * Default constructor.
	 * @param tree - the AST to be interpreted.
	 */
	public Interpreter(Tree<Token> tree){
		//initialize data structures
		programTree = tree;
		bugs = new Vector<Bug>();
		allbugsVariables = new HashMap<String, Double>();
		allbugsFunctions = new HashMap<String, Tree<Token>>();
		lines = new Vector<Command>();
		permissionToGrant = false;
		oneTimePermission = false;
		pauseTime = 250;
		die = false;
		//attempt to interpret any Allbugs code present
		interpret(tree.getChild(0));
		//create a Bug for each in the program and store in "bugs" - set blocked to true for every Bug
		Tree<Token> bugList = tree.getChild(1);
		for (int i = 0; i < bugList.getNumberOfChildren(); i++){
			Bug newBug = new Bug(bugList.getChild(i), this);
			newBug.setBlocked(true);
			bugs.add(newBug);
		}
	}
	
	/**
	 * Starts all Bug threads. Then attempts to repeatedly issue work permits while allowed until all Bugs have terminated.
	 */
	@Override
	public void run(){
		for (Bug bug : bugs){
			bug.start();
		}
		while (bugs.size() > 0){
			//if asked to reset, inform all Bugs, then terminate
			if (die){
				for (int n = 0; n < bugs.size(); n++){
					bugs.get(n).kill();
				}
				break;
			}
			else{
				unblockAllBugs();
			}
		}
	}


    /** 
     * Makes a Bug wait() until it is unblocked 
     */
    synchronized void getWorkPermit(Bug bug) {
        while (bug.isBlocked()) {
            try {
                wait();
            }
            catch (InterruptedException e) {
            }
        }
    }
    
    /** 
     * Bug calls after completing an action. It is set to blocked. 
     */
    synchronized void completeCurrentTask(Bug bug) {
        bug.setBlocked(true);
        notifyAll();
    }

    /** 
     * Called repeatedly by this Interpreter. Waits until all Bugs become blocked.
     * Then releases all again. 
     */
    synchronized void unblockAllBugs() {
    	pause();
    	if (oneTimePermission)
    		setPermissionToGrant(false);
        while (countBlockedBugs() < bugs.size()) {
            try {
                wait();
            }
            catch (InterruptedException e) {
            }
        }
        while (!permissionToGrant) {
            try {
                wait();
            }
            catch (InterruptedException e) {
            }
        }

        for (Bug bug : bugs) {
            bug.setBlocked(false);
        }
        notifyAll();  
    }
    
    /** Counts the number of currently blocked Bugs; since this is
     *  called from a synchronized method, it is effectively synchronized */
    private int countBlockedBugs() {
        int count = 0;
        for (Bug bug : bugs) {
            if (bug.isBlocked()) {
                count++;
            }
        }
        return count;
    }
    
    /** Called by a Bug to terminate; synchronized because it modifies the
     * ArrayList of workers, which is used by other synchronized methods. */
    synchronized void terminateBug(Bug bug) {
        bugs.remove(bug);
    }
	
	/**
	 * Getter method for allBugsVariables HashMap.
	 * @return allBugsVariables
	 */
    public HashMap<String, Double> getAllbugsVariables() {
		return allbugsVariables;
	}
    
    /**
	 * Getter method for allBugsFunctions HashMap.
	 * @return allBugsFunctions
	 */
	public HashMap<String, Tree<Token>> getAllbugsFunctions() {
		return allbugsFunctions;
	}
	
	/**
	 * Getter method for lines ArrayList. 
	 * @return
	 */
	public Vector<Command> getLines(){
		return lines;
	}
	
	/**
	 * Allows Controller to determine whether or not this Interpreter can grant work visas.
	 * @param boo
	 */
	synchronized public void setPermissionToGrant(boolean boo){
		oneTimePermission = false;
		permissionToGrant = boo;
		notifyAll();
	}
	
	/**
	 * Controller uses to allow this Interpreter to issue exactly one work permit to each Bug.
	 */
	synchronized public void giveOneTimePermission(){
		oneTimePermission = true;
		permissionToGrant = true;
		notifyAll();
	}
	
	/**
	 * Allow Controller to determine the amount of time this Interpreter should pause between issuing each set of work permits.
	 * @param newTime
	 */
	synchronized public void updatePauseTime(int newTime){
		pauseTime = newTime;
		notifyAll();
	}
	
	/**
	 * Checks to see if this Interpreter currently has permission to issue work permits.
	 * @return
	 */
	private boolean checkPermission(){
		if (permissionToGrant)
			return true;
		else
			return false;
	}
	
    /** Pause for a random amount of time */
    private void pause() {
        try { sleep(pauseTime); }
        catch (InterruptedException e) {}
    }
    
    /**
     * Getter method for programTree which holds the last program that was loaded by this Interpreter.
     * @return
     */
    public Tree<Token> getLastProgramTree(){
    	return programTree;
    }
    
    /**
     * Used to allow the Controller to inform this Interpreter to stop as soon as possible.
     */
    synchronized public void kill(){
    	die = true;
    }
    
//CODE BELOW THIS POINT INTERPRETS ALLBUGS CODE	
	/**
	 * Takes an AST representing Allbugs syntax and interprets if Allbugs code present.
	 * @param tree
	 */
	protected void interpret(Tree<Token> tree){
		switch(tree.getValue().value){
			case ("Allbugs"):
				interpretAllbugs(tree);
				break;
			case ("list"):
				interpretList(tree);
				break;
			case ("var"):
				interpretVar(tree);
				break;
			case ("function"):
				interpretFunction(tree);
				break;
		}
	}

	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "Allbugs" tree.
	 * @param tree
	 */
	protected void interpretAllbugs(Tree<Token> tree){
		if (tree.getNumberOfChildren() == 2){
			interpret(tree.getChild(0));
			interpret(tree.getChild(1));
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "list" tree.
	 * @param tree
	 */
	protected void interpretList(Tree<Token> tree){
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			interpret(tree.getChild(i));
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "var" tree.
	 * @param tree
	 */
	protected void interpretVar(Tree<Token> tree){
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			store(tree.getChild(i).getValue().value, 0.0);
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "function" tree.
	 * @param tree
	 */
	protected void interpretFunction(Tree<Token> tree){
		allbugsFunctions.put(tree.getChild(0).getValue().value, tree);
	}
	
	/**
	 * Sets the specified variable to the specified value. If the variable does not yet exist, it is created.
	 * @param key
	 * @param value
	 */
	public void store(String key, Double value){
		allbugsVariables.put(key,  value);
	}
	
//METHODS FOR UNIT TESTING	
	public HashMap<String, Double> getABVariables(){
		return allbugsVariables;
	}
	public HashMap<String, Tree<Token>> getABFunctions(){
		return allbugsFunctions;
	}
	public Vector<Bug> getBugs(){
		return bugs;
	}
}
