/* Author: Annie Paul
 * Description: Windows 10 Programming Calculator
 */

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Stack;

public class Calculator extends JFrame implements ActionListener {
	
	public static void main(String[] args) {

        Calculator c = new Calculator(); // calculator extends JFrame itself

        c.setLayout(new GridBagLayout());
        c.setTitle("Calculator");
        c.setVisible(true);
        c.setSize(400, 1000);
        c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c.setResizable(false);
        c.pack();

    } // end main
	
    JButton bOpenParan, bClosedParan, bEmpty, bZero, bDecimal, bE, bF, bOne, bTwo, bThree,
            bEquals, bPlus, bC, bD, bFour, bFive, bSix, bMinus, bA, bB, bSeven, bEight,
            bNine, bMult, bUp, bMod, bCE, bClear, bDelete, bDivide, bLSH, bRSH, bOr,
            bXor, bNot, bAnd, b10Dots, b5Dots, bWord, bMS, bMemory, bMenu;

    GridBagLayout layout;
    GridBagConstraints c;
    JPanel pane;
    private JLabel bigLabel, lilLabel, programmerLabel, binLab, octLab,
            decLab, hexLab;
    private JToggleButton bBin, bOct, bDec, bHex;
    boolean binSelected = false, octSelected = false, decSelected = true, hexSelected = false;
    ButtonGroup myButtonGroup;             // adds all abstract buttons to this group so only 1 can be selected at a time
    Enumeration elements;                  // helps with buttonGroup
    String buttonSelected = "DEC";         // by default sets base to decimal
    String userEnteredTotalString = "";    // updates with exactly what the user types in
    String userEnteredNum = "";            // updates with the number the user types in, updates every click
    String userEnteredOperator = "";       // updates every click with the operator the user types
    String toSendEvaluateExpression = "";  // string to send to evaluateExpression
    String lastEnteredNum;                 // last entered number
    String totalNum = "";                  // stores the whole number the user types in, stores through clicks
    char base = 'd';                       // set default base to decimal
    String decUserInput = "0";             // takes the value from bigLabel and converts to decimal, populates other bases labels

    public Calculator() {
        addEverything();
        enableDecButtons();
    } // end constructor

