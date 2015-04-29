package tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Tree API assignment for CIT594, Spring 2015.
 * 
 * @author Ryan Smith
 * @param <V> The type of value that can be held in each Tree node.
 */
public class Tree<V> implements Iterable<Tree<V>> {
    private V value;
    private ArrayList<Tree<V>> children;
    
    /**
     * Constructs a Tree with the given value in the root node,
     * having the given children.
     * 
     * @param value The value to be put in the root.
     * @param children The immediate children of the root.
     */
    public Tree(V value, Tree<V>... children) {
        this.value = value;
        this.children = new ArrayList<Tree<V>>();
        for(Tree<V> child : children){
        	this.children.add(child);
        }
    }
    
    /**
     * Sets the value in this node.
     * 
     * @param value The value to be stored in this node.
     */
    public void setValue(V value) {
    	//what if different type? how to check?
    	this.value = value;
    }
    
    /**
     * Returns the value in this node.
     * 
     * @return The value in this node.
     */
    public V getValue() {
        return value; // TODO Replace with correct result
    }
    
    /**
     * Adds the child as the new <code>index</code>'th child of this Tree;
     * subsequent nodes are "moved over" as necessary to make room for the
     * new child.
     * 
     * @param index The position in which to put the new child.
     * @param child The child to be added to this node.
     * @throws IllegalArgumentException
     *         If the operation would create a circular Tree.
     */
    public void addChild(int index, Tree<V> child) {
    	if (child.contains(this))
    		throw new IllegalArgumentException("This would create a cycle");
    	else
    		children.add(index, child);
    }
    
    /**
     * Adds the child as the new last child of this node.
     * @param child The child to be added to this node.
     * @throws IllegalArgumentException
     *         If the operation would create a circular Tree.
     */
    public void addChild(Tree<V> child) {
    	//check if tree contains this node, if so there will be a cycle
    	if (child.contains(this))
    		throw new IllegalArgumentException("This would create a cycle");
    	else
    		children.add(child);
    }
    
    /**
     * Adds the children to this node, after the current children.
     * 
     * @param children The nodes to be added as children of this node.
     * @throws IllegalArgumentException
     *         If the operation would create a circular Tree.
     */
    public void addChildren(Tree<V>... children) {
        for (Tree<V> child : children){
        	if (child.contains(this))
        		throw new IllegalArgumentException("This would create a cycle");
        	else
        		this.children.add(child);
        }
    }
    
    /**
     * Returns the number of children that this node has.
     * 
     * @return A count of this node's immediate children.
     */
    public int getNumberOfChildren() {
        return children.size();
    }
    
    /**
     * Returns the <code>index</code>'th child of this node.
     *  
     * @param index The position of the child that is to be returned.
     * @return The child at that position.
     * @throws IndexOutOfBoundsException If <code>index</code> is negative or
     *     is greater than or equal to the current number of children of this node.
     */
    public Tree<V> getChild(int index) {
        return children.get(index);
    }
    
    /**
     * Returns an iterator for the children of this node. 
     * 
     * @return An iterator for this node's immediate children.
     */
    @Override
    public Iterator<Tree<V>> iterator() {
        return children.iterator();
    }
    
    /**
     * Searchs this Tree for a node that is == to <code>node</code>,
     * and returns <code>true</code> if found, <code>false</code> otherwise.
     * 
     * @param node The node to be searched for.
     * @return <code>true</code> if the node is found.
     */
    boolean contains(Tree<V> node) {
        if (this == node)
        	return true;
        for (Tree<V> child : this.children){
        	if (child.contains(node) == true)
        		return true;
        }
        return false;
    }
    
