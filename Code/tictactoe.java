import java.awt.*; // For GUI components (Buttons, panels, frames, etc.)
import java.awt.event.*; // For layout managers, colors, fonts, and other AWT classes
import java.util.Random; // For event handling (mouse clicks, actions)
import javax.swing.*; // For generating random numbers (CPU Moves)  

// Main game class that extends JFrame (window)
public class tictactoe extends JFrame {
    private final JButton[][] buttons = new JButton[3][3]; // 2D array to store references to all 9 game board buttons
    private char currentPlayer = 'X'; // Tracks whose turn it is - Starts with 'X'
    private boolean gameActive = true; // Game state - False when game ends (win/draw)
    private boolean vsCPU = false; // Mode flag - true for Player vs CPU, false for Player vs Player
    private final Random random = new Random(); // Random number generator for CPU moves

    // Colors for UI
    private final Color bgColor = new Color(240,240,240); // Light gray background
    private final Color buttonColor = new Color(255, 255, 255); // White Buttons
    private final Color xColor = new Color(41, 128, 185); // Blue for X player
    private final Color oColor = new Color(231, 76, 60); // Red for O player
    private final Color winColor = new Color(46, 204, 113); // Green for winning line

    // Constructor - called when game object is created
    public tictactoe() {
        setTitle("Tic Tac Toe"); // Set window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close program when window closes
        setSize(400, 500); // Set window size (width, length)
        setLocationRelativeTo(null); // Center window on screen
        setResizable(false); // Prevent window resizing

        showGameModeSelection(); // Show initial menu 
    }

    // Creates and displays the game mode selection screen
    private void showGameModeSelection() {
        JPanel modePanel = new JPanel(new GridLayout(3, 1, 10, 10)); // Create panel with 3 rows , 1 column , and 10px gaps
        modePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add a 50px padding around the panel
        modePanel.setBackground(bgColor); // Set background color

        JLabel titleLabel = new JLabel("Tic Tac Toe", JLabel.CENTER); // Create title label
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font style and size
        titleLabel.setForeground(new Color(52, 73, 94)); // Set text color

        JButton pvpButton = createStyledButton("Player vs Player"); // Create mode selection button
        JButton pvcButton = createStyledButton("Player vs CPU");
        
        pvpButton.addActionListener(e -> { // Add action listener - code that runs when buttons are clicked [Lambda starts]
            vsCPU = false; // Set to Player vs Player
            initializeGame(); // Start Game
        }); // [Lambda Ends]

        pvcButton.addActionListener(e -> { // [Lambda starts]
            vsCPU = true; // Set to Player vs CPU
            initializeGame();
        }); // [Lambda ends]

        // Add components to panel in order
        modePanel.add(titleLabel);
        modePanel.add(pvpButton);
        modePanel.add(pvcButton);

        // Clear any existing content and add the mode panel
        getContentPane().removeAll();
        getContentPane().add(modePanel);
        revalidate(); // Refesh Layout
        repaint(); // Redraw components
    }

