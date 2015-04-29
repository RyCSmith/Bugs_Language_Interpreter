package bugs;
import java.awt.Color;
import java.util.HashMap;
import java.util.Stack;
import java.lang.Math;

import tree.Tree;

/**
 * Bug class for Bugs language Interpreter. Runs an independent Thread and interprets the 
 * functionality of a single Bug.
 * @author Ryan Smith
 * @version March 2015
 */
public class Bug extends Thread{
	private double x, y, angle; //This Bug's position and orientation
	private Color color; //This Bug's color
	private HashMap<String, Double> variables; //this Bug's principle variables
	private HashMap<String, Tree<Token>> functions; //this Bug's functions
	private String bugName; //name of this Bug
	private Stack<Boolean> loopExitStack; //Stack used to hold boolean values that will be updated by "exit if" statement in order to control breaking from nested loops.
	private Interpreter interpreter; //Reference to the Interpreter manipualting this Bug
	private Tree<Token> myTree; //This Bug's AST
	private boolean blocked; // If true, Bug must request a work permit from Interpreter before progressing past an action statement
	private Stack<HashMap<String, Double>> scopes; //fluid Stack of HashMaps holding variables stored during function calls
	private double returnValue; //this will hold the value returned by a "return" statement
	private boolean die; //informs the Bug of a reset request - Bug should terminate as soon as possible
	
	/**
	 * No argument constructor, initializes variables to default values.
	 * Not used within the Interpreter. Included to facilitate unit testing.
	 */
	public Bug(){
		x  = 0.0;
		y = 0.0;
		angle = 0.0;
		color = Color.BLACK;
		variables = new HashMap<String, Double>();
		functions = new HashMap<String, Tree<Token>>();
		loopExitStack = new Stack();
		this.interpreter = interpreter;
		scopes = new Stack<HashMap<String, Double>>();
		scopes.push(variables);
		die = false;
	}
	/**
	 * Constructor.
	 * @param tree - AST representing a Bug definition
	 * @param interpreter - Interpreter that is managing this Bug and possibly holds Allbugs code.
	 */
	public Bug(Tree<Token> tree, Interpreter interpreter){
		x  = 0.0;
		y = 0.0;
		angle = 0.0;
		color = Color.BLACK;
		variables = new HashMap<String, Double>();
		functions = new HashMap<String, Tree<Token>>();
		loopExitStack = new Stack();
		this.interpreter = interpreter;
		bugName = tree.getChild(0).getValue().value;
		myTree = tree;
		scopes = new Stack<HashMap<String, Double>>();
		scopes.push(variables);
		die = false;
	}
	
	@Override
	public void run(){
		interpreter.getWorkPermit(this);
		interpret(myTree);
		if (!die){
			interpreter.completeCurrentTask(this); //this keeps the Interpreter from getting caught in unblockAll function waiting for threads to be blocked
			interpreter.terminateBug(this);
		}
	}
	
	/**
	 * Takes an AST representing components of Bugs program and evaluates it.
	 * @param tree
	 * @return double
	 */
	public double evaluate(Tree<Token> tree){
		if (die)
			return 0.0;
		//Base Case for Recursion - if this is a leaf, return it's value as a double
		if (tree.getNumberOfChildren() == 0){
			return evaluateLeaf(tree);
		}
		//in the case of addition, evaluate children
		if (tree.getValue().value.equals("+")){
			return evaluateAdd(tree);
		}
		else if (tree.getValue().value.equals("-")){
			return evaluateSubtract(tree);
		}
		else if (tree.getValue().value.equals("*")){
			return evaluateMultiply(tree);
		}
		else if (tree.getValue().value.equals("/")){
			return evaluateDivide(tree);
		}
		else if (tree.getValue().value.equals("<")){
			return evaluateLessThan(tree);	
		}
		else if (tree.getValue().value.equals("<=")){
			return evaluateLessThanEqualTo(tree);	
		}
		else if (tree.getValue().value.equals("=")){
			return evaluateEquals(tree);
		}
		else if (tree.getValue().value.equals("!=")){
			return evaluateNotEquals(tree);
		}
		else if (tree.getValue().value.equals(">")){
			return evaluateGreaterThan(tree);
		}
		else if (tree.getValue().value.equals(">=")){
			return evaluateGreaterThanEqualTo(tree);
		}
		else if (tree.getValue().value.equals("case")){
			return evaluateCase(tree);
		}
		else if (tree.getValue().value.equals("call")){
			return evaluateCall(tree);
		}
		else if (tree.getValue().value.equals(".")){
			return evaluateDot(tree);
		}
		return 0.0;
	}
	
