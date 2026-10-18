package my.newproject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Gdx;
import static my.newproject.GameEntities.*;

public class GameLogic {
    private GameState state;
    private SpaceShooterGame game;
    
    public GameLogic(GameState state, SpaceShooterGame game) {
        this.state = state;
        this.game = game;
    }
    
    public void addScore(int points) {
        int gained = (int)(points * state.scoreMultiplier);
        state.score += gained;
        
        if (state.score > state.highScore) {
            state.highScore = state.score;
            Preferences prefs = Gdx.app.getPreferences("SpaceShooter");
            prefs.putInteger("highscore", state.highScore);
            prefs.flush();
        }
    }
    
    public void addCoins(int amount) {
        state.coins += amount;
        Preferences prefs = Gdx.app.getPreferences("SpaceShooter");
        prefs.putInteger("coins", state.coins);
        prefs.flush();
    }
    
    public void playerDamaged() {
    if (state.invincibilityTimer > 0) return;  // Extra safety
    
    if (game != null) game.playHitSound();
    
    state.playerHealth--;
    state.invincibilityTimer = 1.5f;  // 1.5 seconds of invincibility
    addHitEffect(state.playerX, state.playerY);
    
    if (state.playerHealth <= 0) {
        if (game != null) game.playGameOverSound();
        state.isPlaying = false;
    }
}
    
    public void collectPowerUp(PowerUp p) {
    switch(p.type) {
        case 0: // Extra Life
            if (state.playerHealth < 5) {
                state.playerHealth++;
            }
            break;
        case 1: // Rapid Fire
            state.hasFireRateBuff = true;
            state.fireRateBuffTimer = 5.0f;
            break;
        case 2: // Shield
            state.hasShield = true;
            state.shieldTimer = 8.0f;
            break;
        case 3: // Coin Magnet
            state.hasCoinMagnet = true;
            state.coinMagnetTimer = 10.0f;
            break;
        case 4: // Damage Boost
            state.hasDamageBoost = true;
            state.damageBoostTimer = 6.0f;
            state.damageMultiplier = 2.0f;
            break;
    }
    if (game != null) game.playPowerUpSound();
}
    
    public void bossDefeated() {
        if (game != null) game.playBossDeathSound();
        state.totalBossesKilled++;  // ADD THIS
        state.bossActive = false;
        addScore(200);
        addCoins(50);
        state.nextBossScore = state.score + 500;
        addExplosion(state.bossX, state.bossY);
    }
    
    public void addHitEffect(float x, float y) {
        for (int p = 0; p < 8; p++) {
            state.particles.add(new Particle(x, y,
                MathUtils.random(-150, 150), MathUtils.random(-150, 150),
                0.4f, 1f, 0.8f, 0f));
        }
    }
    
    public void addExplosion(float x, float y) {
        if (game != null) game.playExplosionSound();
        for (int p = 0; p < 30; p++) {
            state.particles.add(new Particle(x, y,
                MathUtils.random(-200, 200), MathUtils.random(-200, 200),
                0.6f, 1f, 0.5f, 0f));
        }
    }
    public void addBossExplosion(float x, float y) {
    // Create 120+ particles for epic explosion
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
}