    // Helper method to create consistently styled buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text); // Create button with text
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Set font
        button.setBackground(new Color(52, 152, 219)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false); // Remore focus border
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Add hover effects using mouse listener
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { // Darken when mouse enters
                button.setBackground(new Color(41, 128, 185));
            }
            @Override
            public void mouseExited(MouseEvent e) { // Revert to original when mouse leaves
                button.setBackground(new Color(52, 152, 219));
            }
        });

        return button;
    }

    // Sets up the main game board and interface
    private void initializeGame() {
        getContentPane().removeAll(); // Clear previous content

        // Main panel using border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);

        // Top panel for game status and controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bgColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // Label to show current player
        JLabel statusLabel = new JLabel("Current Player: X", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(new Color(52, 73, 94));

        // Restart button to return to mode selection
        JButton restartButton = createStyledButton("Restart");
        restartButton.setPreferredSize(new Dimension(100, 35)); // Fixed size
        restartButton.addActionListener(e -> showGameModeSelection());

        // Add components to top panel
        topPanel.add(statusLabel, BorderLayout.CENTER);
        topPanel.add(restartButton, BorderLayout.EAST);

        // Game board panel - 3x3 grid with 5px gaps
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        boardPanel.setBackground(new Color(189, 195, 199)); // Gray background
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        // Create 3x3 grid of buttons for the game board
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton(); // Create new button
                buttons[row][col].setFont(new Font("Arial", Font.BOLD, 40)); // Large font for X/O
                buttons[row][col].setBackground(buttonColor); // White background
                buttons[row][col].setFocusPainted(false); // Remove focus border
                buttons[row][col].setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2)); // Add gray border around button

                // Store row and col in final variables for use in lambda
                final int finalRow = row;
                final int finalCol = col;

                // Add click listener to each button
                buttons[row][col].addActionListener(e -> { // [Lambda starts]
                    if(gameActive && buttons[finalRow][finalCol].getText().isEmpty()) { // Check if game is active and button is empty
                        makeMove(finalRow, finalCol, statusLabel); // Make player move

                        // If playing vs CPU and its CPU's turn, make CPU move
                        if(vsCPU && gameActive && currentPlayer == 'O') {
                            cpuMove(statusLabel);
                        }
                    }
                }); // [Lambda ends]

                // Add hover effect to buttons
                buttons[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (gameActive && buttons[finalRow][finalCol].getText().isEmpty()) { // Light gray background when hovering over empty button
                            buttons[finalRow][finalCol].setBackground(new Color(236, 240, 241));
                        }
                    }
                    @Override
                    public void mouseExited(MouseEvent e) { // Return to white when mouse leaves empty button
                        if (gameActive && buttons[finalRow][finalCol].getText().isEmpty()) {
                            buttons[finalRow][finalCol].setBackground(buttonColor);
                        }
                    }
                });

                boardPanel.add(buttons[row][col]); // Add button to board
            }
        }
        
        // Assemble the main interface
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        getContentPane().add(mainPanel); // Add main panel to window
        revalidate(); // Refresh layout
        repaint(); // Redraw components
    }

    // Handles a player making a move
    private void makeMove(int row, int col, JLabel statusLabel) {
        buttons[row][col].setText(String.valueOf(currentPlayer)); // Set button text to current player ( X or O )

        // Set Color for each player
        if (currentPlayer == 'X') {
            buttons[row][col].setForeground(xColor); // Blue for X
        } else {
            buttons[row][col].setForeground(oColor); // Red for O
        }

        // Check if game resulted in win or draw
        if(checkWin()) {
            gameActive = false; // Stop the game
            highlightWinningCells(); // Show winning Combination
            statusLabel.setText("Player " + currentPlayer + " wins."); // Update status
            showGameOverDialog("Player " + currentPlayer + " wins."); // Show win dialog
        } else if (isBoardfull()) { // Check is board is full (draw)
            gameActive = false;
            statusLabel.setText("It's a draw.");
            showGameOverDialog("It's a draw.");
        } else { // Game continues - switch to other player
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            statusLabel.setText("Current Player: " + currentPlayer); // Update display
        }
    }

    // Handles CPU making a move
    private void cpuMove(JLabel statusLabel) { 
        // Simple AI* First attempts to win, else block player, else random move
        int[] move = findBestMove();
        // Small delay to make CPU move visible
        Timer timer = new Timer(500, e -> { // [Lambda starts]
            makeMove(move[0], move[1], statusLabel); // Make the CPU move
        }); // [Lambda ends]
        timer.setRepeats(false); // Only execute once
        timer.start(); // Start the timer
    }

    // AI logic to determine CPU's move
    private int[] findBestMove() {

        // 1) Check if CPU can win
        for (int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(buttons[i][j].getText().isEmpty()) { // If cell is empty
                    buttons[i][j].setText("O"); // Try placing O
                    if (checkWin()) { // Check if this wins
                        buttons[i][j].setText(""); // Undo test move
                        return new int[]{i, j}; // Return winning move
                    }
                    buttons[i][j].setText(""); // Undo test move
                }
            }
        }

        // 2) Block player's winning move
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(buttons[i][j].getText().isEmpty()) {
                    buttons[i][j].setText("X"); // Test player move
                    if (checkWin()) { // Check if player would win
                        buttons[i][j].setText(""); // Undo test
                        return new int[]{i, j}; // Return blocking move
                    }
                    buttons[i][j].setText(""); // Undo test
                }
            }
        }

        // 3) Take center if available (Best strategic position)
        if (buttons[1][1].getText().isEmpty()) {
            return new int[]{1, 1};
        }

        // 4) Take corners
        int[][] corners = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
        for (int[] corner : corners) {
            if (buttons[corner[0]][corner[1]].getText().isEmpty()) {
                return corner;
            }
        }

        // 5) Random move (Last resort)
        while(true) {
            int row = random.nextInt(3); // Random row(0-2)
            int col = random.nextInt(3); // Random column (0-2)
            if(buttons[row][col].getText().isEmpty()) { // If empty
                return new int[]{row, col}; // Return randodm move
            }
        }
    }

    // Check if current player has won
    private boolean checkWin() { // Checks all rows and columns for three in a row
        for(int i = 0; i < 3; i++) {
           
            //Check Rows
            if (!buttons[i][0].getText().isEmpty() && 
            buttons[i][0].getText().equals(buttons[i][1].getText()) && // Match with middle
            buttons[i][0].getText().equals(buttons[i][2].getText())) { // Match with right
                return true;
            }
            
            //Check Columns
            if (!buttons[0][i].getText().isEmpty() && 
            buttons[0][i].getText().equals(buttons[1][i].getText()) && 
            buttons [0][i].getText().equals(buttons[2][i].getText())) {
                return true;
            }
        }

        // Check Diagonals
        if (!buttons[0][0].getText().isEmpty() && // Checks top left and bottom right
        buttons[0][0].getText().equals(buttons[1][1].getText()) && 
        buttons[0][0].getText().equals(buttons[2][2].getText())) {
            return true;
        }
        if (!buttons[0][2].getText().isEmpty() && // Checks top right and bottom left
        buttons[0][2].getText().equals(buttons[1][1].getText()) && 
        buttons[0][2].getText().equals(buttons[2][0].getText())) {
            return true;
        }

        return false; // No win found
    }

    // Highlights the winning combination in green
    private void highlightWinningCells() {
        
        // Check rows for winning combo
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().isEmpty() && 
            buttons[i][0].getText().equals(buttons[i][1].getText()) && 
            buttons[i][0].getText().equals(buttons[i][2].getText())) {
                buttons[i][0].setBackground(winColor); // Highlight winning row
                buttons[i][1].setBackground(winColor);
                buttons[i][2].setBackground(winColor);
                return; // Exit after finding winning line
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (!buttons[0][i].getText().isEmpty() &&
            buttons[0][i].getText().equals(buttons[1][i].getText()) &&
            buttons[0][i].getText().equals(buttons[2][i].getText())) {
                buttons[0][i].setBackground(winColor); // Highlight winning column
                buttons[1][i].setBackground(winColor);
                buttons[2][i].setBackground(winColor);
                return;
            }
        }

        // Check Diagonals - Top left to bottom right
        if (!buttons[0][0].getText().isEmpty() && 
        buttons[0][0].getText().equals(buttons[1][1].getText()) && 
        buttons[0][0].getText().equals(buttons[2][2].getText())) {
            buttons[0][0].setBackground(winColor); // Highlight winning diagonal
            buttons[1][1].setBackground(winColor);
            buttons[2][2].setBackground(winColor);
            return;
        }

        // Check Diagonals - Top right to bottom left
        if (!buttons[0][2].getText().isEmpty() && 
        buttons[0][2].getText().equals(buttons[1][1].getText()) &&
        buttons[0][2].getText().equals(buttons[2][0].getText())) {
            buttons[0][2].setBackground(winColor); // Highlight winning diagonal
            buttons[1][1].setBackground(winColor);
            buttons[2][0].setBackground(winColor);
        }
    }

    // Checks if all cells are filled (draw condition)
    private boolean isBoardfull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    return false; // Found empty cell - board not full
                }
            }
        }
        return true; // No empty cells found - board is full
    }

    // Show game over dialog with options
    private void showGameOverDialog(String message) {
        Object[] options = {"Play again", " Main Menu", "Exit"}; // Define options
        
        // Show option dialog and get user choice
        int choice = JOptionPane.showOptionDialog(this, 
        message + "\nWhat would you like to do?", "Game Over", // Dialog message, Dialog title
        JOptionPane.YES_NO_CANCEL_OPTION, // Button arrangement
        JOptionPane.INFORMATION_MESSAGE, // Message type
        null, // No custom icon
        options, // Button options
        options[0]); // Default selected option
        
        // Handle use choice
        switch (choice) {
            case 0:
                //Play again - Reset the Current Game
                resetGame();
                break;
            case 1:
                //Return to Main Menu
                showGameModeSelection();
                break;
            default:
                //Exit - Close program
                System.exit(0);
        }
    }

    // Resets the game to initial state
    private void resetGame() {
        currentPlayer = 'X'; // Reset to X starting
        gameActive = true; // Reactivate game
        
        // Clear all buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(""); // Clear text
                buttons[i][j].setBackground(buttonColor); // Reset background color
            }
        }
    }
    
    // Main method - Program entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // Use SwingUtilities to ensure thread-safe GUI operations
            try {
                //Set cross-platform look and feel for consistent appearance
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) { // If look and feel fails, continue with default
                System.err.println("Could not set look and feel: " + e.getMessage());
            }
            // Create and display the game window
            tictactoe game = new tictactoe();
            game.setVisible(true); // Make window visible
        });
        
    }
}
/* This program utilizes: Object oriented programming | Methods | Loops | Switch Case | Swing GUI | 
Event Handling | 2D Arrays | Control structures | Game Logic | Lambda expressions | Exception Handling */