package it.bff.sudoku.mainMenu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import it.bff.sudoku.ModalDialog;
import it.bff.sudoku.audio.AudioListener;
import it.bff.sudoku.audio.ControllerAudio;
import it.bff.sudoku.R;
import it.bff.sudoku.audio.WAVFile;
import it.bff.sudoku.mainMenu.fragments.ScoreFragment;
import it.bff.sudoku.mainMenu.fragments.HomeFragment;
import it.bff.sudoku.mainMenu.fragments.SettingsFragment;

public class MenuActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // result code for activity
    public final static int SUDOKU_ACTIVITY_RESULT_CODE = 100;
    public static final int GET_FILE_CODE = 101;

    // parameter name
    public final static String PARAM_STRING_TOAST = "param_string_toast";

    // file name
    public final static String FILE_NAME_MATRIX_EASY = "matrix_easy";
    public final static String FILE_NAME_MATRIX_MEDIUM = "matrix_medium";
    public final static String FILE_NAME_MATRIX_HARD = "matrix_hard";
    private ControllerAudio controllerAudio;
    private Uri BackgroundMusicUri = null;

    public Uri getBackgroundMusicUri() { return BackgroundMusicUri; }
    public void setBackgroundMusicUri(Uri uri) { this.BackgroundMusicUri=uri; }

    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup te navigation bar
        setContentView(R.layout.activity_menu);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().add(R.id.nav_host_fragment, new HomeFragment(this)).commit();
        controllerAudio = new ControllerAudio(this, R.raw.menu_tap, new AudioListener(this));


        /*
        // use to generate sudoku to put into data file into assets/Documents
        SudokuDebugGenerator generator = new SudokuDebugGenerator(this);
        generator.generate(10);*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment;
        prepareSoundAndPlay(controllerAudio);
        switch (item.getItemId()) {
            case R.id.navigation_home:
                selectedFragment = new HomeFragment(this);
                break;
            case R.id.navigation_settings:
                selectedFragment = new SettingsFragment(this);
                break;
            case R.id.navigation_score:
                selectedFragment = new ScoreFragment(this);
                break;
            default:
                return false;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, selectedFragment).commit();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SUDOKU_ACTIVITY_RESULT_CODE) {

            if(resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.menu_activity_toast_unable_load_game), Toast.LENGTH_SHORT).show();
            }
            else if(resultCode == RESULT_OK) {

                Bundle bundle;
                String msg;

                if(data == null)
                    return;
                bundle = data.getExtras();

                if(bundle == null)
                    return;
                msg = bundle.getString(PARAM_STRING_TOAST);

                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == GET_FILE_CODE) {
            if(data == null || (BackgroundMusicUri=data.getData())==null) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.select_background_music), Toast.LENGTH_SHORT);
                return;
            }
            this.checkAudio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(PermissionManager.isAllGranted(grantResults)) {
            if(requestCode == PermissionManager.REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE) {
                launchActivity(PermissionManager.REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE);
            }
        }
        else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_permission_denied), Toast.LENGTH_SHORT);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void launchActivity(int requestCode) {

        if (requestCode == PermissionManager.REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE) {
            Intent intent = new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (intent.resolveActivity(getPackageManager()) != null) {
                this.startActivityForResult(intent , MenuActivity.GET_FILE_CODE);
            } else {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_directory_error), Toast.LENGTH_SHORT);
            }
        }
    }

    private void prepareSoundAndPlay(ControllerAudio controllerAudio)
    {
        try { controllerAudio.prepareSoundAndPlay(); }
        catch (IOException e)
        {
            (new ModalDialog(this, e.getMessage())).show();
            e.printStackTrace();
        }
    }

    private void checkAudio()
    {
        WAVFile wavFile = new WAVFile(this, this.getBackgroundMusicUri().toString());
        try {
            if(!wavFile.checkWavFromUri()) {
                printShortToastMessage(this.getResources().getString(R.string.toast_wav_required));
                this.setBackgroundMusicUri(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printShortToastMessage(CharSequence msg) {
        if (toast == null || !toast.getView().isShown())
        {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 20);
            toast.show();
        }
    }

}
