
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.*;


public class Main {


    private ArrayList<JTextField> solution = new ArrayList<JTextField>();
    private static int solutionLength;

    public static void main(String[] args) {
        // Solution is given by argument
        // The String - 'correct answer' the programme receives it parametrically upon its execution
        String answer = args[0];
        solutionLength = answer.length();

        // System.out.println(args[0]);

        ArrayList<String> charPanel = new ArrayList<String>(); // Creates an ArrayList of the letters of the crossword
        ArrayList<String> words = new ArrayList<String>(); // Creates an ArrayList of the words from the given txt file (data.txt)

        String hints = "";
        Keyboard ui = new Keyboard(); // Creates the object Keyboard
        int counter = 0;
        String[] arrOfStr = {"", ""};

        // -------------------------------- read and parse external file

        try {
            File myObj = new File("data.txt");
            Scanner myReader = new Scanner(myObj);
            String data;


            words.add("Crossword puzzle instructions:");
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();

                // examines the dimensions of the crossword from the data.txt file
                if (counter == 0) {
                    arrOfStr = data.split(" ", 2);

                    //  System.out.println("\nwidth: " + w + " height: " + h);
                }
                // enters the characters of the .txt file in the list
                else if (counter < (Integer.parseInt(arrOfStr[0]) + 1)) {
                    charPanel.add(data);
                } else {
                    words.add(data);
                }
                counter++;
            }

            // printing in order to check the contents
            //printData(charPanel);

            //System.out.println("\nQuestions data:");
            // create final strings (hints) for left panel
            for (String elem : words) {
                if (elem.equals("ACROSS") || elem.equals("DOWN") ){
                    elem = "\n" + elem;
                }
                hints += elem + "\n";
                //System.out.println(elem);
            }

            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. Data file is missing?");
            e.printStackTrace();
        }

        // crates main panel as split panel
        final int w = Integer.parseInt(arrOfStr[0]); // dimensions of the crossword puzzle
        final int h = Integer.parseInt(arrOfStr[1]); // regarding first line at data.txt file

        UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder());
        String finalHints = hints;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            // UI initialization of the overall crossword application
            public void run() {
                new Main().initUI(charPanel, w, h, finalHints, ui, answer);
            }
        });

    }

    //---------------------------------------------------------------------------

    // Method to check if some hints must be bold formatted
    // is not used at the moment
    public static boolean checkHint(String s)
    {
        boolean result = false;
        int pos = -1;
        if (s.length() > 4){
            if(s.contains(". ")){
                pos = s.indexOf(".");
            }
        }
        if (pos != -1){
            if (
                // isolation of hints only
                    Character.isDigit(s.charAt(pos + 2)) &&
                            Character.isDigit(s.charAt(pos + 3)) &&
                            Character.isDigit(s.charAt(pos + 4)) &&
                            Character.isDigit(s.charAt(pos + 5))
            ) {
                result = true;
            }
        }
        return result;
    }

    //---------------------------------------------------------------------------
    //checks if the user finds the correct answer
    private void checkButtonPressed(String answer)
    {
        String result = "";
        String infoMessage = "The answer is not correct. Please try again.";
        String titleBar =  "Results";

        for (JTextField tf : solution)
        {
            result +=  tf.getText();
        }

        if (result.equals(answer.toUpperCase()))
        {
            infoMessage = "Congratulations the correct answer is found!!";
        }

        // creates an infobox result with message
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);

        //System.out.println(result);
    }

    //---------------------------------------------------------------------------

    // UI initialization
    public void initUI(ArrayList<String> charPanel, int w, int h, String hints, Keyboard ui, String answer) {

        JPanel jp = new JPanel();
        jp.setPreferredSize(new Dimension(600, 100));
        JTextField tf;
        int i = 0;

        // creates the panel for the puzzle’s solution field (grey boxes)
        while (i < solutionLength) {

            tf = new JTextField(String.valueOf(' '));
            tf.setEnabled(false);
            tf.setPreferredSize( new Dimension( 45, 45 ) );
            tf.setBackground(Color.GRAY);
            tf.setBorder(new LineBorder(Color.BLACK, 1));
            tf.setDisabledTextColor(Color.BLACK); // Color of text in each box
            JTextField textField = new JTextField(20);
            Font newTextFieldFont = new Font(textField.getFont().getName(),textField.getFont().getStyle(),28);
            tf.setFont(newTextFieldFont);
            tf.setHorizontalAlignment(JTextField.CENTER); // Position of text in each box

            tf.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JTextField cell = (JTextField) e.getSource();
                    ui.showKeyboard(cell);

                }
            });
            solution.add(tf);
            jp.add(tf); //myPanel is the JPanel where I want to put the JTextFields
            i++;
        }

        // creates the ‘Check’ button next to the grey boxes.
        JButton b = new JButton("Check");
        b. setBounds(50, 150, 100, 30);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkButtonPressed(answer);
            }
        } );
        jp.add(b);

        // creates the final panel regarding the positions of each sub-panel
        JSplitPane jspl = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                createAndShowGUI(charPanel, w, h, ui), jp );
        jspl = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getHints(hints), jspl);

        JFrame frame = new JFrame("Swedish-style crossword puzzle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(jspl);
        frame.pack();

        // shows the final UI of the application
        frame.setVisible(true);

    }

    //---------------------------------------------------------------------------

    // configuring the crossword hints and returning them to the corresponding panel
    public JScrollPane getHints(String hints) {
        JTextArea textArea = new JTextArea(20, 20);
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        for (String elem : hints.split("\n")) {
            if(checkHint(elem)){
                textArea.setFont(new Font("SANS_SERIF", Font.BOLD, 14));
            }
            else{
                textArea.setFont(new Font("SANS_SERIF", Font.PLAIN, 14));
            }
            textArea.append(elem);
            textArea.append("\n");
        }
        scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        textArea.setEnabled(false);
        return scrollableTextArea;
    }

    // creates and shows the panel of the crossword
    public static JScrollPane createAndShowGUI(ArrayList<String> charPanel, int w, int h, Keyboard ui) {

        final CrosswordPanel panel = new CrosswordPanel();
        panel.setPreferredSize(new Dimension(650, 650));
        generate(panel, charPanel, w, h, ui);
        JScrollPane container = new JScrollPane(panel);

        return container;
    }

    //--------------------------------------------------------------------------

    //creates the crossword as a data structure
    private static void generate(CrosswordPanel panel, ArrayList<String> charPanel, int w, int h, Keyboard ui) {

        String[][] crossword = new String[w][h];
        ArrayList<String> finalList = new ArrayList<>();

        // extract string form charPanel and add them to final list
        for (String elem : charPanel) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(elem.split(" ")));
            for (String word : list) {
                finalList.add(word);
            }
        }

        int counter = 0;

        // add content of final list into 2-d array, representing the crossword point of view
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                crossword[j][i] = finalList.get(counter);
                counter++;
            }
        }

        panel.setCrossword(crossword, ui);
    }
}



//----------------------------------------------------------------------- class CrosswordPanel

class CrosswordPanel extends JPanel {

    private JTextField[][] tf;
    private JTextField selectedBox;

    //---------------------------------------------------------------------------
    //manages the crossword data structure in relation to the UI, data.txt information and project specifications
    void setCrossword(String[][] array, Keyboard ui) {
        removeAll(); // clear all previous entries
        int w = array.length;
        int h = array[0].length;
        setLayout(new GridLayout(w, h));
        tf = new JTextField[w][h];
        ui.launch();


        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                String str = array[x][y];
                System.out.println(str);
                if (str != "") {

                    tf[x][y] = new JTextField(String.valueOf(' '));
                    tf[x][y].setEnabled(false);
                    tf[x][y].setBorder(new LineBorder(Color.BLACK, 1));
                    tf[x][y].setHorizontalAlignment(JTextField.CENTER); // Position of text in each box
                    tf[x][y].setDisabledTextColor(Color.BLACK); // Color of text in each box

                    if (str.equals("X")) {  // black cell
                        tf[x][y].setBackground(Color.BLACK);


                    } else {
                        selectedBox = tf[0][0];
                        tf[x][y].setFont(tf[x][y].getFont().deriveFont(30.0f));
                        tf[x][y].addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                JTextField cell = (JTextField)e.getSource();
                                ui.showKeyboard(cell);
                                System.out.println(cell.getText());
                                System.out.println("cell clicked - color " + cell.getBackground().getRed());
                            }
                        });

