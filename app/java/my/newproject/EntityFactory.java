package my.newproject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import static my.newproject.GameEntities.*;
import com.badlogic.gdx.Gdx;

public class EntityFactory {
    private GameState state;
    
    public EntityFactory(GameState state) {
        this.state = state;
    }

    // --- ENEMY SYSTEMS ---
    
    public void spawnEnemy() {
        float x = MathUtils.random(50, GameState.WORLD_WIDTH - 50);
        float y = GameState.WORLD_HEIGHT - 50;
        int type = determineEnemyType();
        state.enemies.add(new Enemy(x, y, type, state.enemyTextures));
    }
    
    private int determineEnemyType() {
        if (state.score > 1000) {
            return MathUtils.random(0, 2);
        } else if (state.score > 500) {
            return MathUtils.random(0, 1);
        }
        return 0;
    }

    public void spawnEnemyBullet(float x, float y) {
        EnemyBullet eb = new EnemyBullet(x, y - 20);
        eb.vy = 350 * state.difficultyMultiplier;
        state.enemyBullets.add(eb);
    }

    // --- BOSS SYSTEMS ---

    public void spawnBoss() {
    state.bossActive = true;
    state.bossX = GameState.WORLD_WIDTH / 2;
    state.bossY = GameState.WORLD_HEIGHT - 120;
    
    // Boss type based on score - MORE VARIETY
    if (state.score > 3000) {
        state.bossType = MathUtils.random(0, 4);  // All 5 types
    } else if (state.score > 1500) {
        state.bossType = MathUtils.random(0, 3);  // Types 0-3
    } else if (state.score > 500) {
        state.bossType = MathUtils.random(0, 2);  // Types 0-2
    } else {
        state.bossType = 0;  // First boss always Normal
    }
    
    // Scale Boss HP
    int hpBase = 50 + (state.score / 50);
    state.maxBossHealth = hpBase;
    state.bossHealth = hpBase;
    
    // Special boss buffs
    switch(state.bossType) {
        case 1: // Fast - less health, faster
            state.maxBossHealth = (int)(hpBase * 0.7f);
            break;
        case 2: // Tank - double health
            state.maxBossHealth = hpBase * 2;
            break;
        case 4: // Shield - has shield
            state.bossShield = 30;
            break;
    }
    
    state.bossHealth = state.maxBossHealth;
    state.enemies.clear();
    
    // Debug log
    Gdx.app.log("BOSS", "Spawned type: " + state.bossType + " with HP: " + state.maxBossHealth);
}

    public void spawnBossBarrage(float x, float y, int type) {
        switch(type) {
            case 0: // Normal: 3-way shot
                createBossBullet(x, y, -100);
                createBossBullet(x, y, 0);
                createBossBullet(x, y, 100);
                break;
            case 1: // Fast: Rapid straight shots
                createBossBullet(x, y, 0);
                break;
            case 2: // Tank: Slow heavy shot
                EnemyBullet eb = new EnemyBullet(x, y);
                eb.vy = 200 * state.difficultyMultiplier;
                state.enemyBullets.add(eb);
                break;
            case 3: // Spread: Chaos shot
                for(int i = 0; i < 5; i++) {
                    createBossBullet(x, y, MathUtils.random(-200, 200));
                }
                break;
            case 4: // Shield: Normal pattern
                createBossBullet(x, y, -80);
                createBossBullet(x, y, 80);
                break;
            default: // Default: Targeted shot
                createBossBullet(x, y, (state.playerX - x) * 0.5f);
                break;
        }
    }

    private void createBossBullet(float x, float y, float vx) {
        EnemyBullet eb = new EnemyBullet(x, y);
        eb.vx = vx;
        eb.vy = 400 * state.difficultyMultiplier;
        state.enemyBullets.add(eb);
    }

    // --- PLAYER SYSTEMS ---

    public void shoot() {
        Color bulletColor = state.bulletColors[state.currentBulletColor];
        state.bullets.add(new Bullet(state.playerX, state.playerY + 20, bulletColor));
    }

    // --- PICKUPS ---

    public void spawnPowerUp() {
        float x = MathUtils.random(50, GameState.WORLD_WIDTH - 50);
        float y = GameState.WORLD_HEIGHT - 50;
        
        int type = MathUtils.random(0, 4);
        Color col = Color.WHITE;
        
        if (type == 0) col = Color.GREEN;   // Repair
        if (type == 1) col = Color.CYAN;    // Rapid Fire
        if (type == 2) col = Color.GOLD;    // Points / Coin Magnet
        if (type == 3) col = Color.BLUE;    // Shield
        if (type == 4) col = Color.RED;     // Damage Boost
        
        state.powerUps.add(new PowerUp(x, y, type, col));
    }
}