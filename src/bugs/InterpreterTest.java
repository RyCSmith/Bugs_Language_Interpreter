package bugs;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import tree.Tree;

/**
 * Tests for Interpreter class of Bugs language Interpreter.
 * @author Ryan Smith
 * @version March 2015
 *
 */
public class InterpreterTest {

	Bug testBug;
	Parser parser;
	Interpreter interpreter;
	
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

	//INTERPRETER TEST METHODS
	//*for proper use, private methods will need access changed to protected
    @Test
    public void testConstructorInterpreter(){
    	//will test to make sure interpreter in setUp() executed correctly
    	assertEquals(2, interpreter.getBugs().size());
    	assertEquals("Sally", interpreter.getBugs().get(0).getBugName());
    	assertEquals("henry", interpreter.getBugs().get(1).getBugName());
    	assertEquals("Not equal", 0.0, interpreter.getABVariables().get("abc"), 0.001);
    	assertEquals(interpreter.getABFunctions().get("forward"), tree("function", "forward", 
				tree("var", "n"), tree("block", tree("move", "n"), tree("return", tree("-", "n")))));
    }
    @Test
    public void testInterpretInterpreter(){
    	interpreter = new Interpreter();
    	parser = new Parser("Allbugs{\nvar foo, footoo, footee\nvar moo, mootoo, mootee\nvar coo, cootoo, cootee\ndefine foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isAllbugsCode());
        Tree<Token> tree = parser.stack.pop();
        assertNull(interpreter.getABVariables().get("foo"));
        assertNull(interpreter.getABFunctions().get("foo"));
        interpreter.interpret(tree);
        assertEquals("Not equal", 0.0, interpreter.getABVariables().get("foo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("footoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("footee"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("moo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("mootoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("mootee"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("coo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("cootoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("cootee"), 0.001);
        assertEquals(interpreter.getABFunctions().get("foo"), tree("function", "foo", 
				tree("var", "fooone", "footoo"), 
				tree("block", tree("move", tree(">", "3.0", "3.0")))));
    }
    @Test
    public void testAllbugsInterpreter(){
    	interpreter = new Interpreter();
    	parser = new Parser("Allbugs{\nvar foo, footoo, footee\nvar moo, mootoo, mootee\nvar coo, cootoo, cootee\ndefine foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isAllbugsCode());
        Tree<Token> tree = parser.stack.pop();
        assertNull(interpreter.getABVariables().get("foo"));
        assertNull(interpreter.getABFunctions().get("foo"));
        interpreter.interpretAllbugs(tree);
        assertEquals("Not equal", 0.0, interpreter.getABVariables().get("foo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("footoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("footee"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("moo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("mootoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("mootee"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("coo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("cootoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("cootee"), 0.001);
        assertEquals(interpreter.getABFunctions().get("foo"), tree("function", "foo", 
				tree("var", "fooone", "footoo"), 
				tree("block", tree("move", tree(">", "3.0", "3.0")))));
    }
    @Test
    public void testListInterpreter(){
    	interpreter = new Interpreter();
    	parser = new Parser("Allbugs{\nvar foo, footoo, footee\nvar moo, mootoo, mootee\nvar coo, cootoo, cootee\ndefine foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isAllbugsCode());
        Tree<Token> tree = parser.stack.pop();
        Tree<Token> list1 = tree.getChild(0);
        Tree<Token> list2 = tree.getChild(1);
        //list1 = list of var declarations at this point
        assertNull(interpreter.getABVariables().get("foo"));
        interpreter.interpretList(list1);
        assertEquals("Not equal", 0.0, interpreter.getABVariables().get("foo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("footoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("footee"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("moo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("mootoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("mootee"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("coo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("cootoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("cootee"), 0.001);
		
		//list2 = list of var declarations at this point
        assertNull(interpreter.getABFunctions().get("foo"));
        interpreter.interpretList(list2);
        assertEquals(interpreter.getABFunctions().get("foo"), tree("function", "foo", 
				tree("var", "fooone", "footoo"), 
				tree("block", tree("move", tree(">", "3.0", "3.0")))));
        
    }
    @Test
    public void testFunctionInterpreter(){
    	interpreter = new Interpreter();
    	parser = new Parser("define foo using fooone, footoo {\nmove 3 > 3\n}\n");
        assertTrue(parser.isFunctionDefinition());
        Tree<Token> tree = parser.stack.pop();
        assertNull(interpreter.getABFunctions().get("foo"));
        interpreter.interpretFunction(tree);
        assertEquals(interpreter.getABFunctions().get("foo"), tree("function", "foo", 
        										tree("var", "fooone", "footoo"), 
        										tree("block", tree("move", tree(">", "3.0", "3.0")))));
    }
    @Test
    public void testStoreInterpreter(){
    	//left the setUp() method interpreter in for store test
    	interpreter.store("a", 2.3);
    	interpreter.store("b", 3.4);
    	interpreter.store("c", 3.5);
    	assertEquals("Not equal", 2.3, interpreter.getABVariables().get("a"), 0.001);
    	assertEquals("Not equal", 3.4, interpreter.getABVariables().get("b"), 0.001);
    	assertEquals("Not equal", 3.5, interpreter.getABVariables().get("c"), 0.001);
    	//"abc" should be in there from making the Interpreter
    	assertEquals("Not equal", 0.0, interpreter.getABVariables().get("abc"), 0.001);
    	//update "abc"
    	interpreter.store("abc", 3.5);
    	assertEquals("Not equal", 3.5, interpreter.getABVariables().get("abc"), 0.001);
    }
    @Test
    public void testVarInterpreter(){
    	interpreter = new Interpreter();
    	parser = new Parser("var foo, footoo, footee\n");
        assertTrue(parser.isVarDeclaration());
        Tree<Token> tree = parser.stack.pop();
        assertNull(interpreter.getABVariables().get("foo"));
        interpreter.interpretVar(tree);
        assertEquals("Not equal", 0.0, interpreter.getABVariables().get("foo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("footoo"), 0.001);
		assertEquals("Not equal", 0.0, interpreter.getABVariables().get("footee"), 0.001);
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
