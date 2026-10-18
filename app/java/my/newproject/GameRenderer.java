package my.newproject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import static my.newproject.GameEntities.*;
import my.newproject.GameState;
import static my.newproject.GameEntities.*;

public class GameRenderer {
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private GameState state;  // <-- ADD THIS LINE
    
    // Stars background
    private float[] starX = new float[150];
    private float[] starY = new float[150];
    private float[] starSpeed = new float[150];
    private boolean starsInitialized = false;
    
    // Achievement popup
private float achievementPopupTimer = 0;
private String achievementPopupText = "";
private String achievementPopupDesc = "";
    
    // Add these variables at top
private float achievementTimer = 0;
private String achievementText = "";
private String achievementDesc = "";

// In draw() method, add:

    
    public GameRenderer(SpriteBatch batch, BitmapFont font, BitmapFont titleFont) {
        this.batch = batch;
        this.font = font;
        this.titleFont = titleFont;
    }
    
    private void initStars() {
        if (!starsInitialized) {
            for (int i = 0; i < 150; i++) {
                starX[i] = MathUtils.random(0, GameState.WORLD_WIDTH);
                starY[i] = MathUtils.random(0, GameState.WORLD_HEIGHT);
                starSpeed[i] = MathUtils.random(20, 100);
            }
            starsInitialized = true;
        }
    }
    public void addBossExplosion(GameState state, float x, float y) {
    // Create 100+ particles for epic explosion
    for (int i = 0; i < 120; i++) {
        float angle = MathUtils.random(0, MathUtils.PI2);
        float speed = MathUtils.random(150, 500);
        float vx = MathUtils.cos(angle) * speed;
        float vy = MathUtils.sin(angle) * speed;
        float life = MathUtils.random(0.8f, 1.5f);
        
        // Random colors (red, orange, yellow, white)
        float r = MathUtils.random(0.8f, 1f);
        float g = MathUtils.random(0.3f, 0.8f);
        float b = MathUtils.random(0f, 0.3f);
        
        state.particles.add(new Particle(x, y, vx, vy, life, r, g, b));
    }
    
    // Add shockwave ring effect
    for (int i = 0; i < 360; i += 15) {
        float angle = (float)Math.toRadians(i);
        float vx = MathUtils.cos(angle) * 200;
        float vy = MathUtils.sin(angle) * 200;
        state.particles.add(new Particle(x, y, vx, vy, 0.5f, 1f, 0.8f, 0.2f));
    }
}
    
