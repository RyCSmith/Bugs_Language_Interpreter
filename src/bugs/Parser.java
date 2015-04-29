package bugs;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.*;

import tree.Tree;

/**
 * Bugs Languauge Parser. Recognized Bugs syntax and constructs a tree representing the provided input.
 * 
 * @author Ryan Smith
 * @author Dave Matuszek
 * @version February 2015
 */
public class Parser {
    /** The tokenizer used by this Parser. */
    StreamTokenizer tokenizer = null;
    /** The number of the line of source code currently being processed. */
    private int lineNumber = 1;

    /**
     * The stack used for holding Trees as they are created.
     */
    public Stack<Tree<Token>> stack = new Stack<>();

    /**
     * Constructs a Parser for the given string.
     * @param text The string to be parsed.
     */
    public Parser(String text) {
        Reader reader = new StringReader(text);
        tokenizer = new StreamTokenizer(reader);
        tokenizer.parseNumbers();
        tokenizer.eolIsSignificant(true);
        tokenizer.slashStarComments(true);
        tokenizer.slashSlashComments(true);
        tokenizer.lowerCaseMode(false);
        tokenizer.ordinaryChars(33, 47);
        tokenizer.ordinaryChars(58, 64);
        tokenizer.ordinaryChars(91, 96);
        tokenizer.ordinaryChars(123, 126);
        tokenizer.quoteChar('\"');
        lineNumber = 1;
    }

