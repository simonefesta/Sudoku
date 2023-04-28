package it.bff.sudoku.introActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import it.bff.sudoku.mainMenu.MenuActivity;
import it.bff.sudoku.R;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private static int TIMER_INTRO = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(IntroActivity.this, MenuActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        }, TIMER_INTRO);
    }

}
