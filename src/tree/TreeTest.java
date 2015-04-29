package tree;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test class for Tree.java API.
 * @author ryancsmith
 *
 */
public class TreeTest {
	Tree<String> t0;
	Tree<String> t1;
	Tree<String> t2;
	Tree<String> t3;
	Tree<String> t4;
	Tree<String> t5;
	Tree<String> t6;
	Tree<String> t7;
	Tree<String> t8;
	Tree<String> t9;
	Tree<String> t10;
	Tree<String> t11;
	
    @Before
    public void setUp() throws Exception {
    	t11 = new Tree<String>("Eleven");
    	t10 = new Tree<String>("Ten");
    	t9 = new Tree<String>("Nine");
    	t8 = new Tree<String>("Eight");
    	t7 = new Tree<String>("Seven");
    	t6 = new Tree<String>("Six");
    	t5 = new Tree<String>("Five");
    	t4 = new Tree<String>("Four");
    	t3 = new Tree<String>("Three");
    	t2 = new Tree<String>("Two", t3, t4);
    	t1 = new Tree<String>("One");
    	t0 = new Tree<String>("Zero", t1, t2);
    	
    }

    @Test
    public final void testHashCode() {
        assertEquals(6678, t0.hashCode());
        //create local tree of exact same structure and contents as above
    	Tree<String> t40 = new Tree<String>("Four");
        Tree<String> t30 = new Tree<String>("Three");
    	Tree<String> t20 = new Tree<String>("Two", t30, t40);
    	Tree<String> t10 = new Tree<String>("One");
    	Tree<String> t00 = new Tree<String>("Zero", t10, t20);
    	assertEquals(6678, t00.hashCode());
    	//check to make sure variation occurs
    	assertEquals(412, t4.hashCode());
    }

    @Test
    public final void testTree() {
    	ArrayList<Tree<String>> testAL = new ArrayList<Tree<String>>();
    	testAL.add(t1); testAL.add(t2);
        assertEquals(t0.getChild(0), testAL.get(0));
        assertEquals(t0.getChild(1), testAL.get(1));
    	assertEquals(t2.getChild(0), t3);
    }

    @Test
    public final void testSetValue() {
        t0.setValue("OneMillion");
        assertEquals("OneMillion", t0.getValue());
        t0.setValue("Nothing");
        assertEquals("Nothing", t0.getValue());
    }

    @Test
    public final void testGetValue() {
        assertEquals("Zero", t0.getValue());
        assertEquals("Three", t3.getValue());
        assertEquals("Seven", t7.getValue());

    }

    @Test
    public final void testAddChildIntTreeOfV() {
    	ArrayList<Tree<String>> testAL = new ArrayList<Tree<String>>();
    	testAL.add(t1); testAL.add(t2); testAL.add(t5); testAL.add(t6);
    	t0.addChild(1, t5);
        assertEquals(t0.getChild(1), testAL.get(2));
        t0.addChild(1, t6);
        assertEquals(t0.getChild(1), testAL.get(3));
        assertEquals(t0.getChild(2), testAL.get(2));
    }

    @Test
    public final void testAddChildTreeOfV() {
    	ArrayList<Tree<String>> testAL = new ArrayList<Tree<String>>();
    	testAL.add(t1); testAL.add(t2); testAL.add(t5); testAL.add(t6);
    	t0.addChild(t5);
        assertEquals(t0.getChild(2), testAL.get(2));
        t0.addChild(t6);
        assertEquals(t0.getChild(2), testAL.get(2));
        assertEquals(t0.getChild(3), testAL.get(3));
    }

    @Test
    public final void testAddChildren() {
    	ArrayList<Tree<String>> testAL = new ArrayList<Tree<String>>();
    	testAL.add(t1); testAL.add(t2); testAL.add(t5); testAL.add(t6); testAL.add(t7); testAL.add(t8);
    	t0.addChildren(t5, t6, t7, t8);
        assertEquals(t0.getChild(0), testAL.get(0));
        assertEquals(t0.getChild(5), testAL.get(5));
    }

    @Test
    public final void testGetNumberOfChildren() {
        assertEquals(2, t0.getNumberOfChildren());
        assertEquals(0, t1.getNumberOfChildren());
        t0.addChildren(t3, t4, t5, t6, t7);
        assertEquals(7, t0.getNumberOfChildren());
    }

    @Test
    public final void testGetChild() {
        assertEquals(t1, t0.getChild(0));
        assertEquals(t3, t2.getChild(0));
        t0.addChild(0, t5);
        assertEquals(t5, t0.getChild(0));
    }

    @Test
    public final void testIterator() {
    	Iterator iter = t0.iterator();
    	assertTrue(iter.hasNext());
    	assertEquals(t1, iter.next());
    	assertTrue(iter.hasNext());
    	assertEquals(t2, iter.next());
    	assertFalse(iter.hasNext());
    	
    }

    @Test
    public final void testContains() {
        assertTrue(t0.contains(t1));
        t3.addChild(t5);
        t5.addChildren(t6, t7);
        t7.addChildren(t8, t9, t10);
        assertTrue(t0.contains(t1));
        assertTrue(t0.contains(t2));
        assertTrue(t0.contains(t3));
        assertTrue(t0.contains(t4));
        assertTrue(t0.contains(t5));
        assertTrue(t0.contains(t6));
        assertTrue(t0.contains(t7));
        assertTrue(t0.contains(t8));
        assertTrue(t0.contains(t9));
        assertTrue(t0.contains(t10));
        assertFalse(t0.contains(t11));
        
    }

    @Test
    public final void testToString() {
        assertEquals("Zero (One Two (Three Four))", t0.toString());
        t1.addChildren(t7, t8);
        assertEquals("Zero (One (Seven Eight) Two (Three Four))", t0.toString());
    }

    @Test
    public final void testEqualsObject() {
    	Tree<String> t40 = new Tree<String>("Four");
        Tree<String> t30 = new Tree<String>("Three");
    	Tree<String> t20 = new Tree<String>("Two", t30, t40);
    	Tree<String> t10 = new Tree<String>("One");
    	Tree<String> t00 = new Tree<String>("Zero", t10, t20);
        assertTrue(t0.equals(t00));
        Tree<String> t400 = new Tree<String>("Four");
        Tree<String> t300 = new Tree<String>("Three");
    	Tree<String> t200 = new Tree<String>("Two", t400, t300);
    	Tree<String> t100 = new Tree<String>("One");
    	Tree<String> t000 = new Tree<String>("Zero", t100, t200);
    	//all this does is switch order of nodes on branch but not children - still false
        assertFalse(t0.equals(t000));
        Tree<String> t4000 = new Tree<String>("Four");
        Tree<String> t3000 = new Tree<String>("Three");
    	Tree<String> t1000 = new Tree<String>("One");
    	Tree<String> t2000 = new Tree<String>("Two", t1000, t3000);
    	Tree<String> t0000 = new Tree<String>("Zero", t4000, t3000);
    	//this switches children between nodes
        assertFalse(t0.equals(t0000));

    }

    @Test
    public final void testParseString() {
    	Tree<String> tree = Tree.parse("one (two three (four five(six seven eight)) )");
        assertNotNull(tree);
        assertEquals(tree.getValue(), "one");
        assertEquals("eight", tree.getChild(1).getChild(1).getChild(2).getValue());
    }

}