    /**
     * Returns a one-line string representing this tree.
     * The form of the output is:<br>
     * <code>value(child, child, ..., child)</code>.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toStringHelper(this).toString();
    }
    
    private StringBuilder toStringHelper(Tree<V> tree){
    	StringBuilder sb = new StringBuilder();
    	//if node is a leaf return its value
    	if (tree.getNumberOfChildren() == 0){
    		sb.append(tree.getValue());
    		return sb;
    	}
    	else{
    		//append the value of the current node, then recurse in order (left to right) to append value of its children
    		sb.append(tree.getValue() + " (");
    		for (int i = 0; i < tree.getNumberOfChildren(); i++){
    			sb.append(toStringHelper(tree.getChild(i)));
    			sb.append(" ");
    		}
    		//just removes unwanted space before )
    		sb.deleteCharAt(sb.length() -1);
    		sb.append(")");
    		return sb;
    	}
    }
    
    /**
     * Prints this tree as an indented structure.
     */
    public void print() {
        print(this, "");
    }   
    
    /**
     * Prints the given tree as an indented structure, with the
     * given node indented by the given amount.
     * @param tree The root of the tree or subtree to be printed.
     * @param indent The amount to indent the root.
     */
    public static void print(Tree<?> tree, String indent) {
    	if (tree != null){
    		System.out.printf(indent + tree.value + "\n");
    		String newIndent = indent + "   ";
    		for (int i = 0; i < tree.getNumberOfChildren(); i++) {
    			print(tree.getChild(i), newIndent);
    		}
    	}
    }
    
    /**
     * Tests whether the input argument is a Tree having the same shape
     * and containing the same values as this Tree.
     * 
     * @param obj The object to be compared to this Tree.
     * @return <code>true</code> if the object is equals to this Tree,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
    	//node has different number fo children - false
    	if (this.getNumberOfChildren() != ((Tree)obj).getNumberOfChildren())
    		return false;
    	//last node in the line and they are not equal - false
    	if (this.getNumberOfChildren() == 0 && !this.equalsHelper(obj))
        	return false;
        for (int i = 0; i < this.getNumberOfChildren(); i++){
        	if (this.getChild(i).equals(((Tree) obj).getChild(i)) == false)
        		return false;
        }
        return true;
    }
    
    private boolean equalsHelper(Object obj){
    	if (!(obj instanceof Tree)) 
    		return false;
   	 	Tree<V> argTree = (Tree) obj;
   	 	if (this.value.equals(argTree.value) && this.children.equals(argTree.children))
   	 		return true;
   	 	else
   	 		return false;
    }
        
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
    	//uses String representation of the Tree to produce a unique and consistent hashCode
        int total = hashCodeHelper(this);
        return total;
        
    }

    private int hashCodeHelper(Tree<V> tree){
    	int total = 0;
    	if (tree.getNumberOfChildren() == 0){
    		char[] stringRep = this.toString().toCharArray();
            for (Character c : stringRep){
            	total += c.hashCode();
            }
            return total;
    	}
    	else{	
	        for (int i = 0; i < tree.getNumberOfChildren(); i++){
	        	total += hashCodeHelper(tree.getChild(i));
	        }
    	}
        return total;
    }
    
    /**
     * Creates a Tree of Strings from the input argument, which must have the
     * same form as that produced by the <code>toString()</code> method of
     * this class, namely, <code>value(child, child, ..., child)</code>.
     * 
     * @param input A representation of a Tree.
     * @return The Tree represented by the input string.
     * @throws IllegalArgumentException If the input string is malformed.
     */
    public static Tree<String> parse(String input) {
        PushbackStringTokenizer tokenizer = new PushbackStringTokenizer(input);
        Tree<String> tree = parse(tokenizer);
        if (tokenizer.hasNext()) {
            throw new IllegalArgumentException("Tokenizer error at: " + tokenizer.next());
        }
        return tree;
    }
    
    /**
     * Uses the input <code>tokenizer</code> to read and return a single Tree.
     * Additional tokens are ignored.
     * 
     * @param tokenizer The source of tokens from which to build a Tree.
     * @return A Tree built from the string being tokenized.
     * @throws IllegalArgumentException If the tokenized string is malformed.
     */
    static Tree<String> parse(PushbackStringTokenizer tokenizer) throws IllegalArgumentException {
    	//make sure tokenizer has a String to process
    	if (!tokenizer.hasNext())
    		throw new IllegalArgumentException("Error reading your input String");
    	//get first word and make sure it is a word
        String firstToken = tokenizer.next();
        if (firstToken.equals("(") || firstToken.equals(")") || firstToken == null)
        	throw new IllegalArgumentException("Improper format of String");
    	
        //make a rootNode for the tree
        Tree<String> rootNode = new Tree<String>(firstToken);
        Tree<String> currentRoot = rootNode;
    	//Stack will keep track of nodes being processed (way of backtracking to a parent
    	Stack<Tree<String>> nodeStack = new Stack<Tree<String>>();
    	nodeStack.push(rootNode);
    	//call helper function
    	parseHelper(tokenizer, nodeStack, currentRoot);
    	return rootNode;
        
    
    }
    
