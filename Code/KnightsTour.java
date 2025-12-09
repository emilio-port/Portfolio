import java.util.*;

public class KnightsTour {
    // Possible moves for a knight (8 directions in 2D, 24 in 3D)
    private static final int[][] MOVES_2D = {
        {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
        {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
    };
    
    private static final int[][] MOVES_3D = {
        // 2D moves on each layer
        {2, 1, 0}, {1, 2, 0}, {-1, 2, 0}, {-2, 1, 0},
        {-2, -1, 0}, {-1, -2, 0}, {1, -2, 0}, {2, -1, 0},
        // Vertical moves
        {0, 2, 1}, {0, 1, 2}, {0, -1, 2}, {0, -2, 1},
        {0, -2, -1}, {0, -1, -2}, {0, 1, -2}, {0, 2, -1},
        // Diagonal moves between layers
        {2, 0, 1}, {1, 0, 2}, {-1, 0, 2}, {-2, 0, 1},
        {-2, 0, -1}, {-1, 0, -2}, {1, 0, -2}, {2, 0, -1}
    };
    
    private int boardSize;
    private int boardLayers;
    private int[][][] board;
    private boolean[][][] holes;
    private int totalSquares;
    private int visitedSquares;
    private BoardStyle style;
    private int dimension;
    private boolean closedTour;
    private int startLayer, startRow, startCol;
    private Random random;
    
    public enum BoardStyle {
        REGULAR, WITH_HOLES, HIGH_DIMENSIONAL
    }
    
    public KnightsTour(int size, int layers, BoardStyle style, int dimension, boolean closedTour) {
        this.boardSize = size;
        this.boardLayers = layers;
        this.style = style;
        this.dimension = dimension;
        this.closedTour = closedTour;
        this.board = new int[layers][size][size];
        this.holes = new boolean[layers][size][size];
        this.totalSquares = layers * size * size;
        this.visitedSquares = 0;
        this.random = new Random();
        
        // Initialize board with -1 (unvisited)
        resetBoard();
        
        // Create holes if style is WITH_HOLES
        if (style == BoardStyle.WITH_HOLES) {
            createHoles();
        }
    }
    
    // Create holes in the board
    private void createHoles() {
        int holeCount = (boardSize * boardSize * boardLayers) / 4; // 25% holes
        
        for (int i = 0; i < holeCount; i++) {
            int layer = random.nextInt(boardLayers);
            int row = random.nextInt(boardSize);
            int col = random.nextInt(boardSize);
            
            // Don't put holes in corners
            if (!isCorner(layer, row, col)) {
                holes[layer][row][col] = true;
                totalSquares--; // Reduce total squares as holes are not traversable
            }
        }
        
        System.out.println("Created " + holeCount + " holes in the board.");
    }
    
    private boolean isCorner(int layer, int row, int col) {
        return (row == 0 && col == 0) ||
               (row == 0 && col == boardSize-1) ||
               (row == boardSize-1 && col == 0) ||
               (row == boardSize-1 && col == boardSize-1);
    }
    
    // Check if a position is valid and not visited
    private boolean isValidMove(int layer, int row, int col) {
        if (layer < 0 || layer >= boardLayers || 
            row < 0 || row >= boardSize || 
            col < 0 || col >= boardSize) {
            return false;
        }
        
        // Check if it's a hole
        if (style == BoardStyle.WITH_HOLES && holes[layer][row][col]) {
            return false;
        }
        
        return board[layer][row][col] == -1;
    }
    
    // Count the number of available moves from a given position
    private int countAvailableMoves(int layer, int row, int col) {
        int count = 0;
        
        if (dimension == 2 || style != BoardStyle.HIGH_DIMENSIONAL) {
            // 2D moves
            for (int[] move : MOVES_2D) {
                int nextRow = row + move[0];
                int nextCol = col + move[1];
                if (isValidMove(layer, nextRow, nextCol)) {
                    count++;
                }
            }
        } else {
            // 3D moves for high dimensional
            for (int[] move : MOVES_3D) {
                int nextLayer = layer + (dimension > 2 ? move[2] : 0);
                int nextRow = row + move[0];
                int nextCol = col + move[1];
                if (isValidMove(nextLayer, nextRow, nextCol)) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    // Check if a move returns to the starting position (for closed tour)
    private boolean returnsToStart(int layer, int row, int col) {
        if (dimension == 2 || style != BoardStyle.HIGH_DIMENSIONAL) {
            // 2D moves
            for (int[] move : MOVES_2D) {
                int nextRow = row + move[0];
                int nextCol = col + move[1];
                if (nextRow == startRow && nextCol == startCol && layer == startLayer) {
                    return true;
                }
            }
        } else {
            // 3D moves for high dimensional
            for (int[] move : MOVES_3D) {
                int nextLayer = layer + move[2];
                int nextRow = row + move[0];
                int nextCol = col + move[1];
                if (nextLayer == startLayer && nextRow == startRow && nextCol == startCol) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Find the next move using Warnsdorff's rule with tie-breaking
    private int[] findNextMove(int layer, int row, int col, int movesRemaining) {
        List<int[]> candidates = new ArrayList<>();
        int minDegree = Integer.MAX_VALUE;
        
        // Collect all valid moves
        if (dimension == 2 || style != BoardStyle.HIGH_DIMENSIONAL) {
            for (int[] move : MOVES_2D) {
                int nextRow = row + move[0];
                int nextCol = col + move[1];
                
                if (isValidMove(layer, nextRow, nextCol)) {
                    int degree = countAvailableMoves(layer, nextRow, nextCol);
                    
                    // For closed tour: if this is the last move, check if it returns to start
                    if (closedTour && movesRemaining == 1) {
                        if (returnsToStart(layer, nextRow, nextCol)) {
                            degree = -1; // Highest priority for closing
                        } else {
                            continue; // Skip moves that don't return to start on last move
                        }
                    }
                    
                    candidates.add(new int[]{layer, nextRow, nextCol, degree});
                    minDegree = Math.min(minDegree, degree);
                }
            }
        } else {
            for (int[] move : MOVES_3D) {
                int nextLayer = layer + (dimension > 2 ? move[2] : 0);
                int nextRow = row + move[0];
                int nextCol = col + move[1];
                
                if (isValidMove(nextLayer, nextRow, nextCol)) {
                    int degree = countAvailableMoves(nextLayer, nextRow, nextCol);
                    
                    if (closedTour && movesRemaining == 1) {
                        if (nextLayer == startLayer && nextRow == startRow && nextCol == startCol) {
                            degree = -1;
                        } else {
                            continue;
                        }
                    }
                    
                    candidates.add(new int[]{nextLayer, nextRow, nextCol, degree});
                    minDegree = Math.min(minDegree, degree);
                }
            }
        }
        
        if (candidates.isEmpty()) {
            return null;
        }
        
        // Filter to minimum degree candidates
        List<int[]> minCandidates = new ArrayList<>();
        for (int[] candidate : candidates) {
            if (candidate[3] == minDegree) {
                minCandidates.add(candidate);
            }
        }
        
        // Apply tie-breaking
        return breakTie(minCandidates);
    }
    
    // Enhanced tie-breaking method
    private int[] breakTie(List<int[]> candidates) {
        if (candidates.size() == 1) {
            int[] candidate = candidates.get(0);
            return new int[]{candidate[0], candidate[1], candidate[2]};
        }
        
        // Strategy 1: For closed tours, prefer moves toward center
        if (closedTour) {
            int[] centerMove = selectMoveTowardCenter(candidates);
            if (centerMove != null) {
                return centerMove;
            }
        }
        
        // Strategy 2: Random selection (default and effective)
        int[] chosen = candidates.get(random.nextInt(candidates.size()));
        return new int[]{chosen[0], chosen[1], chosen[2]};
    }
    
    // Select move that goes toward the board center (for closed tours)
    private int[] selectMoveTowardCenter(List<int[]> candidates) {
        double centerRow = (boardSize - 1) / 2.0;
        double centerCol = (boardSize - 1) / 2.0;
        
        int[] bestMove = null;
        double minDistance = Double.MAX_VALUE;
        
        for (int[] candidate : candidates) {
            int l = candidate[0];
            int r = candidate[1];
            int c = candidate[2];
            
            // Calculate distance to center
            double distance = Math.sqrt(
                Math.pow(r - centerRow, 2) + 
                Math.pow(c - centerCol, 2)
            );
            
            if (distance < minDistance) {
                minDistance = distance;
                bestMove = candidate;
            }
        }
        
        if (bestMove != null) {
            return new int[]{bestMove[0], bestMove[1], bestMove[2]};
        }
        return null;
    }
    
    // Solve the Knight's Tour starting from given position
    public boolean solve(int startL, int startR, int startC) {
        this.startLayer = startL;
        this.startRow = startR;
        this.startCol = startC;
        
        // Check if starting position is valid
        if (!isValidMove(startLayer, startRow, startCol)) {
            System.out.println("Invalid starting position!");
            return false;
        }
        
        int currentLayer = startLayer;
        int currentRow = startRow;
        int currentCol = startCol;
        int moveNumber = 1;
        
        // Make the first move
        board[currentLayer][currentRow][currentCol] = moveNumber;
        visitedSquares = 1;
        
        // Continue making moves
        while (visitedSquares < totalSquares) {
            int movesRemaining = totalSquares - visitedSquares;
            int[] nextMove = findNextMove(currentLayer, currentRow, currentCol, movesRemaining);
            
            if (nextMove == null) {
                // No valid moves available
                System.out.println("Stuck at move " + moveNumber + "! Could only visit " + 
                                 visitedSquares + " out of " + totalSquares + " squares.");
                return false;
            }
            
            currentLayer = nextMove[0];
            currentRow = nextMove[1];
            currentCol = nextMove[2];
            board[currentLayer][currentRow][currentCol] = ++moveNumber;
            visitedSquares++;
        }
        
        // Check if it's a closed tour
        if (closedTour) {
            boolean isClosed = returnsToStart(currentLayer, currentRow, currentCol);
            if (!isClosed) {
                System.out.println("Tour completed but not closed (final position not adjacent to start).");
                return false;
            }
            System.out.println("✓ Closed tour achieved!");
        }
        
        return true;
    }
    
    // Check if the tour is closed (last move returns to start)
    public boolean isTourClosed() {
        if (!hasValidSolution()) return false;
        
        // Find the last move position
        int lastLayer = -1, lastRow = -1, lastCol = -1;
        for (int l = 0; l < boardLayers; l++) {
            for (int r = 0; r < boardSize; r++) {
                for (int c = 0; c < boardSize; c++) {
                    if (board[l][r][c] == totalSquares) {
                        lastLayer = l;
                        lastRow = r;
                        lastCol = c;
                    }
                }
            }
        }
        
        return returnsToStart(lastLayer, lastRow, lastCol);
    }
    
    // Alternative solve method with random start positions
    public boolean solveWithRandomStart(int maxAttempts) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Reset board
            resetBoard();
            
            // Try random starting position
            int startL = random.nextInt(boardLayers);
            int startR = random.nextInt(boardSize);
            int startC = random.nextInt(boardSize);
            
            // Ensure starting position is not a hole
            while (style == BoardStyle.WITH_HOLES && holes[startL][startR][startC]) {
                startL = random.nextInt(boardLayers);
                startR = random.nextInt(boardSize);
                startC = random.nextInt(boardSize);
            }
            
            if (solve(startL, startR, startC)) {
                System.out.println("Found solution starting at Layer " + startL + 
                                 ", Row " + startR + ", Col " + startC);
                return true;
            }
        }
        
        return false;
    }
    
    private void resetBoard() {
        for (int l = 0; l < boardLayers; l++) {
            for (int i = 0; i < boardSize; i++) {
                Arrays.fill(board[l][i], -1);
            }
        }
        visitedSquares = 0;
    }
    
    // Display the board
    public void printBoard() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("KNIGHT'S TOUR SOLUTION");
        System.out.println("=".repeat(70));
        System.out.println("Board Size: " + boardSize + "x" + boardSize);
        System.out.println("Layers: " + boardLayers);
        System.out.println("Style: " + style);
        System.out.println("Dimension: " + dimension + "D");
        System.out.println("Tour Type: " + (closedTour ? "CLOSED" : "OPEN"));
        System.out.println("Visited Squares: " + visitedSquares + "/" + totalSquares);
        System.out.println("Tour Status: " + (isTourClosed() ? "CLOSED ✓" : "OPEN"));
        System.out.println("=".repeat(70));
        
        for (int l = 0; l < boardLayers; l++) {
            System.out.println("\nLAYER " + l + ":");
            System.out.println("-".repeat(boardSize * 6 + 1));
            
            for (int i = 0; i < boardSize; i++) {
                System.out.print("|");
                for (int j = 0; j < boardSize; j++) {
                    if (style == BoardStyle.WITH_HOLES && holes[l][i][j]) {
                        System.out.print("  H  |");
                    } else if (board[l][i][j] != -1) {
                        // Highlight start and end positions
                        if (l == startLayer && i == startRow && j == startCol) {
                            System.out.printf(" S%2d |", board[l][i][j]); // Start
                        } else if (board[l][i][j] == totalSquares) {
                            System.out.printf(" E%2d |", board[l][i][j]); // End
                        } else {
                            System.out.printf(" %3d |", board[l][i][j]);
                        }
                    } else {
                        System.out.print("  .  |");
                    }
                }
                System.out.println("\n" + "-".repeat(boardSize * 6 + 1));
            }
        }
        
        // Print summary
        if (closedTour && isTourClosed()) {
            System.out.println("\n★ CLOSED TOUR ACHIEVED!");
            System.out.println("  The knight returns to a square adjacent to the starting position.");
        } else if (hasValidSolution()) {
            System.out.println("\n★ OPEN TOUR COMPLETED!");
            System.out.println("  All squares visited successfully.");
        }
    }
    
    // Display the board with move sequence visualization
    public void printBoardWithPath() {
        System.out.println("\nPATH VISUALIZATION:");
        
        int maxDigits = String.valueOf(totalSquares).length();
        String format = "%" + maxDigits + "d ";
        
        for (int l = 0; l < boardLayers; l++) {
            System.out.println("\nLayer " + l + ":");
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (style == BoardStyle.WITH_HOLES && holes[l][i][j]) {
                        System.out.printf("%" + maxDigits + "s ", "H");
                    } else if (board[l][i][j] != -1) {
                        if (l == startLayer && i == startRow && j == startCol) {
                            System.out.printf("%" + maxDigits + "s ", "S");
                        } else if (board[l][i][j] == totalSquares) {
                            System.out.printf("%" + maxDigits + "s ", "E");
                        } else {
                            System.out.printf(format, board[l][i][j]);
                        }
                    } else {
                        System.out.printf("%" + maxDigits + "s ", ".");
                    }
                }
                System.out.println();
            }
        }
        
        // Show path summary
        if (hasValidSolution()) {
            System.out.println("\nPATH SUMMARY:");
            System.out.println("S = Start (" + board[startLayer][startRow][startCol] + ")");
            System.out.println("E = End (" + totalSquares + ")");
            if (isTourClosed()) {
                System.out.println("→ Closed loop achieved!");
            }
        }
    }
    
    // Check if board has a valid solution
    public boolean hasValidSolution() {
        return visitedSquares == totalSquares;
    }
    
    // Interactive setup method
    public static KnightsTour interactiveSetup(Scanner scanner) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("KNIGHT'S TOUR SETUP");
        System.out.println("=".repeat(70));
        
        // Get board size
        int boardSize = 8;
        System.out.print("\nEnter board size (default 8): ");
        String sizeInput = scanner.nextLine().trim();
        if (!sizeInput.isEmpty()) {
            try {
                boardSize = Integer.parseInt(sizeInput);
                if (boardSize < 3) {
                    System.out.println("Board size too small! Using minimum size 3.");
                    boardSize = 3;
                } else if (boardSize > 20) {
                    System.out.println("Large board size may take longer to compute...");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Using default size 8.");
            }
        }
        
        // Get number of layers (for 3D)
        int layers = 1;
        System.out.print("Enter number of layers (for 3D boards, default 1): ");
        String layersInput = scanner.nextLine().trim();
        if (!layersInput.isEmpty()) {
            try {
                layers = Integer.parseInt(layersInput);
                if (layers < 1) {
                    System.out.println("Invalid number of layers! Using 1.");
                    layers = 1;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Using default 1 layer.");
            }
        }
        
        // Get board style
        BoardStyle style = BoardStyle.REGULAR;
        System.out.println("\nSelect board style:");
        System.out.println("1. Regular (standard chessboard)");
        System.out.println("2. Board with holes (some squares are blocked)");
        System.out.println("3. Higher dimensional (3D knight moves)");
        System.out.print("Enter choice (1-3, default 1): ");
        String styleInput = scanner.nextLine().trim();
        
        if (!styleInput.isEmpty()) {
            switch (styleInput) {
                case "2":
                    style = BoardStyle.WITH_HOLES;
                    break;
                case "3":
                    style = BoardStyle.HIGH_DIMENSIONAL;
                    break;
                default:
                    style = BoardStyle.REGULAR;
            }
        }
        
        // Get dimension
        int dimension = 2;
        if (style == BoardStyle.HIGH_DIMENSIONAL) {
            System.out.print("Enter dimension (2 for 2D, 3 for 3D, default 3): ");
            String dimInput = scanner.nextLine().trim();
            if (!dimInput.isEmpty()) {
                try {
                    dimension = Integer.parseInt(dimInput);
                    if (dimension < 2 || dimension > 3) {
                        System.out.println("Only 2D and 3D supported! Using 3D.");
                        dimension = 3;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Using 3D.");
                    dimension = 3;
                }
            } else {
                dimension = 3;
            }
            
            if (dimension == 3 && layers == 1) {
                System.out.println("For 3D boards, increasing layers to 3...");
                layers = 3;
            }
        }
        
        // Ask for tour type
        boolean closedTour = false;
        System.out.println("\nSelect tour type:");
        System.out.println("1. Open Tour (knight ends anywhere)");
        System.out.println("2. Closed Tour (knight ends adjacent to start)");
        System.out.print("Enter choice (1-2, default 1): ");
        String tourInput = scanner.nextLine().trim();
        
        if (tourInput.equals("2")) {
            closedTour = true;
            System.out.println("Closed tour selected. This is more challenging!");
            if (boardSize % 2 == 1) {
                System.out.println("Note: Closed tours on odd-sized boards are very rare.");
            }
        } else {
            System.out.println("Open tour selected.");
        }
        
        return new KnightsTour(boardSize, layers, style, dimension, closedTour);
    }
    
    // Get starting position from user
    public static int[] getStartingPosition(Scanner scanner, KnightsTour tour) {
        int[] position = new int[3];
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("STARTING POSITION");
        System.out.println("=".repeat(50));
        
        System.out.print("Enter starting layer (0 to " + (tour.boardLayers - 1) + 
                       ", or -1 for random): ");
        try {
            String layerInput = scanner.nextLine().trim();
            if (!layerInput.isEmpty()) {
                position[0] = Integer.parseInt(layerInput);
            } else {
                position[0] = 0;
            }
        } catch (NumberFormatException e) {
            position[0] = 0;
        }
        
        if (position[0] >= 0) {
            System.out.print("Enter starting row (0 to " + (tour.boardSize - 1) + "): ");
            try {
                String rowInput = scanner.nextLine().trim();
                if (!rowInput.isEmpty()) {
                    position[1] = Integer.parseInt(rowInput);
                } else {
                    position[1] = 0;
                }
            } catch (NumberFormatException e) {
                position[1] = 0;
            }
            
            System.out.print("Enter starting column (0 to " + (tour.boardSize - 1) + "): ");
            try {
                String colInput = scanner.nextLine().trim();
                if (!colInput.isEmpty()) {
                    position[2] = Integer.parseInt(colInput);
                } else {
                    position[2] = 0;
                }
            } catch (NumberFormatException e) {
                position[2] = 0;
            }
            
            // Validate starting position
            if (position[0] < 0 || position[0] >= tour.boardLayers ||
                position[1] < 0 || position[1] >= tour.boardSize ||
                position[2] < 0 || position[2] >= tour.boardSize) {
                System.out.println("Invalid starting position! Using (0, 0, 0)");
                position[0] = 0;
                position[1] = 0;
                position[2] = 0;
            }
            
            // Check if starting position is a hole
            if (tour.style == BoardStyle.WITH_HOLES && 
                tour.holes[position[0]][position[1]][position[2]]) {
                System.out.println("Warning: Starting position is a hole!");
                System.out.println("Finding nearest valid position...");
                // Find nearest valid position
                for (int l = 0; l < tour.boardLayers; l++) {
                    for (int r = 0; r < tour.boardSize; r++) {
                        for (int c = 0; c < tour.boardSize; c++) {
                            if (!tour.holes[l][r][c]) {
                                position[0] = l;
                                position[1] = r;
                                position[2] = c;
                                System.out.println("New starting position: Layer " + l + 
                                                 ", Row " + r + ", Col " + c);
                                return position;
                            }
                        }
                    }
                }
            }
        }
        
        return position;
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=".repeat(70));
        System.out.println("COMPLETE KNIGHT'S TOUR WITH WARNSTORFF'S ALGORITHM");
        System.out.println("=".repeat(70));
        System.out.println("Features:");
        System.out.println("• Multiple board sizes (3x3 to 20x20+)");
        System.out.println("• 2D and 3D boards");
        System.out.println("• Boards with holes (randomly placed)");
        System.out.println("• Open and Closed tours");
        System.out.println("• Warnsdorff's algorithm with tie-breaking");
        System.out.println("• User-defined starting positions");
        System.out.println("=".repeat(70));
        
        try {
            // Interactive setup
            KnightsTour tour = interactiveSetup(scanner);
            
            // Get starting position
            int[] startPos = getStartingPosition(scanner, tour);
            
            boolean success = false;
            long startTime = System.currentTimeMillis();
            
            if (startPos[0] >= 0) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println("SOLVING TOUR...");
                System.out.println("=".repeat(50));
                System.out.println("Starting at:");
                System.out.println("  Layer: " + startPos[0]);
                System.out.println("  Row: " + startPos[1]);
                System.out.println("  Column: " + startPos[2]);
                
                success = tour.solve(startPos[0], startPos[1], startPos[2]);
                
                if (!success || !tour.hasValidSolution()) {
                    System.out.println("\nNo solution found from specified starting position.");
                    if (tour.closedTour) {
                        System.out.println("Closed tours are more difficult. Trying random positions...");
                    }
                    System.out.println("Attempting to find solution from random positions...");
                    success = tour.solveWithRandomStart(tour.closedTour ? 2000 : 500);
                }
            } else {
                System.out.println("\nFinding random starting position...");
                success = tour.solveWithRandomStart(tour.closedTour ? 3000 : 1000);
            }
            
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            
            if (success && tour.hasValidSolution()) {
                System.out.println("\n" + "=".repeat(70));
                System.out.println("✓ TOUR COMPLETED SUCCESSFULLY!");
                System.out.println("  Time: " + elapsedTime + "ms");
                System.out.println("=".repeat(70));
                tour.printBoard();
                
                System.out.print("\nShow compact path visualization? (y/n, default y): ");
                String showSimple = scanner.nextLine().trim();
                if (!showSimple.equalsIgnoreCase("n")) {
                    tour.printBoardWithPath();
                }
                
                // Show statistics
                System.out.println("\n" + "=".repeat(50));
                System.out.println("TOUR STATISTICS");
                System.out.println("=".repeat(50));
                System.out.println("Board squares: " + tour.totalSquares);
                System.out.println("Tour type: " + (tour.closedTour ? "Closed" : "Open"));
                System.out.println("Status: " + (tour.isTourClosed() ? "Closed ✓" : "Open"));
                System.out.println("Computation time: " + elapsedTime + "ms");
                
            } else {
                String errorMsg = "\n" + "=".repeat(70) + "\n";
                errorMsg += "ERROR: No complete solution found!\n";
                errorMsg += "=".repeat(70) + "\n";
                errorMsg += "Configuration:\n";
                errorMsg += "  Board: " + tour.boardSize + "x" + tour.boardSize + "\n";
                errorMsg += "  Layers: " + tour.boardLayers + "\n";
                errorMsg += "  Style: " + tour.style + "\n";
                errorMsg += "  Tour Type: " + (tour.closedTour ? "Closed" : "Open") + "\n";
                errorMsg += "  Visited: " + tour.visitedSquares + "/" + tour.totalSquares + " squares\n";
                errorMsg += "\nSuggestions:\n";
                errorMsg += "1. Try a different starting position\n";
                errorMsg += "2. Try an open tour instead of closed\n";
                errorMsg += "3. Reduce board size\n";
                errorMsg += "4. Remove holes from board\n";
                errorMsg += "5. Try more random attempts\n";
                errorMsg += "=".repeat(70);
                
                throw new RuntimeException(errorMsg);
            }
            
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("\nPlease try different parameters.");
        } finally {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("Thank you for using Knight's Tour!");
            System.out.println("=".repeat(70));
            scanner.close();
        }
    }
}
