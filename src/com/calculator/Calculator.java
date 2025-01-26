/** *************************************************************
 * Package: com.calculator
 *
 * This specifies that our Calculator class belongs to the
 * 'com.calculator' package. Packages are used to organize
 * and group related classes in Java.
 ************************************************************** */
package com.calculator;

/**
 * *************************************************************
 * Import Statements:
 *
 * - java.awt.*: Includes classes for creating and managing graphical user
 * interface (GUI) elements such as Colors, Fonts, and general 2D graphics. -
 * java.awt.event.*: We now import all events to easily handle ActionEvent,
 * MouseEvent, KeyEvent, etc. - java.awt.geom.RoundRectangle2D: Provides a shape
 * for a rectangle with rounded corners (used to paint buttons). -
 * javax.swing.*: Swing library classes such as JFrame, JPanel, JButton,
 * JTextField, etc., used for GUI components. - javax.swing.border.EmptyBorder:
 * A border that leaves space around its edges. Used to add padding to
 * components. *************************************************************
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * *************************************************************
 * Class: Calculator
 *
 * This class contains the main method (entry point of the program) and all the
 * code needed to build and display a simple calculator using Swing components.
 * *************************************************************
 */
public class Calculator {

    /**
     * *********************************************************
     * Color constants:
     *
     * These static final variables store Color objects that we'll reuse
     * throughout the GUI. We define them here so our code is easier to maintain
     * and we only need to change these values in one place to update the theme.
     * *********************************************************
     */
    private static final Color DARK_BACKGROUND = new Color(28, 28, 30);
    private static final Color BUTTON_BACKGROUND = new Color(44, 44, 46);
    private static final Color OPERATOR_BACKGROUND = new Color(255, 149, 0);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color DISPLAY_BACKGROUND = new Color(36, 36, 38);

    /**
     * *********************************************************
     * A string of valid operator characters. We make this static so it can be
     * accessed from static methods.
     * *********************************************************
     */
    private static final String OPERATORS = "+-*/%";

    /**
     * *********************************************************
     * Checks if a given character is one of our recognized operators.
     * *********************************************************
     */
    private static boolean isOperator(char c) {
        return OPERATORS.indexOf(c) != -1;
    }

    /**
     * *********************************************************
     * clearIfError:
     *
     * A small helper method to ensure that if the text field currently shows
     * "Error", we clear it before typing more input. This allows the user to
     * start fresh. *********************************************************
     */
    private static void clearIfError(JTextField field) {
        if ("Error".equals(field.getText())) {
            field.setText("");
        }
    }

    /**
     * *********************************************************
     * handleOperator:
     *
     * This method ensures we don't stack multiple operators. It replaces the
     * last operator with the new one if there's already an operator at the end
     * of the current text. If the field is empty, only '-' is allowed (for
     * negative numbers).
     *
     * Additional rule added: - If the field is exactly "-", we do NOT allow any
     * further operator (e.g., +, -, /, *) to overwrite it.
     * *********************************************************
     */
    private static void handleOperator(JTextField field, String op) {
        String current = field.getText();

        // If empty, allow only minus
        if (current.isEmpty()) {
            if (op.equals("-")) {
                field.setText(op);
            }
            // If user tries to start with +, /, *, %, do nothing
            return;
        }

        // If the text is exactly "-", do nothing if an operator is pressed
        // This prevents overwriting the initial minus with +, /, etc.
        if (current.equals("-")) {
            return;
        }

        // Otherwise, check if the last character is already an operator
        char lastChar = current.charAt(current.length() - 1);
        if (isOperator(lastChar)) {
            // Replace operator with the new one
            String newText = current.substring(0, current.length() - 1) + op;
            field.setText(newText);
        } else {
            // Append operator to the existing number
            field.setText(current + op);
        }
    }

    /**
     * *********************************************************
     * main Method:
     *
     * The main method is the entry point of a Java application. When you run
     * the program, the JVM calls main() to start executing your code.
     * *********************************************************
     */
    public static void main(String[] args) {

        /**
         * *********************************************************
         * Try to set a "native" look and feel:
         *
         * UIManager.setLookAndFeel(...) attempts to use the platform's default
         * look & feel (LAF). If this fails (for example, if we can't load it on
         * this system), we just catch the exception and proceed.
         * *********************************************************
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * *********************************************************
         * JFrame Setup:
         *
         * - JFrame is the top-level window that holds all our components
         * (buttons, text fields, panels). - We set its title to "Calculator",
         * specify what happens when the user closes it (EXIT_ON_CLOSE), set
         * size, and background color. - setResizable(false) prevents the user
         * from resizing the window, maintaining our layout at a fixed size.
         * *********************************************************
         */
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 500);
        frame.setResizable(false);
        frame.getContentPane().setBackground(DARK_BACKGROUND);

