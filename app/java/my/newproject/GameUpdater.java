package my.newproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import static my.newproject.GameEntities.*;

public class GameUpdater {
    private GameState state;
    private EntityFactory factory;
    
    // Timers
    private float spawnTimer = 0;
    private float powerUpTimer = 0;
    private float autoShootTimer = 0;
    
    public GameUpdater(GameState state, EntityFactory factory) {
        this.state = state;
        this.factory = factory;
    }
    
    public void update(float delta) {
        updatePlayer(delta);
        updateBuffs(delta);
        updateAutoShoot(delta);
        updateInvincibility(delta);
        
        updateBullets(delta);
        updateEnemyBullets(delta);
        updateEnemies(delta);
        updateBoss(delta);
        updatePowerUps(delta);
        updateParticles(delta);
        
        updateSpawning(delta);
        updatePowerUpSpawning(delta);
        updateBossSpawning();
    }
    
    // --- PLAYER ---
    
    private void updatePlayer(float delta) {
        // Player movement happens in SpaceShooterGame
        // Just clamp position
        state.playerX = MathUtils.clamp(state.playerX, 40, GameState.WORLD_WIDTH - 40);
        state.playerY = MathUtils.clamp(state.playerY, 80, GameState.WORLD_HEIGHT - 200);
    }
    
    // --- BUFFS ---
    
    private void updateBuffs(float delta) {
        if (state.hasFireRateBuff) {
            state.fireRateBuffTimer -= delta;
            if (state.fireRateBuffTimer <= 0) {
                state.hasFireRateBuff = false;
            }
        }
        
        if (state.hasShield) {
            state.shieldTimer -= delta;
            if (state.shieldTimer <= 0) {
                state.hasShield = false;
            }
        }
        
        if (state.hasCoinMagnet) {
            state.coinMagnetTimer -= delta;
            if (state.coinMagnetTimer <= 0) {
                state.hasCoinMagnet = false;
            }
        }
        
        if (state.hasDamageBoost) {
            state.damageBoostTimer -= delta;
            if (state.damageBoostTimer <= 0) {
                state.hasDamageBoost = false;
                state.damageMultiplier = 1.0f;
            }
        }
    }
    
    // --- AUTO SHOOT ---
    
    private void updateAutoShoot(float delta) {
        float shootDelay = state.hasFireRateBuff ? 0.08f : 0.15f;
        autoShootTimer += delta;
        if (autoShootTimer >= shootDelay) {
            autoShootTimer = 0;
            factory.shoot();
        }
    }
    
    // --- INVINCIBILITY ---
    
    private void updateInvincibility(float delta) {
        if (state.invincibilityTimer > 0) {
            state.invincibilityTimer -= delta;
        }
    }
    
    // --- BULLETS ---
    
    private void updateBullets(float delta) {
        for (int i = state.bullets.size - 1; i >= 0; i--) {
            Bullet b = state.bullets.get(i);
            b.y += 600 * delta;
            if (b.y > GameState.WORLD_HEIGHT + 50) {
                state.bullets.removeIndex(i);
            }
        }
    }
    
    // --- ENEMY BULLETS ---
    
    private void updateEnemyBullets(float delta) {
        for (int i = state.enemyBullets.size - 1; i >= 0; i--) {
            EnemyBullet eb = state.enemyBullets.get(i);
            eb.x += eb.vx * delta;
            eb.y -= eb.vy * delta;
            if (eb.y < -50 || eb.x < -50 || eb.x > GameState.WORLD_WIDTH + 50) {
                state.enemyBullets.removeIndex(i);
            }
        }
    }
    
    // --- ENEMIES ---
    
    private void updateEnemies(float delta) {
        for (int i = state.enemies.size - 1; i >= 0; i--) {
            Enemy e = state.enemies.get(i);
            
            // Enemy speed scales with score and difficulty
            float speed = 180 + (state.score / 500f);
            speed = Math.min(speed, 400f);
            speed *= state.difficultyMultiplier;
            
            // Type modifiers
            if (e.type == 1) speed *= 1.5f;  // Fast
            if (e.type == 2) speed *= 0.7f;  // Tank
            
            e.y -= speed * delta;
            
            // Enemy shooting
            e.shootTimer += delta;
            float enemyShootDelay = (e.type == 2) ? 1.2f : 0.8f;
            if (e.shootTimer >= enemyShootDelay) {
                e.shootTimer = 0;
                factory.spawnEnemyBullet(e.x, e.y);
            }
            
            // Remove if off screen
            if (e.y < -50) {
                state.enemies.removeIndex(i);
            }
        }
    }
    
    // --- BOSS ---
    
    private void updateBoss(float delta) {
        if (!state.bossActive) return;
        
        // Boss movement based on type
        switch(state.bossType) {
            case 0: // Normal
                state.bossX += MathUtils.sin(Gdx.graphics.getFrameId() * 0.03f) * 150 * delta;
                break;
            case 1: // Fast
                state.bossX += MathUtils.sin(Gdx.graphics.getFrameId() * 0.06f) * 200 * delta;
                break;
            case 2: // Tank
                state.bossX += MathUtils.sin(Gdx.graphics.getFrameId() * 0.02f) * 80 * delta;
                break;
            case 3: // Spread
                state.bossX += MathUtils.sin(Gdx.graphics.getFrameId() * 0.04f) * 180 * delta;
                break;
            case 4: // Shield
                state.bossX += MathUtils.sin(Gdx.graphics.getFrameId() * 0.03f) * 120 * delta;
                break;
        }
        
        state.bossX = MathUtils.clamp(state.bossX, 60, GameState.WORLD_WIDTH - 60);
        
        // Boss shooting
        state.bossShootTimer += delta;
        float bossShootDelay = Math.max(0.4f, 0.7f - (state.score / 2000f));
        
        if (state.bossShootTimer >= bossShootDelay) {
            state.bossShootTimer = 0;
            factory.spawnBossBarrage(state.bossX, state.bossY - 40, state.bossType);
        }
    }
    
    // --- POWER-UPS ---
    
    private void updatePowerUps(float delta) {
        for (int i = state.powerUps.size - 1; i >= 0; i--) {
            PowerUp p = state.powerUps.get(i);
            p.y -= 150 * delta;
            if (p.y < -50) {
                state.powerUps.removeIndex(i);
            }
        }
    }
    
    // --- PARTICLES ---
    
    private void updateParticles(float delta) {
        for (int i = state.particles.size - 1; i >= 0; i--) {
            Particle p = state.particles.get(i);
            p.x += p.vx * delta;
            p.y += p.vy * delta;
            p.life -= delta;
            if (p.life <= 0) {
                state.particles.removeIndex(i);
            }
        }
    }
    
    // --- SPAWNING ---
    
    private void updateSpawning(float delta) {
        if (state.bossActive) return;
        
        spawnTimer += delta;
        float spawnDelay = MathUtils.clamp(1.0f - (state.score / 2000f), 0.4f, 1.0f);
        spawnDelay /= state.difficultyMultiplier;
        
        if (spawnTimer > spawnDelay) {
            spawnTimer = 0;
            factory.spawnEnemy();
        }
    }
    
    private void updatePowerUpSpawning(float delta) {
        powerUpTimer += delta;
        if (powerUpTimer > 8.0f) {
            powerUpTimer = 0;
            factory.spawnPowerUp();
        }
    }
    
    private void updateBossSpawning() {
        if (!state.bossActive && state.score >= state.nextBossScore && state.score > 0) {
            factory.spawnBoss();
            state.nextBossScore = state.score + 500;
        }
    }
}