    private static int parseHelper(PushbackStringTokenizer tokenizer, Stack<Tree<String>> nodeStack, Tree<String> currentRoot){
		//if end of tokens return
		if (!tokenizer.hasNext())
			return 1;
		String token = tokenizer.next();
		//update the currentRoot to most recently added (top of Stack)
		if (token.equals("(")){
			currentRoot = nodeStack.peek();
			parseHelper(tokenizer, nodeStack, currentRoot);
		}
		//backup the currentRoot to the last root and continue (all this currentRoot's children have been added)
		else if (token.equals(")")){
			if (!nodeStack.isEmpty()){
				Tree<String> node = nodeStack.pop();
				//pops back to currentRoot
				while (node != currentRoot){
					node = nodeStack.pop();
				}
				//if stack now empty, that was beginning root, can back up no further else continue popping
				//until finding last root node (no children)
				if (nodeStack.isEmpty())
					return 1;
				else
					node = nodeStack.pop();
				while (node.getNumberOfChildren() == 0){
					node = nodeStack.pop();
				}
				//put the node back on stack
				nodeStack.push(node);
    			currentRoot = nodeStack.peek();
				parseHelper(tokenizer, nodeStack, currentRoot);
			}
		}
		//add the child node to currentRoot
		else{
	    	Tree<String> newNode = new Tree<String>(token);
	    	nodeStack.push(newNode);
	    	currentRoot.addChild(newNode);
	    	parseHelper(tokenizer, nodeStack, currentRoot);
	    }
		//if here with remaining tokens, something went wrong
		if (tokenizer.hasNext())
			return -1;
    	return 1;
	}
    
    //---------------------------------------------------------------------
    
    /**
     * A Tokenizer that returns one of four things: a left parenthesis, a
     * right parenthesis, a sequence of non-whitespace, non-parenthesis
     * characters, or <code>null</code> if there are no more tokens.
     * 
     * @author David Matuszek
     */
    static class PushbackStringTokenizer {
        private StringTokenizer tokenizer;
        private String pushedValue = null;
        
        /**
         * Constructs a tokenizer for the input that uses whitespace and
         * parentheses as delimiters.
         * 
         * @param input The string to be tokenized.
         */
        PushbackStringTokenizer(String input) {
            tokenizer = new StringTokenizer(input, " \t\n\r\f()", true);
            pushedValue = null;
        }
        
        /**
         * Tests if there are more tokens in the input string.
         * 
         * @return <code>true</code> if there are more tokens,
         *         <code>false</code> otherwise.         
         */
        boolean hasNext() {
            return pushedValue != null || tokenizer.hasMoreTokens();
        }
        
        /**
         * Returns the next token (or a pushed back token, if there is
         * one.) A token may be a left parenthesis, a right parenthesis,
         * or any sequence of other, non-whitespace characters.
         * <p>
         * Unlike most tokenizers, this tokenizer will return
         * <code>null</code> if there are no remaining tokens.
         * 
         * @return The next token, or <code>null</code> if there are no more.
         */
        String next() {
            String temp = pushedValue;
            if (temp == null && tokenizer.hasMoreTokens()) {
                temp = tokenizer.nextToken().trim();
            }
            pushedValue = null;
            // skip whitespace tokens
            if (temp != null && temp.length() == 0) {
                temp = next();
            }
            return temp;
        }
        
        /**
         * Returns a token to this tokenizer so that it will be returned by
         * the next call to the <code>next()</code> method.
         * 
         * @param token The token to be reused.
         */
        void pushBack(String token) {
            pushedValue = token;
        }
    }
}
