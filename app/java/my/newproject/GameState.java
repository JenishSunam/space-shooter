package my.newproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import static my.newproject.GameEntities.*;

public class GameState {
    // --- CONSTANTS ---
    public static final float WORLD_WIDTH = 720;
    public static final float WORLD_HEIGHT = 1280;
    
    // --- PLAYER DATA ---
    public float playerX, playerY;
    public int playerHealth = 3;
    public int maxPlayerHealth = 3;
    public float invincibilityTimer = 0;
    public int currentShipSkin = 0;
    public int currentBulletColor = 0;
    public final Color[] shipColors = {Color.WHITE, Color.CYAN, Color.MAGENTA, Color.GOLD};
    public final Color[] bulletColors = {Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.GREEN};
    
    // --- BASE DATA ---
    public float baseX, baseY;
    public int baseHealth = 10;
    public int maxBaseHealth = 10;
    public boolean baseActive = true;
    public float baseRegenTimer = 0;

    // --- GAME STATE ---
    public int score = 0;
    public int highScore = 0;
    public int coins = 0;
    public float scoreMultiplier = 1.0f;
    public boolean isPlaying = true;
    public boolean isPaused = false;
    public int difficulty = 1; // 0=Easy, 1=Normal, 2=Hard
    public float difficultyMultiplier = 1.0f;
    
    // --- STATISTICS & ACHIEVEMENTS ---
    public boolean[] achievements = new boolean[10];
    public int totalEnemiesKilled = 0;
    public int totalBossesKilled = 0;
    public int perfectRuns = 0;
    public float maxBaseHealthPercent = 0;

    // --- BOSS DATA ---
    public boolean bossActive = false;
    public float bossX, bossY;
    public int bossHealth;
    public int maxBossHealth = 50;
    public int bossType = 0; // 0=Normal, 1=Fast, 2=Tank, 3=Spread, 4=Shield
    public float bossShield = 0;
    public float bossShootTimer = 0;
    public float bossShootDelay = 0.7f;
    public int nextBossScore = 500;
    public float bossDeathTimer = 0;
    public boolean bossExploding = false;

    // --- POWER-UPS & BUFFS ---
    public boolean hasFireRateBuff = false;
    public float fireRateBuffTimer = 0;
    public boolean hasShield = false;
    public float shieldTimer = 0;
    public boolean hasCoinMagnet = false;
    public float coinMagnetTimer = 0;
    public boolean hasDamageBoost = false;
    public float damageBoostTimer = 0;
    public float damageMultiplier = 1.0f;

    // --- TEXTURES ---
    public Texture playerTexture;
    public Texture bossTexture;
    public Texture bulletTexture;
    public Texture enemyBulletTexture;
    public Texture powerUpTexture;
    public Texture particleTexture;
    public Texture[] enemyTextures = new Texture[3];

    // --- COLLECTIONS ---
    public Array<Bullet> bullets;
    public Array<Enemy> enemies;
    public Array<EnemyBullet> enemyBullets;
    public Array<Particle> particles;
    public Array<PowerUp> powerUps;

    // --- CONSTRUCTOR ---
    public GameState() {
        // Initialize Collections
        bullets = new Array<>();
        enemies = new Array<>();
        enemyBullets = new Array<>();
        particles = new Array<>();
        powerUps = new Array<>();

        // Initial Positions
        playerX = WORLD_WIDTH / 2;
        playerY = 200;
        baseX = WORLD_WIDTH / 2;
        baseY = 55;
    }
}