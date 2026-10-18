package my.newproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.graphics.Color;

public class AchievementsActivity extends Activity {
    
    private Button btnBack;
    private LinearLayout achievementsContainer;
    
    // Achievement data
    private final String[] achievementNames = {
        "⭐ FIRST BLOOD",
        "⚔️ ENEMY SLAYER",
        "👑 LEGENDARY",
        "💀 BOSS SLAYER",
        "🔥 BOSS HUNTER",
        "🏆 BOSS MASTER",
        "🛡️ BASE DEFENDER",
        "💰 COIN COLLECTOR",
        "💎 RICH",
        "✨ PERFECT RUN"
    };
    
    private final String[] achievementDesc = {
        "Destroy 10 enemies",
        "Destroy 50 enemies",
        "Destroy 200 enemies",
        "Defeat your first boss",
        "Defeat 5 bosses",
        "Defeat 10 bosses",
        "Keep base above 80% health",
        "Collect 500 coins",
        "Collect 2000 coins",
        "Finish with full health & base"
    };
    
    private final String[] achievementRewards = {
        "100 coins",
        "200 coins",
        "500 coins",
        "150 coins",
        "300 coins",
        "500 coins",
        "200 coins",
        "250 coins",
        "500 coins",
        "1000 coins"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create layout programmatically
        ScrollView scrollView = new ScrollView(this);
        achievementsContainer = new LinearLayout(this);
        achievementsContainer.setOrientation(LinearLayout.VERTICAL);
        achievementsContainer.setPadding(20, 20, 20, 20);
        
        // Title
        TextView title = new TextView(this);
        title.setText("🏆 ACHIEVEMENTS 🏆");
        title.setTextSize(28);
        title.setTextColor(Color.WHITE);
        title.setGravity(android.view.Gravity.CENTER);
        title.setPadding(0, 20, 0, 40);
        achievementsContainer.addView(title);
        
        // Load achievements
        SharedPreferences prefs = getSharedPreferences("SpaceShooter", MODE_PRIVATE);
        
        // Add each achievement
        for (int i = 0; i < achievementNames.length; i++) {
            boolean unlocked = prefs.getBoolean("achievement_" + i, false);
            addAchievementCard(i, unlocked, prefs);
        }
        
        // Back button
        btnBack = new Button(this);
        btnBack.setText("◀ BACK TO MENU");
        btnBack.setTextSize(16);
        btnBack.setBackgroundColor(Color.parseColor("#FF5722"));
        btnBack.setTextColor(Color.WHITE);
        btnBack.setPadding(20, 15, 20, 15);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        achievementsContainer.addView(btnBack);
        scrollView.addView(achievementsContainer);
        setContentView(scrollView);
        
        // Set background
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#1a1a2e"));
    }
    
    private void addAchievementCard(int index, boolean unlocked, SharedPreferences prefs) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 15, 20, 15);
        card.setBackgroundColor(Color.parseColor("#33000000"));
        
        // Title row
        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView nameView = new TextView(this);
        nameView.setText(achievementNames[index]);
        nameView.setTextSize(16);
        nameView.setTextColor(unlocked ? Color.parseColor("#FFD700") : Color.parseColor("#AAAAAA"));
        nameView.setTypeface(null, android.graphics.Typeface.BOLD);
        
        TextView statusView = new TextView(this);
        statusView.setText(unlocked ? "✓ UNLOCKED" : "🔒 LOCKED");
        statusView.setTextSize(12);
        statusView.setTextColor(unlocked ? Color.parseColor("#4CAF50") : Color.parseColor("#FF5722"));
        statusView.setPadding(10, 0, 0, 0);
        
        titleRow.addView(nameView);
        titleRow.addView(statusView);
        
        // Description
        TextView descView = new TextView(this);
        descView.setText(achievementDesc[index]);
        descView.setTextSize(12);
        descView.setTextColor(Color.parseColor("#CCCCCC"));
        descView.setPadding(0, 5, 0, 5);
        
        // Reward
        TextView rewardView = new TextView(this);
        rewardView.setText("Reward: " + achievementRewards[index]);
        rewardView.setTextSize(11);
        rewardView.setTextColor(Color.parseColor("#FFD700"));
        
        // Add to card
        card.addView(titleRow);
        card.addView(descView);
        card.addView(rewardView);
        
        // Separator line
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 1));
        line.setBackgroundColor(Color.parseColor("#444444"));
        line.setPadding(0, 10, 0, 10);
        
        achievementsContainer.addView(card);
        achievementsContainer.addView(line);
    }
}