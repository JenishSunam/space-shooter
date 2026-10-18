package my.newproject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    public float x, y;
    private Texture texture;
    private Rectangle bounds;
    private int health = 99;
    private float invincibilityTimer = 0f;
    private float speed = 400f;
    
    public Player(float x, float y) {
        this.x = x;
        this.y = y;
        
        // Create simple triangle texture
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillTriangle(0, 32, 16, 0, 32, 32);
        texture = new Texture(pixmap);
        pixmap.dispose();
        
        bounds = new Rectangle(x - 16, y - 16, 32, 32);
    }
    
    public void update(float delta) {
        if (invincibilityTimer > 0) {
            invincibilityTimer -= delta;
        }
        
        bounds.setPosition(x - 16, y - 16);
    }
    
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x - 16, y - 16);
    }
    
    public void damage() {
        if (invincibilityTimer <= 0) {
            health--;
            invincibilityTimer = 1.0f;
        }
    }
    
    public int getHealth() { return health; }
    public Rectangle getBounds() { return bounds; }
    public void dispose() { texture.dispose(); }
}