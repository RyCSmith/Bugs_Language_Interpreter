package bugs;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import tree.*;

/**
 * Unit tests for <code>Bug.java</code>
 * @author Ryan Smith
 * @version March 2015
 */
public class BugTest {
	
	Bug testBug;
	Parser parser;
	Interpreter interpreter;
	
	/**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    	Parser p = new Parser(
                "// Example Bugs program by David Matuszek\n" + 
		"/* Nonsense program to test out the recognizer */\n" + 
		"Allbugs {\n" + 
		"    var abc\n" + 
		"   \n" + 
		"    define forward using n {\n" + 
		"        move n // random pointless comment \n" + 
		"        return -n\n" + 
		"    }\n" + 
		"    define abc123 {\n" + 
		"        abc = 123\n" + 
		"    }\n" + 
		"}\n" + 
		"\n" + 
		"Bug Sally {\n" + 
		"    var a, b, c\n" + 
		"    var x, y\n" + 
		"    \n" + 
		"    initially {\n" + 
		"        x = -50\n" + 
		"        color red\n" + 
		"        line 0, 0, 25.3, 100/3\n" + 
		"    }\n" + 
		"    \n" + 
		"    y = 2 + 3 * a - b / c\n" + 
		"    y = ((2+3)*a)-(b/c)\n" + 
		"    loop{\n" + 
		"        y = y / 2.0\n" + 
		"        exit if y<=0.5\n" + 
		"    }\n" + 
		"    switch {\n" + 
		"    }\n" + 
		"    switch {\n" + 
		"        case x < y\n" + 
		"            moveto 3, x+y\n" + 
		"            turn x-y\n" + 
		"        case a <= x < y = z !=a >= b > c\n" + 
		"            turnto -abc123() + forward(x)\n" + 
		"    }\n" + 
		"    do forward(a)\n" + 
		"}\n" + 
		"Bug henry {\n" + 
		"    x = Sally.x\n" + 
		"    y = -Sally.y + 100\n" + 
		"}\n");
    	assertTrue(p.isProgram());
    	interpreter = new Interpreter(p.stack.pop());
    	testBug = new Bug();
    	testBug.setInterpreter(interpreter);
    }
    
//BUG CLASS TEST METHODS
//*for proper use, all getWorkPermit/completeCurrentTask/terminateBug lines need to commented out
    
