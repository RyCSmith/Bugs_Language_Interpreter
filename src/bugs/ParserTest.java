package bugs;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.EmptyStackException;

import org.junit.Before;
import org.junit.Test;

import tree.Tree;

/**
 * Unit tests for Parser.java.
 * 
 * @author Ryan Smith
 * @author Dave Matuszek
 * @version February 2015
 */
public class ParserTest {
    Parser parser;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testParser() {
        parser = new Parser("");
        parser = new Parser("2 + 2");
    }
    @Test
	public void testIsAction() {
    	use("line 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\n");
        assertTrue(parser.isAction());
        assertStackTopEquals(tree("line", tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0")));
        use("moveto 3 <= 3, 3 <= 3\n");
        assertTrue(parser.isAction());
        assertStackTopEquals(tree("moveto", tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0")));
        use("move 3 <= 3\n");
        assertTrue(parser.isAction());
        assertStackTopEquals(tree("move", tree("<=", "3.0", "3.0")));
        use("turn 3 <= 3\n");
        assertTrue(parser.isAction());
        assertStackTopEquals(tree("turn", tree("<=", "3.0", "3.0")));
        use("turnto 3 <= 3\n");
        assertTrue(parser.isAction());
        assertStackTopEquals(tree("turnto", tree("<=", "3.0", "3.0")));
        try {
            use("line 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isAction());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsAllbugsCode() {
		use("Allbugs{\nvar foo, footoo, footee\ndefine foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isAllbugsCode());
        assertStackTopEquals(tree("Allbugs", 
        								tree("list", tree("var", "foo", "footoo", "footee")),
        								tree("list", tree("function", "foo", 
        										tree("var", "fooone", "footoo"), 
        										tree("block", tree("move", tree(">", "3.0", "3.0")))))));
        
        use("Allbugs { \n var a , b , c \n } \n");
    	assertTrue(parser.isAllbugsCode());
    	assertStackTopEquals(tree("Allbugs",
    				tree("list", tree("var", "a", "b", "c")),
    				"list"));
    	
    	//this is the one that fails
    	use("Allbugs { \n var a , b , c \n define abc { \n move 3 \n turn 4 \n } \n } \n");
    	assertTrue(parser.isAllbugsCode());
    	assertStackTopEquals(tree("Allbugs",
    				 tree("list", tree("var", "a", "b", "c")),
    				 tree("list", tree("function", "abc", "var",
    				   tree("block", tree("move", "3.0"), tree("turn", "4.0"))))));
        try {
            use("foo = 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isAssignmentStatement());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsAssignmentStatement() {
		use("foo = 3 <= 3\n");
        assertTrue(parser.isAssignmentStatement());
        assertStackTopEquals(tree("assign", tree("foo"), tree("<=", "3.0", "3.0")));
        try {
            use("foo = 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isAssignmentStatement());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsBlock() {
		use("{\nmove 3 > 3\n}\n");
        assertTrue(parser.isBlock());
        assertStackTopEquals(tree("block", tree("move", tree(">", "3.0", "3.0"))));

        try {
            use("{block 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isBlock());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsBugDefinition() {
		use("Bug foo{\nvar foo\ninitially{\nmove 3 > 3\n}\nline 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\ndefine foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isBugDefinition());
        assertStackTopEquals(tree("Bug", 
        							"foo", 
        							tree("list", tree("var", "foo")), 
        							tree("initially", tree("block", tree("move", tree(">", "3.0", "3.0")))),
        							tree("block", tree("line", 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"))),
        							tree("list", tree("function", "foo", 
    														tree("var", "fooone", "footoo"), 
    														tree("block", tree("move", tree(">", "3.0", "3.0")))))));

        try {
            use("Bug foo {block 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isBugDefinition());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsColorStatement() {
		use("color switch\n");
        assertTrue(parser.isColorStatement());
        assertStackTopEquals(tree("color", tree("switch")));

        try {
            use("color 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isColorStatement());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsCommand() {
		use("line 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\n");
        assertTrue(parser.isCommand());
        assertStackTopEquals(tree("line", tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0")));
        
        use("switch { \n case 3+4\nmove 3 <= 3\n}\n");
        assertTrue(parser.isCommand());
        assertStackTopEquals(tree("switch",
        						tree("case",
        							tree("+", "3.0", "4.0"),
        							tree("block", tree("move", tree("<=", "3.0", "3.0"))))));
        try {
            use("line 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isCommand());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsComparator() {
        use("<=");
        assertTrue(parser.isComparator());
        assertStackTopEquals(tree("<="));
        
        use("=");
        assertTrue(parser.isComparator());
        assertStackTopEquals(tree("="));
        
        use("!=");
        assertTrue(parser.isComparator());
        assertStackTopEquals(tree("!="));
        
        use(">=");
        assertTrue(parser.isComparator());
        assertStackTopEquals(tree(">="));
	}

	@Test
	public void testIsDoStatement() {
		use("do name(foo, footoo, footee)\n");
        assertTrue(parser.isDoStatement());
        assertStackTopEquals(tree("call", "name",
        						tree("var", "foo", "footoo", "footee")));

        try {
            use("do 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isDoStatement());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsEol() {
		use("\n");
		assertTrue(parser.isEol());
		try{
			assertStackTopEquals(tree("foo"));
			fail();
		}
		catch (EmptyStackException e){
		}
		
	}

	@Test
	public void testIsExitIfStatement() {
		use("exit if 3 <= 3\n");
        assertTrue(parser.isExitIfStatement());
        assertStackTopEquals(tree("exit", tree("<=", "3.0", "3.0")));

        try {
            use("exit if 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isExitIfStatement());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsFunctionCall() {
		use("foo(fooone, footoo)");
        assertTrue(parser.isFunctionCall());
        assertStackTopEquals(tree("call", "foo", tree("var", "fooone", "footoo")));

        try {
            use("foo if 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isFunctionCall());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsFunctionDefinition() {
		use("define foo using fooone, footoo {\nmove 3 > 3\n}\n");
        assertTrue(parser.isFunctionDefinition());
        assertStackTopEquals(tree("function", "foo", 
        										tree("var", "fooone", "footoo"), 
        										tree("block", tree("move", tree(">", "3.0", "3.0")))));

        try {
            use("define foo if 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isFunctionDefinition());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsInitializationBlock() {
		use("initially{\nmove 3 > 3\n}\n");
        assertTrue(parser.isInitializationBlock());
        assertStackTopEquals(tree("initially", tree("block", tree("move", tree(">", "3.0", "3.0")))));

        try {
            use("initially func def");
            assertFalse(parser.isInitializationBlock());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsLineAction() {
		use("line 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\n");
        assertTrue(parser.isLineAction());
        assertStackTopEquals(tree("line", tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0")));

        try {
            use("line 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isLineAction());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsLoopStatement() {
		use("loop{\nmove 3 > 3\n}\n");
        assertTrue(parser.isLoopStatement());
        assertStackTopEquals(tree("loop", tree("block", tree("move", tree(">", "3.0", "3.0")))));

        try {
            use("loop block 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isLoopStatement());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsMoveToAction() {
		use("moveto 3 <= 3, 3 <= 3\n");
        assertTrue(parser.isMoveToAction());
        assertStackTopEquals(tree("moveto", tree("<=", "3.0", "3.0"), tree("<=", "3.0", "3.0")));

        try {
            use("moveto 17 +");
            assertFalse(parser.isMoveToAction());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsMoveAction() {
        use("move 3 <= 3\n");
        assertTrue(parser.isMoveAction());
        assertStackTopEquals(tree("move", tree("<=", "3.0", "3.0")));

        try {
            use("move 17 +");
            assertFalse(parser.isMoveAction());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsProgram() {
		use("Bug foo{\nvar foo\ninitially{\nmove 3 > 3\n}\nline 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\n"
				+ "define foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isProgram());
        assertStackTopEquals(tree("program", "Allbugs", tree("list", tree("Bug", "foo", 
        							tree("list", tree("var", "foo")), 
        							tree("initially", tree("block", tree("move", tree(">", "3.0", "3.0")))),
        							tree("block", tree("line", 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"))),
        											tree("list", tree("function", "foo", 
    														tree("var", "fooone", "footoo"), 
    														tree("block", tree("move", tree(">", "3.0", "3.0")))))))));
        
        use("Allbugs{\nvar foo, footoo, footee\ndefine foo using fooone, footoo {\nmove 3 > 3\n}\n}\n"
        		+ "Bug foo{\nvar foo\ninitially{\nmove 3 > 3\n}\nline 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\n"
        		+ "define foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isProgram());
        assertStackTopEquals(tree("program", tree("Allbugs", 
				tree("list", tree("var", "foo", "footoo", "footee")),
				tree("list", tree("function", "foo", 
						tree("var", "fooone", "footoo"), 
						tree("block", tree("move", tree(">", "3.0", "3.0")))))), 
						
						tree("list", tree("Bug", "foo", 
        							tree("list", tree("var", "foo")), 
        							tree("initially", tree("block", tree("move", tree(">", "3.0", "3.0")))),
        							tree("block", tree("line", 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"))),
        											tree("list", tree("function", "foo", 
    														tree("var", "fooone", "footoo"), 
    														tree("block", tree("move", tree(">", "3.0", "3.0")))))))));
        
        use("Allbugs{\nvar foo, footoo, footee\ndefine foo using fooone, footoo {\nmove 3 > 3\n}\n}\n"
        		+ "Bug foo{\nvar foo\ninitially{\nmove 3 > 3\n}\nline 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\n"
        		+ "define foo using fooone, footoo {\nmove 3 > 3\n}\n}\n"
        		+ "Bug foo{\nvar foo\ninitially{\nmove 3 > 3\n}\nline 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\n"
        		+ "define foo using fooone, footoo {\nmove 3 > 3\n}\n}\n"
        		+ "Bug foo{\nvar foo\ninitially{\nmove 3 > 3\n}\nline 3 <= 3, 3 <= 3, 3 <= 3, 3 <= 3\n"
        		+ "define foo using fooone, footoo {\nmove 3 > 3\n}\n}\n");
        assertTrue(parser.isProgram());
        assertStackTopEquals(tree("program", tree("Allbugs", 
				tree("list", tree("var", "foo", "footoo", "footee")),
				tree("list", tree("function", "foo", 
						tree("var", "fooone", "footoo"), 
						tree("block", tree("move", tree(">", "3.0", "3.0")))))), 
						
						tree("list", tree("Bug", "foo", 
        							tree("list", tree("var", "foo")), 
        							tree("initially", tree("block", tree("move", tree(">", "3.0", "3.0")))),
        							tree("block", tree("line", 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"), 
        											tree("<=", "3.0", "3.0"))),
        											tree("list", tree("function", "foo", 
    													tree("var", "fooone", "footoo"), 
    													tree("block", tree("move", tree(">", "3.0", "3.0")))))),
    								tree("Bug", "foo", 
    							  	tree("list", tree("var", "foo")), 
    							   	tree("initially", tree("block", tree("move", tree(">", "3.0", "3.0")))),
    							    tree("block", tree("line", 
    							       				tree("<=", "3.0", "3.0"), 
    							       				tree("<=", "3.0", "3.0"), 
    							       				tree("<=", "3.0", "3.0"), 
    							       				tree("<=", "3.0", "3.0"))),
    							        			tree("list", tree("function", "foo", 
    							    					tree("var", "fooone", "footoo"), 
    							    					tree("block", tree("move", tree(">", "3.0", "3.0")))))),
    							    tree("Bug", "foo", 
    							    tree("list", tree("var", "foo")), 
    							    tree("initially", tree("block", tree("move", tree(">", "3.0", "3.0")))),
    							    tree("block", tree("line", 
    							    				tree("<=", "3.0", "3.0"), 
    							    				tree("<=", "3.0", "3.0"), 
    							    				tree("<=", "3.0", "3.0"), 
    							    				tree("<=", "3.0", "3.0"))),
    							    				tree("list", tree("function", "foo", 
    							    					tree("var", "fooone", "footoo"), 
    							    					tree("block", tree("move", tree(">", "3.0", "3.0")))))))));
        try {
            use("Bug foo {block 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isProgram());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsReturnStatement() {
		use("return 3 <= 3\n");
        assertTrue(parser.isReturnStatement());
        assertStackTopEquals(tree("return", tree("<=", "3.0", "3.0")));

        try {
            use("return 17 +");
            assertFalse(parser.isReturnStatement());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsStatement() {
		use("loop{\nmove 3 > 3\n}\n");
        assertTrue(parser.isStatement());
        assertStackTopEquals(tree("loop", tree("block", tree("move", tree(">", "3.0", "3.0")))));
        
        use("exit if 3 <= 3\n");
        assertTrue(parser.isStatement());
        assertStackTopEquals(tree("exit", tree("<=", "3.0", "3.0")));
        
        use("switch { \n case 3+4\nmove 3 <= 3\n}\n");
        assertTrue(parser.isStatement());
        assertStackTopEquals(tree("switch",
        						tree("case",
        							tree("+", "3.0", "4.0"),
        							tree("block", tree("move", tree("<=", "3.0", "3.0"))))));
        
        use("return 3 <= 3\n");
        assertTrue(parser.isStatement());
        assertStackTopEquals(tree("return", tree("<=", "3.0", "3.0")));
        
		use("foo = 3 <= 3\n");
        assertTrue(parser.isStatement());
        assertStackTopEquals(tree("assign", tree("foo"), tree("<=", "3.0", "3.0")));
        
        use("do name(foo, footoo, footee)\n");
        assertTrue(parser.isDoStatement());
        assertStackTopEquals(tree("call", "name",
        						tree("var", "foo", "footoo", "footee")));
        
        use("color switch\n");
        assertTrue(parser.isStatement());
        assertStackTopEquals(tree("color", tree("switch")));
        try {
            use("foo = 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isStatement());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsSwitchStatement() {
		use("switch { \n case 3+4\nmove 3 <= 3\n}\n");
        assertTrue(parser.isSwitchStatement());
        assertStackTopEquals(tree("switch",
        						tree("case",
        							tree("+", "3.0", "4.0"),
        							tree("block", tree("move", tree("<=", "3.0", "3.0"))))));

        try {
            use("switch 3 <= 3, 3 <= 3, 3 <= 3\n");
            assertFalse(parser.isSwitchStatement());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsTurnAction() {        
        use("turn 3 <= 3\n");
        assertTrue(parser.isTurnAction());
        assertStackTopEquals(tree("turn", tree("<=", "3.0", "3.0")));

        try {
            use("turn 17 +");
            assertFalse(parser.isTurnAction());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsTurnToAction() {
        use("turnto 3 <= 3\n");
        assertTrue(parser.isTurnToAction());
        assertStackTopEquals(tree("turnto", tree("<=", "3.0", "3.0")));

        try {
            use("turnto 17 +");
            assertFalse(parser.isTurnToAction());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

	@Test
	public void testIsVarDeclaration() {
		use("var foo, footoo, footee\n");
        assertTrue(parser.isVarDeclaration());
        assertStackTopEquals(tree("var", "foo", "footoo", "footee"));

        try {
            use("var 17 +");
            assertFalse(parser.isVarDeclaration());
            fail();
        }
        catch (SyntaxException e) {
        }
	}

    @Test
    public void testIsExpression() {
        Tree<Token> expected;
        
        use("(3 <= 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("<=", "3.0", "3.0"));
        
        use("(3 = 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("=", "3.0", "3.0"));
        
        use("(3 != 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("!=", "3.0", "3.0"));
        
        use("(3 >= 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree(">=", "3.0", "3.0"));
        
        use("250");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createNode("250.0"));
        
        use("hello");
        assertTrue(parser.isExpression());
        assertStackTopEquals(createNode("hello"));

        use("(xyz + 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("+", "xyz", "3.0"));

        use("a + b + c");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("+", tree("+", "a", "b"), "c"));

        use("a * b * c");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("*", tree("*", "a", "b"), "c"));

        use("3 * 12.5 - 7");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("-", tree("*", "3.0", "12.5"), createNode("7.0")));

        use("12 * 5 - 3 * 4 / 6 + 8");
        assertTrue(parser.isExpression());
        expected = tree("+",
                      tree("-",
                         tree("*", "12.0", "5.0"),
                         tree("/",
                            tree("*", "3.0", "4.0"),
                            "6.0"
                           )
                        ),
                      "8.0"
                     );
        assertStackTopEquals(expected);
                     
        use("12 * ((5 - 3) * 4) / 6 + (8)");
        assertTrue(parser.isExpression());
        expected = tree("+",
                      tree("/",
                         tree("*",
                            "12.0",
                            tree("*",
                               tree("-","5.0","3.0"),
                               "4.0")),
                         "6.0"),
                      "8.0");
        assertStackTopEquals(expected);
        
        use("");
        assertFalse(parser.isExpression());
        
        use("#");
        assertFalse(parser.isExpression());

        try {
            use("17 +");
            assertFalse(parser.isExpression());
            fail();
        }
        catch (SyntaxException e) {
        }
        try {
            use("22 *");
            assertFalse(parser.isExpression());
            fail();
        }
        catch (SyntaxException e) {
        }
    }

    @Test
    public void testUnaryOperator() {       
        use("-250");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("-", "250.0"));
        
        use("+250");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("+", "250.0"));
        
        use("- hello");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("-", "hello"));

        use("-(xyz + 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("-", tree("+", "xyz", "3.0")));

        use("(-xyz + 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("+", tree("-", "xyz"), "3.0"));

        use("+(-xyz + 3)");
        assertTrue(parser.isExpression());
        assertStackTopEquals(tree("+",
                                        tree("+",
                                                   tree("-", "xyz"), "3.0")));
    }

    @Test
    public void testIsTerm() {        
        use("12");
        assertTrue(parser.isTerm());
        assertStackTopEquals(createNode("12.0"));
        
        use("12.5");
        assertTrue(parser.isTerm());
        assertStackTopEquals(createNode("12.5"));

        use("3*12");
        assertTrue(parser.isTerm());
        assertStackTopEquals(tree("*", "3.0", "12.0"));

        use("x * y * z");
        assertTrue(parser.isTerm());
        assertStackTopEquals(tree("*", tree("*", "x", "y"), "z"));
        
        use("20 * 3 / 4");
        assertTrue(parser.isTerm());
        assertEquals(tree("/", tree("*", "20.0", "3.0"), createNode("4.0")),
                     stackTop());

        use("20 * 3 / 4 + 5");
        assertTrue(parser.isTerm());
        assertEquals(tree("/", tree("*", "20.0", "3.0"), "4.0"),
                     stackTop());
        followedBy(parser, "+ 5");
        
        use("");
        assertFalse(parser.isTerm());
        followedBy(parser, "");
        
        use("#");
        assertFalse(parser.isTerm());followedBy(parser, "#");

    }

    @Test
    public void testIsFactor() {
        use("12");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createNode("12.0"));

        use("hello");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createNode("hello"));
        
        use("(xyz + 3)");
        assertTrue(parser.isFactor());
        assertStackTopEquals(tree("+", "xyz", "3.0"));
        
        use("12 * 5");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createNode("12.0"));
        followedBy(parser, "* 5.0");
        
        use("17 +");
        assertTrue(parser.isFactor());
        assertStackTopEquals(createNode("17.0"));
        followedBy(parser, "+");

        use("");
        assertFalse(parser.isFactor());
        followedBy(parser, "");
        
        use("#");
        assertFalse(parser.isFactor());
        followedBy(parser, "#");
    }

    @Test
    public void testIsFactor2() {
        use("hello.world");
        assertTrue(parser.isFactor());
        assertStackTopEquals(tree(".", "hello", "world"));
        
        use("foo(bar)");
        assertTrue(parser.isFactor());
        assertStackTopEquals(tree("call", "foo",
                                        tree("var", "bar")));
        
        use("foo(bar, baz)");
        assertTrue(parser.isFactor());
        assertStackTopEquals(tree("call", "foo",
                                        tree("var", "bar", "baz")));
        
        use("foo(2*(3+4))");
        assertTrue(parser.isFactor());
        assertStackTopEquals(tree("call", "foo",
                                 tree("var",
                                     tree("*", "2.0",
                                         tree("+", "3.0", "4.0")))));
    }

    @Test
    public void testIsAddOperator() {
        use("+ - + $");
        assertTrue(parser.isAddOperator());
        assertTrue(parser.isAddOperator());
        assertTrue(parser.isAddOperator());
        assertFalse(parser.isAddOperator());
        followedBy(parser, "$");
    }

    @Test
    public void testIsMultiplyOperator() {
        use("* / $");
        assertTrue(parser.isMultiplyOperator());
        assertTrue(parser.isMultiplyOperator());
        assertFalse(parser.isMultiplyOperator());
        followedBy(parser, "$");
    }

    @Test
    public void testNextToken() {
        use("12 12.5 bogus switch + \n");
        assertEquals(new Token(Token.Type.NUMBER, "12.0"), parser.nextToken());
        assertEquals(new Token(Token.Type.NUMBER, "12.5"), parser.nextToken());
        assertEquals(new Token(Token.Type.NAME, "bogus"), parser.nextToken());
        assertEquals(new Token(Token.Type.KEYWORD, "switch"), parser.nextToken());
        assertEquals(new Token(Token.Type.SYMBOL, "+"), parser.nextToken());
        assertEquals(new Token(Token.Type.EOL, "\n"), parser.nextToken());
        assertEquals(new Token(Token.Type.EOF, "EOF"), parser.nextToken());
    }
    
//  ----- "Helper" methods
    
    /**
     * Sets the <code>parser</code> instance to use the given string.
     * 
     * @param s The string to be parsed.
     */
    private void use(String s) {
        parser = new Parser(s);
    }
    
    /**
     * Returns the current top of the stack.
     *
     * @return The top of the stack.
     */
    private Object stackTop() {
        return parser.stack.peek();
    }
    
    /**
     * Tests whether the top element in the stack is correct.
     *
     * @return <code>true</code> if the top element of the stack is as expected.
     */
    private void assertStackTopEquals(Tree<Token> expected) {
        assertEquals(expected, stackTop());
    }
    
    /**
     * This method is given a String containing some or all of the
     * tokens that should yet be returned by the Tokenizer, and tests
     * whether the Tokenizer in fact has those Tokens. To succeed,
     * everything in the given String must still be in the Tokenizer,
     * but there may be additional (untested) Tokens to be returned.
     * This method is primarily to test whether Tokens are pushed
     * back appropriately.
     * @param parser TODO
     * @param expectedTokens The Tokens we expect to get from the Tokenizer.
     */
    private void followedBy(Parser parser, String expectedTokens) {
        int expectedType;
        int actualType;
        StreamTokenizer actual = parser.tokenizer;

        Reader reader = new StringReader(expectedTokens);
        StreamTokenizer expected = new StreamTokenizer(reader);

        try {
            while (true) {
                expectedType = expected.nextToken();
                if (expectedType == StreamTokenizer.TT_EOF) break;
                actualType = actual.nextToken();
                assertEquals(typeName(expectedType), typeName(actualType));
                if (actualType == StreamTokenizer.TT_WORD) {
                    assertEquals(expected.sval, actual.sval);
                }
                else if (actualType == StreamTokenizer.TT_NUMBER) {
                    assertEquals(expected.nval, actual.nval, 0.001);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String typeName(int type) {
        switch(type) {
            case StreamTokenizer.TT_EOF: return "EOF";
            case StreamTokenizer.TT_EOL: return "EOL";
            case StreamTokenizer.TT_WORD: return "WORD";
            case StreamTokenizer.TT_NUMBER: return "NUMBER";
            default: return "'" + (char)type + "'";
        }
    }
    
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