    public void actionPerformed(ActionEvent e) {
        System.out.println("==================");
        String bigLabelExistingNums = bigLabel.getText();

        // Actions
        //01. Number Typed: 0-9, A-F
        //02. Operator Typed: +, -, *, /, %, (, ), =
        //03. Base Changed: HEX, DEC, OCT, BIN
        //04. DataType Changed: BYTE, WORD, DWORD, QWORD
        //05. Other buttons: C, CE, DEL,

        // clear button function
        if (e.getSource() == bClear) {
            userEnteredTotalString = "";
            totalNum = "";
            clearNumAndOperator();
            bigLabel.setText("0");
            lilLabel.setText(userEnteredTotalString);
            updateAllBases(bigLabel.getText());
            return;
        }

        // clear entry button
        if (e.getSource() == bCE) {
            totalNum = "";
            bigLabel.setText("0");
            clearNumAndOperator();
            updateAllBases(bigLabel.getText());
            return;
        }

        // Delete button - Delete only if the user keyed-in values are all numbers
        if (e.getSource() == bDelete) {
            if (totalNum != null && totalNum.length() > 0 && Utils.isNum(totalNum)) {
                totalNum = totalNum.substring(0, totalNum.length() - 1);
                userEnteredTotalString = userEnteredTotalString.substring(0, userEnteredTotalString.length() - 1);
            }
            if (bigLabel.getText() != null && bigLabel.getText().length() > 0 && Utils.isNum(bigLabel.getText())) {
                bigLabel.setText(bigLabel.getText().substring(0, bigLabel.getText().length() -1));
            }
            if (bigLabel.getText() != null && bigLabel.getText().length() == 0) {
                bigLabel.setText("0");
            }
            clearNumAndOperator();
            updateAllBases(bigLabel.getText());
            return;
        }

        setUserEnteredNum(e);       //populates userEnteredNum and lastEnteredNum
        setUserEnteredOperator(e);  //userEnteredOperator

        totalNum += userEnteredNum; // concatenate the number the user just clicked with the previously clicked numbers

        userEnteredTotalString += userEnteredNum + userEnteredOperator; // update userEnteredTotalString with the number or operator the user typed

        clearNumAndOperator();     // clears userEnteredNum and userEnteredOperator for next time

        System.out.println("userEnteredTotalString: " + userEnteredTotalString);

        // only proceed if the user has typed something
        if (userEnteredTotalString.length() > 0 && totalNum != "") {

            // first check if totalNum is within bounds
            if (totalNumWithinUpperBounds()) {

            	// Updates big label on conditions depending if last char is operator or operand
                char lastChar = userEnteredTotalString.charAt(userEnteredTotalString.length() - 1);
                if (lastChar == '(') {
                    System.out.println("INSIDE lastChar == '('");
                    lilLabel.setText(userEnteredTotalString);
                    return;
                } else if (lastChar == ')') {
                    lilLabel.setText(userEnteredTotalString);
                }

                if (lastChar == '+' || lastChar == '-' || lastChar == '='|| lastChar == '('|| lastChar == '/'
                        || lastChar == '%' || lastChar == '*'|| lastChar == ')' || lastChar == '%') {

                    // only send to evaluateExpression if none or matched parantheses
                    if (Utils.checkMatchedOpenClosedParan(userEnteredTotalString)) {
                        System.out.println("inside check match closed");
                    	toSendEvaluateExpression = userEnteredTotalString.substring(0, userEnteredTotalString.length() - 1);
                        System.out.println("evaluating: " + toSendEvaluateExpression);

                        // if base is not decimal, convert to decimal and then evaluate
                        if (base != 'd') {
                            toSendEvaluateExpression = Utils.convertToDec(toSendEvaluateExpression, base);
                        }

                        //Convert decimal back to base
                        String result = evaluateExpression(toSendEvaluateExpression, base);
                        String decResult = result; // hold decimal result for later

                        System.out.println("result straight out of evaluateExpression: " + result);

                        if (base != 'd') {
                            result = Utils.convertToType(result, base);
                        }

                        System.out.println("big label" + result);
                        bigLabel.setText(result);

                        // set lilbaselabels on left
                        // take result, convert to each base, then display
                        System.out.println("displaying all vals on left");
                        System.out.println("decResult: " + decResult);
                        displayHexVal(decResult);
                        displayDecVal(decResult);
                        displayOctVal(decResult);
                        displayBinVal(decResult);

                        lilLabel.setText(userEnteredTotalString);
                        if (lastChar == '=') {
                            userEnteredTotalString = " ";
                            lilLabel.setText(userEnteredTotalString);
                            return;
                        }
                    } else {                           // if parantheses don't match
                        bigLabel.setText(totalNum);
                        lilLabel.setText(userEnteredTotalString);
                    }
                    totalNum = "";

                } else {                               // if a number (and not an operator) is pressed
                    bigLabel.setText(totalNum);
                }
            } else {                                   // if the number is above bounds based on mode
                userEnteredTotalString = userEnteredTotalString.substring(0, userEnteredTotalString.length() - 1); // remove what the user typed
                totalNum = totalNum.substring(0, totalNum.length() - 1);
                return;
            } // end if totalNum within bounds

        } // end if user has typed something

        bigLabelExistingNums = bigLabel.getText();

        // allows selection of only one of dec, hex, bin, oct
        elements = myButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton)elements.nextElement();
            if (button.isSelected()) {
                buttonSelected = button.getText();
                System.out.println("Selected Base Button: " + buttonSelected);
            }
        } // end while
        
        String temp = "";
        
        // set buttons and labels correctly for each mode
        if (buttonSelected == "BIN") {
            enableBinaryButtons();
            if (base != 'b') {
            	temp = Utils.convertToDec(userEnteredTotalString, base);
            	userEnteredTotalString = Utils.convertToType(temp, 'b');
            	lilLabel.setText(userEnteredTotalString);
                bigLabel.setText(binLab.getText());
                totalNum = bigLabel.getText();
            }
            base = 'b';

        } else if (buttonSelected == "OCT") {
            enableOctButtons();
            if (base != 'o') {
            	temp = Utils.convertToDec(userEnteredTotalString, base);
            	userEnteredTotalString = Utils.convertToType(temp, 'o');
            	lilLabel.setText(userEnteredTotalString);
            	
            	bigLabel.setText(octLab.getText());
                totalNum = bigLabel.getText();
            }
            base = 'o';
        } else if (buttonSelected == "DEC") {
            enableDecButtons();
            if (base != 'd') {
            	temp = Utils.convertToDec(userEnteredTotalString, base);
            	userEnteredTotalString = Utils.convertToType(temp, 'd');
            	lilLabel.setText(userEnteredTotalString);
            	
                bigLabel.setText(decLab.getText());
                totalNum = bigLabel.getText();
            }
            base = 'd';

        } else if (buttonSelected == "HEX") {
            enableHexButtons();
            if (base != 'h') {
            	temp = Utils.convertToDec(userEnteredTotalString, base);
            	userEnteredTotalString = Utils.convertToType(temp, 'h');
            	lilLabel.setText(userEnteredTotalString);
            	
                bigLabel.setText(hexLab.getText());
                totalNum = bigLabel.getText();
            }
            base = 'h';
        }
        
        // update calculator with correct max size IF USER CLICKS BWORD(which is default when calculator first runs)
        if (e.getSource() == bWord) {
        	if (bWord.getText() == "QWORD") {
                setBigLabelAfterBoundChange();
        	    bWord.setText("DWORD");

        	} else if (bWord.getText() == "DWORD") {
        		bWord.setText("WORD");
        	} else if (bWord.getText() == "WORD") {
        		bWord.setText("BYTE");
        	} else if (bWord.getText() == "BYTE") {
        		bWord.setText("QWORD");
        	}
        }

        // update other bases as the user types in numbers
        bigLabelExistingNums = bigLabel.getText();

        // convert current base bigLabel content into decimal
        if (base == 'd') {
            decUserInput = bigLabelExistingNums;
        } else if (base == 'o') {
            decUserInput = Utils.convertFromOct(bigLabelExistingNums);
        } else if (base == 'b') {
            decUserInput = Utils.convertFromBinary(bigLabelExistingNums);
        } else if (base == 'h') {
            decUserInput = Utils.convertFromHex(bigLabelExistingNums);
        }

        // convert that decimal into all other bases and set label
        decLab.setText(decUserInput);
        octLab.setText(Utils.convertToOct(decUserInput));
        hexLab.setText(Utils.convertToHex(decUserInput));
        binLab.setText(Utils.convertToBinary(decUserInput));

    } // end actionPerformed

    // make sure that bigLabel has at least 0 to avoid error
    public void setBigLabelAfterBoundChange() {
        if (bigLabel.getText() == "") {
            bigLabel.setText("0");
        }
    }

    // checks if the number the user entered is within the bounds of the mode
    public boolean totalNumWithinUpperBounds() {
        String decVal = "";

    	if (totalNum != "" && !totalNum.isEmpty()) {
    	    // convert totalNum from base to decimal
            if (base == 'd') {
                decVal = totalNum;
            } else if (base == 'o') {
                decVal = Utils.convertFromOct(totalNum);
            } else if (base == 'b') {
                decVal = Utils.convertFromBinary(totalNum);
            } else if (base == 'h') {
                decVal = Utils.convertFromHex(totalNum);
            }

            if ((bWord.getText() == "BYTE" && (Long.valueOf(decVal) < 128)) ||
                    (bWord.getText() == "WORD" && (Long.valueOf(decVal) < 32768)) ||
                    (bWord.getText() == "DWORD" && (Long.valueOf(decVal) <= 2147483647)) ||
                    (bWord.getText() == "QWORD" && (Long.valueOf(decVal) <= 2147483647))) { // should be 9223372036854775807 but too large
                return true;
            } else {
                return false;
            }
        } else {
    	    return true;
        }

    } // end totalNumWithinUpperBounds

    // update base labels
    public void updateAllBases(String str) {
        displayHexVal(str);
        displayDecVal(str);
        displayOctVal(str);
        displayBinVal(str);
    }

    // clear userEnteredNum and userEnteredOperator to get ready for next click
    public void clearNumAndOperator() {
        userEnteredNum = "";
        userEnteredOperator = "";
    }

    // gets the action listener click and sets the userEnteredNum and lastEnteredNum
    public void setUserEnteredNum(ActionEvent e) {
        if (e.getSource() == bZero) {
            userEnteredNum = "0";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bOne) {
            userEnteredNum = "1";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bTwo) {
            userEnteredNum = "2";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bThree) {
            userEnteredNum = "3";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bFour) {
            userEnteredNum = "4";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bFive) {
            userEnteredNum = "5";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bSix) {
            userEnteredNum = "6";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bSeven) {
            userEnteredNum = "7";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bEight) {
            userEnteredNum = "8";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bNine) {
            userEnteredNum = "9";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bA) {
            userEnteredNum = "A";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bB) {
            userEnteredNum = "B";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bC) {
            userEnteredNum = "C";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bD) {
            userEnteredNum = "D";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bE) {
            userEnteredNum = "E";
            lastEnteredNum = userEnteredNum;
        } else if (e.getSource() == bF) {
            userEnteredNum = "F";
            lastEnteredNum = userEnteredNum;
        }
    } // end setUserEnteredNum

    // gets the action listener click and sets userEnteredOperator
    public void setUserEnteredOperator(ActionEvent e) {
        if (e.getSource() == bPlus) {
            userEnteredOperator = "+";
        } else if (e.getSource() == bMinus) {
            userEnteredOperator = "-";
        } else if (e.getSource() == bMult) {
            userEnteredOperator = "*";
        } else if (e.getSource() == bDivide) {
            userEnteredOperator = "/";
        } else if (e.getSource() == bEquals) {
            userEnteredOperator = "=";
        } else if (e.getSource() == bOpenParan) {
            userEnteredOperator = "(";
        } else if (e.getSource() == bClosedParan) {
            userEnteredOperator = ")";
        } else if (e.getSource() == bMod) {
            userEnteredOperator = "%";
        }

    } // end setUserEnteredOperator

    // enable buttons for binary base
    public void enableBinaryButtons() {
        bZero.setEnabled(true);
        bOne.setEnabled(true);

        bTwo.setEnabled(false);
        bThree.setEnabled(false);
        bFour.setEnabled(false);
        bFive.setEnabled(false);
        bSix.setEnabled(false);
        bSeven.setEnabled(false);
        bEight.setEnabled(false);
        bNine.setEnabled(false);
        bA.setEnabled(false);
        bB.setEnabled(false);
        bC.setEnabled(false);
        bD.setEnabled(false);
        bE.setEnabled(false);
        bF.setEnabled(false);
    } // end enableBinaryButtons

    // enable buttons for octal base
    public void enableOctButtons() {
        bZero.setEnabled(true);
        bOne.setEnabled(true);
        bTwo.setEnabled(true);
        bThree.setEnabled(true);
        bFour.setEnabled(true);
        bFive.setEnabled(true);
        bSix.setEnabled(true);
        bSeven.setEnabled(true);

        bEight.setEnabled(false);
        bNine.setEnabled(false);
        bA.setEnabled(false);
        bB.setEnabled(false);
        bC.setEnabled(false);
        bD.setEnabled(false);
        bE.setEnabled(false);
        bF.setEnabled(false);
    }

    // enable buttons for decimal base
    public void enableDecButtons() {
        bZero.setEnabled(true);
        bOne.setEnabled(true);
        bTwo.setEnabled(true);
        bThree.setEnabled(true);
        bFour.setEnabled(true);
        bFive.setEnabled(true);
        bSix.setEnabled(true);
        bSeven.setEnabled(true);
        bEight.setEnabled(true);
        bNine.setEnabled(true);

        bA.setEnabled(false);
        bB.setEnabled(false);
        bC.setEnabled(false);
        bD.setEnabled(false);
        bE.setEnabled(false);
        bF.setEnabled(false);
    }

    // enable buttons for hex base
    public void enableHexButtons() {
        bZero.setEnabled(true);
        bOne.setEnabled(true);
        bTwo.setEnabled(true);
        bThree.setEnabled(true);
        bFour.setEnabled(true);
        bFive.setEnabled(true);
        bSix.setEnabled(true);
        bSeven.setEnabled(true);
        bEight.setEnabled(true);
        bNine.setEnabled(true);
        bA.setEnabled(true);
        bB.setEnabled(true);
        bC.setEnabled(true);
        bD.setEnabled(true);
        bE.setEnabled(true);
        bF.setEnabled(true);
    }

    // makes sure string isnt empty and sets hex value on hex label
    public void displayHexVal(String str) {
        if (str != null &&!str.isEmpty()) {
            long decVal = Long.valueOf(str);
            String Hex = Long.toHexString(decVal);
            hexLab.setText(Hex);
        } else {
            hexLab.setText("0");
        }
    }

    // makes sure string isnt empty and sets decimal value on decimal label
    public void displayDecVal(String str) {
        if (str != null &&!str.isEmpty()) {
            decLab.setText(str);
        } else {
            decLab.setText("0");
        }
    }

    // makes sure string isnt empty and sets octal value on octal label
    public void displayOctVal(String str) {
        if (str != null &&!str.isEmpty()) {
            long decVal = Long.valueOf(str);
            String Oct = Long.toOctalString(decVal);
            octLab.setText(Oct);
        } else {
            octLab.setText("0");
        }
    }

    // makes sure string isnt empty and sets binary value on binary label
    public void displayBinVal(String str) {
        if (str != null &&!str.isEmpty()) {
            long decVal = Long.valueOf(str);
            String Bin = Long.toBinaryString(decVal);
            binLab.setText(Bin);
        } else {
            binLab.setText("0");
        }
    }

    // function that actually does the calculations
    public String evaluateExpression(String expression, char base) {
        // Create operandStack to store operands
        Stack<Long> operandStack = new Stack<Long>();

        // Create operatorStack to store operators
        Stack<Character> operatorStack = new Stack<Character>();

        // Insert blanks around ()+-/*
        expression = Utils.insertBlanks(expression);
        System.out.println("evaluateExpression(): Insert blanks: " + expression);

        // Extract operands and operators
        String [] tokens = expression.split(" ");
        System.out.println("evaluateExpression(): Individual tokens");
        System.out.println("evaluateExpression(): -----------");
        for (String token: tokens) {
            System.out.println(token);
        }
        System.out.println("evaluateExpression(): ------------");
        for (String token: tokens) {
            System.out.println("evaluateExpression(): token: " + token);
            // if blank space then get next token
            if (token.length() == 0 || token.charAt(0) == ' ') {
                continue;
            } else if (token.charAt(0) == '+' || token.charAt(0) == '-') {
                // Process all +, -, *, / in the top of operator stack
                while (!operatorStack.isEmpty() && (operatorStack.peek() == '+' ||
                        operatorStack.peek() == '-' ||
                        operatorStack.peek() == '*' ||
                        operatorStack.peek() == '/' ||
                        operatorStack.peek() == '%')) {
                    processAnOperator(operandStack, operatorStack, base);
                } // end while

                // Push the + or - operator into operator stack
                operatorStack.push(token.charAt(0));
            } else if (token.charAt(0) == '*' || token.charAt(0) == '/' || token.charAt(0) == '%') {
                // Process all * / at top of operator stack
                while(!operatorStack.isEmpty() && (operatorStack.peek() == '*' ||
                        operatorStack.peek() == '/' || operatorStack.peek() == '%')) {
                    processAnOperator(operandStack, operatorStack, base);
                }

                // push * / onto operator stack
                operatorStack.push(token.charAt(0));
            }
            else if (token.trim().charAt(0) == '(') {
                operatorStack.push('('); // Push ( to stack
            } else if (token.trim().charAt(0) == ')') {
                // Process all operators until you get to (
                while (operatorStack.peek() != '(') {
                    processAnOperator(operandStack, operatorStack, base);
                }

                operatorStack.pop(); // get rid of (
            } else { // only thing thats left is operand
                // push operand to stack
                System.out.println("evaluateExpression(): about to push: " + token);
                operandStack.push(new Long(token));
                
            }
        } // end for loop

        // process remaining operators in stack
        while (!operatorStack.isEmpty()) {
            System.out.println("evaluateExpression(): about to process operator");
            processAnOperator(operandStack, operatorStack, base);
            System.out.println("evaluateExpression(): finished processing operator");
        }
        
        String result = "";

        return Long.toString(operandStack.pop());
    } // end evaluateExpression

    // takes the operand and operator from the stacks and does the calculations
    public void processAnOperator(Stack<Long> operandStack, Stack<Character> operatorStack, char base) {
        char operator = operatorStack.pop();
        long operand1 = operandStack.pop();
        System.out.println("in process an operator");
        long operand2 = operandStack.pop();

        if (operator == '+') {
            operandStack.push(operand2 + operand1);
        } else if (operator == '-') {
            operandStack.push(operand2 - operand1);
        } else if (operator == '*') {
            operandStack.push(operand2 * operand1);
        } else if (operator == '/') {
            operandStack.push(operand2 / operand1);
        } else if (operator == '%') {
            operandStack.push(operand2 % operand1);
        }
    } // end processAnOperator

    // initializes a gridbaglayout panel, buttongroup, all buttons, and adds panel to the frame
    void addEverything() {
        // Create a panel and set it to gridbaglayout
        pane = new JPanel();
        pane.setLayout(new GridBagLayout());

        // create gridbagconstraint object
        c = new GridBagConstraints();

        // make sure only one mode is selected
        myButtonGroup = new ButtonGroup();

        bOpenParan = new JButton("(");
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 15;
        c.weightx = 0.5;
        c.weighty = 0.5;
        // add this set preferred size for one button in each row
        bOpenParan.setPreferredSize(new Dimension(60, 60));
        pane.add(bOpenParan, c);
        bOpenParan.addActionListener(this);

        bClosedParan = new JButton(")");
        c.gridx = 1;
        c.gridy = 15;
        pane.add(bClosedParan, c);
        bClosedParan.addActionListener(this);

        bEmpty = new JButton("+/-");
        c.gridx = 2;
        c.gridy = 15;
        pane.add(bEmpty, c);

        bZero = new JButton("0");
        c.gridx = 3;
        c.gridy = 15;
        pane.add(bZero, c);
        bZero.addActionListener(this);
        bZero.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bDecimal = new JButton(".");
        c.gridx = 4;
        c.gridy = 15;
        pane.add(bDecimal, c);
        bDecimal.setEnabled(false);

        bE = new JButton("E");
        c.gridx = 0;
        c.gridy = 14;
        pane.add(bE, c);
        bE.addActionListener(this);
        bE.setPreferredSize(new Dimension(60, 60));

        bF = new JButton("F");
        c.gridx = 1;
        c.gridy = 14;
        pane.add(bF, c);
        bF.addActionListener(this);

        bOne = new JButton("1");
        c.gridx = 2;
        c.gridy = 14;
        pane.add(bOne, c);
        bOne.addActionListener(this);
        bOne.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bTwo = new JButton("2");
        c.gridx = 3;
        c.gridy = 14;
        pane.add(bTwo, c);
        bTwo.addActionListener(this);
        bTwo.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bThree = new JButton("3");
        c.gridx = 4;
        c.gridy = 14;
        pane.add(bThree, c);
        bThree.addActionListener(this);
        bThree.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bEquals = new JButton("=");
        c.gridx = 5;
        c.gridy = 15;
        pane.add(bEquals, c);
        bEquals.addActionListener(this);
        bEquals.setFont(new Font("Sans-Serif", Font.PLAIN, 17));

        bPlus = new JButton("+");
        c.gridx = 5;
        c.gridy = 14;
        pane.add(bPlus, c);
        bPlus.addActionListener(this);
        bPlus.setFont(new Font("Sans-Serif", Font.PLAIN, 17));

        bC = new JButton("C");
        c.gridx = 0;
        c.gridy = 13;
        pane.add(bC, c);
        bC.addActionListener(this);
        bC.setPreferredSize(new Dimension(60, 60));

        bD = new JButton("D");
        c.gridx = 1;
        c.gridy = 13;
        pane.add(bD, c);
        bD.addActionListener(this);

        bFour = new JButton("4");
        c.gridx = 2;
        c.gridy = 13;
        pane.add(bFour, c);
        bFour.addActionListener(this);
        bFour.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bFive = new JButton("5");
        c.gridx = 3;
        c.gridy = 13;
        pane.add(bFive, c);
        bFive.addActionListener(this);
        bFive.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bSix = new JButton("6");
        c.gridx = 4;
        c.gridy = 13;
        pane.add(bSix, c);
        bSix.addActionListener(this);
        bSix.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bMinus = new JButton("-");
        c.gridx = 5;
        c.gridy = 13;
        pane.add(bMinus, c);
        bMinus.addActionListener(this);
        bMinus.setFont(new Font("Sans-Serif", Font.PLAIN, 17));

        bA = new JButton("A");
        c.gridx = 0;
        c.gridy = 12;
        pane.add(bA, c);
        bA.addActionListener(this);
        bA.setPreferredSize(new Dimension(60, 60));

        bB = new JButton("B");
        c.gridx = 1;
        c.gridy = 12;
        pane.add(bB, c);
        bB.addActionListener(this);

        bSeven = new JButton("7");
        c.gridx = 2;
        c.gridy = 12;
        pane.add(bSeven, c);
        bSeven.addActionListener(this);
        bSeven.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bEight = new JButton("8");
        c.gridx = 3;
        c.gridy = 12;
        pane.add(bEight, c);
        bEight.addActionListener(this);
        bEight.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bNine = new JButton("9");
        c.gridx = 4;
        c.gridy = 12;
        pane.add(bNine, c);
        bNine.addActionListener(this);
        bNine.setFont(new Font("Sans-Serif", Font.BOLD, 17));

        bMult = new JButton("X");
        c.gridx = 5;
        c.gridy = 12;
        pane.add(bMult, c);
        bMult.addActionListener(this);
        bMult.setFont(new Font("Sans-Serif", Font.PLAIN, 14));

        bUp = new JButton("\u2191");
        c.gridx = 0;
        c.gridy = 11;
        pane.add(bUp, c);
        bUp.setPreferredSize(new Dimension(60, 60));

        bMod = new JButton("Mod");
        c.gridx = 1;
        c.gridy = 11;
        pane.add(bMod, c);
        bMod.addActionListener(this);

        bCE = new JButton("CE");
        c.gridx = 2;
        c.gridy = 11;
        pane.add(bCE, c);
        bCE.addActionListener(this);

        bClear = new JButton("C");
        c.gridx = 3;
        c.gridy = 11;
        pane.add(bClear, c);
        bClear.addActionListener(this);

        bDelete = new JButton("\u232B");
        c.gridx = 4;
        c.gridy = 11;
        pane.add(bDelete, c);
        bDelete.addActionListener(this);

        bDivide = new JButton("/");
        c.gridx = 5;
        c.gridy = 11;
        pane.add(bDivide, c);
        bDivide.addActionListener(this);
        bDivide.setFont(new Font("Sans-Serif", Font.PLAIN, 17));

        bLSH = new JButton("Lsh");
        c.gridx = 0;
        c.gridy = 10;
        pane.add(bLSH, c);
        bLSH.setPreferredSize(new Dimension(60, 60));

        bRSH = new JButton("Rsh");
        c.gridx = 1;
        c.gridy = 10;
        pane.add(bRSH, c);

        bOr = new JButton("Or");
        c.gridx = 2;
        c.gridy = 10;
        pane.add(bOr, c);

        bXor = new JButton("Xor");
        c.gridx = 3;
        c.gridy = 10;
        pane.add(bXor, c);

        bNot = new JButton("Not");
        c.gridx = 4;
        c.gridy = 10;
        pane.add(bNot, c);

        bAnd = new JButton("And");
        c.gridx = 5;
        c.gridy = 10;
        pane.add(bAnd, c);

        b10Dots = new JButton("", new ImageIcon("10dots.png"));
        c.gridx = 0;
        c.gridy = 9;
        pane.add(b10Dots, c);

        b5Dots = new JButton("", new ImageIcon("5dots.png"));
        c.gridx = 1;
        c.gridy = 9;
        pane.add(b5Dots, c);

        bWord = new JButton("QWORD");
        c.gridwidth = 2;
        c.gridx = 2;
        c.gridy = 9;
        pane.add(bWord, c);
        bWord.addActionListener(this);
        bWord.setFont(new Font("Sans-Serif", Font.BOLD, 14));

        bMS = new JButton("MS");
        //bMS = new JButton("\u25BE");
        c.gridwidth = 1;
        c.gridx = 4;
        c.gridy = 9;
        pane.add(bMS, c);
        bMS.setFont(new Font("Sans-Serif", Font.BOLD, 14));

        bMemory = new JButton("M\u25BE");
        c.gridx = 5;
        c.gridy = 9;
        pane.add(bMemory, c);
        bMemory.setFont(new Font("Sans-Serif", Font.BOLD, 14));

        bBin = new JToggleButton("BIN");
        c.gridx = 0;
        c.gridy = 8;
        pane.add(bBin, c);
        bBin.addActionListener(this);
        myButtonGroup.add(bBin);

        bOct = new JToggleButton("OCT");
        c.gridx = 0;
        c.gridy = 7;
        pane.add(bOct, c);
        bOct.addActionListener(this);
        myButtonGroup.add(bOct);

        bDec = new JToggleButton("DEC");
        c.gridx = 0;
        c.gridy = 6;
        pane.add(bDec, c);
        bDec.addActionListener(this);
        myButtonGroup.add(bDec);

        bHex = new JToggleButton("HEX");
        c.gridx = 0;
        c.gridy = 5;
        pane.add(bHex, c);
        bHex.addActionListener(this);
        myButtonGroup.add(bHex);
        myButtonGroup.setSelected(bDec.getModel(), true);

        bMenu = new JButton("\u2630");
        c.gridx = 0;
        c.gridy = 2;
        pane.add(bMenu, c);

        bigLabel = new JLabel("0", SwingConstants.RIGHT);
        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 10;
        bigLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        pane.add(bigLabel, c);

        lilLabel = new JLabel(" ", SwingConstants.RIGHT);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 6;
        lilLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pane.add(lilLabel, c);

        programmerLabel = new JLabel("Programmer");
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        programmerLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        pane.add(programmerLabel, c);

        hexLab = new JLabel("0");
        c.gridx = 1;
        c.gridy = 5;
        c.gridwidth = 6;
        hexLab.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pane.add(hexLab, c);

        decLab = new JLabel("0");
        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 6;
        decLab.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pane.add(decLab, c);

        octLab = new JLabel("0");
        c.gridx = 1;
        c.gridy = 7;
        c.gridwidth = 6;
        octLab.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pane.add(octLab, c);

        binLab = new JLabel("0");
        c.gridx = 1;
        c.gridy = 8;
        c.gridwidth = 6;
        binLab.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pane.add(binLab, c);

        // add panel to frame
        add(pane, BorderLayout.PAGE_END);
    } // end addEverything
} // end Calculator Class