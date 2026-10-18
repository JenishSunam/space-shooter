package my.newproject;

import android.os.Bundle;
import android.os.Handler;
import android.content.pm.ActivityInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class GameActivity extends AndroidApplication {
    
    private SpaceShooterGame game;
    private boolean dialogShown = false;  // ADD THIS
    private int selectedDifficulty = 1;    // ADD THIS
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.useAccelerometer = false;
        config.useCompass = false;
        
        game = new SpaceShooterGame();
        initialize(game, config);
        
        // Delay the dialog to ensure LibGDX is initialized
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDifficultyDialog();
            }
        }, 100);
    }
    
    private void showDifficultyDialog() {
    if (dialogShown) return;
    dialogShown = true;
    
    final String[] difficulties = {"EASY", "NORMAL", "HARD"};  // Removed ENDLESS
    
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("SELECT DIFFICULTY");
    builder.setItems(difficulties, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            selectedDifficulty = which;
            game.setDifficulty(selectedDifficulty);
            game.startGame();
        }
    });
    builder.setCancelable(false);
    builder.show();
}
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
}