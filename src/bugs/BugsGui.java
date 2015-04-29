package bugs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tree.Tree;


/**
 * GUI for Bugs language Interpreter.
 * @author Ryan Smith
 * @author Dave Matuszek
 * @version March 2015
 */
public class BugsGui extends JFrame {
    private static final long serialVersionUID = 1L;
    JPanel display;
    JSlider speedControl;
    JButton stepButton;
    JButton runButton;
    JButton pauseButton;
    JButton resetButton;
    Interpreter interpreter;
    View view;
    
    /**
     * GUI constructor.
     */
    public BugsGui() {
        super();
        setSize(600, 600);
        setLayout(new BorderLayout());
        createAndInstallMenus();
        createDisplayPanel();
        createControlPanel();
        initializeButtons();
        setVisible(true);
    }
    
    /**
     * Creates and installs GUI menus.
     */
    private void createAndInstallMenus() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");      
        JMenuItem loadMenuItem = new JMenuItem("Load");
        JMenuItem quitMenuItem = new JMenuItem("Quit");
        
        
        menuBar.add(fileMenu);
        fileMenu.add(loadMenuItem);
        fileMenu.add(quitMenuItem);
        
        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                quit();
            }});
        
        loadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                load();
            }});
        
        this.setJMenuBar(menuBar);
    }
    
    /**
     * Creates display panels.
     */
    private void createDisplayPanel() {
    	//creates View object (custom JPanel) and places it in the center of the GUI
        view = new View();
        add(view, BorderLayout.CENTER);
    }

    /**
     * Creates control panel.
     */
    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        
        addSpeedLabel(controlPanel);       
        addSpeedControl(controlPanel);
        addStepButton(controlPanel);
        addRunButton(controlPanel);
        addPauseButton(controlPanel);
        addResetButton(controlPanel);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Adds label for Speed.
     */
    private void addSpeedLabel(JPanel controlPanel) {
        controlPanel.add(new JLabel("Speed:"));
    }
    
    /**
     * Adds speed control to the GUI
     */
    private void addSpeedControl(JPanel controlPanel) {
        speedControl = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 90);
        speedControl.setMajorTickSpacing(10);
        speedControl.setMinorTickSpacing(5);
        speedControl.setPaintTicks(true);
        speedControl.setPaintLabels(true);
        speedControl.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent arg0) {
            	if (!speedControl.getValueIsAdjusting()){
            		interpreter.updatePauseTime(calculateSpeed(speedControl.getValue()));
            	}
            }
        });
        controlPanel.add(speedControl);
    }
    
    /**
     * Calculates speed to be represented by changes in speed controller.
     */
    private int calculateSpeed(int speedSetting){
    	speedSetting = speedSetting * 10;
    	if (speedSetting == 1000){
    		speedSetting = 5;
    	}
    	else if (speedSetting == 0){
    		speedSetting = 1000;
    	}
    	else{
    		speedSetting = 1000 - speedSetting;
    	}
    	return speedSetting;
    }
    
    /**
     * Adds step button to GUI.
     */
    private void addStepButton(JPanel controlPanel) {
        stepButton = new JButton("Step");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepAnimation();
            }
        });
        controlPanel.add(stepButton);
    }

    /**
     * Adds run button to GUI.
     */
    private void addRunButton(JPanel controlPanel) {
        runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runAnimation();
            }
        });
        controlPanel.add(runButton);
    }

    /**
     * Adds pause button to GUI.
     */
    private void addPauseButton(JPanel controlPanel) {
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseAnimation();
            }
        });
        controlPanel.add(pauseButton);
    }

    /**
     * Adds reset button to GUI.
     */
    private void addResetButton(JPanel controlPanel) {
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetAnimation();
            }
        });
        controlPanel.add(resetButton);
    }
    
    /**
     * Initializes buttons on GUI.
     */
    private void initializeButtons() {
        stepButton.setEnabled(false);
        runButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
    }
    
    /**
     * Responds to step button.
     */
    protected void stepAnimation() {
    	interpreter.giveOneTimePermission();
        stepButton.setEnabled(true);
        runButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(true);
    }
    
    /**
     * Responds to run button.
     */
    protected void runAnimation() {
    	interpreter.setPermissionToGrant(true);
        stepButton.setEnabled(true);
        runButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resetButton.setEnabled(true);
    	
    }
    
    /**
     * Responds to pause button.
     */
    protected void pauseAnimation() {
    	interpreter.setPermissionToGrant(false);
        stepButton.setEnabled(true);
        runButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(true);
    }
    
    /**
     * Responds to reset button.
     */
    protected void resetAnimation() {
    	Tree<Token> programTree = interpreter.getLastProgramTree();
    	interpreter.kill();
    	speedControl.setValue(90);
    	interpreter = new Interpreter(programTree);
    	view.addInterpreter(interpreter);
    	interpreter.start();
        stepButton.setEnabled(true);
        runButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
    }
    
    /**
     * Responds to quit button.
     */
    protected void quit() {
        System.exit(0);
    }
    
    /**
     * Responds to load menu item.
     */
    protected void load(){
    	try {
    		speedControl.setValue(90);
    		//use JFileChooser to allow user to select Bugs program and read in the whole file
			BufferedReader reader = getFileReader();
			StringBuffer entireFile = new StringBuffer();
			String currentLine = null;
			while((currentLine = reader.readLine())!=null){
				entireFile.append(currentLine).append("\n");
			}
			//Parse the file
			Parser parser = new Parser(entireFile.toString());
			if (!parser.isProgram()){
				JOptionPane.showMessageDialog(this, "There was an error parsing the file.");
			}
			else{
				//Create a new Interpreter and provide with the AST created by the Parser
				interpreter = new Interpreter(parser.stack.pop());
				//adds the Interpreter to the View for drawing
				view.addInterpreter(interpreter);
				stepButton.setEnabled(true);
		        runButton.setEnabled(true);
		        interpreter.start();
			}
				
		} 
    	catch (IOException e) {
    		JOptionPane.showMessageDialog(this, "There was an error reading the file.");
		}
    	catch (SyntaxException se) {
    		String message = "There was an error parsing the file.\n" + se.toString();
    		JOptionPane.showMessageDialog(this, message);
    	}
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
    /**
     * Runs GUI.
     * @param args
     */
    public static void main(String[] args) {
        new BugsGui();
    }
}
