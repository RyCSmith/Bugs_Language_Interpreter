package bugs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import tree.Tree;

public class TestInterpreter {
	static Interpreter interpreter;
	public static void main(String[] args) {
		load();

	}
    protected static void load(){
//    	try {
//    		//use JFileChooser to allow user to select Bugs program and read in the whole file
//			BufferedReader reader = getFileReader();
//			StringBuffer entireFile = new StringBuffer();
//			String currentLine = null;
//			while((currentLine = reader.readLine())!=null){
//				entireFile.append(currentLine).append("\n");
//			}
//			//Parse the file
//			Parser parser = new Parser(entireFile.toString());
//			if (!parser.isProgram()){
//				System.out.println("There was an error parsing the file.");
//			}
//			else{
//				//Create a new Interpreter and provide with the AST created by the Parser
////				interpreter = new Interpreter(parser.stack.pop());
//				Tree<Token> tree = parser.stack.pop();
//				double d = 3.0;
//			}
////			interpreter.start();
//				
//		} 
//    	catch (IOException e) {
//    		System.out.println("There was an error reading the file.");
//		}
//    	catch (SyntaxException se) {
//    		String message = "There was an error parsing the file.\n" + se.toString();
//    		System.out.println(message);
//    	}
    	Parser parser = new Parser("Bug TestRecursion {\nx = 0\ny = 50\ncolor black\ndo drawJaggedLine(100, 0)\ndefine drawJaggedLine using dist, dir {\nturnto dir\nswitch {\ncase dist < 2\nmove dist\ncase dist >= 2\ndo drawJaggedLine(dist / 3, dir)\ndo drawJaggedLine(dist / 3, dir + 60)\ndo drawJaggedLine(dist / 3, dir - 60)\ndo drawJaggedLine(dist / 3, dir)\n}\nreturn 0\n}\n}\n");
    	parser.isProgram();
    	Tree<Token> tree =parser.stack.pop();
    	int s = 2;
    }
    
    /**
	 * Opens JFileChooser and allows the user to select a file of puzzles.
	 * @return BufferedReader references the file stream 
	 */
	private static BufferedReader getFileReader() throws IOException {
        BufferedReader reader = null;
        String fileName;
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load which file?");
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                fileName = file.getCanonicalPath();
                reader = new BufferedReader(new FileReader(fileName));
            }
        }
        return reader;
    }
}
