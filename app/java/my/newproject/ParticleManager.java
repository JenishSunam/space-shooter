package my.newproject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

public class ParticleManager {
    private Array<Particle> particles = new Array<>();
    private ShapeRenderer shapeRenderer;
    
    private static final int MAX_PARTICLES = 1000;
    
    public ParticleManager() {
        shapeRenderer = new ShapeRenderer();
    }
    
    public void addExplosion(float x, float y) {
        // Add 50 particles for explosion
        for (int i = 0; i < 50 && particles.size < MAX_PARTICLES; i++) {
            float angle = MathUtils.random(0, MathUtils.PI2);
            float speed = MathUtils.random(50, 200);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;
            
            particles.add(new Particle(x, y, vx, vy, 0.5f, 1, 1, 1));
        }
    }
    
    public void addHitEffect(float x, float y) {
        // Add 20 particles for hit
        for (int i = 0; i < 20 && particles.size < MAX_PARTICLES; i++) {
            float angle = MathUtils.random(0, MathUtils.PI2);
            float speed = MathUtils.random(100, 300);
            float vx = MathUtils.cos(angle) * speed;
            float vy = MathUtils.sin(angle) * speed;
            
            particles.add(new Particle(x, y, vx, vy, 0.3f, 1, 0.8f, 0));
        }
    }
    
    public void update(float delta) {
        for (int i = 0; i < particles.size; i++) {
            Particle p = particles.get(i);
            p.update(delta);
            
            if (p.life <= 0) {
                particles.removeIndex(i);
                i--;
            }
        }
    }
    
    public void draw(SpriteBatch batch, float delta) {
        batch.end();
        
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (Particle p : particles) {
            shapeRenderer.setColor(p.r, p.g, p.b, p.life);
            shapeRenderer.rect(p.x - 2, p.y - 2, 4, 4);
        }
        
        shapeRenderer.end();
        batch.begin();
    }
    
    public void dispose() {
        shapeRenderer.dispose();
    }
    
    private static class Particle {
        float x, y, vx, vy;
        float life;
        float r, g, b;
        
        Particle(float x, float y, float vx, float vy, float life, float r, float g, float b) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.life = life;
            this.r = r;
            this.g = g;
            this.b = b;
        }
        
        void update(float delta) {
            x += vx * delta;
            y += vy * delta;
            life -= delta;
        }
    }
}