//EVALUATE test methods
	@Test
	public void testEvaluateAdd(){
		parser = new Parser("(2 + 2)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 4.0, testBug.evaluate(tree), 0.001);
		
		testBug.getVariables().put("a", 5.4);
		testBug.getVariables().put("b", 3.4);
		parser = new Parser("(a + b)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 8.8, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateSubtract(){
		parser = new Parser("(2 - 3)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", -1.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateMultiply(){
		parser = new Parser("(2 * 3)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 6.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateDivide(){
		
		parser = new Parser("(6 / 3)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 2.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateLessThan(){
		parser = new Parser("(2 < 3)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);

		parser = new Parser("(3 < 2)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 0.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateLessThanEqualTo(){
		parser = new Parser("(2 <= 3)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(3 <= 2)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 0.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(3 <= 3)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateEquals(){
		parser = new Parser("(2 = 3)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 0.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(3 = 3)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(2.9999999 = 3)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(3 = 2.9999999)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateNotEquals(){
		parser = new Parser("(2 != 3)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(3 != 3)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 0.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(2.9999999 != 3)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 0.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(3 != 2.9999999)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 0.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateGreaterThan(){
		parser = new Parser("(2 > 3)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 0.0, testBug.evaluate(tree), 0.001);
	
		parser = new Parser("(3 > 2)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateGreaterThanEqualTo(){
		parser = new Parser("(2 >= 3)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 0.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(3 >= 2)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
		
		parser = new Parser("(3 >= 3)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateFullExpression(){
		parser = new Parser("12 * 5 - 3 * 4 / 6 + 8");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 66.0, testBug.evaluate(tree), 0.001);
	}
	@Test
	public void testEvaluateCase(){
		parser = new Parser("switch { \n case 0+0\nmoveto 1, 2 \ncase 3+4\nmoveto 15, 30 \n}\n");
        assertTrue(parser.isSwitchStatement());
        Tree<Token> tree = parser.stack.pop();
        Tree<Token> case1 = tree.getChild(0);
        assertEquals("Not equal", 0.0, testBug.evaluate(case1), 0.001);
        
        Tree<Token> case2 = tree.getChild(1);
        assertEquals("Not equal", 7.0, testBug.evaluate(case2), 0.001);
	}
	@Test
	public void testEvaluateCall(){
		//testing using "forward" function in Allbugs code from setUp()
		parser = new Parser("forward(5)");
        assertTrue(parser.isFunctionCall());
        Tree<Token> tree = parser.stack.pop();
        assertEquals("Not equal", -5.0, testBug.evaluate(tree), 0.001);
        assertEquals("Not equal", 5.0, testBug.fetch("x"), 0.001);
        
        //testing using new function inserted into bug's functions HashMap with call to interpretFunction
        parser = new Parser("define mult using a, b{\na = a * b\nreturn a\n}\n");
        assertTrue(parser.isFunctionDefinition());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        
        parser = new Parser("mult(4, 6)");
        assertTrue(parser.isFunctionCall());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        assertEquals("Not equal", 24.0, testBug.evaluate(tree), 0.001);
        
        //now using new function as an expression to update allbugs variable "abc"
        parser = new Parser("abc = mult(4, 6)\n");
        assertTrue(parser.isAssignmentStatement());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        assertEquals("Not equal", 24.0, testBug.fetch("abc"), 0.001);
        
        //define new function that has return statement in the middle
        parser = new Parser("define adder using a, b{\na = a + b\nreturn a\na = a / 2\n}\n");
        assertTrue(parser.isFunctionDefinition());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        
        parser = new Parser("abc = adder(4, 6)\n");
        assertTrue(parser.isAssignmentStatement());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        assertEquals("Not equal", 10.0, testBug.fetch("abc"), 0.001);
        
        //checking use of do statement
        parser = new Parser("do forward(12)\n");
        assertTrue(parser.isDoStatement());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        assertEquals("Not equal", 17.0, testBug.fetch("x"), 0.001);
        
        parser = new Parser("do adder(4, 6)\n");
        assertTrue(parser.isDoStatement());
        tree = parser.stack.pop();
        assertEquals("Not equal", 10.0, testBug.evaluate(tree), 0.001);
	}
	@Test public void testEvaluateDot(){
		Bug testBug2 = new Bug();
		testBug2.setBugName("testBug2");
		testBug2.setPosition(5, 0);
		testBug2.setInterpreter(interpreter);
		Bug testBug3 = new Bug();
		testBug3.setBugName("testBug3");
		testBug3.setPosition(0, -5);
		testBug3.setInterpreter(interpreter);
		Bug testBug4 = new Bug();
		testBug4.setBugName("testBug4");
		testBug4.setPosition(-5, 0);
		testBug4.setAngle(100);
		testBug4.setInterpreter(interpreter);
		interpreter.getBugs().add(testBug2);
		interpreter.getBugs().add(testBug3);
		interpreter.getBugs().add(testBug4);
		testBug2.getVariables().put("a", 7.0);
		testBug2.getVariables().put("b", 14.0);
		testBug2.getVariables().put("c", 21.0);
		testBug3.getVariables().put("a", 2.0);
		testBug3.getVariables().put("b", 22.0);
		testBug3.getVariables().put("c", 222.0);
		testBug4.getVariables().put("a", 5.0);
		testBug4.getVariables().put("b", 15.0);
		testBug4.getVariables().put("c", 25.0);
		
		//testing with single Bug value in an expression
		parser = new Parser("(2 < testBug2.x)");
		assertTrue(parser.isExpression());
		Tree<Token> tree = parser.stack.pop();
		assertEquals("Not equal", 1.0, testBug.evaluate(tree), 0.001);
		parser = new Parser("(2 > testBug2.x)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 0.0, testBug.evaluate(tree), 0.001);
		
		//testing chaining multiple Bugs 
		parser = new Parser("(testBug3.y + testBug2.x + testBug4.angle)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 100.0, testBug.evaluate(tree), 0.001);
		
		//testing with values in variable HashMap
		parser = new Parser("(testBug3.a + testBug2.a + testBug4.a)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", 14.0, testBug.evaluate(tree), 0.001);
		
		//testing with multiple operations and variable from main testBug inserted
		parser = new Parser("(testBug3.a + testBug2.a + testBug4.a - x - testBug3.b - testBug4.c)");
		assertTrue(parser.isExpression());
		tree = parser.stack.pop();
		assertEquals("Not equal", -33.0, testBug.evaluate(tree), 0.001);  
	}

//INTERPRET test methods

	@Test
	public void testInterpretMoveTo(){
		parser = new Parser("moveto 3 <= 3, 3 <= 3\n");
        assertTrue(parser.isMoveToAction());
        Tree<Token> tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 0.0, testBug.fetch("y"), 0.001);
        assertTrue(interpreter.getLines().size() == 0);
        testBug.interpret(tree);
        assertEquals("Not equal", 1.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 1.0, testBug.fetch("y"), 0.001);
        //testing to show that a line is added in the Interpreter's lines ArrayList to reflect the moveto
        assertTrue(interpreter.getLines().size() == 1);
        Command command = interpreter.getLines().get(0);
        assertEquals("Not equal", 0.0, command.getX1(), 0.001);
        assertEquals("Not equal", 0.0, command.getY1(), 0.001);
        assertEquals("Not equal", 1.0, command.getX2(), 0.001);
        assertEquals("Not equal", 1.0, command.getY2(), 0.001);
        
        parser = new Parser("moveto 15, 30\n");
        assertTrue(parser.isMoveToAction());
        tree = parser.stack.pop();
        assertTrue(interpreter.getLines().size() == 1);
        testBug.interpret(tree);
        assertEquals("Not equal", 15.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 30.0, testBug.fetch("y"), 0.001);
        assertTrue(interpreter.getLines().size() == 2);
        command = interpreter.getLines().get(1);
        assertEquals("Not equal", 1.0, command.getX1(), 0.001);
        assertEquals("Not equal", 1.0, command.getY1(), 0.001);
        assertEquals("Not equal", 15.0, command.getX2(), 0.001);
        assertEquals("Not equal", 30.0, command.getY2(), 0.001);
	}
	@Test
	public void testInterpretTurn(){
		parser = new Parser("turn 3 <= 3\n");
        assertTrue(parser.isTurnAction());
        Tree<Token> tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("angle"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 1.0, testBug.fetch("angle"), 0.001);
        
        parser = new Parser("turn 150\n");
        assertTrue(parser.isTurnAction());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        assertEquals("Not equal", 151.0, testBug.fetch("angle"), 0.001);
        
        parser = new Parser("turn 300\n");
        assertTrue(parser.isTurnAction());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        assertEquals("Not equal", 91.0, testBug.fetch("angle"), 0.001);
	}
	@Test
	public void testInterpretTurnTo(){
		parser = new Parser("turnto 3 <= 3\n");
        assertTrue(parser.isTurnToAction());
        Tree<Token> tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("angle"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 1.0, testBug.fetch("angle"), 0.001);
        
        parser = new Parser("turnto 150\n");
        assertTrue(parser.isTurnToAction());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        assertEquals("Not equal", 150.0, testBug.fetch("angle"), 0.001);
        
        parser = new Parser("turnto 400\n");
        assertTrue(parser.isTurnToAction());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        assertEquals("Not equal", 40.0, testBug.fetch("angle"), 0.001);
	}
	@Test
	public void testInterpretAssign(){
		parser = new Parser("foo = 3 <= 3\n");
        assertTrue(parser.isAssignmentStatement());
        Tree<Token> tree = parser.stack.pop();
        testBug.getVariables().put("foo", 5.0);
        assertEquals("Not equal", 5.0, testBug.fetch("foo"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 1.0, testBug.fetch("foo"), 0.001);
        
		parser = new Parser("foo = 300\n");
        assertTrue(parser.isAssignmentStatement());
        tree = parser.stack.pop();
        testBug.interpret(tree);
        assertEquals("Not equal", 300.0, testBug.fetch("foo"), 0.001);
        
        parser = new Parser("bugName = 300\n");
        assertTrue(parser.isAssignmentStatement());
        tree = parser.stack.pop();
        try{
        	testBug.interpret(tree);
        	fail();
        }
        catch (RuntimeException e){	
        }
	}
	@Test
	public void testInterpretColor(){
		parser = new Parser("color red\n");
        assertTrue(parser.isColorStatement());
        Tree<Token> tree = parser.stack.pop();
        assertEquals(Color.BLACK, testBug.getColor());
        testBug.interpret(tree);
        assertEquals(Color.RED, testBug.getColor());
	}
	@Test
	public void testInterpretFunction(){
		parser = new Parser("define foo using fooone, footoo {\nmove 3 > 3\n}\n");
        assertTrue(parser.isFunctionDefinition());
        Tree<Token> tree = parser.stack.pop();
        try{
        	testBug.fetchFunctions("foo");
        	fail();
        }
        catch(RuntimeException e){      	
        }
        testBug.interpret(tree);
        assertEquals(testBug.fetchFunctions("foo"), tree("function", "foo", 
        										tree("var", "fooone", "footoo"), 
        										tree("block", tree("move", tree(">", "3.0", "3.0")))));
	}
	@Test
	public void testInterpretMove(){
		parser = new Parser("move 10\n");
        assertTrue(parser.isMoveAction());
        Tree<Token> tree = parser.stack.pop();
        assertTrue(interpreter.getLines().size() == 0);
        testBug.store("x", 10.0);
        testBug.store("y", 10.0);
        testBug.interpret(tree);
        assertEquals("Not equal", 20.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 10.0, testBug.fetch("y"), 0.001);
        //testing to show that a line is added in the Interpreter's lines ArrayList to reflect the move
        assertTrue(interpreter.getLines().size() == 1);
        Command command = interpreter.getLines().get(0);
        assertEquals("Not equal", 10.0, command.getX1(), 0.001);
        assertEquals("Not equal", 10.0, command.getY1(), 0.001);
        assertEquals("Not equal", 20.0, command.getX2(), 0.001);
        assertEquals("Not equal", 10.0, command.getY2(), 0.001);
        
        testBug.store("x", 10.0);
        testBug.store("y", 10.0);
        testBug.store("angle", 90.0);
        assertTrue(interpreter.getLines().size() == 1);
        testBug.interpret(tree);
        assertEquals("Not equal", 10.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 0.0, testBug.fetch("y"), 0.001);
        //checking to see that second line is created.
        assertTrue(interpreter.getLines().size() == 2);
        command = interpreter.getLines().get(1);
        assertEquals("Not equal", 10.0, command.getX1(), 0.001);
        assertEquals("Not equal", 10.0, command.getY1(), 0.001);
        assertEquals("Not equal", 10.0, command.getX2(), 0.001);
        assertEquals("Not equal", 0.0, command.getY2(), 0.001);
        
        testBug.store("x", 10.0);
        testBug.store("y", 10.0);
        testBug.store("angle", 180.0);
        testBug.interpret(tree);
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 10.0, testBug.fetch("y"), 0.001);
        
        testBug.store("x", 10.0);
        testBug.store("y", 10.0);
        testBug.store("angle", 270.0);
        testBug.interpret(tree);
        assertEquals("Not equal", 10.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 20.0, testBug.fetch("y"), 0.001);
        
        testBug.store("x", 10.0);
        testBug.store("y", 10.0);
        testBug.store("angle", 360.0);
        testBug.interpret(tree);
        assertEquals("Not equal", 20.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 10.0, testBug.fetch("y"), 0.001);
	}
	@Test
	public void testInterpretList(){
		parser = new Parser("Allbugs{\nvar foo, footoo, footee\nvar moo, mootoo, mootee\nvar coo, cootoo, cootee\ndefine foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isAllbugsCode());
        Tree<Token> tree = parser.stack.pop();
        tree = tree.getChild(0);
        //tree = list of var declarations at this point
        try{
        	testBug.fetch("foo");
        	fail();
        }
        catch (RuntimeException e){
        }
        try{
        	testBug.fetch("moo");
        	fail();
        }
        catch (RuntimeException e){
        }
        try{
        	testBug.fetch("coo");
        	fail();
        }
        catch (RuntimeException e){
        }
        
        testBug.interpret(tree);
        assertEquals("Not equal", 0.0, testBug.fetch("foo"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("footoo"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("footee"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("moo"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("mootoo"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("mootee"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("coo"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("cootoo"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("cootee"), 0.001);
	}
	@Test
	public void testInterpretVar(){
		parser = new Parser("var foo, footoo, footee\n");
        assertTrue(parser.isVarDeclaration());
        Tree<Token> tree = parser.stack.pop();
        try{
        	testBug.fetch("foo");
        	fail();
        }
        catch (RuntimeException e){
        }
        try{
        	testBug.fetch("footoo");
        	fail();
        }
        catch (RuntimeException e){
        }
        
        testBug.interpret(tree);
        assertEquals("Not equal", 0.0, testBug.fetch("foo"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("footoo"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("footee"), 0.001);
	}
	@Test
	public void testInterpretBlock(){
		parser = new Parser("{\nmove 10\n}\n");
        assertTrue(parser.isBlock());
        Tree<Token> tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 10.0, testBug.fetch("x"), 0.001);
        
        testBug = new Bug();
        testBug.setInterpreter(interpreter);
		parser = new Parser("{\nmove 10\nmoveto 15, 30\n}\n");
        assertTrue(parser.isBlock());
        tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 15.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 30.0, testBug.fetch("y"), 0.001);        
        
        //the following shows that a block will stop executing commands once a return statement is encountered
        testBug = new Bug();
        testBug.setInterpreter(interpreter);
		parser = new Parser("{\nreturn 1\nmoveto 15, 30\n}\n");
        assertTrue(parser.isBlock());
        tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 0.0, testBug.fetch("y"), 0.001); 
	}
	@Test
	public void testInterpretReturn(){
		//demonstrates return statement leaving return value in returnValue variable
		testBug = new Bug();
		parser = new Parser("return 1\n");
        assertTrue(parser.isReturnStatement());
		Tree<Token> tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.getReturnValue(), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 1.0, testBug.getReturnValue(), 0.001);
        
        //shows return with expression
        parser = new Parser("return 1 > 2\n");
        assertTrue(parser.isReturnStatement());
		tree = parser.stack.pop();
		testBug.interpret(tree);
        assertEquals("Not equal", 0.0, testBug.getReturnValue(), 0.001);
	}
	@Test
	public void testInterpretInitially(){
		parser = new Parser("initially{\nmove 10\n}\n");
        assertTrue(parser.isInitializationBlock());
        Tree<Token> tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 10.0, testBug.fetch("x"), 0.001);
        
        testBug = new Bug();
		testBug.setInterpreter(interpreter);
        parser = new Parser("initially{\nmove 10\nmoveto 15, 30\n}\n");
        assertTrue(parser.isInitializationBlock());
        tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 15.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 30.0, testBug.fetch("y"), 0.001);
	}
	@Test
	public void testInterpretSwitch(){
		parser = new Parser("switch { \n case 3+4\nmoveto 15, 30 \n}\n");
        assertTrue(parser.isSwitchStatement());
        Tree<Token> tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 0.0, testBug.fetch("y"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 15.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 30.0, testBug.fetch("y"), 0.001);
        
        testBug = new Bug();
        testBug.setInterpreter(interpreter);
		parser = new Parser("switch { \n case 0+0\nmoveto 1, 2 \ncase 3+4\nmoveto 15, 30 \n}\n");
        assertTrue(parser.isSwitchStatement());
        tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 0.0, testBug.fetch("y"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 15.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 30.0, testBug.fetch("y"), 0.001);
        
        testBug = new Bug();
        testBug.setInterpreter(interpreter);
		parser = new Parser("switch { \n case 1+0\nmoveto 1, 2 \ncase 3+4\nmoveto 15, 30 \n}\n");
        assertTrue(parser.isSwitchStatement());
        tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 0.0, testBug.fetch("y"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 1.0, testBug.fetch("x"), 0.001);
        assertEquals("Not equal", 2.0, testBug.fetch("y"), 0.001);
	}
	@Test
	public void testInterpretExit(){
		parser = new Parser("exit if 3 <= 3\n");
        assertTrue(parser.isExitIfStatement());
        Tree<Token> tree = parser.stack.pop();
        assertTrue(testBug.getLoopExitStack().empty());
        testBug.pushLoopExitStack(false); //simulate a loop adding a false value
        assertFalse(testBug.getLoopExitStack().empty());
        testBug.interpret(tree);
        assertEquals(true, testBug.getLoopExitStack().pop());
	}
	@Test
	public void testInterpretLoop(){
		parser = new Parser("var foo\n");
        assertTrue(parser.isVarDeclaration());
        Tree<Token> tree = parser.stack.pop();
        testBug.interpret(tree);
        parser = new Parser("loop{\nfoo = foo + 1\nexit if foo > 3\n}\n");
        assertTrue(parser.isLoopStatement());
        tree = parser.stack.pop();
        assertEquals("Not equal", 0.0, testBug.fetch("foo"), 0.001);
        testBug.interpret(tree);
        assertEquals("Not equal", 4.0, testBug.fetch("foo"), 0.001);
	}
	@Test
	public void testInterpretBug(){
		parser = new Parser("Bug foo{\nvar foo\ninitially{\nmove 3 > 3\n}\nline 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\ndefine foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isBugDefinition());
        Tree<Token> tree = parser.stack.pop();
        try{
        	testBug.fetch("foo");
        	fail();
        }
        catch (RuntimeException e){
        }
        assertNull(testBug.getBugName());
        testBug.interpret(tree);
        assertEquals("foo", testBug.getBugName());
        assertEquals("Not equal", 0.0, testBug.fetch("foo"), 0.001);     
	}
	@Test
	public void testInterpretLine(){
		parser = new Parser("line 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\n");
        assertTrue(parser.isLineAction());
        Tree<Token> tree = parser.stack.pop();
        assertTrue(interpreter.getLines().size() == 0);
        testBug.interpret(tree);
        assertTrue(interpreter.getLines().size() == 1);
        Command command = interpreter.getLines().get(0);
        assertEquals("Not equal", 1.0, command.getX1(), 0.001);
        assertEquals("Not equal", 1.0, command.getX2(), 0.001);
        assertEquals("Not equal", 1.0, command.getY1(), 0.001);
        assertEquals("Not equal", 1.0, command.getY2(), 0.001);
        
        parser = new Parser("line 2, 4, -4, -2\n");
        assertTrue(parser.isLineAction());
        tree = parser.stack.pop();
        assertTrue(interpreter.getLines().size() == 1);
        testBug.interpret(tree);
        assertTrue(interpreter.getLines().size() == 2);
        command = interpreter.getLines().get(1);
        assertEquals("Not equal", 2.0, command.getX1(), 0.001);
        assertEquals("Not equal", -4.0, command.getX2(), 0.001);
        assertEquals("Not equal", 4.0, command.getY1(), 0.001);
        assertEquals("Not equal", -2.0, command.getY2(), 0.001);
        
	}
	
//OTHER test methods	
	@Test
	public void testDistance(){
		testBug.setPosition(0, 0);
		Bug testBug2 = new Bug();
		testBug2.setBugName("testBug2");
		testBug2.setPosition(5, 0);
		testBug2.setInterpreter(interpreter);
		Bug testBug3 = new Bug();
		testBug3.setBugName("testBug3");
		testBug3.setPosition(0, -5);
		testBug3.setInterpreter(interpreter);
		Bug testBug4 = new Bug();
		testBug4.setBugName("testBug4");
		testBug4.setPosition(-5, 0);
		testBug4.setInterpreter(interpreter);
		Bug testBug5 = new Bug();
		testBug5.setBugName("testBug5");
		testBug5.setPosition(0, 5);
		testBug5.setInterpreter(interpreter);
		Bug testBug6 = new Bug();
		testBug6.setBugName("testBug6");
		testBug6.setPosition(4, 2);
		testBug6.setInterpreter(interpreter);
		Bug testBug7 = new Bug();
		testBug7.setBugName("testBug7");
		testBug7.setPosition(4, -2);
		testBug7.setInterpreter(interpreter);
		Bug testBug8 = new Bug();
		testBug8.setBugName("testBug8");
		testBug8.setPosition(-4, -2);
		testBug8.setInterpreter(interpreter);
		Bug testBug9 = new Bug();
		testBug9.setBugName("testBug9");
		testBug9.setPosition(-4, 2);	
		testBug9.setInterpreter(interpreter);
		interpreter.getBugs().add(testBug2);
		interpreter.getBugs().add(testBug3);
		interpreter.getBugs().add(testBug4);
		interpreter.getBugs().add(testBug5);
		interpreter.getBugs().add(testBug6);
		interpreter.getBugs().add(testBug7);
		interpreter.getBugs().add(testBug8);
		interpreter.getBugs().add(testBug9);

		assertEquals("Not equal", 5.0, testBug.distance("testBug2"), 0.001);
		assertEquals("Not equal", 5.0, testBug.distance("testBug3"), 0.001);
		assertEquals("Not equal", 5.0, testBug.distance("testBug4"), 0.001);
		assertEquals("Not equal", 5.0, testBug.distance("testBug5"), 0.001);
		assertEquals("Not equal", 4.4721, testBug.distance("testBug6"), 0.001);
		assertEquals("Not equal", 4.4721, testBug.distance("testBug7"), 0.001);
		assertEquals("Not equal", 4.4721, testBug.distance("testBug8"), 0.001);
		assertEquals("Not equal", 4.4721, testBug.distance("testBug9"), 0.001);
	}
	@Test
	public void testDirection(){
		testBug.setPosition(0, 0);
		Bug testBug2 = new Bug();
		testBug2.setBugName("testBug2");
		testBug2.setPosition(5, 0);
		testBug2.setInterpreter(interpreter);
		Bug testBug3 = new Bug();
		testBug3.setBugName("testBug3");
		testBug3.setPosition(0, -5);
		testBug3.setInterpreter(interpreter);
		Bug testBug4 = new Bug();
		testBug4.setBugName("testBug4");
		testBug4.setPosition(-5, 0);
		testBug4.setInterpreter(interpreter);
		Bug testBug5 = new Bug();
		testBug5.setBugName("testBug5");
		testBug5.setPosition(0, 5);
		testBug5.setInterpreter(interpreter);
		Bug testBug6 = new Bug();
		testBug6.setBugName("testBug6");
		testBug6.setPosition(4, 2);
		testBug6.setInterpreter(interpreter);
		Bug testBug7 = new Bug();
		testBug7.setBugName("testBug7");
		testBug7.setPosition(4, -2);
		testBug7.setInterpreter(interpreter);
		Bug testBug8 = new Bug();
		testBug8.setBugName("testBug8");
		testBug8.setPosition(-4, -2);
		testBug8.setInterpreter(interpreter);
		Bug testBug9 = new Bug();
		testBug9.setBugName("testBug9");
		testBug9.setPosition(-4, 2);	
		testBug9.setInterpreter(interpreter);
		interpreter.getBugs().add(testBug2);
		interpreter.getBugs().add(testBug3);
		interpreter.getBugs().add(testBug4);
		interpreter.getBugs().add(testBug5);
		interpreter.getBugs().add(testBug6);
		interpreter.getBugs().add(testBug7);
		interpreter.getBugs().add(testBug8);
		interpreter.getBugs().add(testBug9);
		
		assertEquals("Not equal", 0.0, testBug.direction("testBug2"), 0.001);
		assertEquals("Not equal", 90.0, testBug.direction("testBug3"), 0.001);
		assertEquals("Not equal", 180.0, testBug.direction("testBug4"), 0.001);
		assertEquals("Not equal", 270.0, testBug.direction("testBug5"), 0.001);
		assertEquals("Not equal", 333.4349488, testBug.direction("testBug6"), 0.001);
		assertEquals("Not equal", 26.5650512, testBug.direction("testBug7"), 0.001);
		assertEquals("Not equal", 153.4349488, testBug.direction("testBug8"), 0.001);
		assertEquals("Not equal", 206.5650512, testBug.direction("testBug9"), 0.001);
	}
	
	@Test
	public void testStore() {
		try{
			assertNull(testBug.fetch("sally"));
			fail();
		}
		catch (RuntimeException e){
		}
		
		testBug.store("x", 1.2);
		assertEquals("Not equal", 1.2, testBug.fetch("x"), 0.001);
		testBug.store("y", 2.3);
		assertEquals("Not equal", 2.3, testBug.fetch("y"), 0.001);
		testBug.store("angle", 4.33232);
		assertEquals("Not equal", 4.33232, testBug.fetch("angle"), 0.001);
		//this tests fetch with variable in Allbugs HashMap
		assertEquals("Not equal", 0.0, testBug.fetch("abc"), 0.001);
		//testing with variable in Bug's variables HashMap
		testBug.getVariables().put("sally", 0.0);
		assertEquals("Not equal", 0.0, testBug.fetch("sally"), 0.001);
		testBug.store("sally", 45.6);
		assertEquals("Not equal", 45.6, testBug.fetch("sally"), 0.001);
		
		//adding additional maps to scopes
		HashMap<String, Double> func1 = new HashMap<String, Double>();
		HashMap<String, Double> func2 = new HashMap<String, Double>();
		testBug.addTableToScopes(func1);
		testBug.addTableToScopes(func2);
		func1.put("a", 0.0);
		func1.put("b", 0.0);
		//duplicating variable "a" in different scopes
		func2.put("a", 0.0);
		func2.put("c", 0.0);
		//duplicating variable from Allbugs variables with more local scope
		func2.put("abc", 0.0);
		
		testBug.store("abc", 43.0);
		testBug.store("b", 3.4);
		testBug.store("a", 3.2);
		testBug.store("sally", 32.0);
		assertEquals("Not equal", 3.2, testBug.fetch("a"), 0.001);
		assertEquals("Not equal", 3.4, testBug.fetch("b"), 0.001);
		assertEquals("Not equal", 43.0, testBug.fetch("abc"), 0.001);
		assertEquals("Not equal", 32.0, testBug.fetch("sally"), 0.001);
		testBug.popTableFromScopes();
		//"a" now refers to previous "a" and "abc" now refers to Allbugs variable "abc"
		assertEquals("Not equal", 0.0, testBug.fetch("a"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("abc"), 0.001);
		assertEquals("Not equal", 3.4, testBug.fetch("b"), 0.001);
		assertEquals("Not equal", 32.0, testBug.fetch("sally"), 0.001);
		try{
			testBug.store("abcdefg", 15.0);
			fail();
		}
		catch(RuntimeException e){
		}
	}
	
	@Test
	public void testFetch() {
		assertEquals("Not equal", 0.0, testBug.fetch("x"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("y"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("angle"), 0.001);
		testBug.getVariables().put("sally", 5.4);
		assertEquals("Not equal", 5.4, testBug.fetch("sally"), 0.001);
		
		//this tests fetch with variables in function HashMaps
		HashMap<String, Double> func1 = new HashMap<String, Double>();
		HashMap<String, Double> func2 = new HashMap<String, Double>();
		testBug.addTableToScopes(func1);
		testBug.addTableToScopes(func2);
		func1.put("a", 2.3);
		func1.put("b", 3.0);
		//duplicating variable "a" in different scopes
		func2.put("a", 1.0);
		func2.put("c", 2.5);
		//duplicating variable from Allbugs variables with more local scope
		func2.put("abc", 45.0);
		assertEquals("Not equal", 3.0, testBug.fetch("b"), 0.001);
		assertEquals("Not equal", 2.5, testBug.fetch("c"), 0.001);
		assertEquals("Not equal", 1.0, testBug.fetch("a"), 0.001);
		assertEquals("Not equal", 45.0, testBug.fetch("abc"), 0.001);
		testBug.popTableFromScopes();
		assertEquals("Not equal", 2.3, testBug.fetch("a"), 0.001);
		assertEquals("Not equal", 0.0, testBug.fetch("abc"), 0.001);
		testBug.popTableFromScopes();
		try{
			assertEquals("Not equal", 3.0, testBug.fetch("b"), 0.001);
			fail();
		}
		catch(RuntimeException e){
		}
	}
	
	@Test
	public void testIsTrue(){
		assertTrue(Bug.isTrue(2));
		assertFalse(Bug.isTrue(0.001));
		assertFalse(Bug.isTrue(-0.001));
		assertTrue(Bug.isTrue(-1));
	}

//HELPER methods
    /**
     * Returns a Tree node consisting of a single leaf; the
     * node will contain a Token with a String as its value. <br>
     * Given a Tree, return the same Tree.<br>
     * Given a Token, return a Tree with the Token as its value.<br>
     * Given a String, make it into a Token, return a Tree
     * with the Token as its value.
     * 
     * @param value A Tree, Token, or String from which to
              construct the Tree node.
     * @return A Tree leaf node containing a Token whose value
     *         is the parameter.
     */
    private Tree<Token> createNode(Object value) {
        if (value instanceof Tree) {
            return (Tree) value;
        }
        if (value instanceof Token) {
            return new Tree<Token>((Token) value);
        }
        else if (value instanceof String) {
            return new Tree<Token>(new Token((String) value));
        }
        assert false: "Illegal argument: tree(" + value + ")";
        return null; 
    }
    /**
     * Builds a Tree that can be compared with the one the
     * Parser produces. Any String or Token arguments will be
     * converted to Tree nodes containing Tokens.
     * 
     * @param op The String value to use in the Token in the root.
     * @param children The objects to be made into children.
     * @return The resultant Tree.
     */
    private Tree<Token> tree(String op, Object... children) {
        Tree<Token> tree = new Tree<Token>(new Token(op));
        for (int i = 0; i < children.length; i++) {
            tree.addChild(createNode(children[i]));
        }
        return tree;
    }
}
