package bugs;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for Bugs Language Parser.
 * Runs 4 test files in this package (tree).
 * 
 * @author Ryan Smith
 * @author Dave Matuszek
 * @version February 2015
 */
@RunWith(value=Suite.class)
@SuiteClasses(value= {TokenTest.class,
                      ParserTest.class,
                      tree.TreeTest.class,
                      TreeParserTest.class,
                      BugTest.class,
                      InterpreterTest.class})
public class AllTests {
    // Empty class
}