    private void drawStars(float delta) {
        initStars();
        batch.setColor(Color.WHITE);
        for (int i = 0; i < 150; i++) {
            starY[i] -= starSpeed[i] * delta;
            if (starY[i] < 0) {
                starY[i] = GameState.WORLD_HEIGHT;
                starX[i] = MathUtils.random(0, GameState.WORLD_WIDTH);
                starSpeed[i] = MathUtils.random(20, 100);
            }
            float size = starSpeed[i] / 50f + 1f;
            batch.draw(state.particleTexture, starX[i], starY[i], size, size);
        }
        batch.setColor(Color.WHITE);
    }
    private void drawBoss(GameState state) {
    batch.draw(state.bossTexture, state.bossX - 40, state.bossY - 40);
    
    // Boss type name with color
    String bossName = "";
    Color nameColor = Color.WHITE;
    switch(state.bossType) {
        case 0: bossName = "NORMAL BOSS"; nameColor = Color.RED; break;
        case 1: bossName = "FAST BOSS"; nameColor = Color.ORANGE; break;
        case 2: bossName = "TANK BOSS"; nameColor = Color.PURPLE; break;
        case 3: bossName = "SPREAD BOSS"; nameColor = Color.YELLOW; break;
        case 4: bossName = "SHIELD BOSS"; nameColor = Color.CYAN; break;
    }
    
    batch.setColor(nameColor);
    font.draw(batch, bossName, state.bossX - 45, state.bossY + 70);
    batch.setColor(Color.WHITE);
    
    // Health bar
    float healthPercent = (float)state.bossHealth / state.maxBossHealth;
    batch.setColor(Color.RED);
    batch.draw(state.bulletTexture, state.bossX - 50, state.bossY + 50, 100, 12);
    batch.setColor(Color.GREEN);
    batch.draw(state.bulletTexture, state.bossX - 50, state.bossY + 50, 100 * healthPercent, 12);
    batch.setColor(Color.WHITE);
    
    // Shield bar for shield boss
    if (state.bossType == 4 && state.bossShield > 0) {
        float shieldPercent = state.bossShield / 30f;
        batch.setColor(Color.CYAN);
        batch.draw(state.bulletTexture, state.bossX - 50, state.bossY + 65, 100 * shieldPercent, 6);
        batch.setColor(Color.WHITE);
        font.draw(batch, "SHIELD: " + (int)state.bossShield, state.bossX - 25, state.bossY + 62);
    }
    
    font.draw(batch, "HP: " + state.bossHealth, state.bossX - 25, state.bossY + 45);
}
    private void drawBase(GameState state) {
    if (!state.baseActive) return;
    
    // Draw base (shield generator)
    batch.setColor(Color.CYAN);
    batch.draw(state.bulletTexture, state.baseX - 30, state.baseY - 15, 60, 30);
    batch.setColor(Color.WHITE);
    
    // Base health bar
    float healthPercent = (float)state.baseHealth / state.maxBaseHealth;
    
    // Color based on health
    if (healthPercent > 0.6f) {
        batch.setColor(Color.GREEN);
    } else if (healthPercent > 0.3f) {
        batch.setColor(Color.ORANGE);
    } else {
        batch.setColor(Color.RED);
    }
    
    batch.draw(state.bulletTexture, state.baseX - 35, state.baseY - 25, 70 * healthPercent, 8);
    batch.setColor(Color.WHITE);
    
    // Outline
    batch.setColor(Color.WHITE);
    batch.draw(state.bulletTexture, state.baseX - 35, state.baseY - 25, 70, 8);
    batch.setColor(Color.WHITE);
    
    font.draw(batch, "BASE", state.baseX - 15, state.baseY - 10);
    font.draw(batch, state.baseHealth + "/" + state.maxBaseHealth, state.baseX - 20, state.baseY - 20);
}

    public void showAchievement(String title, String desc) {
    achievementPopupTimer = 3.0f;
    achievementPopupText = title;
    achievementPopupDesc = desc;
}

    
    