        /**
         * *********************************************************
         * mainPanel:
         *
         * This panel uses a custom painting method to draw a gradient
         * background. We'll add our GUI components (like the text field and
         * buttons) onto this panel.
         * *********************************************************
         */
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Cast Graphics to Graphics2D for advanced 2D drawing capabilities
                Graphics2D g2d = (Graphics2D) g;

                // Enable antialiasing so edges look smoother
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create a vertical gradient paint from DARK_BACKGROUND at the top
                // to a slightly different color at the bottom
                GradientPaint gp = new GradientPaint(
                        0, 0, DARK_BACKGROUND,
                        0, getHeight(), new Color(40, 40, 42)
                );

                // Fill the entire panel area with this gradient
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        // We use an absolute layout (null layout) approach, where we manually set bounds
        mainPanel.setLayout(null);

        /**
         * *********************************************************
         * JTextField (textField):
         *
         * This is where we display the expression being typed and the result
         * after evaluation. We're making it large and setting the font to 32px.
         * We also right-align the text.
         *
         * We set it to non-editable so that the user cannot type directly into
         * it. The calculator logic must come from the buttons only.
         * *********************************************************
         */
        JTextField textField = new JTextField();
        textField.setBounds(25, 30, 300, 60);
        textField.setFont(new Font("SansSerif", Font.PLAIN, 32));
        textField.setBackground(DISPLAY_BACKGROUND);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(new EmptyBorder(5, 10, 5, 10));
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setCaretColor(TEXT_COLOR); // Cursor color (for selection)
        textField.setEditable(false);        // Make the field non-editable
        mainPanel.add(textField);

        /**
         * *********************************************************
         * StyledButton Class (inner class):
         *
         * Extends JButton to customize: 1) Rounded corners via
         * RoundRectangle2D. 2) Hover and press effects by changing the
         * background color. 3) Common font, size, and style settings for our
         * calculator. *********************************************************
         */
        class StyledButton extends JButton {

            // Constructor takes text to display and a background color
            StyledButton(String text, Color bgColor) {
                super(text);  // Pass the label text to JButton's constructor
                setForeground(TEXT_COLOR); // Text is always white
                setFont(new Font("SansSerif", Font.PLAIN, 18));  // Set button font
                setBorderPainted(false);    // Hide default JButton borders
                setFocusPainted(false);     // Remove focus (dotted) border when clicked
                setContentAreaFilled(false);// We'll paint the background ourselves
                setBackground(bgColor);     // Use the color passed in

                // Add a MouseAdapter to handle hover/press color changes
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // Brighten background on hover
                        setBackground(bgColor.brighter());
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        // Return to original color when mouse leaves
                        setBackground(bgColor);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        // Darken color on press
                        setBackground(bgColor.darker());
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        // Return to default color when mouse is released
                        setBackground(bgColor);
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                // Enable smoother edges
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw a rounded rectangle background
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

                // Paint the button text and any default elements (like the label)
                super.paintComponent(g);
            }
        }

