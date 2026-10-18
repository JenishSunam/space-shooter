package my.newproject;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class SpaceShooterGame implements ApplicationListener {
    
    // Core components
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private BitmapFont titleFont;
    private Vector3 touchPos = new Vector3();
    
    // Game modules
    private GameState state;
    private GameLogic logic;
    private EntityFactory factory;
    private GameUpdater updater;
    private CollisionDetector collisions;
    private GameRenderer renderer;
    
    // Touch
    private boolean touchActive = false;
    private float touchX, touchY;
    
    // Sound effects
    private Sound shootSound;
    private Sound hitSound;
    private Sound explosionSound;
    private Sound powerUpSound;
    private Sound gameOverSound;
    private Music backgroundMusic;
    private long lastShootTime = 0;
    
    //game
    private boolean gameStarted = false;  // ADD THIS
    
    // Achievement popup
private float achievementPopupTimer = 0;
private String achievementPopupText = "";
private String achievementPopupDesc = "";
    
    @Override
public void create() {
    batch = new SpriteBatch();
    camera = new OrthographicCamera();
    camera.setToOrtho(false, GameState.WORLD_WIDTH, GameState.WORLD_HEIGHT);
    font = new BitmapFont();
    titleFont = new BitmapFont();
    titleFont.getData().setScale(2);
    
    // 1. INITIALIZE STATE FIRST (Move this up!)
    state = new GameState();
    
    // 2. NOW SET THE BASE (Now 'state' exists, so this won't crash)
    state.baseX = GameState.WORLD_WIDTH / 2;
    state.baseY = 50;
    
    // 3. Load the rest
    loadGameData();
    loadSounds();
    createTextures();
    
    // Load achievements
loadAchievements();

     //texure
     Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
whitePixmap.setColor(Color.WHITE);
whitePixmap.fill();
state.bulletTexture = new Texture(whitePixmap);
whitePixmap.dispose();

    
    // Initialize modules
    logic = new GameLogic(state, this);
    factory = new EntityFactory(state);
    updater = new GameUpdater(state, factory);
    collisions = new CollisionDetector(state, logic, this);
    renderer = new GameRenderer(batch, font, titleFont);
    
    // 4. Setup player (This was already working because it was below the 'new')
    state.playerX = GameState.WORLD_WIDTH / 2;
    state.playerY = 100;
    
    setupInput();
}
      public void startGame() {
    gameStarted = true;
    state.isPlaying = true;
    state.isPaused = false;
}



    
    private void loadSounds() {
    try {
        // Load Sounds
        shootSound = loadSound("shoot.wav");
        hitSound = loadSound("hit.wav");
        explosionSound = loadSound("explosion.wav");
        powerUpSound = loadSound("powerup.wav");
        gameOverSound = loadSound("gameover.wav");

        // Load and Configure Music
        if (Gdx.files.internal("background.mp3").exists()) {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
            backgroundMusic.play();
        }
    } catch (Exception e) {
        Gdx.app.log("Audio", "Error loading audio: " + e.getMessage());
    }
}
    private void loadAchievements() {
    com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("SpaceShooter");
    for (int i = 0; i < 10; i++) {
        state.achievements[i] = prefs.getBoolean("achievement_" + i, false);
    }
    state.totalBossesKilled = prefs.getInteger("totalBosses", 0);
    state.totalEnemiesKilled = prefs.getInteger("totalEnemies", 0);
}

private void saveAchievement(int index) {
    if (!state.achievements[index]) {
        state.achievements[index] = true;
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("SpaceShooter");
        prefs.putBoolean("achievement_" + index, true);
        prefs.flush();
        
        // Show achievement popup
        showAchievementPopup(index);
    }
}   public void addCoins(int amount) {
    state.coins += amount;
    com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("SpaceShooter");
    prefs.putInteger("coins", state.coins);
    prefs.flush();
}

private void showAchievementPopup(int index) {
    String[] names = {
        "⭐ FIRST BLOOD",           // 0: Kill 10 enemies
        "⚔️ ENEMY SLAYER",          // 1: Kill 50 enemies
        "👑 LEGENDARY",             // 2: Kill 200 enemies
        "💀 BOSS SLAYER",           // 3: Kill 1 boss
        "🔥 BOSS HUNTER",           // 4: Kill 5 bosses
        "🏆 BOSS MASTER",           // 5: Kill 10 bosses
        "💰 COIN COLLECTOR",        // 7: Collect 500 coins
        "💎 RICH",                  // 8: Collect 2000 coins
        
    };
    String[] desc = {
        "Destroy 10 enemies",
        "Destroy 50 enemies",
        "Destroy 200 enemies",
        "Defeat your first boss",
        "Defeat 5 bosses",
        "Defeat 10 bosses",
        "Collect 500 coins",
        "Collect 2000 coins",
        
    };
    
    achievementPopupTimer = 3.0f;
    achievementPopupText = names[index] + " UNLOCKED!";
    achievementPopupDesc = desc[index];
}
    private void checkAchievements() {
    // 0: Kill 10 enemies
    if (state.totalEnemiesKilled >= 10 && !state.achievements[0]) unlockAchievement(0);
    // 1: Kill 50 enemies
    if (state.totalEnemiesKilled >= 50 && !state.achievements[1]) unlockAchievement(1);
    // 2: Kill 200 enemies
    if (state.totalEnemiesKilled >= 200 && !state.achievements[2]) unlockAchievement(2);
    // 3: Kill 1 boss
    if (state.totalBossesKilled >= 1 && !state.achievements[3]) unlockAchievement(3);
    // 4: Kill 5 bosses
    if (state.totalBossesKilled >= 5 && !state.achievements[4]) unlockAchievement(4);
    // 5: Kill 10 bosses
    if (state.totalBossesKilled >= 10 && !state.achievements[5]) unlockAchievement(5);if (state.baseHealth >= state.maxBaseHealth * 0.8f && !state.achievements[6]) unlockAchievement(6);
    // 7: Collect 500 coins
    if (state.coins >= 500 && !state.achievements[7]) unlockAchievement(7);
    // 8: Collect 2000 coins
    if (state.coins >= 2000 && !state.achievements[8]) unlockAchievement(8);
    // 9: Perfect run (call this separately when game ends)
}

private void unlockAchievement(int index) {
    if (state.achievements[index]) return;
    state.achievements[index] = true;
    
    int[] rewards = {100, 200, 500, 150, 300, 200, 250, 500, 1000};
    addCoins(rewards[index]);
    
    com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("SpaceShooter");
    prefs.putBoolean("achievement_" + index, true);
    prefs.flush();
    
    showAchievementPopup(index);
}

// Helper method to prevent crashes if a file is missing
private Sound loadSound(String path) {
    if (Gdx.files.internal(path).exists()) {
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }
    return null; // Logic in GameLogic (like game.playHitSound()) already checks for null!
}

    
    public void playExplosionSound() {
        if (explosionSound != null) explosionSound.play(0.6f);
    }
    
    public void playBossDeathSound() {
        if (explosionSound != null) explosionSound.play(1.0f);
    }
    
    public void playHitSound() {
        if (hitSound != null) hitSound.play(0.7f);
    }
    
    public void playGameOverSound() {
        if (gameOverSound != null) gameOverSound.play(0.8f);
    }
    
    public void playShootSound() {
        if (shootSound != null && System.currentTimeMillis() - lastShootTime > 100) {
            shootSound.play(0.5f);
            lastShootTime = System.currentTimeMillis();
        }
      }  
    public void playPowerUpSound() {
    if (powerUpSound != null) {
        powerUpSound.play(0.5f);
    }
}
     public void setDifficulty(int diff) {
    state.difficulty = diff;
    switch(diff) {
        case 0: // Easy
            state.difficultyMultiplier = 0.7f;
            state.playerHealth = 5;
            state.maxPlayerHealth = 5;
            state.baseHealth = 15;
            state.maxBaseHealth = 15;
            break;
        case 1: // Normal
            state.difficultyMultiplier = 1.0f;
            state.playerHealth = 3;
            state.maxPlayerHealth = 3;
            state.baseHealth = 10;
            state.maxBaseHealth = 10;
            break;
        case 2: // Hard
            state.difficultyMultiplier = 1.5f;
            state.playerHealth = 3;
            state.maxPlayerHealth = 3;
            state.baseHealth = 8;
            state.maxBaseHealth = 8;
            break;
    }
}

     

    
    private void loadGameData() {
    com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("SpaceShooter");
    state.highScore = prefs.getInteger("highscore", 0);
    state.coins = prefs.getInteger("coins", 0);
    
    // Load owned skins (if you want to track in game)
    int ownedShipsFlag = prefs.getInteger("ownedShips", 1);
    // You can store this in GameState if needed
    
    state.currentShipSkin = prefs.getInteger("selectedShipSkin", 0);
    state.currentBulletColor = prefs.getInteger("selectedBulletColor", 0);
}
    
    private void createTextures() {
        createPlayerTexture();
        createBossTexture();
        createSharedTextures();
    }
    
    private void createPlayerTexture() {
        Pixmap pixmap = new Pixmap(40, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(state.shipColors[state.currentShipSkin]);
        pixmap.fillTriangle(20, 5, 5, 35, 35, 35);
        
        if (state.currentShipSkin == 3) {
            pixmap.setColor(Color.YELLOW);
            pixmap.fillTriangle(20, 8, 12, 30, 28, 30);
        } else if (state.currentShipSkin == 1) {
            pixmap.setColor(Color.WHITE);
            pixmap.fillTriangle(20, 12, 16, 28, 24, 28);
        } else if (state.currentShipSkin == 2) {
            pixmap.setColor(Color.PINK);
            pixmap.fillTriangle(20, 10, 14, 32, 26, 32);
        }
        
        state.playerTexture = new Texture(pixmap);
        pixmap.dispose();
    }
    
    private void createBossTexture() {
        Pixmap pixmap = new Pixmap(80, 80, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.PURPLE);
        pixmap.fillRectangle(20, 20, 40, 40);
        state.bossTexture = new Texture(pixmap);
        pixmap.dispose();
    }
    
    private void createSharedTextures() {
    Pixmap pixmap;
    
    // 1. Player Bullet (Small White Circle)
    pixmap = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.WHITE);
    pixmap.fillCircle(4, 4, 3);
    state.bulletTexture = new Texture(pixmap);
    pixmap.dispose();
    
    // 2. Enemy Bullet (Small Orange Circle)
    pixmap = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.ORANGE);
    pixmap.fillCircle(4, 4, 3);
    state.enemyBulletTexture = new Texture(pixmap);
    pixmap.dispose();
    
    // 3. Power-Up (White Orb)
    pixmap = new Pixmap(30, 30, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.WHITE);
    pixmap.fillCircle(15, 15, 12);
    state.powerUpTexture = new Texture(pixmap);
    pixmap.dispose();
    
    // 4. Particle (Square Spark)
    pixmap = new Pixmap(4, 4, Pixmap.Format.RGBA8888);
    pixmap.setColor(Color.WHITE);
    pixmap.fillRectangle(0, 0, 4, 4);
    state.particleTexture = new Texture(pixmap);
    pixmap.dispose();
    // --- NEW ENEMY DESIGNS (Flipped to face DOWN) ---

// Enemy 0: Normal Fighter (Red Triangle - Facing Player)
pixmap = new Pixmap(40, 40, Pixmap.Format.RGBA8888);
pixmap.setColor(Color.RED);
// Tip at bottom center (20, 35), base at top (5, 5) and (35, 5)
pixmap.fillTriangle(20, 35, 5, 5, 35, 5); 
pixmap.setColor(Color.ORANGE);
// Engine glow at the top base
pixmap.fillTriangle(20, 10, 12, 2, 28, 2); 
state.enemyTextures[0] = new Texture(pixmap);
pixmap.dispose();

// Enemy 1: Fast Interceptor (Orange Diamond)
// Diamonds look the same up or down, but let's move the cockpit to the "front" (bottom)
pixmap = new Pixmap(35, 35, Pixmap.Format.RGBA8888);
pixmap.setColor(Color.ORANGE);
int[] xPoints = {17, 30, 17, 4};
int[] yPoints = {4, 17, 30, 17};
for (int i = 0; i < 4; i++) {
    int next = (i + 1) % 4;
    pixmap.fillTriangle(xPoints[i], yPoints[i], xPoints[next], yPoints[next], 17, 17);
}
pixmap.setColor(Color.YELLOW);
pixmap.fillCircle(17, 24, 3); // Cockpit moved slightly toward the bottom tip
state.enemyTextures[1] = new Texture(pixmap);
pixmap.dispose();

// Enemy 2: Tank Battleship (Purple Hexagon)
pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
pixmap.setColor(Color.PURPLE);
// Hexagon points adjusted to look more "aggressive" downward
int[] hexX = {25, 45, 40, 25, 10, 5};
int[] hexY = {45, 30, 10, 5, 10, 30}; 
for (int i = 0; i < 6; i++) {
    int next = (i + 1) % 6;
    pixmap.fillTriangle(hexX[i], hexY[i], hexX[next], hexY[next], 25, 25);
}
pixmap.setColor(Color.MAGENTA);
pixmap.fillCircle(25, 20, 8); // Core moved slightly up (away from the front)
state.enemyTextures[2] = new Texture(pixmap);
pixmap.dispose();

}

    
    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touchPos.set(screenX, screenY, 0);
                camera.unproject(touchPos);
                
                float worldX = touchPos.x;
                float worldY = touchPos.y;
                
                if (worldX > GameState.WORLD_WIDTH - 60 && worldY > GameState.WORLD_HEIGHT - 60) {
                    state.isPaused = !state.isPaused;
                    return true;
                }
                
                if (state.isPaused) return true;
                
                touchActive = true;
                touchX = worldX;
                touchY = worldY;
                
                if (!state.isPlaying) {
                    restartGame();
                }
                
                return true;
            }
            
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (state.isPlaying && !state.isPaused) {
                    touchPos.set(screenX, screenY, 0);
                    camera.unproject(touchPos);
                    touchX = touchPos.x;
                    touchY = touchPos.y;
                    touchActive = true;
                }
                return true;
            }
            
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                touchActive = false;
                return true;
            }
        });
    }
    
    private void restartGame() {
        state.playerHealth = 3;
        state.score = 0;
        state.bossActive = false;
        state.nextBossScore = 500;
        state.isPlaying = true;
        state.isPaused = false;
        state.invincibilityTimer = 0;
        state.hasFireRateBuff = false;
        
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("SpaceShooter");
        state.currentShipSkin = prefs.getInteger("selectedShipSkin", 0);
        state.currentBulletColor = prefs.getInteger("selectedBulletColor", 0);
        createPlayerTexture();
        
        state.enemies.clear();
        state.bullets.clear();
        state.enemyBullets.clear();
        state.particles.clear();
        state.powerUps.clear();
        
        state.playerX = GameState.WORLD_WIDTH / 2;
        state.playerY = 100;
        touchX = state.playerX;
        touchY = state.playerY;
    }
   @Override