                        if (str.equals("O")) {  // white cell
                            tf[x][y].setBackground(Color.WHITE);
                        } else if (str.equals("S")) {  // grey cell
                            tf[x][y].setBackground(Color.GRAY);
                        } else {  // cyan cell
                            tf[x][y].setBackground(Color.CYAN); // r=0,g=255,b=255
                            tf[x][y].setToolTipText(str.substring(2,3));
                            ToolTipManager.sharedInstance().setEnabled(false);
                        }

                    }

                    add(tf[x][y]);
                } else {
                    add(new JLabel());
                }
            }
        }
    }
}


// -------------------------------------------------------- class keyboard

class Keyboard {

    private final JFrame frm = new JFrame("Enter a character");
    private JPanel keyboard = new JPanel();
    private static final String[][] key = {
            {"A", "Z", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"Q", "S", "D", "F", "G", "H", "J", "K", "L", "M"},
            {"W", "X", "C", "V", "B", "N"}
    };
    private JTextField active;
    private String content;
    private ArrayList<JButton> allKeyboardButtons = new ArrayList<JButton>();

    //--------------------------------------------------------------------------

    public Keyboard() {
        // create a keyboard with the appearance and functionality of all its buttons
        createNormalKeyboard();
    }

    //--------------------------------------------------------------------------

    public void showKeyboard(JTextField selectedBox) {

        frm.setVisible(true); //display the keyboard widget

        active = selectedBox; // save the most recently selected box selected by the user
        int i = 0;
        int found = 0;
        int times = 4;

        for (JButton jb : allKeyboardButtons)
        {
            jb.setEnabled(true);
        }

        if (selectedBox.getToolTipText() != null)
        {
            //find out if the user clicked on a blue box and check for the hidden information found as a tool tip text in it.
            for (JButton jb : allKeyboardButtons) {
                if (!jb.getText().equals(selectedBox.getToolTipText())) {
                    jb.setEnabled(false); // disable all the buttons except the button that is in the cyan box.
                } else {
                    found = i;
                }
                i++;
            }
            int randomNum;

            // activation of 4 more buttons
            do {
                do{
                    randomNum = ThreadLocalRandom.current().nextInt(0, 26); // 26 buttons
                }while (found == randomNum);
                allKeyboardButtons.get(randomNum).setEnabled(true);
                times--;
            }while (times > 0);

        }

    }

    //--------------------------------------------------------------------------

    // specifications of the virtual keyboard
    public void launch() {
        frm.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frm.pack();
        frm.setResizable(false);
        frm.setLocationRelativeTo(null);
        frm.setVisible(false);
    }

    //--------------------------------------------------------------------------

    // creation of the Keyboard with respect the instructions form the project assignment
    private void createNormalKeyboard(){

        keyboard.setLayout(new GridBagLayout());
        Insets zeroInset = new Insets(3, 6, 3, 6);
        Font sansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
        JPanel kbRow;
        JButton btn;
        GridBagConstraints kbClmn = new GridBagConstraints();
        GridBagConstraints cButton = new GridBagConstraints();
        kbClmn.anchor = GridBagConstraints.CENTER;
        cButton.ipady = 6;

        // first dimension of the key array
        // representing a row on the keyboard
        for (int row = 0, i = 0; row < key.length; ++row) {
            kbRow = new JPanel(new GridBagLayout());

            kbClmn.gridy = row;

            // second dimension representing each key
            for (int col = 0; col < key[row].length; ++col, ++i) {

                // specify padding and insets for the buttons
                switch (key[row][col]) {
                    default:
                        cButton.ipadx = 15;
                        cButton.insets = zeroInset;
                }

                // specifications of each button in order to be similar to those in the assignment
                btn = new JButton(key[row][col]);
                btn.setFont(sansSerif);
                btn.setFocusable(false);
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // discover which button is pressed
                        content = ((JButton) e.getSource()).getText();

                        //add content of button into active box of crossword
                        active.setText(content);

                        // hide keyboard from user
                        frm.setVisible(false);
                    }
                });
                kbRow.add(btn, cButton);
                allKeyboardButtons.add(btn); // add each button to a list
            }

            keyboard.add(kbRow, kbClmn);
        }

        frm.add(keyboard);

    }

}
