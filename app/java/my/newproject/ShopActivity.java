package my.newproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;

public class ShopActivity extends Activity {
    
    private TextView coinsText;
    private Button btnBack;
    private Button[] shipButtons = new Button[4];
    private Button[] bulletButtons = new Button[4];
    private Button[] selectShipButtons = new Button[4];
    private Button[] selectBulletButtons = new Button[4];
    private int coins;
    private boolean[] ownedShips = new boolean[4];
    private boolean[] ownedBullets = new boolean[4];
    private int selectedShip;
    private int selectedBullet;
    
    private final int[] shipPrices = {0, 100, 200, 500};
    private final int[] bulletPrices = {0, 80, 150, 300};
    private final String[] shipNames = {"CLASSIC", "ICE", "ROYAL", "GOLDEN"};
    private final String[] bulletNames = {"STANDARD", "CYAN", "MAGENTA", "TOXIC"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop);
        
        // Load data
        SharedPreferences prefs = getSharedPreferences("SpaceShooter", MODE_PRIVATE);
        coins = prefs.getInt("coins", 0);
        
        // Load owned skins (store as bit flags)
        int ownedShipsFlag = prefs.getInt("ownedShips", 1); // Bit 0 = Classic
        for (int i = 0; i < 4; i++) {
            ownedShips[i] = (ownedShipsFlag & (1 << i)) != 0;
        }
        
        int ownedBulletsFlag = prefs.getInt("ownedBullets", 1);
        for (int i = 0; i < 4; i++) {
            ownedBullets[i] = (ownedBulletsFlag & (1 << i)) != 0;
        }
        
        selectedShip = prefs.getInt("selectedShipSkin", 0);
        selectedBullet = prefs.getInt("selectedBulletColor", 0);
        
        // Find views
        coinsText = (TextView) findViewById(R.id.coinsText);
        btnBack = (Button) findViewById(R.id.btnBack);
        
        // Ship buy buttons
        shipButtons[0] = (Button) findViewById(R.id.btnShipClassic);
        shipButtons[1] = (Button) findViewById(R.id.btnShipIce);
        shipButtons[2] = (Button) findViewById(R.id.btnShipRoyal);
        shipButtons[3] = (Button) findViewById(R.id.btnShipGolden);
        
        // Bullet buy buttons
        bulletButtons[0] = (Button) findViewById(R.id.btnBulletStandard);
        bulletButtons[1] = (Button) findViewById(R.id.btnBulletCyan);
        bulletButtons[2] = (Button) findViewById(R.id.btnBulletMagenta);
        bulletButtons[3] = (Button) findViewById(R.id.btnBulletToxic);
        
        // Ship select buttons
        selectShipButtons[0] = (Button) findViewById(R.id.btnSelectClassic);
        selectShipButtons[1] = (Button) findViewById(R.id.btnSelectIce);
        selectShipButtons[2] = (Button) findViewById(R.id.btnSelectRoyal);
        selectShipButtons[3] = (Button) findViewById(R.id.btnSelectGolden);
        
        // Bullet select buttons
        selectBulletButtons[0] = (Button) findViewById(R.id.btnSelectStandard);
        selectBulletButtons[1] = (Button) findViewById(R.id.btnSelectCyan);
        selectBulletButtons[2] = (Button) findViewById(R.id.btnSelectMagenta);
        selectBulletButtons[3] = (Button) findViewById(R.id.btnSelectToxic);
        
        // Set listeners
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        for (int i = 0; i < 4; i++) {
            final int index = i;
            shipButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyShip(index);
                }
            });
            bulletButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyBullet(index);
                }
            });
            selectShipButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectShip(index);
                }
            });
            selectBulletButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectBullet(index);
                }
            });
        }
        
        updateUI();
    }
    
    private void buyShip(int index) {
        // Skip if already owned
        if (ownedShips[index]) return;
        
        if (coins >= shipPrices[index]) {
            coins -= shipPrices[index];
            ownedShips[index] = true;
            saveData();
            updateUI();
        }
    }
    
    private void buyBullet(int index) {
        if (ownedBullets[index]) return;
        
        if (coins >= bulletPrices[index]) {
            coins -= bulletPrices[index];
            ownedBullets[index] = true;
            saveData();
            updateUI();
        }
    }
    
    private void selectShip(int index) {
        if (ownedShips[index]) {
            selectedShip = index;
            saveData();
            updateUI();
        }
    }
    
    private void selectBullet(int index) {
        if (ownedBullets[index]) {
            selectedBullet = index;
            saveData();
            updateUI();
        }
    }
    
    private void saveData() {
        SharedPreferences prefs = getSharedPreferences("SpaceShooter", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coins", coins);
        
        // Save owned ships as bit flags
        int ownedShipsFlag = 0;
        for (int i = 0; i < 4; i++) {
            if (ownedShips[i]) ownedShipsFlag |= (1 << i);
        }
        editor.putInt("ownedShips", ownedShipsFlag);
        
        int ownedBulletsFlag = 0;
        for (int i = 0; i < 4; i++) {
            if (ownedBullets[i]) ownedBulletsFlag |= (1 << i);
        }
        editor.putInt("ownedBullets", ownedBulletsFlag);
        
        editor.putInt("selectedShipSkin", selectedShip);
        editor.putInt("selectedBulletColor", selectedBullet);
        editor.apply();
    }
    
    private void updateUI() {
        coinsText.setText("💰 COINS: " + coins);
        
        for (int i = 0; i < 4; i++) {
            // Buy buttons
            if (ownedShips[i]) {
                shipButtons[i].setText(shipNames[i] + "\nOWNED");
                shipButtons[i].setEnabled(false);
            } else {
                shipButtons[i].setText(shipNames[i] + "\n" + shipPrices[i]);
                shipButtons[i].setEnabled(coins >= shipPrices[i]);
            }
            
            if (ownedBullets[i]) {
                bulletButtons[i].setText(bulletNames[i] + "\nOWNED");
                bulletButtons[i].setEnabled(false);
            } else {
                bulletButtons[i].setText(bulletNames[i] + "\n" + bulletPrices[i]);
                bulletButtons[i].setEnabled(coins >= bulletPrices[i]);
            }
            
            // Select buttons
            if (ownedShips[i]) {
                selectShipButtons[i].setEnabled(true);
                if (i == selectedShip) {
                    selectShipButtons[i].setText("✓ SELECTED");
                    selectShipButtons[i].setEnabled(false);
                } else {
                    selectShipButtons[i].setText("SELECT");
                }
            } else {
                selectShipButtons[i].setText("LOCKED");
                selectShipButtons[i].setEnabled(false);
            }
            
            if (ownedBullets[i]) {
                selectBulletButtons[i].setEnabled(true);
                if (i == selectedBullet) {
                    selectBulletButtons[i].setText("✓ SELECTED");
                    selectBulletButtons[i].setEnabled(false);
                } else {
                    selectBulletButtons[i].setText("SELECT");
                }
            } else {
                selectBulletButtons[i].setText("LOCKED");
                selectBulletButtons[i].setEnabled(false);
            }
        }
    }
}