public void render() {
    float delta = Gdx.graphics.getDeltaTime();  // delta defined HERE
    
    // Wait for difficulty selection
    if (!gameStarted) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, "SELECT DIFFICULTY...", GameState.WORLD_WIDTH/2 - 100, GameState.WORLD_HEIGHT/2);
        font.draw(batch, "Choose in the dialog above", GameState.WORLD_WIDTH/2 - 110, GameState.WORLD_HEIGHT/2 - 50);
        batch.end();
        return;  // Exit early, don't run game logic
    }
    
    // Normal game logic - delta is still accessible here
    if (state.isPlaying && !state.isPaused) {
        if (touchActive) {
            state.playerX = touchX;
            state.playerY = touchY;
        }
        updater.update(delta);
        collisions.checkAllCollisions();
    }
    
    // Clear screen
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
    // Draw
    batch.setProjectionMatrix(camera.combined);
    batch.begin();
    renderer.draw(state, delta);
    batch.end();
}
    
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, GameState.WORLD_WIDTH, GameState.WORLD_HEIGHT);
    }
    
    @Override
    public void pause() {}
    
    @Override
    public void resume() {}
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        titleFont.dispose();
        if (state.playerTexture != null) state.playerTexture.dispose();
        if (state.bossTexture != null) state.bossTexture.dispose();
        if (state.bulletTexture != null) state.bulletTexture.dispose();
        if (state.enemyBulletTexture != null) state.enemyBulletTexture.dispose();
        if (state.powerUpTexture != null) state.powerUpTexture.dispose();
        if (state.particleTexture != null) state.particleTexture.dispose();
        for (Texture tex : state.enemyTextures) {
            if (tex != null) tex.dispose();
        }
        if (shootSound != null) shootSound.dispose();
        if (hitSound != null) hitSound.dispose();
        if (explosionSound != null) explosionSound.dispose();
        if (powerUpSound != null) powerUpSound.dispose();
        if (gameOverSound != null) gameOverSound.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
    }
}