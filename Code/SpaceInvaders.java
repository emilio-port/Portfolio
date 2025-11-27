// Import necessary Java libraries for GUI, events, and data structures
import javax.swing.*; // For GUI components (JPanel, JFrame, Timer)
import java.awt.*; // For graphics and drawing (Color, Graphics, Font)
import java.awt.event.*; // For event handling (ActionListener, KeyListener)
import java.util.ArrayList; // For dynamic arrays to store game objects
import java.util.Iterator; // For safe removal of objects during iteration
import java.util.Random; // For generating random numbers

// Main game class that extends JPanel for drawing and implements listeners for game loop and input
public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    // Game constants - LARGER BOARD
    private static final int WIDTH = 1024; // Game window width in pixels
    private static final int HEIGHT = 768; // Game window height in pixels
    private static final int PLAYER_WIDTH = 60; // Width of player ship
    private static final int PLAYER_HEIGHT = 40; // Height of player ship
    private static final int INVADER_SIZE = 35; // Size of each alien invader
    private static final int BULLET_SIZE = 6; // Size of player bullets
    private static final int BOSS_WIDTH = 100; // Width of boss enemy
    private static final int BOSS_HEIGHT = 60; // Height of boss enemy
    
    // Game state variables
    private Timer timer; // Game timer that triggers the game loop
    private boolean gameRunning = true; // Whether the game is currently running
    private boolean gameOver = false; // Whether the game has ended
    
    // Player position and movement
    private int playerX = WIDTH / 2 - PLAYER_WIDTH / 2; // Player's X position (centered)
    private int playerY = HEIGHT - 100; // Player's Y position (near bottom)
    private int playerSpeed = 10; // How fast the player moves left/right
    
    // Game statistics
    private int score = 0; // Player's current score
    private int lives = 3; // Number of lives remaining
    private int wave = 1; // Current wave number
    private boolean bossLevel = false; // Whether currently in a boss level
    private boolean postBossMode = false; // Whether enemies are tougher after boss rounds
    
    // Shooting control variables
    private boolean canShoot = true; // Whether player can currently shoot
    private long lastShotTime = 0; // Time when last shot was fired
    private final long SHOT_DELAY = 200; // Delay between shots in milliseconds
    private int MAX_BULLETS = 3; // Maximum number of bullets allowed on screen (will increase)
    
    // Weapon upgrade system
    private int weaponLevel = 1; // Current weapon level
    private boolean powerfulRounds = false; // Whether bullets are more powerful
    
    // Enemy shooting control
    private long lastEnemyShotTime = 0; // Time when last enemy shot was fired
    private final long ENEMY_SHOT_DELAY = 2000; // 2 seconds between enemy shots
    
    // Extra life system variables
    private int lastExtraLifeScore = 0; // Score at which last extra life was given
    private final int EXTRA_LIFE_INTERVAL = 2000; // Points needed for each extra life
    
    // Keyboard input states (for smooth movement)
    private boolean leftPressed = false; // Whether left arrow key is pressed
    private boolean rightPressed = false; // Whether right arrow key is pressed
    private boolean spacePressed = false; // Whether space bar is pressed
    
    // Collections to store game objects
    private ArrayList<Invader> invaders; // List of all alien invaders
    private ArrayList<Bullet> bullets; // List of player's bullets
    private ArrayList<BossBullet> bossBullets; // List of boss's bullets
    private ArrayList<EnemyBullet> enemyBullets; // List of enemy bullets
    private Boss boss; // The boss enemy (null when no boss)
    
    // Random number generator and background elements
    private Random random = new Random(); // For random number generation
    private ArrayList<Star> stars = new ArrayList<>(); // List of background stars
    
    // Animation control variables
    private boolean alienAntennaUp = false; // State of alien antenna animation
    private int animationCounter = 0; // Counter for timing animations
    
    // Constructor - sets up the game
    public SpaceInvaders() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); // Set panel size
        setBackground(new Color(10, 10, 40)); // Set dark blue background color
        setFocusable(true); // Allow panel to receive keyboard focus
        addKeyListener(this); // Register this class to handle keyboard events
        
        initializeStars(); // Create background stars
        invaders = new ArrayList<>(); // Initialize empty list for invaders
        bullets = new ArrayList<>(); // Initialize empty list for bullets
        bossBullets = new ArrayList<>(); // Initialize empty list for boss bullets
        enemyBullets = new ArrayList<>(); // Initialize empty list for enemy bullets
        
        createInvaders(); // Create the first wave of invaders
        
        timer = new Timer(16, this); // Create timer that triggers every 16ms (~60fps)
        timer.start(); // Start the game timer
    }
    
    // Initialize background stars with random positions and speeds
    private void initializeStars() {
        stars.clear(); // Remove any existing stars
        for (int i = 0; i < 30; i++) { // Create 30 stars
            stars.add(new Star( // Add new star to the list
                random.nextInt(WIDTH), // Random X position across screen width
                random.nextInt(HEIGHT), // Random Y position across screen height
                random.nextInt(2) + 1, // Random speed (1 or 2 pixels per frame)
                random.nextInt(3) + 1 // Random size (1, 2, or 3 pixels)
            ));
        }
    }
    
    // Create a new wave of alien invaders
    private void createInvaders() {
        invaders.clear(); // Remove any existing invaders
        
        // Calculate base health - increases by 1 each wave, minimum 1
        int baseHealth = Math.max(1, wave);
        
        // Limit total enemies to 20
        int maxEnemies = 20;
        int rows = Math.min(4, 2 + (wave / 2)); // Max 4 rows
        int cols = Math.min(5, 6); // Max 5 columns to keep under 20
        
        postBossMode = (wave > 4); // Enable tougher enemies after wave 4
        
        // Different colors for each row of invaders
        Color[] rowColors = {
            new Color(0, 200, 0),    // Green
            new Color(200, 0, 200),  // Magenta  
            new Color(0, 200, 200),  // Cyan
            new Color(200, 200, 0)   // Yellow
        };
        
        // Create grid of invaders (limited to maxEnemies)
        int enemyCount = 0;
        for (int row = 0; row < rows && enemyCount < maxEnemies; row++) { // For each row
            for (int col = 0; col < cols && enemyCount < maxEnemies; col++) { // For each column
                int x = 150 + col * (INVADER_SIZE + 30); // Calculate X position with spacing
                int y = 80 + row * (INVADER_SIZE + 30); // Calculate Y position with spacing
                int speed = 1 + (wave / 3); // Speed increases with waves
                Color color = rowColors[row % rowColors.length]; // Cycle through colors
                int health = postBossMode ? baseHealth + 1 : baseHealth; // Extra health after boss levels
                invaders.add(new Invader(x, y, speed, color, health)); // Add new invader
                enemyCount++;
            }
        }
        
        // Update weapon level every 3 rounds
        weaponLevel = 1 + (wave / 3);
        MAX_BULLETS = 3 + weaponLevel - 1; // Increase max bullets with weapon level
        
        // Enable powerful rounds after wave 5
        powerfulRounds = (wave >= 5);
    }
    
    // Create a boss enemy
    private void createBoss() {
        boss = new Boss(WIDTH / 2 - BOSS_WIDTH / 2, 80, 3); // Create boss centered at top
        bossLevel = true; // Set boss level flag
    }
    
    // Main game update method - called every frame by timer
    private void updateGame() {
        if (!gameRunning || gameOver) return; // Skip update if game not running
        
        updateStars(); // Update background star positions
        
        animationCounter++; // Increment animation counter
        if (animationCounter % 30 == 0) { // Every 30 frames (about twice per second)
            alienAntennaUp = !alienAntennaUp; // Toggle alien antenna state
        }
        
        handleContinuousInput(); // Process continuous keyboard input
        handleContinuousShooting(); // Process continuous shooting
        
        for (Invader invader : invaders) { // Update each invader
            invader.move(); // Move the invader
        }
        
        // Enemies shoot back with delay - only one enemy can shoot every 2 seconds
        long currentTime = System.currentTimeMillis();
        if (!invaders.isEmpty() && currentTime - lastEnemyShotTime > ENEMY_SHOT_DELAY) {
            // Pick a random enemy to shoot
            Invader shooter = invaders.get(random.nextInt(invaders.size()));
            enemyBullets.add(new EnemyBullet(
                shooter.x + INVADER_SIZE / 2, 
                shooter.y + INVADER_SIZE,
                random.nextInt(3) + 2 + wave / 5 // Speed increases with waves
            ));
            lastEnemyShotTime = currentTime; // Update last shot time
        }
        
        if (bossLevel && boss != null) { // If in boss level and boss exists
            boss.move(); // Move the boss
            
            // Boss shoots with max 3 bullets on screen
            if (random.nextInt(100) < 2 && bossBullets.size() < 3) { // 2% chance each frame to shoot, max 3 bullets
                bossBullets.add(new BossBullet(boss.x + BOSS_WIDTH / 2, boss.y + BOSS_HEIGHT));
            }
        }
        
        // Update player bullets and remove off-screen ones
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet = bulletIter.next();
            bullet.move(); // Move bullet upward
            if (bullet.y < 0) { // If bullet goes off top of screen
                bulletIter.remove(); // Remove the bullet
            }
        }
        
        // Update boss bullets and remove off-screen ones
        Iterator<BossBullet> bossBulletIter = bossBullets.iterator();
        while (bossBulletIter.hasNext()) {
            BossBullet bullet = bossBulletIter.next();
            bullet.move(); // Move bullet downward
            if (bullet.y > HEIGHT) { // If bullet goes off bottom of screen
                bossBulletIter.remove(); // Remove the bullet
            }
        }
        
        // Update enemy bullets and remove off-screen ones
        Iterator<EnemyBullet> enemyBulletIter = enemyBullets.iterator();
        while (enemyBulletIter.hasNext()) {
            EnemyBullet bullet = enemyBulletIter.next();
            bullet.move(); // Move bullet downward
            if (bullet.y > HEIGHT) { // If bullet goes off bottom of screen
                enemyBulletIter.remove(); // Remove the bullet
            }
        }
        
        checkCollisions(); // Check for collisions between objects
        checkWaveCompletion(); // Check if current wave is complete
        checkExtraLife(); // Check if player earned extra life
    }
    
    // Update star positions for parallax background effect
    private void updateStars() {
        for (Star star : stars) { // For each star
            star.y += star.speed; // Move star downward
            if (star.y > HEIGHT) { // If star moves off bottom of screen
                star.y = 0; // Reset to top of screen
                star.x = random.nextInt(WIDTH); // Random X position
            }
        }
    }
    
    // Handle continuous keyboard input for smooth movement
    private void handleContinuousInput() {
        if (leftPressed && playerX > 0) { // If left pressed and not at left edge
            playerX -= playerSpeed; // Move player left
        }
        if (rightPressed && playerX < WIDTH - PLAYER_WIDTH) { // If right pressed and not at right edge
            playerX += playerSpeed; // Move player right
        }
    }
    
    // Handle continuous shooting with delay between shots
    private void handleContinuousShooting() {
        if (spacePressed && canShoot) { // If space pressed and can shoot
            shoot(); // Fire a bullet
            canShoot = false; // Prevent immediate follow-up shot
            lastShotTime = System.currentTimeMillis(); // Record shot time
        }
        
        // Check if enough time has passed to shoot again
        if (!canShoot && System.currentTimeMillis() - lastShotTime > SHOT_DELAY) {
            canShoot = true; // Allow shooting again
        }
    }
    
    // Check for collisions between game objects
    private void checkCollisions() {
        // Check player bullets against invaders and boss
        Iterator<Bullet> bulletIter = bullets.iterator();
        while (bulletIter.hasNext()) {
            Bullet bullet = bulletIter.next();
            
            if (bossLevel && boss != null) { // Check collision with boss
                if (bullet.x >= boss.x && bullet.x <= boss.x + BOSS_WIDTH &&
                    bullet.y >= boss.y && bullet.y <= boss.y + BOSS_HEIGHT) {
                    boss.health--; // Reduce boss health
                    bulletIter.remove(); // Remove bullet
                    score += 50; // Add points for hitting boss
                    
                    if (boss.health <= 0) { // If boss defeated
                        boss = null; // Remove boss
                        bossLevel = false; // Exit boss level
                        wave++; // Advance to next wave
                        score += 500; // Bonus points for defeating boss
                        postBossMode = true; // Enable tougher enemies
                        createInvaders(); // Create new wave
                    }
                    continue; // Skip to next bullet
                }
            }
            
            // Check collision with regular invaders
            Iterator<Invader> invaderIter = invaders.iterator();
            while (invaderIter.hasNext()) {
                Invader invader = invaderIter.next();
                if (bullet.x >= invader.x && bullet.x <= invader.x + INVADER_SIZE &&
                    bullet.y >= invader.y && bullet.y <= invader.y + INVADER_SIZE) {
                    
                    // Powerful rounds do double damage after wave 5
                    int damage = powerfulRounds ? 2 : 1;
                    invader.health -= damage;
                    bulletIter.remove(); // Remove bullet
                    
                    if (invader.health <= 0) { // If invader destroyed
                        invaderIter.remove(); // Remove invader
                        score += postBossMode ? 20 : 10; // More points for tough enemies
                    }
                    break; // Stop checking this bullet
                }
            }
        }
        
        // Check boss bullets against player
        Iterator<BossBullet> bossBulletIter = bossBullets.iterator();
        while (bossBulletIter.hasNext()) {
            BossBullet bullet = bossBulletIter.next();
            if (bullet.x >= playerX && bullet.x <= playerX + PLAYER_WIDTH &&
                bullet.y >= playerY && bullet.y <= playerY + PLAYER_HEIGHT) {
                bossBulletIter.remove(); // Remove boss bullet
                loseLife(); // Player loses a life
                break; // Stop checking
            }
        }
        
        // Check enemy bullets against player
        Iterator<EnemyBullet> enemyBulletIter = enemyBullets.iterator();
        while (enemyBulletIter.hasNext()) {
            EnemyBullet bullet = enemyBulletIter.next();
            if (bullet.x >= playerX && bullet.x <= playerX + PLAYER_WIDTH &&
                bullet.y >= playerY && bullet.y <= playerY + PLAYER_HEIGHT) {
                enemyBulletIter.remove(); // Remove enemy bullet
                loseLife(); // Player loses a life
                break; // Stop checking
            }
        }
        
        // Check if invaders reached the bottom (player loses life)
        Iterator<Invader> invaderIter = invaders.iterator();
        while (invaderIter.hasNext()) {
            Invader invader = invaderIter.next();
            if (invader.y + INVADER_SIZE >= playerY) { // If invader reaches player level
                loseLife(); // Player loses a life
                invaderIter.remove(); // Remove the invader
                break; // Only one invader causes damage per frame
            }
        }
    }
    
    // Check if current wave is complete and advance to next wave
    private void checkWaveCompletion() {
        if (!bossLevel && invaders.isEmpty()) { // If no boss and all invaders destroyed
            wave++; // Advance to next wave
            if (wave == 4 || wave == 9) { // Check if this is a boss wave
                createBoss(); // Create boss for waves 4 and 9
            } else {
                createInvaders(); // Create regular wave
            }
        }
    }
    
    // Check if player earned an extra life
    private void checkExtraLife() {
        if (score >= lastExtraLifeScore + EXTRA_LIFE_INTERVAL) { // If crossed threshold
            lives++; // Award extra life
            lastExtraLifeScore = (score / EXTRA_LIFE_INTERVAL) * EXTRA_LIFE_INTERVAL; // Update threshold
        }
    }
    
    // Player loses a life
    private void loseLife() {
        lives--; // Decrease life count
        if (lives <= 0) { // If no lives left
            gameOver = true; // Game over
        }
        
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2; // Reset player position to center
        bullets.clear(); // Clear all player bullets
        bossBullets.clear(); // Clear all boss bullets
        enemyBullets.clear(); // Clear all enemy bullets
    }
    
    // Create a new player bullet
    public void shoot() {
        if (bullets.size() < MAX_BULLETS) { // Only shoot if fewer than max bullets
            bullets.add(new Bullet(playerX + PLAYER_WIDTH / 2 - BULLET_SIZE / 2, playerY, powerfulRounds));
        }
    }
    
    // Main drawing method - called automatically when panel needs redrawing
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call parent method to clear background
        
        drawStars(g); // Draw background stars
        drawPlayerShip(g); // Draw player ship
        
        for (Invader invader : invaders) { // Draw each invader
            drawAlienEmojiSprite(g, invader.x, invader.y, invader.color, invader.health);
        }
        
        drawBullets(g); // Draw player bullets
        drawEnemyBullets(g); // Draw enemy bullets
        
        if (bossLevel && boss != null) { // If boss exists
            drawBossSprite(g); // Draw boss
            drawBossBullets(g); // Draw boss bullets
            
            // Draw boss bullet counter
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Boss Shots: " + bossBullets.size() + "/3", boss.x, boss.y - 25);
        }
        
        drawHUD(g); // Draw score, lives, etc.
        
        if (gameOver) { // If game over
            drawGameOverScreen(g); // Draw game over screen
        }
    }
    
    // Draw background stars
    private void drawStars(Graphics g) {
        g.setColor(Color.WHITE); // Set star color to white
        for (Star star : stars) { // Draw each star
            g.fillRect(star.x, star.y, star.size, star.size); // Draw star as small square
        }
    }
    
    // Draw player ship as a triangle with details
    private void drawPlayerShip(Graphics g) {
        // Define triangle points for ship body
        int[] xPoints = {
            playerX + PLAYER_WIDTH / 2,        // Nose tip (center top)
            playerX + 15,                      // Left bottom corner
            playerX + PLAYER_WIDTH - 15        // Right bottom corner
        };
        int[] yPoints = {
            playerY + 5,                       // Nose Y position
            playerY + PLAYER_HEIGHT - 5,       // Left bottom Y
            playerY + PLAYER_HEIGHT - 5        // Right bottom Y
        };
        
        g.setColor(new Color(0, 255, 255)); // Set ship color to cyan
        g.fillPolygon(xPoints, yPoints, 3); // Draw filled triangle
        
        g.setColor(new Color(0, 150, 255)); // Set cockpit color to blue
        g.fillOval(playerX + PLAYER_WIDTH / 2 - 8, playerY, 16, 12); // Draw cockpit dome
        
        g.setColor(new Color(0, 200, 255)); // Set wing color
        g.fillRect(playerX + 5, playerY + PLAYER_HEIGHT - 12, PLAYER_WIDTH - 10, 8); // Draw wings
        
        // Create gradient for engine glow
        GradientPaint engineGlow = new GradientPaint(
            playerX + PLAYER_WIDTH / 2 - 4, playerY + PLAYER_HEIGHT, // Start position
            Color.YELLOW, // Start color
            playerX + PLAYER_WIDTH / 2 - 4, playerY + PLAYER_HEIGHT + 10, // End position  
            Color.ORANGE // End color
        );
        ((Graphics2D) g).setPaint(engineGlow); // Apply gradient
        g.fillRect(playerX + PLAYER_WIDTH / 2 - 4, playerY + PLAYER_HEIGHT, 8, 10); // Draw engine
    }
    
    // Draw alien invader in emoji style
    private void drawAlienEmojiSprite(Graphics g, int x, int y, Color color, int health) {
        g.setColor(color); // Set alien body color
        g.fillOval(x + 5, y + 8, INVADER_SIZE - 10, INVADER_SIZE - 16); // Draw main body
        
        g.setColor(color.brighter()); // Set brighter color for head
        g.fillOval(x + 8, y + 2, INVADER_SIZE - 16, 12); // Draw head dome
        
        g.setColor(Color.BLACK); // Set eye color to black
        g.fillOval(x + 10, y + 10, 8, 10); // Draw left eye
        g.fillOval(x + INVADER_SIZE - 18, y + 10, 8, 10); // Draw right eye
        
        g.setColor(Color.WHITE); // Set highlight color
        g.fillOval(x + 12, y + 12, 3, 4); // Draw left eye highlight
        g.fillOval(x + INVADER_SIZE - 16, y + 12, 3, 4); // Draw right eye highlight
        
        g.setColor(Color.BLACK); // Set mouth color
        g.fillRect(x + 15, y + 25, INVADER_SIZE - 30, 2); // Draw mouth line
        
        g.setColor(color); // Set antenna color
        if (alienAntennaUp) { // If antenna should be up
            g.fillOval(x + INVADER_SIZE / 2 - 2, y - 3, 4, 6); // Draw antenna
        }
        
        // Draw health bar for enemies with more than 1 health
        if (health > 1) {
            g.setColor(Color.RED); // Set health bar background color
            g.fillRect(x, y - 5, INVADER_SIZE, 3); // Draw full health bar background
            g.setColor(Color.GREEN); // Set health bar color
            int healthWidth = (INVADER_SIZE * health) / 10; // Calculate current health width
            g.fillRect(x, y - 5, healthWidth, 3); // Draw current health
        }
    }
    
    // Draw player bullets as laser beams
    private void drawBullets(Graphics g) {
        for (Bullet bullet : bullets) { // For each bullet
            // Use different colors for powerful rounds
            if (bullet.powerful) {
                // Create gradient for powerful laser effect
                GradientPaint laserGlow = new GradientPaint(
                    bullet.x, bullet.y, Color.YELLOW, // Top color (yellow)
                    bullet.x, bullet.y + BULLET_SIZE * 4, Color.RED // Bottom color (red)
                );
                ((Graphics2D) g).setPaint(laserGlow); // Apply gradient
                g.fillRect(bullet.x - 1, bullet.y, BULLET_SIZE + 2, BULLET_SIZE * 4); // Draw larger laser
            } else {
                // Create gradient for regular laser effect
                GradientPaint laserGlow = new GradientPaint(
                    bullet.x, bullet.y, Color.CYAN, // Top color (cyan)
                    bullet.x, bullet.y + BULLET_SIZE * 3, Color.BLUE // Bottom color (blue)
                );
                ((Graphics2D) g).setPaint(laserGlow); // Apply gradient
                g.fillRect(bullet.x, bullet.y, BULLET_SIZE, BULLET_SIZE * 3); // Draw laser beam
            }
        }
    }
    
    // Draw enemy bullets
    private void drawEnemyBullets(Graphics g) {
        g.setColor(Color.RED); // Set enemy bullet color to red
        for (EnemyBullet bullet : enemyBullets) { // For each enemy bullet
            g.fillOval(bullet.x - 3, bullet.y, 6, 10); // Draw enemy bullet as oval
        }
    }
    
    // Draw boss enemy
    private void drawBossSprite(Graphics g) {
        g.setColor(new Color(180, 0, 180)); // Set boss color to purple
        g.fillOval(boss.x + 10, boss.y + 15, BOSS_WIDTH - 20, BOSS_HEIGHT - 20); // Draw body
        
        g.setColor(new Color(200, 50, 200)); // Set head color
        g.fillOval(boss.x + 20, boss.y + 5, BOSS_WIDTH - 40, 20); // Draw head
        
        g.setColor(Color.BLACK); // Set eye color
        g.fillOval(boss.x + 25, boss.y + 20, 15, 20); // Draw left eye
        g.fillOval(boss.x + BOSS_WIDTH - 40, boss.y + 20, 15, 20); // Draw right eye
        
        g.setColor(Color.WHITE); // Set eye highlight color
        g.fillOval(boss.x + 28, boss.y + 23, 6, 8); // Draw left eye highlight
        g.fillOval(boss.x + BOSS_WIDTH - 37, boss.y + 23, 6, 8); // Draw right eye highlight
        
        g.setColor(Color.BLACK); // Set mouth color
        g.fillRect(boss.x + 35, boss.y + 45, BOSS_WIDTH - 70, 4); // Draw mouth
        
        // Draw boss health bar
        g.setColor(Color.RED); // Set health bar background color
        g.fillRect(boss.x, boss.y - 15, BOSS_WIDTH, 8); // Draw full health bar
        g.setColor(Color.GREEN); // Set health bar color
        int healthWidth = (BOSS_WIDTH * boss.health) / 10; // Calculate current health width
        g.fillRect(boss.x, boss.y - 15, healthWidth, 8); // Draw current health
        g.setColor(Color.WHITE); // Set border color
        g.drawRect(boss.x, boss.y - 15, BOSS_WIDTH, 8); // Draw health bar border
    }
    
    // Draw boss bullets as fireballs
    private void drawBossBullets(Graphics g) {
        g.setColor(Color.ORANGE); // Set outer fireball color
        for (BossBullet bullet : bossBullets) { // For each boss bullet
            g.fillOval(bullet.x - 4, bullet.y, 8, 8); // Draw outer fireball
            g.setColor(Color.YELLOW); // Set inner fireball color
            g.fillOval(bullet.x - 2, bullet.y + 2, 4, 4); // Draw inner fireball
        }
    }
    
    // Draw Heads-Up Display (score, lives, wave info)
    private void drawHUD(Graphics g) {
        g.setColor(Color.WHITE); // Set text color to white
        g.setFont(new Font("Arial", Font.BOLD, 20)); // Set font
        g.drawString("Score: " + score, 20, 30); // Draw score at top-left
        g.drawString("Lives: " + lives, 20, 60); // Draw lives below score
        g.drawString("Wave: " + wave, WIDTH - 120, 30); // Draw wave at top-right
        
        g.setColor(Color.CYAN); // Set bullet counter color
        g.drawString("Shots: " + bullets.size() + "/" + MAX_BULLETS, WIDTH - 120, 60); // Draw bullet count
        
        int nextExtraLifeAt = lastExtraLifeScore + EXTRA_LIFE_INTERVAL; // Calculate next life threshold
        g.setColor(Color.YELLOW); // Set extra life indicator color
        g.drawString("Next Life: " + nextExtraLifeAt, WIDTH / 2 - 80, 60); // Draw next life info
        
        // Display weapon level and upgrades
        g.setColor(Color.GREEN);
        g.drawString("Weapon Lvl: " + weaponLevel, WIDTH / 2 - 80, 90);
        
        if (postBossMode) { // If in tough enemy mode
            g.setColor(Color.YELLOW); // Set warning color
            g.drawString("TOUGH ENEMIES!", WIDTH / 2 - 80, 120); // Draw warning
        }
        
        if (powerfulRounds) { // If powerful rounds are active
            g.setColor(Color.ORANGE); // Set power-up color
            g.drawString("POWER SHOTS!", WIDTH / 2 - 70, 150); // Draw power-up indicator
        }
        
        for (int i = 0; i < lives; i++) { // Draw life icons
            drawLifeIcon(g, 20 + i * 25, 80); // Draw small ship for each life
        }
        
        if (bossLevel) { // If in boss level
            g.setColor(Color.RED); // Set boss warning color
            g.setFont(new Font("Arial", Font.BOLD, 28)); // Larger font for boss
            g.drawString("BOSS BATTLE!", WIDTH / 2 - 90, 60); // Draw boss warning
        }
    }
    
    // Draw small life icon (mini ship)
    private void drawLifeIcon(Graphics g, int x, int y) {
        g.setColor(Color.CYAN); // Set ship color
        int[] lifeXPoints = {x + 4, x, x + 8}; // Triangle points X
        int[] lifeYPoints = {y, y + 6, y + 6}; // Triangle points Y
        g.fillPolygon(lifeXPoints, lifeYPoints, 3); // Draw small triangle ship
    }
    
    // Draw game over screen overlay
    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180)); // Set semi-transparent black overlay
        g.fillRect(0, 0, WIDTH, HEIGHT); // Cover entire screen
        
        g.setColor(Color.RED); // Set game over text color
        g.setFont(new Font("Arial", Font.BOLD, 48)); // Large font for game over
        g.drawString("GAME OVER", WIDTH / 2 - 140, HEIGHT / 2 - 50); // Draw game over text
        
        g.setColor(Color.WHITE); // Set info text color
        g.setFont(new Font("Arial", Font.BOLD, 24)); // Smaller font for info
        g.drawString("Final Score: " + score, WIDTH / 2 - 100, HEIGHT / 2 + 20); // Draw final score
        g.drawString("Reached Wave: " + wave, WIDTH / 2 - 100, HEIGHT / 2 + 50); // Draw wave reached
        g.drawString("Press R to Restart", WIDTH / 2 - 100, HEIGHT / 2 + 90); // Draw restart instructions
    }
    
    // Timer event handler - called every frame
    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame(); // Update game logic
        repaint(); // Request screen redraw
    }
    
    // Key pressed event handler
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode(); // Get which key was pressed
        
        if (gameOver && key == KeyEvent.VK_R) { // If game over and R pressed
            restartGame(); // Restart the game
            return; // Exit early
        }
        
        if (!gameRunning || gameOver) return; // Ignore input if game not running
        
        switch (key) { // Handle different keys
            case KeyEvent.VK_LEFT: // Left arrow
                leftPressed = true; // Set left movement flag
                break;
            case KeyEvent.VK_RIGHT: // Right arrow
                rightPressed = true; // Set right movement flag
                break;
            case KeyEvent.VK_SPACE: // Space bar
                spacePressed = true; // Set shooting flag
                break;
        }
    }
    
    // Key released event handler
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode(); // Get which key was released
        
        switch (key) { // Handle different keys
            case KeyEvent.VK_LEFT: // Left arrow
                leftPressed = false; // Clear left movement flag
                break;
            case KeyEvent.VK_RIGHT: // Right arrow
                rightPressed = false; // Clear right movement flag
                break;
            case KeyEvent.VK_SPACE: // Space bar
                spacePressed = false; // Clear shooting flag
                break;
        }
    }
    
    // Key typed event handler (not used but required by interface)
    @Override
    public void keyTyped(KeyEvent e) {}
    
    // Reset game to initial state
    private void restartGame() {
        score = 0; // Reset score to zero
        lives = 3; // Reset lives to three
        wave = 1; // Reset to first wave
        gameOver = false; // Clear game over flag
        bossLevel = false; // Clear boss level flag
        postBossMode = false; // Clear tough enemy flag
        powerfulRounds = false; // Clear powerful rounds flag
        weaponLevel = 1; // Reset weapon level
        MAX_BULLETS = 3; // Reset max bullets
        leftPressed = false; // Clear left key flag
        rightPressed = false; // Clear right key flag
        spacePressed = false; // Clear space key flag
        canShoot = true; // Allow shooting
        lastEnemyShotTime = 0; // Reset enemy shot timer
        lastExtraLifeScore = 0; // Reset extra life tracking
        bullets.clear(); // Clear all bullets
        bossBullets.clear(); // Clear all boss bullets
        enemyBullets.clear(); // Clear all enemy bullets
        initializeStars(); // Reset background stars
        createInvaders(); // Create first wave
        requestFocusInWindow(); // Ensure keyboard focus
    }
    
    // Inner class representing a background star
    class Star {
        int x, y, speed, size; // Position, movement speed, and size
        
        Star(int x, int y, int speed, int size) {
            this.x = x; // X coordinate
            this.y = y; // Y coordinate  
            this.speed = speed; // Pixels to move each frame
            this.size = size; // Width and height of star
        }
    }
    
    // Inner class representing an alien invader
    class Invader {
        int x, y, speed, direction = 1, health; // Position, speed, movement direction, health
        Color color; // Color of the invader
        
        Invader(int x, int y, int speed, Color color, int health) {
            this.x = x; // X position
            this.y = y; // Y position
            this.speed = speed; // Movement speed
            this.color = color; // Color
            this.health = health; // Number of hits required to destroy
        }
        
        // Move the invader horizontally, changing direction at screen edges
        void move() {
            x += speed * direction; // Move in current direction
            
            if (x <= 0 || x >= WIDTH - INVADER_SIZE) { // If hit left or right edge
                direction *= -1; // Reverse direction
                y += 35; // Move down one row
            }
        }
    }
    
    // Inner class representing a player bullet
    class Bullet {
        int x, y; // Position
        int speed = -10; // Upward movement speed (negative Y)
        boolean powerful; // Whether this is a powerful bullet
        
        Bullet(int x, int y, boolean powerful) {
            this.x = x; // X position (centered on player)
            this.y = y; // Y position (at player's top)
            this.powerful = powerful; // Set power level
        }
        
        void move() {
            y += speed; // Move upward
        }
    }
    
    // Inner class representing a boss bullet  
    class BossBullet {
        int x, y; // Position
        int speed = 6; // Downward movement speed (positive Y)
        
        BossBullet(int x, int y) {
            this.x = x; // X position (centered on boss)
            this.y = y; // Y position (at boss's bottom)
        }
        
        void move() {
            y += speed; // Move downward
        }
    }
    
    // Inner class representing an enemy bullet
    class EnemyBullet {
        int x, y; // Position
        int speed; // Downward movement speed
        
        EnemyBullet(int x, int y, int speed) {
            this.x = x; // X position (centered on enemy)
            this.y = y; // Y position (at enemy's bottom)
            this.speed = speed; // Movement speed
        }
        
        void move() {
            y += speed; // Move downward
        }
    }
    
    // Inner class representing a boss enemy
    class Boss {
        int x, y, speed, direction = 1; // Position, speed, movement direction
        int health = 10; // Health points (requires 10 hits)
        
        Boss(int x, int y, int speed) {
            this.x = x; // X position
            this.y = y; // Y position
            this.speed = speed; // Movement speed
        }
        
        // Move boss horizontally, bouncing between screen edges
        void move() {
            x += speed * direction; // Move in current direction
            
            if (x <= 0 || x >= WIDTH - BOSS_WIDTH) { // If hit left or right edge
                direction *= -1; // Reverse direction
            }
        }
    }
    
    // Main method - program entry point
    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread-safe GUI creation
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Space Invaders - Enhanced Version"); // Create game window
            SpaceInvaders game = new SpaceInvaders(); // Create game instance
            
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close on X button
            frame.getContentPane().add(game); // Add game panel to frame
            frame.pack(); // Size frame to fit game panel
            frame.setLocationRelativeTo(null); // Center window on screen
            frame.setResizable(false); // Prevent window resizing
            frame.setVisible(true); // Make window visible
            game.requestFocusInWindow(); // Ensure game receives keyboard input
        });
    }
}