	/**
	 * Takes an AST representing components of Bugs program and interprets it.
	 * @param tree
	 */
	public void interpret(Tree<Token> tree){
		if (die)
			return;
		switch(tree.getValue().value){
			case ("Bug"):
				interpretBug(tree);
				break;
			case ("list"):
				interpretList(tree);
				break;
			case ("var"):
				interpretVar(tree);
				break;
			case("initially"):
				interpretInitially(tree);
				break;
			case ("block"):
				interpretBlock(tree);
				break;
			case ("move"):
				interpretMove(tree);
				interpreter.completeCurrentTask(this);
				interpreter.getWorkPermit(this);
				break;
			case ("moveto"):
				interpretMoveTo(tree);
				interpreter.completeCurrentTask(this);
				interpreter.getWorkPermit(this);
				break;
			case ("turn"):
				interpretTurn(tree);
				interpreter.completeCurrentTask(this);
				interpreter.getWorkPermit(this);
				break;
			case ("turnto"):
				interpretTurnto(tree);
				interpreter.completeCurrentTask(this);
				interpreter.getWorkPermit(this);
				break;
			case ("return"):
				interpretReturn(tree);
				break;
			case ("line"):
				interpretLine(tree);
				interpreter.completeCurrentTask(this);
				interpreter.getWorkPermit(this);
				break;
			case ("assign"):
				interpretAssign(tree);
				break;
			case ("loop"):
				interpretLoop(tree);
				break;
			case ("exit"):
				interpretExit(tree);
				break;
			case ("switch"):
				interpretSwitch(tree);
				break;
			case ("color"):
				interpretColor(tree);	
				break;
			case ("function"):
				interpretFunction(tree);
				break;
			case ("call"):
				evaluateCall(tree);
				break;
		}
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a <code>Tree&ltToken&gt</code> that is a leaf.
	 * @param tree
	 */
	private double evaluateLeaf(Tree<Token> tree){
		if (isNumber(tree))
			return Double.valueOf(tree.getValue().value);
		else
			return fetch(tree.getValue().value);
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate an "+" tree.
	 * @param tree
	 */
	private double evaluateAdd(Tree<Token> tree){
		if (tree.getNumberOfChildren() == 1){
			return evaluate(tree.getChild(0));
		}
		else{
			return evaluate(tree.getChild(0)) + evaluate(tree.getChild(1));
		}
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a "-" tree.
	 * @param tree
	 */
	private double evaluateSubtract(Tree<Token> tree){
		//if negative, make so before returning 
		if (tree.getNumberOfChildren() == 1){
			return 0 - evaluate(tree.getChild(0));
		}
		else{
			return evaluate(tree.getChild(0)) - evaluate(tree.getChild(1));
		}
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a "*" tree.
	 * @param tree
	 */
	private double evaluateMultiply(Tree<Token> tree){
		return evaluate(tree.getChild(0)) * evaluate(tree.getChild(1));
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a "/" tree.
	 * @param tree
	 */
	private double evaluateDivide(Tree<Token> tree){
		return evaluate(tree.getChild(0)) / evaluate(tree.getChild(1));
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a "<" tree.
	 * @param tree
	 */
	private double evaluateLessThan(Tree<Token> tree){
		if (evaluate(tree.getChild(1)) - evaluate(tree.getChild(0)) > 0.001){ 
			return 1.0;
		}
		return 0.0;	
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a "<=" tree.
	 * @param tree
	 */
	private double evaluateLessThanEqualTo(Tree<Token> tree){
		if (evaluate(tree.getChild(1)) - evaluate(tree.getChild(0)) > -0.001){ 
			return 1.0;
		}
		return 0.0;
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate an "=" tree.
	 * @param tree
	 */
	private double evaluateEquals(Tree<Token> tree){
		if (evaluate(tree.getChild(0)) - evaluate(tree.getChild(1)) > 0.001 || 
				evaluate(tree.getChild(0)) - evaluate(tree.getChild(1)) < -0.001){
			return 0.0;
		}
		return 1.0;
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a "!=" tree.
	 * @param tree
	 */
	private double evaluateNotEquals(Tree<Token> tree){
		if (evaluate(tree.getChild(0)) - evaluate(tree.getChild(1)) > 0.001 || 
				evaluate(tree.getChild(0)) - evaluate(tree.getChild(1)) < -0.001){
			return 1.0;
		}
		return 0.0;
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a ">" tree.
	 * @param tree
	 */
	private double evaluateGreaterThan(Tree<Token> tree){
		if (evaluate(tree.getChild(0)) - evaluate(tree.getChild(1)) > 0.001){ 
			return 1.0;
		}
		return 0.0;	
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a ">=" tree.
	 * @param tree
	 */
	private double evaluateGreaterThanEqualTo(Tree<Token> tree){
		if (evaluate(tree.getChild(0)) - evaluate(tree.getChild(1)) > -0.001){ 
			return 1.0;
		}
		return 0.0;
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a "case" tree.
	 * @param tree
	 */
	private double evaluateCase(Tree<Token> tree){
		double result = evaluate(tree.getChild(0));
		if (result > 0.001 || result < -0.001)
			interpret(tree.getChild(1));
		return result;
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a "call" tree.
	 * @param tree
	 */
	private double evaluateCall(Tree<Token> tree){
		//check to see if the function refers to one of the 2 special functions and handle it if so.
		if ((tree.getChild(0).getValue().value).equals("distance"))
			return distance(tree.getChild(1).getChild(0).getValue().value);
		else if ((tree.getChild(0).getValue().value).equals("direction"))
			return direction(tree.getChild(1).getChild(0).getValue().value);
		
		HashMap<String, Double> funcVars = new HashMap<String, Double>();
		Tree<Token> function;
		//find the function corresponding to this call
		if (functions.containsKey(tree.getChild(0).getValue().value))
			function = functions.get(tree.getChild(0).getValue().value);
		else if (interpreter.getAllbugsFunctions().containsKey(tree.getChild(0).getValue().value))
			function = interpreter.getAllbugsFunctions().get(tree.getChild(0).getValue().value);
		else
			throw new RuntimeException("Function not defined!");
		//make sure num arguments ==  num variables
		if (tree.getChild(1).getNumberOfChildren() != function.getChild(1).getNumberOfChildren())
			throw new RuntimeException("Function call does not match number of arguments in function!");
		//assign the arguments to the parameters and store them in the new HashMap
		//if someone tries to assign 
		for (int i = 0; i < tree.getChild(1).getNumberOfChildren(); i++){
			String param = function.getChild(1).getChild(i).getValue().value;
			if (param.equals("x") || param.equalsIgnoreCase("y") || param.equals("angle"))
				throw new RuntimeException("Trying to declare local var " + param + " in function " + function.getChild(0).getValue().value);
			funcVars.put(param, evaluate(tree.getChild(1).getChild(i)));
		}
		//put new HashMap for variables in scopes. not added earlier because during recursion we want values to be
		//updated with values from previous functions
		scopes.push(funcVars);
		//interpret the block of the function
		interpret(function.getChild(2));
		//remove last hashmap from this function before it goes out of scope
		scopes.pop();
		//get the value from return statement (if any) then reset the variable to 0.0 for the next call, return that value
		double tempReturnValue = returnValue;
		returnValue = 0.0;
		return tempReturnValue;
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>.
	 * Attempts to evaluate a "." tree representing dot notation to access another Bug's variables. 
	 * @param tree
	 * @return value - value of the requested Bug's variable.
	 */
	private double evaluateDot(Tree<Token> tree){
		//find the requested bug
		Bug requestedBug = findOtherBugFromName(tree.getChild(0).getValue().value);
		//if Bug not found throw RuntimeException
		if (requestedBug == null){
			throw new RuntimeException("The requested Bug \"" + tree.getChild(0).getValue().value +
					"\" was not found. Could not access desired bug variable \"" + tree.getChild(1).getValue().value +
					"\".");
		}
		//find the variable that we want from the requestedBug and throw RuntimeException if it is not found
		switch (tree.getChild(1).getValue().value){
			case ("x"):
				return requestedBug.x;
			case ("y"):
				return requestedBug.y;
			case ("angle"):
				return requestedBug.angle;
			default:
				if (requestedBug.scopes.get(0).get(tree.getChild(1).getValue().value) == null){
					throw new RuntimeException("The requested variable \"" + tree.getChild(1).getValue().value +
							"\" in Bug " + requestedBug.getBugName() + "\" was not found");
				}
				else{
					return requestedBug.scopes.get(0).get(tree.getChild(1).getValue().value);
				}
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "bug" tree.
	 * @param tree
	 */
	private void interpretBug(Tree<Token> tree){
		bugName = tree.getChild(0).getValue().value;
		interpret(tree.getChild(1));//var declarations
		interpret(tree.getChild(4));//this Bug's functions
		interpret(tree.getChild(2));//initialization block
		interpret(tree.getChild(3));//main program
	}
	//Note: should not have to worry about declaring Bug variables in wrong HashMap because var declarations should
	//be processed before function definitions and the calls to those functions that would add HashMaps to the Stack
	//would not take place until interpreting begins
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "list" tree.
	 * @param tree
	 */
	private void interpretList(Tree<Token> tree){
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			interpret(tree.getChild(i));
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "var" tree.
	 * @param tree
	 */
	private void interpretVar(Tree<Token> tree){
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			variables.put(tree.getChild(i).getValue().value, 0.0);
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "initially" tree.
	 * @param tree
	 */
	private void interpretInitially(Tree<Token> tree){
		if (tree.getNumberOfChildren() > 0){
			interpret(tree.getChild(0));
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "block" tree.
	 * @param tree
	 */
	private void interpretBlock(Tree<Token> tree){
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			//need to return in a fashion similar to Java - Bugs return statements are commands, commands only
			//occur within blocks - we can handle this by checking to see if the child is a command - if so,
			//we will execute it and then break the loop to cease executing the block. otherwise execute normally
			if (tree.getChild(i).getValue().value.equals("return")){
				interpret(tree.getChild(i));
				break;
			}
			
			interpret(tree.getChild(i));
			//if you encounter an "exit-if" statement and it updates 
			//the stack-top to true get out of the block
			//(outer if protects against NullPointerExceptions) if stack is empty, it has not been updated by "exit"
			if (!loopExitStack.empty()){
				if (loopExitStack.peek() == true)
					break;
			}
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "move" tree.
	 * @param tree
	 */
	private void interpretMove(Tree<Token> tree){
		double radians = angle * (Math.PI/180);
		double newY = y - (evaluate(tree.getChild(0)) * Math.sin(radians));
		double newX = x + (evaluate(tree.getChild(0)) * Math.cos(radians));
		interpreter.getLines().add(new Command(x, y, newX, newY, color));
		x = newX;
		y = newY;
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "moveto" tree.
	 * @param tree
	 */
	private void interpretMoveTo(Tree<Token> tree){
		double newX = evaluate(tree.getChild(0));
		double newY = evaluate(tree.getChild(1));
		interpreter.getLines().add(new Command(x, y, newX, newY, color));
		x = newX;
		y = newY;
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "turn" tree.
	 * @param tree
	 */
	private void interpretTurn(Tree<Token> tree){
		if (angle + evaluate(tree.getChild(0)) > 360.0)
			angle = (angle + evaluate(tree.getChild(0))) % 360.0;
		else
			angle += evaluate(tree.getChild(0));
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "turnto" tree.
	 * @param tree
	 */
	private void interpretTurnto(Tree<Token> tree){
		if (evaluate(tree.getChild(0)) > 360.0)
			angle = evaluate(tree.getChild(0)) % 360.0;
		else
			angle = evaluate(tree.getChild(0));
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "return" tree.
	 * @param tree
	 */
	private void interpretReturn(Tree<Token> tree){
		//takes value from the return expression and places it into the global variable used for passing return values
		returnValue = evaluate(tree.getChild(0));
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "line" tree.
	 * @param tree
	 */
	private void interpretLine(Tree<Token> tree){
		interpreter.getLines().add(new Command(evaluate(tree.getChild(0)), evaluate(tree.getChild(1)), evaluate(tree.getChild(2)), evaluate(tree.getChild(3)), color));
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "assign" tree.
	 * @param tree
	 */
	private void interpretAssign(Tree<Token> tree){
		if (tree.getChild(0).getValue().value.equals("x"))
			x = evaluate(tree.getChild(1));
		else if (tree.getChild(0).getValue().value.equals("y"))
			y = evaluate(tree.getChild(1));
		else if (tree.getChild(0).getValue().value.equals("angle"))
			angle = evaluate(tree.getChild(1));
		else
			store(tree.getChild(0).getValue().value, evaluate(tree.getChild(1)));
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "loop" tree.
	 * @param tree
	 */
	private void interpretLoop(Tree<Token> tree){
		//each loop puts on its own value of false
		loopExitStack.push(false);
		while(true){
			interpret(tree.getChild(0));
			//after interpreting each child, if stack-top is true
			//remove it and break the loop
			if (loopExitStack.peek() == true){
				loopExitStack.pop();
				break;
			}
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "exit" tree.
	 * @param tree
	 */
	private void interpretExit(Tree<Token> tree){
		//if exit if expression is true, remove the "false" added in the start
		//of the loop and replace it will "true
		//(inner if statement protects against NullPointerExceptions from misplaced exit if statements)
		if (isTrue(evaluate(tree.getChild(0)))){
			if (!loopExitStack.empty()){
				loopExitStack.pop();
				loopExitStack.push(true);
			}
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "switch" tree.
	 * @param tree
	 */
	private void interpretSwitch(Tree<Token> tree){
		for (int i = 0; i < tree.getNumberOfChildren(); i++){
			double result = evaluate(tree.getChild(i));
			if (result > 0.001 || result < -0.001)
				break;
		}
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "color" tree.
	 * @param tree
	 */
	private void interpretColor(Tree<Token> tree){
		String newColor = tree.getChild(0).getValue().value;
		if (newColor.equalsIgnoreCase("black"))
			color = Color.BLACK;
		else if (newColor.equalsIgnoreCase("blue"))
			color = Color.BLUE;
		else if (newColor.equalsIgnoreCase("cyan"))
			color = Color.CYAN;
		else if (newColor.equalsIgnoreCase("darkGray"))
			color = Color.DARK_GRAY;
		else if (newColor.equalsIgnoreCase("gray"))
			color = Color.GRAY;
		else if (newColor.equalsIgnoreCase("green"))
			color = Color.GREEN;
		else if (newColor.equalsIgnoreCase("lightGray"))
			color = Color.LIGHT_GRAY;
		else if (newColor.equalsIgnoreCase("magenta"))
			color = Color.MAGENTA;
		else if (newColor.equalsIgnoreCase("orange"))
			color = Color.ORANGE;
		else if (newColor.equalsIgnoreCase("pink"))
			color = Color.PINK;
		else if (newColor.equalsIgnoreCase("red"))
			color = Color.RED;
		else if (newColor.equalsIgnoreCase("white"))
			color = Color.WHITE;
		else if (newColor.equalsIgnoreCase("yellow"))
			color = Color.YELLOW;
		else if (newColor.equalsIgnoreCase("brown"))
			color = new Color(153, 76, 0);
		else if (newColor.equalsIgnoreCase("purple"))
			color = new Color(153, 51, 255);
		else if (newColor.equalsIgnoreCase("none"))
			color = null;
		else
			throw new RuntimeException("Not a valid color.");
	}
	
	/**
	 * Helper method for <code>interpret(Tree&ltToken&gt)</code>.
	 * Attempts to interpret "function" tree.
	 * @param tree
	 */
	private void interpretFunction(Tree<Token> tree){
		functions.put(tree.getChild(0).getValue().value, tree);
	}
	
	/**
	 * Sets the specified variable to the specified value. Starts with top scope and works back to Allbugs variables.
	 * @throws RuntimeException if variable is not found.
	 * @param key
	 * @param value
	 */
	public void store(String key, Double value){
		switch(key){
			case ("x"):
				x = value;
				break;
			case ("y"):
				y = value;
				break;
			case ("angle"):
				angle = value;
				break;
			default:
				for (int i = scopes.size() - 1; i >= 0; i--){
					if (scopes.get(i).containsKey(key)){
						scopes.get(i).put(key, value);
						return;
					}
				}
				//if here, var not found in scopes
				if (interpreter.getAllbugsVariables().containsKey(key)){
					interpreter.getAllbugsVariables().put(key, value);
					return;
				}
				throw new RuntimeException("Variable \"" + key + "\" could not be found");
		}
	}
	
	/**
	 * Returns the requested variables value from the HashMap. Throws new RuntimeException if the variable does not exist.
	 * @param variable
	 * @return value
	 * @throws RuntimeException
	 */
	public double fetch(String variable){
		switch(variable){
		case ("x"):
			if (Double.isNaN(x)) {
				throw new RuntimeException("Error: variable \"x\" has value: NaN.");
			}
			else {return this.x;}
		case ("y"):
			if (Double.isNaN(y)) {
				throw new RuntimeException("Error: variable \"y\" has value: NaN.");
			}
			else {return this.y;}
		case ("angle"):
			if (Double.isNaN(angle)) {
				throw new RuntimeException("Error: variable \"angle\" has value: NaN.");
			}
			else {return this.angle;}
		default:
			for (int i = scopes.size() - 1; i >= 0; i--){
				if (scopes.get(i).containsKey(variable)){
					return scopes.get(i).get(variable);
				}
			}
			//if here, var not found in scopes
			if (interpreter.getAllbugsVariables().containsKey(variable)){
				return interpreter.getAllbugsVariables().get(variable);
			}
			throw new RuntimeException("Variable \"" + variable + "\" could not be found");
		}		
	}
	
	/**
	 * Returns the name of this bug.
	 * @return bugName
	 */
	public String getBugName(){
		return bugName;
	}
	
	/**
	 * Returns this Bug's x coordinate.
	 * @return
	 */
	public double getX(){
		return x;
	}
	
	/**
	 * Returns this Bug's y coordinate.
	 * @return
	 */
	public double getY(){
		return y;
	}
	
	/**
	 * Sets the name of this bug to the provided <code>name</code>.
	 * @param name
	 */
	public void setBugName(String name){
		bugName = name;
	}
	
	/**
	 * Returns this bug's current Color.
	 * @return color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Returns the distance between this Bug and the argument Bug.
	 * @param bug
	 * @return
	 */
	public double distance(String bugName){
		Bug otherBug = findOtherBugFromName(bugName);
		if (otherBug == null)
			throw new RuntimeException("The bug named \"" + bugName + "\" could not be found and distance could not be calculated.");
		return Math.sqrt(Math.pow((otherBug.x - x), 2) + Math.pow((otherBug.y - y), 2));
	}
	
	/**
	 * Computes the angle this Bug should be set to to directly face the argument Bug.
	 * @param bug
	 * @return
	 */
	public double direction(String bugName){
		Bug otherBug = findOtherBugFromName(bugName);
		if (otherBug == null)
			throw new RuntimeException("The bug named \"" + bugName + "\" could not be found and direction could not be calculated.");
		
		if (otherBug.x > x && otherBug.y == y){
			return 0.0;
		}
		else if (otherBug.x < x && otherBug.y == y){
			return 180.0;
		}
		else if (otherBug.y > y && otherBug.x == x){
			return 270.0;
		}
		else if (otherBug.y < y && otherBug.x == x){
			return 90.0;
		}
		else if (otherBug.x > x && otherBug.y > y){
			double opposite = otherBug.y - y;
			double adjacent = otherBug.x - x;
			return 360 - Math.toDegrees(Math.atan(opposite/adjacent));
		}
		else if (otherBug.x > x && otherBug.y < y){
			double opposite = y - otherBug.y;
			double adjacent = otherBug.x - x;
			return Math.toDegrees(Math.atan(opposite/adjacent));
		}
		else if (otherBug.x < x && otherBug.y > y){
			double opposite = otherBug.y - y;
			double adjacent = x - otherBug.x;
			return 180 + Math.toDegrees(Math.atan(opposite/adjacent));
		}
		else { //(otherBug.x < x && otherBug.y < y)
			double opposite = y - otherBug.y;
			double adjacent = x - otherBug.x;
			return 180 - Math.toDegrees(Math.atan(opposite/adjacent));
		}
	}
	
	/**
	 * Helper method for <code>evaluateDot(Tree&ltToken&gt)</code>.
	 * Takes the name of a Bug, finds the Bug in the Intrepreter's list of Bugs and returns a reference to it.
	 * @param name - the name of the desired Bug
	 * @return currentBug - the desired Bug. null if the Bug is not in the Intrepreter's list.
	 */
	private Bug findOtherBugFromName(String name){
		for (int s = 0; s < interpreter.getBugs().size(); s++){
			Bug currentBug = interpreter.getBugs().get(s);
			if (currentBug.getBugName().equals(name))
				return currentBug;
		}
		return null;
	}
	
	/**
	 * Helper method for <code>evaluate(Tree&ltToken&gt)</code>. Determines whether value in given tree represents a number.
	 * @param tree
	 * @return isNumber
	 */
    private static boolean isNumber(Tree<Token> tree){
    	boolean isNumber = true;
    	String currentVal = tree.getValue().value;
        char chars[] = currentVal.toCharArray();
        for (char c : chars){
        	if (!Character.isDigit(c) && !(c == '.'))
        		isNumber = false;
        }
        return isNumber;
    }
    
    /**
     * Checks to see if the number is "true-ish" (package visibility for unit testing)
     * @param number
     * @return false if 0.001 > number > -0.001, true otherwise
     */
    static boolean isTrue(double number){
    	if (number > 0.001 || number < -0.001){
    		return true;
    	}
    	return false;
    }	
    
    /**
     * Sets this bug's blocked variable.
     * @param b - T/F
     */
    public void setBlocked(boolean b) { 
    	blocked = b; 
    }
    
   /**
    * Returns true if this bug is currently blocked, false otherwise.
    * @return
    */
    public boolean isBlocked() { 
    	return blocked; 
    }
    
    /**
     * Returns the angle of this Bug.
     * @return angle
     */
    public double getAngle(){
    	return angle;
    }
    
    /**
     * Sets this Bug's die variable to true to inform it of a reset request.
     */
    synchronized public void kill(){
    	die = true;
    }
    
//METHODS FOR UNIT TESTING  
	/**
	 * Returns the tree representing the function found under the key provided by the user
	 * @param key
	 */
	public Tree<Token> fetchFunctions(String key){
		if (functions.get(key) != null)
			return functions.get(key);
		else
			throw new RuntimeException("There is not a function associated with the provided name.");
	}
    
	/**
     * Pushes a HashMap<Sting, Double> on the top of scopes. Used for unit testing store/fetch.
     * @param table
     */
    public void addTableToScopes(HashMap<String, Double> table){
    	scopes.push(table);
    }
    
    /**
     * Pops the top HashMap from scopes. Used for unit testing.
     */
    public void popTableFromScopes(){
    	scopes.pop();
    }
	
    /**
	 * Getter method for loopExitStack (for unit testing only) (package visibility for unit testing)
	 * @return loopExitStack
	 */
	Stack<Boolean> getLoopExitStack(){
		return loopExitStack;
	}
	
	/**
	 * Setter method for loopExitStack (for unit testing only). (package visibility for unit testing)
	 * Pushes the provided value on the top of this loopExitStack.
	 */
	void pushLoopExitStack(boolean value){
		if (value == true)
			loopExitStack.push(true);
		else 
			loopExitStack.push(false);
	}
	
    /**
     * Setter method to set interpreter. Used for unit testing.
     * @param interpreter
     */
    public void setInterpreter(Interpreter interpreter){
    	this.interpreter = interpreter;
    }
    
    /**
     * Getter function for this bug's variables HashMap. Used for unit testing.
     * @return variables
     */
    public HashMap<String, Double> getVariables(){
    	return variables;
    }
    
    /**
     * Getter function for returnValue. Used for unit testing.
     * @return
     */
    public double getReturnValue(){
    	return returnValue;
    }
    
    /**
     * Setter function for Bug's position. Used for unit testing.
     * @param x
     * @param y
     */
    public void setPosition(double x, double y){
    	this.x = x;
    	this.y = y;
    }
    
    /**
     * Setter method for Bug's angle. Used for unit testing.
     * @param angle
     */
    public void setAngle(double angle){
    	this.angle = angle;
    }
}