    /**
  	 * Tries to build an &lt;action&gt; on the global stack.
  	 * <pre>&ltaction&gt ::= &ltmove action&gt
  	 * 		| &ltmoveto action&gt
  	 * 		| &ltturn action&gt
  	 * 		| &ltturnto action&gt
  	 * 		| &ltline action&gt</pre>
  	 * @return <code>true</code> if an &lt;action&gt; is recognized.
  	 */
  	public boolean isAction(){
  		//no trees to be made here
  		//if any method call is successful, a tree with the corresponding action will be left on the stack
  		if (isMoveAction()) return true;
  		if (isMoveToAction()) return true;
  		if (isTurnAction()) return true;
  		if (isTurnToAction()) return true;
  		if (isLineAction()) return true;
  		return false;
  	}
  	/**
  	 * Tries to build an &lt;allbugs code&gt; on the global stack.
  	 * <pre>&ltallbugs code> ::= "Allbugs"  "{" &lteol&gt
  	 * 		{ &ltvar declaration&gt }
  	 * 		{ &ltfunction definition&gt }
  	 * 	"}" &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if an &lt;allbugs code&gt; is recognized.
  	 */
  	public boolean isAllbugsCode(){
  		if (!keyword("Allbugs")) return false;
  		if (!symbol("{")) error("Error in allBugsCode after \"Allbugs\"");
  		//remove {
  		stack.pop();
  		if (!isEol()) error("Error in allBugsCode after \"{\"");
  		//add list node
  		pushNewNode("list");
  		while (isVarDeclaration()){
  			//add to list node
  			makeTree(2, 1);
  		}
  		//add list node to Allbugs
  		makeTree(2, 1);
  		//make new list node
  		pushNewNode("list");
  		while (isFunctionDefinition()){
  			//add to list node
  			makeTree(2, 1);
  		}
  		//add completed list to AllBugs
  		makeTree(2, 1);
  		if (!symbol("}")) error("Error in allBugsCode after EOL");
  		//remove }
  		stack.pop();
  		if (!isEol()) error("Error in allBugsCode after \"}\"");
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;assignment statement&gt; on the global stack.
  	 * <pre>&ltassignment statement&gt ::= &ltvariable&gt "=" &ltexpression&gt &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if an &lt;assignment statement&gt; is recognized.
  	 */
  	public boolean isAssignmentStatement(){
  		//add "assign" tree
  		pushNewNode("assign");
  		//var name will be added to stack by nextTokenMatches()
  		if (!isVariable()){
  			//remove "assign"
  			stack.pop();
  			return false;
  		}
  		if (!symbol("=")) error("Error in assignment statement after variable");
  		//remove "=" from stack
  		stack.pop();
  		//expression tree will be added by isExpression()
  		if (!isExpression()) error("Error in assignment statement after =");
  		if (!isEol()) error("Error in assignment statement after expression.");
  		makeTree(3, 2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build a &lt;block&gt; on the global stack.
  	 * <pre>&ltblock&gt ::= "{" &lteol&gt { &ltcommand&gt }  "}" &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;block&gt; is recognized.
  	 */
  	public boolean isBlock(){
  		if (!symbol("{")) return false;
  		//pop "{"
  		stack.pop();
  		if (!isEol()) error("Error in block after \"{\".");
  		//add block node to stack
  		pushNewNode("block");
  		while (isCommand()){
  			//add every command to block
  			makeTree(2, 1);
  		};
  		if (!symbol("}")) error("Error in block after EOL.");
  		//pop "}"
  		stack.pop();
  		if (!isEol()) error("Error in block after \"}\".");
  		return true;
  	}
  	/**
  	 * Tries to build a &lt;bug definition&gt; on the global stack.
  	 * <pre>&ltbug definition&gt ::= "Bug" &ltname&gt "{" &lteol&gt
  	 * 		{ &ltvar declaration&gt }
  	 * 		[ &ltinitialization block&gt ]
  	 * 		&ltcommand&gt
  	 * 		{ &ltcommand&gt }
  	 * 		{ &ltfunction definition&gt } 
  	 * 	"}" &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;bug definition&gt; is recognized.
  	 */
  	public boolean isBugDefinition(){
  		if (!keyword("Bug")) return false;
  		if (!name()) error("Error in Bug Definition after \"Bug\".");
  		//add name to "Bug"
  		makeTree(2, 1);
  		if (!symbol("{")) error("Error in Bug Definition after name.");
  		//pop "{" from stack
  		stack.pop();
  		if (!isEol()) error("Error in Bug Definition after \"{\".");
  		//add "list" to stack
  		pushNewNode("list");
  		while (isVarDeclaration()){
  			//add each var-dec to list tree
  			makeTree(2, 1);
  		}
  		//add full list of var-decs to "bug"
  		makeTree(2, 1);
  		if (isInitializationBlock()){
  			//if we got an initialization block, add to "Bug"
  			makeTree(2, 1);
  		}
  		else{
  			//make a placeholder initially block and add to Bug
  			pushNewNode("initially");
  			makeTree(2, 1);
  		}
  		//make new tree "block"
  		pushNewNode("block");
  		if (!isCommand()) error("Error in Bug Definition after EOL.");
  		//add command to block
  		makeTree(2, 1);
  		while (isCommand()){
  			//add each remaining tree to block
  			makeTree(2, 1);
  		}
  		//add block of commands to Bug
  		makeTree(2, 1);
  		//add "list" tree to hold func-defs
  		pushNewNode("list");
  		while (isFunctionDefinition()){
  			//add each new func-def to list
  			makeTree(2, 1);
  		}
  		//add list to Bug
  		makeTree(2, 1);
  		if (!symbol("}")) error("Error in Bug Definition after function defintion.");
  		//pop "}" from stack
  		stack.pop();
  		if (!isEol()) error("Error in Bug Definition after \"}\".");
  		return true;	
  	}
  	/**
  	 * Tries to build a &lt;color statement&gt; on the global stack.
  	 * <pre>&ltcolor statement&gt ::= "color" &ltKEYWORD&gt &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;color statement&gt; is recognized.
  	 */
  	public boolean isColorStatement(){
  		if (!keyword("color")) return false;
  		if(!nextTokenMatches(Token.Type.KEYWORD)) error("Error in color statement after \"color\".");
  		if(!isEol()) error("Error in color statement after keyword.");
  		//"color" and a keyword added above, just makeTree
  		makeTree(2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build a &lt;command&gt; on the global stack.
  	 * <pre>&ltcommand&gt ::= &ltaction&gt
  	 * 	    | &ltstatement&gt </pre>
  	 * @return <code>true</code> if a &lt;command&gt; is recognized.
  	 */
  	public boolean isCommand(){
  		//nothing to be added, either method call will place tree on stack if successful
  		if (isAction()) return true;
  		if (isStatement()) return true;
  		return false;
  	}
  	/**
  	 * Tries to build a &lt;comparator&gt; on the global stack.
  	 * <pre>&lt;comparator&gt; ::= "<" | "<=" | "=" | "!=" | ">=" | ">"</pre>
  	 * @return <code>true</code> if a &lt;comparator&gt; is recognized.
  	 */
  	public boolean isComparator(){
  		if (symbol("<")){
  			if (symbol("=")){
  				//if here, pop last two trees (< and =) and add <= tree to stack
  				stack.pop();
  				stack.pop();
  				pushNewNode("<=");
  				return true;
  			}
  			//no need to add "<" to stack, added in nextTokenMatches()
			return true;
  		}
  		if (symbol("=")){
  			//no need to add "=" to stack, added in nextTokenMatches()
  			return true;
  		}
  		if (symbol("!"))
  			if(symbol("=")){
  			//if here, pop last two trees (! and =) and add != tree to stack
  				stack.pop();
  				stack.pop();
  				pushNewNode("!=");
  				return true;
  			}
  		if (symbol(">")){
  			if (symbol("=")){
  			//if here, pop last two trees (> and =) and add >= tree to stack
  				stack.pop();
  				stack.pop();
  				pushNewNode(">=");
  				return true;
  			}
  			//no need to add "<" to stack, added in nextTokenMatches()
			return true;
  		}
  		return false;
  	}
  	/**
  	 * Tries to build a &lt;do statement&gt; on the global stack.
  	 * <pre>&ltdo statement&gt ::= "do" &ltvariable&gt [ &ltparameter list&gt ] &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;do statement&gt; is recognized.
  	 */
  	public boolean isDoStatement(){
  		if (!keyword("do")) return false;
  		//pop do add call
  		stack.pop();
  		pushNewNode("call");
  		if (!isVariable()) error("Error in do statement after \"do\".");
  		//add variable name under "call"
  		makeTree(2, 1);
  		//"do" and varName added to stack above
  		//check to see if param list is added, make tree accordingly
  		if(isParameterList()){
  			makeTree(2, 1);
  		}
  		else {
  			//if no params, add "var" without children
  			pushNewNode("var");
  			makeTree(2, 1);
  		}
  		if (!isEol()) error("Error in do statement after variable.");
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;EOL&gt; on the global stack.
  	 * <pre>&lteol&gt ::= &ltEOL&gt { &ltEOL&gt } </pre>
  	 * @return <code>true</code> if an &lt;EOL&gt; is recognized.
  	 */
  	public boolean isEol(){
  		//if nextTokenMatches succeeds it will leave EOL token on stack.
  		//remove all that appear
  		if (!nextTokenMatches(Token.Type.EOL)) return false;
  		stack.pop();
  		while (nextTokenMatches(Token.Type.EOL)){
  			stack.pop();
  		}
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;exit if statement&gt; on the global stack.
  	 * <pre>&ltexit if statement&gt ::= "exit" "if" &ltexpression&gt &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if an &lt;exit if statement&gt; is recognized.
  	 */
  	public boolean isExitIfStatement(){
  		//"exit" tree added to stack by keyword
  		if (!keyword("exit")) return false;
  		if (!keyword("if")) error("Error in exit if statement after \"exit\".");
  		//remove "if" from stack
  		stack.pop();
  		//expression added by isExpression()
  		if (!isExpression()) error("Error in exit if statement after \"if\".");
  		if (!isEol()) error("Error in exit if statement after expression.");
  		makeTree(2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;function call&gt; on the global stack.
  	 * <pre>&ltfunction call&gt ::= &ltNAME&gt &ltparameter list&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;function call&gt; is recognized.
  	 */
  	public boolean isFunctionCall(){
  		//add "call to stack
  		pushNewNode("call");
  		//following 2 produce the children
  		if (!name()) {
  			//remove call from the stack
  			stack.pop();
  			return false;
  		}
  		if (!isParameterList()) error("Error in function call after name.");
  		makeTree(3, 2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;function definition&gt; on the global stack.
  	 * <pre>&ltfunction definition&gt ::= "define" &ltNAME&gt [ "using" &ltvariable&gt { "," &ltvariable&gt }  ] &ltblock&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;function definition&gt; is recognized.
  	 */
  	public boolean isFunctionDefinition(){
  		//add "function" tree
  		pushNewNode("function");
  		if (!keyword("define")){
  			//remove "function" from stack
  			stack.pop();
  			return false;
  		}
  		//remove "define" from stack
  		stack.pop();
  		if (!name()) error("Syntax error in function def. Nothing following \"define\".");
	  	pushNewNode("var");
  		if (keyword("using")){
  	  		//remove "using" from stack
  	  		stack.pop();
  			if (!isVariable()) error("Syntax error in function def. Nothing following 'using'.");
  			//make a tree with the first variable
  			makeTree(2, 1);
  			while (symbol(",")){
  				//for each additional variable, pop the "," and add it to the "var" tree
  				stack.pop();
  				if (!isVariable()) error("Syntax error in function def. Nothing following ','.");
  				makeTree(2, 1);
  			}
  		}
  		if (!isBlock()) error("Syntax error in function def.");
  		//make Tree - "function", name, vars, block
  		makeTree(4, 3, 2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;initialization block&gt; on the global stack.
  	 * <pre>&ltinitialization block&gt ::= "initially" &ltblock&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if an &lt;initialization block&gt; is recognized.
  	 */
  	public boolean isInitializationBlock(){
  		if (!keyword("initially")) return false;
  		if (!isBlock()) error("Syntax error in initialization block after \"initially\".");
  		//stack filled by previous 2 calls
  		//add the block under initially
  		makeTree(2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;line action&gt; on the global stack.
  	 * <pre>&ltline action&gt ::= "line" &ltexpression&gt ","&ltexpression&gt ","&ltexpression&gt "," &ltexpression&gt &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;line action&gt; is recognized.
  	 */
  	public boolean isLineAction(){
  		//puts "line" on stack
  		if (!keyword("line")) return false;
  		if (!isExpression()) error("Syntax error in line action after \"line\".");
  		if (!symbol(",")) error("Syntax error in line action after expression.");
  		//pop all ","
  		stack.pop();
  		if (!isExpression()) error("Syntax error in line action after \",\".");
  		if (!symbol(",")) error("Syntax error in line action after \",\".");
  		stack.pop();
  		if (!isExpression()) error("Syntax error in line action after expression.");
  		if (!symbol(",")) error("Syntax error in line action after \",\".");
  		stack.pop();
  		if (!isExpression()) error("Syntax error in line action after expression.");
  		if (!isEol()) error("Syntax error in line action after EOL.");
  		makeTree(5, 4, 3, 2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;loop statement&gt; on the global stack.
  	 * <pre>&ltloop statement&gt ::= "loop" &ltblock&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;loop statement&gt; is recognized.
  	 */
  	public boolean isLoopStatement(){
  		if (!keyword("loop")) return false;
  		if (!isBlock()) error("Syntax error in loop statement after \"loop\".");
  		//"loop" added by keyword, attach block (put on stack by isBlock()
  		makeTree(2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;moveto action&gt; on the global stack.
  	 * <pre>&ltmoveto action&gt ::= "moveto" &ltexpression&gt "," &ltexpression&gt &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;moveto action&gt; is recognized.
  	 */
  	public boolean isMoveToAction(){
  		if (!keyword("moveto")) return false;
  		//no need to add Tree("move") because keyword()/nextTokenMatches() handles it
  		if (!isExpression()) error("Syntax error in moveto action after \"moveto\".");
  		if (!symbol(",")) error("Syntax error in moveto action after expression.");
  		//remove "," from stack
  		stack.pop();
  		if (!isExpression()) error("Syntax error in moveto action after \",\".");
  		if (!isEol()) error("Syntax error in moveto action after expression.");
  		makeTree(3, 2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;move action&gt; on the global stack.
  	 * <pre>&ltmove action&gt ::= "move" &ltexpression&gt &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;move action&gt; is recognized.
  	 */
  	public boolean isMoveAction(){
  		if (!keyword("move")) return false;
  		//no need to add Tree("move") because keyword()/nextTokenMatches() handles it
  		if (!isExpression()) error("Syntax error in move action after \"move\".");
  		if (!isEol()) error("Syntax error in move action. Missing EOL.");
  		makeTree(2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build a &lt;program&gt; on the global stack.
  	 * <pre>&ltprogram&gt ::= [ &ltallbugs code> ]
  	 * 		&ltbug definition&gt
  	 * 		{ &ltbug definition&gt }</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;program&gt; is recognized.
  	 */
	public boolean isProgram(){
		//isEol() will discard any newlines left by comments preceding the program
		isEol();
  		//add program node
  		pushNewNode("program");
  		if (isAllbugsCode()){
  			pushNewNode("list");
  			if (!isBugDefinition()) error("Syntax error in program after allbugs code.");
  		}
  		else{
  			pushNewNode("Allbugs");
  			pushNewNode("list");
  			if (!isBugDefinition()){
  	  			//pop stuff we put on stack
  	  			stack.pop();
  	  			stack.pop();
  	  			stack.pop();
  	  			return false;
  	  		}
  		}
  		//if made it here, bug on stack, add bug def to list tree
  		makeTree(2, 1);
  		while (isBugDefinition()){
  			//continue adding bug defs to list tree
  			makeTree(2, 1);
  		}
  		if (!nextTokenMatches(Token.Type.EOF)) error("Syntax error in program. EOF not detected");
  		//remove EOF from stack
  		stack.pop();
  		//assemble the full tree
  		makeTree(3, 2, 1);
  		return true;
	}
  	/**
  	 * Tries to build an &lt;return statement&gt; on the global stack.
  	 * <pre>&ltreturn statement&gt ::= "return" &ltexpression&gt &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;return statement&gt; is recognized.
  	 */
  	public boolean isReturnStatement(){
  		if (!keyword("return")) return false;
  		//no need to add Tree("move") because keyword()/nextTokenMatches() handles it
  		if (!isExpression()) error("Syntax error in return statement after \"return\".");
  		if (!isEol()) error("Syntax error in return statement after expression.");
  		makeTree(2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;statement&gt; on the global stack.
  	 * <pre>&ltstatement&gt ::= &ltassignment statement&gt
  	 * 		| &ltloop statement&gt
  	 * 		| &ltexit if statement&gt
  	 * 		| &ltswitch statement&gt
  	 * 		| &ltreturn statement&gt
  	 * 		| &ltdo statement&gt
  	 * 		| &ltcolor statement&gt</pre>
  	 * @return <code>true</code> if a &ltstatement&gt is recognized.
  	 */
  	public boolean isStatement(){
  		//no trees constructed here, if a call is successful, tree is added by called method
  		if(isAssignmentStatement()) return true;
  		if (isLoopStatement()) return true;
  		if (isExitIfStatement()) return true;
  		if (isSwitchStatement()) return true;
  		if (isReturnStatement()) return true;
  		if (isDoStatement()) return true;
  		if (isColorStatement()) return true;
  		return false;
  	}
  	/**
  	 * Tries to build an &lt;switch statement&gt; on the global stack.
  	 * <pre>&ltswitch statement&gt ::= "switch" "{" &lteol&gt
  	 * { "case" &ltexpression&gt &lteol&gt
  	 * 	{ &ltcommand&gt } }
  	 * "}" &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &ltswitch statement&gt is recognized.
  	 */
  	public boolean isSwitchStatement(){
  		if (!keyword("switch")) return false;
  		if (!symbol("{")) error("Syntax error in switch statement. Nothing after 'switch'.");
  		//pop "{" from stack
  		stack.pop();
  		if (!isEol()) error("Syntax error in switch statement. Nothing after '{'.");
  		while (keyword("case")){
  			if (!isExpression()) error("Syntax error in switch statement. Nothing after 'case'.");
  			if(!isEol()) error("Syntax error in switch statement. Missing EOL.");
  			//add new block to stack to hold commands
  			pushNewNode("block");
  			while (isCommand()){
  				//add each new command to the block
  				makeTree(2, 1);
  			};
  			//add expression and block(w/commands) to "case"
  			makeTree(3, 2, 1);
  			//add case to switch
  			makeTree(2, 1);
  		}
  		if (!symbol("}")) error("Syntax error in switch statement.");
  		//pop "}" from stack
  		stack.pop();
  		if (!isEol()) error("Syntax error in switch statement. Nothing after '}'.");
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;turn action&gt; on the global stack.
  	 * <pre>&ltturn action&gt ::= "turn" &ltexpression&gt &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;turn action&gt; is recognized.
  	 */
  	public boolean isTurnAction(){
  		if (!keyword("turn")) return false;
	  	//no need to add Tree("turn") because keyword()/nextTokenMatches() handles it
  		if (!isExpression()) error("Syntax error in turn action after \"turn\".");
  		if (!isEol()) error("Syntax error in turn action after expression.");
  		makeTree(2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;turnto action&gt; on the global stack..
  	 * <pre>&ltturnto action&gt ::= "turnto" &ltexpression&gt &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &lt;turnto action&gt; is recognized.
  	 */
  	public boolean isTurnToAction(){
  		if (!keyword("turnto")) return false;
  		//no need to add Tree("turn") because keyword()/nextTokenMatches() handles it
  		if (!isExpression()) error("Syntax error in turnto action after \"turnto\".");
  		if (!isEol()) error("Syntax error in turnto action after expression.");
  		makeTree(2, 1);
  		return true;
  	}
  	/**
  	 * Tries to build an &lt;var declaration&gt; on the global stack.
  	 * <pre>&ltvar declaration&gt ::= "var" &ltNAME&gt { "," &ltNAME&gt } &lteol&gt</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if a &ltvar declaration&gt is recognized.
  	 */
  	public boolean isVarDeclaration(){
  		if (!keyword("var")) return false;
  		if (!name()) error("Error in the var declaration, nothing follows 'var'");
  		//make a tree from var and name
  		makeTree(2, 1);
  		while (symbol(",")){
  			//pop ","
  			stack.pop();
  			if (!name()) error("Error in the var declaration, nothing follows ','");
  			//add each additional name to "var"
  			makeTree(2, 1);
  		}
  		if (!isEol()) error("Error in the var declaration.");
  		return true;
  	}
  	
  	/**
  	 * Tries to parse an &lt;expression&gt;.
  	 * <pre>&ltexpression&gt ::= &ltarithmetic expression&gt {  &ltcomparator&gt &ltarithmetic expression&gt }</pre>
  	 * A <code>SyntaxException</code> will be thrown for improper format.
  	 * @return <code>true</code> if an expression is recognized.
  	 */
    public boolean isExpression() {
        if (!isArithmeticExpression()) return false;
        while (isComparator()) {
            if (!isArithmeticExpression()) error("Illegal expression after comparator");
            makeTree(2, 3, 1);
        }
        return true;
    }

    /**
     * Tries to build an &lt;expression&gt; on the global stack.
     * <pre>&lt;expression&gt; ::= &lt;term&gt; { &lt;add_operator&gt; &lt;expression&gt; }</pre>
     * A <code>SyntaxException</code> will be thrown if the add_operator
     * is present but not followed by a valid &lt;expression&gt;.
     * @return <code>true</code> if an expression is recognized.
     */
    public boolean isArithmeticExpression() {
        if (!isTerm())
            return false;
        while (isAddOperator()) {
            if (!isTerm()) error("Error in expression after '+' or '-'");
            makeTree(2, 3, 1);
        }
        return true;
    }

    /**
     * Tries to build a &lt;term&gt; on the global stack.
     * <pre>&lt;term&gt; ::= &lt;factor&gt; { &lt;multiply_operator&gt; &lt;term&gt; }</pre>
     * A <code>SyntaxException</code> will be thrown if the multiply_operator
     * is present but not followed by a valid &lt;term&gt;.
     * @return <code>true</code> if a term is parsed.
     */

    public boolean isTerm() {
        if (!isFactor()) {
            return false;
        }
        while (isMultiplyOperator()) {
            if (!isFactor()) {
                error("No term after '*' or '/'");
            }
            makeTree(2, 3, 1);
        }
        return true;
    }

    /**
     * Tries to build a &lt;factor&gt; on the global stack.
     * <pre>&lt;factor&gt; ::= [ &lt;unsigned factor&gt; ] &lt;name&gt;</pre>
     * A <code>SyntaxException</code> will be thrown if the opening
     * parenthesis is present but not followed by a valid
     * &lt;expression&gt; and a closing parenthesis.
     * @return <code>true</code> if a factor is parsed.
     */
    public boolean isFactor() {
        if(symbol("+") || symbol("-")) {
            if (isUnsignedFactor()) {
                makeTree(2, 1);
                return true;
            }
            error("No factor following unary plus or minus");
            return false; // Can't ever get here
        }
        return isUnsignedFactor();
    }

    /**
     * Tries to build an &lt;unsigned factor&gt; on the global stack.
     * <pre>&lt;unsigned factor&gt; ::= &lt;variable&gt; . &lt;variable&gt;
     *                    | &lt;function call&gt;
     *                    | &lt;variable&gt;
     *                    | &lt;number&gt;
     *                    | "(" &lt;expression&gt; ")"</pre>
     * A <code>SyntaxException</code> will be thrown if the opening
     * parenthesis is present but not followed by a valid
     * &lt;expression&gt; and a closing parenthesis.
     * @return <code>true</code> if a factor is parsed.
     */
    public boolean isUnsignedFactor() {
        if (name()) {
            if (symbol(".")) {
                // reference to another Bug
                if (name()) {
                    makeTree(2, 3, 1);
                }
                else error("Incorrect use of dot notation");
            }
            else if (isParameterList()) {
                // function call
                pushNewNode("call");
                makeTree(1, 3, 2);
            }
            else {
                // just a variable; leave it on the stack
            }
        }
        else if (number()) {
            // leave the number on the stack
        }
        else if (symbol("(")) {
            stack.pop();
            if (!isExpression()) {
                error("Error in parenthesized expression");
            }
            if (!symbol(")")) {
                error("Unclosed parenthetical expression");
            }
            stack.pop();
        }
        else {
            return false;
        }
       return true;
    }
    
    /**
     * Tries to recognize a &lt;parameter list&gt;.
     * <pre>&ltparameter list&gt; ::= "(" [ &lt;expression&gt; { "," &lt;expression&gt; } ] ")"
     * @return <code>true</code> if a parameter list is recognized.
     */
    public boolean isParameterList() {
        if (!symbol("(")) return false;
        stack.pop(); // remove open paren
        pushNewNode("var");
        if (isExpression()) {
            makeTree(2, 1);
            while (symbol(",")) {
                stack.pop(); // remove comma
                if (!isExpression()) error("No expression after ','");
                makeTree(2, 1);
            }
        }
        if (!symbol(")")) error("Parameter list doesn't end with ')'");
        stack.pop(); // remove close paren
        return true;
    }

    /**
     * Tries to recognize an &lt;add_operator&gt; and put it on the global stack.
     * <pre>&lt;add_operator&gt; ::= "+" | "-"</pre>
     * @return <code>true</code> if an addop is recognized.
     */
    public boolean isAddOperator() {
        return symbol("+") || symbol("-");
    }

    /**
     * Tries to recognize a &lt;multiply_operator&gt; and put it on the global stack.
     * <pre>&lt;multiply_operator&gt; ::= "*" | "/"</pre>
     * @return <code>true</code> if a multiply_operator is recognized.
     */
    public boolean isMultiplyOperator() {
        return symbol("*") || symbol("/");
    }
    
    /**
     * Tries to parse a &lt;variable&gt;; same as &lt;isName&gt;.
     * <pre>&lt;variable&gt; ::= &lt;NAME&gt;</pre>
     * @return <code>true</code> if a variable is parsed.
     */
    public boolean isVariable() {
        return name();
    }

    //------------------------- Private "helper" methods
    
    /**
     * Creates a new Tree consisting of a single node containing a
     * Token with the correct type and the given <code>value</code>,
     * and pushes it onto the global stack. 
     *
     * @param value The value of the token to be pushed onto the global stack.
     */
    private void pushNewNode(String value) {
        stack.push(new Tree<>(new Token(Token.typeOf(value), value)));
    }

    /**
     * Tests whether the next token is a number. If it is, the token
     * is moved to the stack, otherwise it is not.
     * 
     * @return <code>true</code> if the next token is a number.
     */
    private boolean number() {
        return nextTokenMatches(Token.Type.NUMBER);
    }

    /**
     * Tests whether the next token is a name. If it is, the token
     * is moved to the stack, otherwise it is not.
     * 
     * @return <code>true</code> if the next token is a name.
     */
    private boolean name() {
        return nextTokenMatches(Token.Type.NAME);
    }

    /**
     * Tests whether the next token is the expected name. If it is, the token
     * is moved to the stack, otherwise it is not.
     * 
     * @param expectedName The String value of the expected next token.
     * @return <code>true</code> if the next token is a name with the expected value.
     */
    private boolean name(String expectedName) {
        return nextTokenMatches(Token.Type.NAME, expectedName);
    }

    /**
     * Tests whether the next token is the expected keyword. If it is, the token
     * is moved to the stack, otherwise it is not.
     *
     * @param expectedKeyword The String value of the expected next token.
     * @return <code>true</code> if the next token is a keyword with the expected value.
     */
    private boolean keyword(String expectedKeyword) {
        return nextTokenMatches(Token.Type.KEYWORD, expectedKeyword);
    }

    /**
     * Tests whether the next token is the expected symbol. If it is,
     * the token is moved to the stack, otherwise it is not.
     * 
     * @param expectedSymbol The single-character String that is expected
     *        as the next symbol.
     * @return <code>true</code> if the next token is the expected symbol.
     */
    private boolean symbol(String expectedSymbol) {
        return nextTokenMatches(Token.Type.SYMBOL, expectedSymbol);
    }

    /**
     * If the next Token has the expected type, it is used as the
     * value of a new (childless) Tree node, and that node
     * is then pushed onto the stack. If the next Token does not
     * have the expected type, this method effectively does nothing.
     * 
     * @param type The expected type of the next token.
     * @return <code>true</code> if the next token has the expected type.
     */
    private boolean nextTokenMatches(Token.Type type) {
        Token t = nextToken();
        if (t.type == type) {
            stack.push(new Tree<>(t));
            return true;
        }
        pushBack();
        return false;
    }

    /**
     * If the next Token has the expected type and value, it is used as
     * the value of a new (childless) Tree node, and that node
     * is then pushed onto the stack; otherwise, this method does
     * nothing.
     * 
     * @param type The expected type of the next token.
     * @param value The expected value of the next token; must
     *              not be <code>null</code>.
     * @return <code>true</code> if the next token has the expected type.
     */
    private boolean nextTokenMatches(Token.Type type, String value) {
        Token t = nextToken();
        if (type == t.type && value.equals(t.value)) {
            stack.push(new Tree<>(t));
            return true;
        }
        pushBack();
        return false;
    }

    /**
     * Returns the next Token. Increments the global variable
     * <code>lineNumber</code> when an EOL is returned.
     * 
     * @return The next Token.
     */
    Token nextToken() {
        int code;
        try { code = tokenizer.nextToken(); }
        catch (IOException e) { throw new Error(e); } // Should never happen
        switch (code) {
            case StreamTokenizer.TT_WORD:
                if (Token.KEYWORDS.contains(tokenizer.sval)) {
                    return new Token(Token.Type.KEYWORD, tokenizer.sval);
                }
                return new Token(Token.Type.NAME, tokenizer.sval);
            case StreamTokenizer.TT_NUMBER:
                return new Token(Token.Type.NUMBER, tokenizer.nval + "");
            case StreamTokenizer.TT_EOL:
                lineNumber++;
                return new Token(Token.Type.EOL, "\n");
            case StreamTokenizer.TT_EOF:
                return new Token(Token.Type.EOF, "EOF");
            default:
                return new Token(Token.Type.SYMBOL, ((char) code) + "");
        }
    }

    /**
     * Returns the most recent Token to the tokenizer. Decrements the global
     * variable <code>lineNumber</code> if an EOL is pushed back.
     */
    void pushBack() {
        tokenizer.pushBack();
        if (tokenizer.ttype == StreamTokenizer.TT_EOL) lineNumber--;
    }

    /**
     * Assembles some number of elements from the top of the global stack
     * into a new Tree, and replaces those elements with the new Tree.<p>
     * <b>Caution:</b> The arguments must be consecutive integers 1..N,
     * in any order, but with no gaps; for example, makeTree(2,4,1,5)
     * would cause problems (3 was omitted).
     * 
     * @param rootIndex Which stack element (counting from 1) to use as
     * the root of the new Tree.
     * @param childIndices Which stack elements to use as the children
     * of the root.
     */    
    void makeTree(int rootIndex, int... childIndices) {
        // Get root from stack
        Tree<Token> root = getStackItem(rootIndex);
        // Get other trees from stack and add them as children of root
        for (int i = 0; i < childIndices.length; i++) {
            root.addChild(getStackItem(childIndices[i]));
        }
        // Pop root and all children from stack
        for (int i = 0; i <= childIndices.length; i++) {
            stack.pop();
        }
        // Put the root back on the stack
        stack.push(root);
    }
    
    /**
     * Returns the n-th item from the top of the global stack (counting the
     * top element as 1).
     * 
     * @param n Which stack element to return.
     * @return The n-th element in the global stack.
     */
    private Tree<Token> getStackItem(int n) {
        return stack.get(stack.size() - n);
    }

    /**
     * Utility routine to throw a <code>SyntaxException</code> with the
     * given message.
     * @param message The text to put in the <code>SyntaxException</code>.
     */
    private void error(String message) {
        throw new SyntaxException("Line " + lineNumber + ": " + message);
    }
}