        /**
         * *********************************************************
         * Number Buttons (0-9):
         *
         * We create an array of 10 buttons for digits 0 through 9. Each button
         * appends its digit to the text field when clicked.
         *
         * We also call clearIfError(...) so that if "Error" is currently in the
         * text field, it is removed before appending digits.
         * *********************************************************
         */
        JButton[] numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new StyledButton(String.valueOf(i), BUTTON_BACKGROUND);
            final int num = i;  // We need a final (or effectively final) variable
            numberButtons[i].addActionListener(e -> {
                clearIfError(textField);
                textField.setText(textField.getText() + num);
            });
        }

        /**
         * *********************************************************
         * Operation Buttons:
         *
         * These are the buttons for +, -, ×, ÷, decimal point, equals, delete
         * (⌫), clear (C), and also "00" and "%".
         *
         * - Operators (+, -, ×, ÷, %) are handled by handleOperator(...) to
         * avoid stacking multiple operators. - equals (=) triggers the
         * evaluation of the expression. - delete (⌫) removes the last
         * character. - clear (C) clears the entire input. - "00" appends two
         * zeros at once. - decimal (.) simply appends '.' to the current text.
         *
         * Additionally, we call clearIfError(...) so that if "Error" is
         * showing, it is removed when the user clicks a new operator or decimal
         * or "00". *********************************************************
         */
        JButton addButton = new StyledButton("+", OPERATOR_BACKGROUND);
        JButton subButton = new StyledButton("-", OPERATOR_BACKGROUND);
        JButton mulButton = new StyledButton("×", OPERATOR_BACKGROUND);
        JButton divButton = new StyledButton("÷", OPERATOR_BACKGROUND);
        JButton modButton = new StyledButton("%", BUTTON_BACKGROUND);
        JButton decButton = new StyledButton(".", BUTTON_BACKGROUND);
        JButton equButton = new StyledButton("=", OPERATOR_BACKGROUND);
        JButton delButton = new StyledButton("⌫", BUTTON_BACKGROUND);
        JButton clrButton = new StyledButton("C", BUTTON_BACKGROUND);

        // Create a "00" button for double zero
        JButton doubleZeroButton = new StyledButton("00", BUTTON_BACKGROUND);
        doubleZeroButton.addActionListener(e -> {
            clearIfError(textField);
            textField.setText(textField.getText() + "00");
        });

        /**
         * *********************************************************
         * buttonPanel (GridLayout):
         *
         * We organize our buttons in a 5x4 grid. This panel arranges them
         * neatly in rows and columns with 10px gaps (horizontal & vertical).
         * *********************************************************
         */
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBounds(25, 110, 300, 330);

        // Make the panel transparent so we see the gradient
        buttonPanel.setOpaque(false);

        /**
         * *********************************************************
         * Placing Buttons in the Grid:
         *
         * The order below defines how the buttons appear row by row, left to
         * right. *********************************************************
         */
        // Row 1
        buttonPanel.add(clrButton);
        buttonPanel.add(delButton);
        buttonPanel.add(modButton);
        buttonPanel.add(divButton);

        // Row 2
        buttonPanel.add(numberButtons[7]);
        buttonPanel.add(numberButtons[8]);
        buttonPanel.add(numberButtons[9]);
        buttonPanel.add(mulButton);

        // Row 3
        buttonPanel.add(numberButtons[4]);
        buttonPanel.add(numberButtons[5]);
        buttonPanel.add(numberButtons[6]);
        buttonPanel.add(subButton);

        // Row 4
        buttonPanel.add(numberButtons[1]);
        buttonPanel.add(numberButtons[2]);
        buttonPanel.add(numberButtons[3]);
        buttonPanel.add(addButton);

        // Row 5
        buttonPanel.add(doubleZeroButton);
        buttonPanel.add(numberButtons[0]);
        buttonPanel.add(decButton);
        buttonPanel.add(equButton);

        // Add this buttonPanel to our main panel
        mainPanel.add(buttonPanel);

        // Finally, add our mainPanel to the frame, center on screen, and show
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        /**
         * *********************************************************
         * Operator Button Listeners:
         *
         * When these are clicked, we: 1) clearIfError(...) so "Error" is
         * removed first, if present. 2) call handleOperator(...) to avoid
         * stacking multiple operators.
         *
         * For "×" we pass "*", for "÷" we pass "/", since our parser reads '*'
         * and '/'. *********************************************************
         */
        addButton.addActionListener(e -> {
            clearIfError(textField);
            handleOperator(textField, "+");
        });
        subButton.addActionListener(e -> {
            clearIfError(textField);
            handleOperator(textField, "-");
        });
        mulButton.addActionListener(e -> {
            clearIfError(textField);
            handleOperator(textField, "*");
        });
        divButton.addActionListener(e -> {
            clearIfError(textField);
            handleOperator(textField, "/");
        });
        modButton.addActionListener(e -> {
            clearIfError(textField);
            handleOperator(textField, "%");
        });

        /**
         * *********************************************************
         * decButton (Decimal):
         *
         * Simply appends a '.' to the text. We do not restrict multiple
         * decimals in one number here, but that could be an enhancement if
         * needed. *********************************************************
         */
        decButton.addActionListener(e -> {
            clearIfError(textField);
            textField.setText(textField.getText() + ".");
        });

        /**
         * *********************************************************
         * equButton (Equals):
         *
         * This triggers the evaluation of whatever string the user has typed.
         * If the parsing or math fails, we display "Error".
         * *********************************************************
         */
        equButton.addActionListener(e -> {
            try {
                String result = evalExpression(textField.getText());
                textField.setText(result);
            } catch (Exception ex) {
                textField.setText("Error");
            }
        });

        /**
         * *********************************************************
         * delButton (Delete):
         *
         * Removes the last character in the text field as long as the text
         * isn't empty. If it's empty, nothing happens.
         *
         * If the field says "Error", we do not do partial deletion— we just do
         * nothing. The user must press a digit or operator (which will clear
         * "Error") or press 'C'.
         * *********************************************************
         */
        delButton.addActionListener(e -> {
            String text = textField.getText();
            if (text != null && text.length() > 0 && !"Error".equals(text)) {
                textField.setText(text.substring(0, text.length() - 1));
            }
        });

        /**
         * *********************************************************
         * clrButton (Clear):
         *
         * Clears the text field entirely, resetting it to "".
         * *********************************************************
         */
        clrButton.addActionListener(e -> textField.setText(""));

        /**
         * *********************************************************
         * KeyListener for special keys:
         *
         * We add a KeyListener to the main frame so that when keys are pressed,
         * we simulate "pressing" the respective calculator buttons. This
         * ensures the same logic is used for both mouse clicks and keyboard
         * presses.
         *
         * Note: The textField is set non-editable, so we must do everything via
         * "button.doClick()".
         *
         * We do not automatically clear "Error" in the key listener. Instead,
         * the button's doClick() calls the same ActionListener which calls
         * clearIfError(...).
         * *********************************************************
         */
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                int keyCode = e.getKeyCode();
                char c = e.getKeyChar();

                // Backspace => delButton
                if (keyCode == KeyEvent.VK_BACK_SPACE) {
                    delButton.doClick();
                    return;
                }

                // Delete => clrButton
                if (keyCode == KeyEvent.VK_DELETE) {
                    clrButton.doClick();
                    return;
                }

                // Enter => equButton
                if (keyCode == KeyEvent.VK_ENTER) {
                    equButton.doClick();
                    return;
                }

                // If user presses '=', do the same as Enter
                if (c == '=') {
                    equButton.doClick();
                    return;
                }

                // '.' or ',' => decButton
                if (c == '.' || c == ',') {
                    decButton.doClick();
                    return;
                }

                // If '0'..'9' => press that digit button
                if (c >= '0' && c <= '9') {
                    numberButtons[c - '0'].doClick();
                    return;
                }

                // Operators: +, -, *, /, '%'
                if (c == '+') {
                    addButton.doClick();
                    return;
                }
                if (c == '-') {
                    subButton.doClick();
                    return;
                }
                if (c == '*') {
                    mulButton.doClick();
                    return;
                }
                if (c == '/') {
                    divButton.doClick();
                    return;
                }
                if (c == '%') {
                    modButton.doClick();
                    return;
                }

                // If none of the above, ignore the key.
            }
        });

        // So that the frame actually receives focus and can detect key presses:
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    /**
     * *************************************************************
     * evalExpression Method:
     *
     * Takes a string like "9+9" and evaluates it as a math expression. We first
     * remove spaces, then call parseExpression to actually compute the result.
     *
     * - If the computation is successful, we return either an integer-like
     * string if the result is an exact integer (e.g., "18"), or a decimal
     * string if it's a fraction. - If there's any error (bad syntax, etc.), we
     * return "Error".
     * *************************************************************
     */
    public static String evalExpression(String expr) {
        // Remove all whitespace (for safety)
        expr = expr.replaceAll("\\s+", "");
        try {
            double value = parseExpression(expr);
            // Decide whether to show a whole number or decimal
            if (value == (long) value) {
                return String.valueOf((long) value);
            } else {
                return String.valueOf(value);
            }
        } catch (Exception e) {
            return "Error";
        }
    }

    /**
     * *************************************************************
     * parseExpression Method:
     *
     * A small recursive‐descent parser that can handle +, -, *, /, parentheses,
     * and unary +/- operators. This returns a double result of the parsed math
     * expression.
     *
     * We create an anonymous inner class here, which keeps track of the current
     * position (pos) and the current character (ch).
     * *************************************************************
     */
    private static double parseExpression(final String str) {
        return new Object() {

            // pos: the current index in the string
            // ch: the current character's integer value (or -1 if none)
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }

            // Grammar:
            // expression = term | expression '+' term | expression '-' term
            // term       = factor | term '*' factor | term '/' factor
            // factor     = '+' factor | '-' factor | '(' expression ')'
            //            | number
            //            | number '.' number
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) {
                        x += parseTerm(); // addition
                    } else if (eat('-')) {
                        x -= parseTerm(); // subtraction
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) {
                        x *= parseFactor(); // multiplication
                    } else if (eat('/')) {
                        x /= parseFactor(); // division
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor() {
                if (eat('+')) {
                    return +parseFactor(); // unary plus

                }
                if (eat('-')) {
                    return -parseFactor(); // unary minus
                }
                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        nextChar();
                    }
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected character: " + (char) ch);
                }
                return x;
            }
        }.parse();
    }

}
