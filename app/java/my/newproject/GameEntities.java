package my.newproject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;


public class GameEntities {
    
    // --- PLAYER BULLETS ---
    public static class Bullet {
        public float x, y;
        public Color color;
        public Bullet(float x, float y, Color color) {
            this.x = x; this.y = y; this.color = color;
        }
    }
    
    // --- ENEMIES ---
    public static class Enemy {
    public float x, y, shootTimer;
    public int type;
    public int health;       
    public boolean active = true;
    
    // CHANGE THIS LINE to Texture (Remove TextureRegion)
    public com.badlogic.gdx.graphics.Texture texture; 

    public Enemy(float x, float y, int type, Texture[] textures) {
    this.x = x;
    this.y = y;
    this.type = type;
    this.texture = textures[type]; // The constructor does the work!
    
    // Auto-set health based on type
    this.health = (type == 2) ? 3 : 1; 
      }

    }

    // --- ENEMY BULLETS ---
    public static class EnemyBullet {
    public float x, y, vx, vy;
    
    public EnemyBullet(float x, float y) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 300;
    }
    
    public EnemyBullet(float x, float y, float vx) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = 300;
    }
}

    // --- POWER UPS ---
    public static class PowerUp {
        public float x, y;
        public int type; 
        public Color color;
        public PowerUp(float x, float y, int type, Color color) {
            this.x = x; this.y = y; this.type = type; this.color = color;
        }
    }
    
    // --- PARTICLES (EXPLOSIONS) ---
    public static class Particle {
        public float x, y, vx, vy, life;
        public float r, g, b;
        public Particle(float x, float y, float vx, float vy, float life, float r, float g, float b) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.life = life;
            this.r = r; this.g = g; this.b = b;
        }
    }
} // <--- THIS is the only brace that should be at the very bottom!
