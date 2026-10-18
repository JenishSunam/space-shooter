package my.newproject;

import com.badlogic.gdx.math.MathUtils;
import static my.newproject.GameEntities.*;

public class CollisionDetector {
    private GameState state;
    private GameLogic logic;
    private SpaceShooterGame game;
    
    public CollisionDetector(GameState state, GameLogic logic, SpaceShooterGame game) {
        this.state = state;
        this.logic = logic;
        this.game = game;
    }
    
    public void checkAllCollisions() {
        checkBulletEnemyCollisions();
        checkPlayerEnemyBulletCollisions();
        checkPlayerEnemyCollisions();
        checkPlayerPowerUpCollisions();
        checkEnemyBaseCollisions();
    }
    
    // --- ENEMY BASE COLLISIONS ---
    
    private void checkEnemyBaseCollisions() {
        if (!state.baseActive) return;
        
        for (int i = state.enemies.size - 1; i >= 0; i--) {
            Enemy e = state.enemies.get(i);
            
            if (e.y <= 40) { 
                int damage = 1;
                switch(e.type) {
                    case 0: damage = 1; break;
                    case 1: damage = 2; break;
                    case 2: damage = 3; break;
                }
                
                state.baseHealth -= damage;
                state.enemies.removeIndex(i);
                
                if (game != null) game.playExplosionSound();
                logic.addHitEffect(state.baseX, state.baseY);
                
                if (state.baseHealth <= 0) {
                    state.baseActive = false;
                    state.isPlaying = false;
                    if (game != null) game.playGameOverSound();
                    break;
                }
            }
        }
    }
    
    // --- BULLET VS ENEMY ---
    
    private void checkBulletEnemyCollisions() {
        for (int i = state.bullets.size - 1; i >= 0; i--) {
            Bullet b = state.bullets.get(i);
            boolean hit = false;
            
            for (int j = state.enemies.size - 1; j >= 0; j--) {
                Enemy e = state.enemies.get(j);
                int radius = (e.type == 2) ? 35 : 25;
                
                if (Math.abs(b.x - e.x) < radius && Math.abs(b.y - e.y) < radius) {
                    e.health--;
                    logic.addHitEffect(e.x, e.y);
                    state.bullets.removeIndex(i);
                    
                    if (e.health <= 0) {
                        if (game != null) game.playExplosionSound();
                        logic.addExplosion(e.x, e.y);
                        
                        int points = (e.type == 2) ? 30 : (e.type == 1) ? 15 : 10;
                        logic.addScore(points);
                        state.totalEnemiesKilled++;
                        
                        // Coin magnet effect
                        if (state.hasCoinMagnet) {
                            logic.addCoins(points / 5);
                        } else {
                            logic.addCoins(points / 10);
                        }
                        
                        state.enemies.removeIndex(j);
                    }
                    hit = true;
                    break;
                }
            }
            
            if (!hit && state.bossActive) {
                checkBulletBossCollision(b, i);
            }
        }
    }
    
    // --- BULLET VS BOSS ---
    
    private void checkBulletBossCollision(Bullet b, int index) {
        if (Math.abs(b.x - state.bossX) < 50 && Math.abs(b.y - state.bossY) < 50) {
            // Check shield first
            if (state.bossType == 4 && state.bossShield > 0) {
                state.bossShield--;
                state.bullets.removeIndex(index);
                logic.addHitEffect(state.bossX, state.bossY);
                return;
            }
            
            state.bossHealth--;
            state.bullets.removeIndex(index);
            logic.addHitEffect(state.bossX, state.bossY);
            
            if (state.bossHealth <= 0) {
                if (game != null) game.playBossDeathSound();
                logic.addBossExplosion(state.bossX, state.bossY);
                state.totalBossesKilled++;
                logic.bossDefeated();
            }
        }
    }
    
    // --- PLAYER VS ENEMY BULLETS ---
    
    private void checkPlayerEnemyBulletCollisions() {
        for (int i = state.enemyBullets.size - 1; i >= 0; i--) {
            EnemyBullet eb = state.enemyBullets.get(i);
            if (Math.abs(state.playerX - eb.x) < 25 && Math.abs(state.playerY - eb.y) < 25) {
                if (state.invincibilityTimer <= 0) {
                    logic.playerDamaged();
                }
                state.enemyBullets.removeIndex(i);
            }
        }
    }
    
    // --- PLAYER VS ENEMIES ---
    
    private void checkPlayerEnemyCollisions() {
        for (int i = state.enemies.size - 1; i >= 0; i--) {
            Enemy e = state.enemies.get(i);
            if (Math.abs(state.playerX - e.x) < 35 && Math.abs(state.playerY - e.y) < 35) {
                if (state.invincibilityTimer <= 0) {
                    logic.playerDamaged();
                }
                state.enemies.removeIndex(i);
            }
        }
        
        if (state.bossActive && Math.abs(state.playerX - state.bossX) < 60 && Math.abs(state.playerY - state.bossY) < 60) {
            if (state.invincibilityTimer <= 0) {
                logic.playerDamaged();
            }
        }
    }
    
    // --- PLAYER VS POWER-UPS ---
    
    private void checkPlayerPowerUpCollisions() {
        for (int i = state.powerUps.size - 1; i >= 0; i--) {
            PowerUp p = state.powerUps.get(i);
            if (Math.abs(state.playerX - p.x) < 35 && Math.abs(state.playerY - p.y) < 35) {
                if (game != null) game.playPowerUpSound();
                logic.collectPowerUp(p);
                state.powerUps.removeIndex(i);
            }
        }
    }
}