    public void draw(GameState state, float delta) {
        this.state = state;  // <-- Store the reference
        
        // Draw stars background
        drawStars(delta);
        
        // Draw boss
        if (state.bossActive) {
            batch.draw(state.bossTexture, state.bossX - 40, state.bossY - 40);
            float healthPercent = (float)state.bossHealth / state.maxBossHealth;
            batch.setColor(Color.RED);
            batch.draw(state.bulletTexture, state.bossX - 50, state.bossY + 50, 100, 12);
            batch.setColor(Color.GREEN);
            batch.draw(state.bulletTexture, state.bossX - 50, state.bossY + 50, 100 * healthPercent, 12);
            batch.setColor(Color.WHITE);
            font.draw(batch, "BOSS", state.bossX - 20, state.bossY + 45);
        }
        // achievements 
        if (achievementTimer > 0) {
    achievementTimer -= delta;
    float alpha = Math.min(1.0f, achievementTimer);
    batch.setColor(1, 1, 0.5f, alpha);
    titleFont.draw(batch, achievementText, GameState.WORLD_WIDTH/2 - 100, GameState.WORLD_HEIGHT - 100);
    font.draw(batch, achievementDesc, GameState.WORLD_WIDTH/2 - 80, GameState.WORLD_HEIGHT - 130);
    batch.setColor(Color.WHITE);
}
        
        // Draw player
        if (state.invincibilityTimer <= 0 || (int)(System.currentTimeMillis() / 100) % 2 == 0) {
            batch.draw(state.playerTexture, state.playerX - 20, state.playerY - 20);
        }
        
        // Draw bullets
        for (Bullet b : state.bullets) {
            batch.setColor(b.color);
            batch.draw(state.bulletTexture, b.x - 4, b.y - 4);
        }
        batch.setColor(Color.WHITE);
        
        // Draw enemies
        for (Enemy e : state.enemies) {
    if (e.texture != null) {
        // Draw centered: (texture, x - halfWidth, y - halfHeight, width, height)
        batch.draw(e.texture, e.x - 20, e.y - 20, 40, 40);
    }
}


        
         drawBase(state); 
        
        // Draw enemy bullets
        for (EnemyBullet eb : state.enemyBullets) {
            batch.draw(state.enemyBulletTexture, eb.x - 4, eb.y - 4);
        }
        
        // Draw power-ups
        for (PowerUp p : state.powerUps) {
            batch.setColor(p.color);
            batch.draw(state.powerUpTexture, p.x - 15, p.y - 15);
        }
        batch.setColor(Color.WHITE);
        
        // Draw particles
        for (Particle p : state.particles) {
            batch.setColor(p.r, p.g, p.b, p.life);
            batch.draw(state.particleTexture, p.x - 2, p.y - 2);
        }
        batch.setColor(Color.WHITE);
        
        // UI
        font.draw(batch, "Score: " + state.score, 20, GameState.WORLD_HEIGHT - 20);
        font.draw(batch, "High: " + state.highScore, 20, GameState.WORLD_HEIGHT - 45);
        font.draw(batch, "Coins: " + state.coins, 20, GameState.WORLD_HEIGHT - 70);
        font.draw(batch, "x" + (int)state.scoreMultiplier, GameState.WORLD_WIDTH - 60, GameState.WORLD_HEIGHT - 20);
        
        for (int i = 0; i < state.playerHealth; i++) {
            batch.draw(state.playerTexture, 90 + (i * 30), GameState.WORLD_HEIGHT - 95, 20, 20);
        }
        
        if (state.hasFireRateBuff) {
            font.draw(batch, "RAPID FIRE!", GameState.WORLD_WIDTH - 150, GameState.WORLD_HEIGHT - 50);
        }
        
        font.draw(batch, state.isPaused ? "▶" : "[]", GameState.WORLD_WIDTH - 40, GameState.WORLD_HEIGHT - 25);
        
        if (state.isPaused) {
            titleFont.draw(batch, "PAUSED", GameState.WORLD_WIDTH/2 - 60, GameState.WORLD_HEIGHT/2);
            font.draw(batch, "Tap [] to resume", GameState.WORLD_WIDTH/2 - 70, GameState.WORLD_HEIGHT/2 - 50);
        }
        
        if (!state.isPlaying) {
            titleFont.draw(batch, "GAME OVER", GameState.WORLD_WIDTH/2 - 80, GameState.WORLD_HEIGHT/2 + 50);
            font.draw(batch, "Score: " + state.score, GameState.WORLD_WIDTH/2 - 40, GameState.WORLD_HEIGHT/2);
            font.draw(batch, "Tap to restart", GameState.WORLD_WIDTH/2 - 60, GameState.WORLD_HEIGHT/2 - 50);
        }
    }
    private void drawBossDeathEffect(GameState state, float delta) {
    if (!state.bossExploding) return;
    
    // Draw expanding ring
    float ringSize = (0.5f - state.bossDeathTimer) * 400;
    batch.setColor(1, 0.5f, 0, 1 - state.bossDeathTimer * 2);
    batch.draw(state.bulletTexture, state.bossX - ringSize/2, state.bossY - ringSize/2, ringSize, ringSize);
    batch.setColor(Color.WHITE);
}
}