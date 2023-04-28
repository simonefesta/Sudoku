package it.bff.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;

import it.bff.sudoku.audio.AudioListener;
import it.bff.sudoku.audio.ControllerAudio;
import it.bff.sudoku.mainMenu.MenuActivity;
import it.bff.sudoku.storage.PreferencesManager;
import it.bff.sudoku.storage.StoreManager;
import it.bff.sudoku.sudokuView.exceptions.SudokuException;

public class SudokuActivity extends AppCompatActivity {

    private SudokuActivityHolder holder;
    private ControllerAudio controllerBackgroundMusic;


    private final int BACKGROUND_ID_SESSION = 1;

    // parameters in intent
    public static final String PARAM_MATRIX = "param_matrix";

    // name of the file that contains the last game played
    public static final String SAVE_FILE_NAME = "saved_game";

    // Uri background music
    public static final String URI_BACKGROUND_MUSIC = "uri_background_music";

    private ExitType exitType;
    private Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        data = new Intent();
        exitType = ExitType.TYPE_SAVE;

        // read data from intent
        Intent intent = getIntent();
        String matrixToParse = intent.getStringExtra(PARAM_MATRIX);
        String backgroundMusicId = intent.getStringExtra(URI_BACKGROUND_MUSIC);


        // prepare the audio
        AudioListener audioListener = new AudioListener(this);
        if(backgroundMusicId!=null)
            controllerBackgroundMusic = new ControllerAudio(this, backgroundMusicId, audioListener, BACKGROUND_ID_SESSION);
        else
            controllerBackgroundMusic = new ControllerAudio(this, R.raw.cameo, audioListener, BACKGROUND_ID_SESSION);

        holder = new SudokuActivityHolder(this);
        try {
            holder.setSudokuTable(matrixToParse);
        } catch (SudokuException e) {
            exit(ExitType.TYPE_ERROR);
        }

        // check the preference
        PreferencesManager preferencesManager = new PreferencesManager(this);
        if((boolean) preferencesManager.get(PreferencesManager.KEY_SOUND))
            prepareSoundAndPlay(controllerBackgroundMusic);
        if(!(boolean) preferencesManager.get(PreferencesManager.KEY_HELP))
            holder.disableHelp();
    }

    @Override
    public void onBackPressed() {

        switch (exitType) {
            case TYPE_ERROR:
                setResult(RESULT_CANCELED, data);
                break;
            case TYPE_DESTROY_SAVE:
                StoreManager.deleteFile(SAVE_FILE_NAME,
                        StoreManager.FileFormat.FORMAT_DATA,
                        StoreManager.FileType.TYPE_DOCUMENT, this);
                setResult(2, data);
                break;
            default:
                String resultString;
                String game = holder.getActualGameMatrix();
                boolean saved = StoreManager.storeTextFile(SAVE_FILE_NAME, game,
                        StoreManager.FileFormat.FORMAT_DATA,
                        StoreManager.FileType.TYPE_DOCUMENT, this);

                if(saved)
                    resultString = getResources().getString(R.string.main_activity_toast_saved_game);
                else
                    resultString = getResources().getString(R.string.main_activity_toast_not_saved_game);


                data.putExtra(MenuActivity.PARAM_STRING_TOAST, resultString);
                setResult(RESULT_OK, data);
                break;
        }

        // audio
        if(controllerBackgroundMusic.isInitialized())
            controllerBackgroundMusic.stopAndRelase();
        super.onBackPressed();
    }

    void exit(ExitType exitType) {
        setExitType(exitType);
        onBackPressed();
    }

    void setExitType(ExitType exitType) {
        this.exitType = exitType;
    }


    @Override
    public void onPause() {
        // audio
        if(controllerBackgroundMusic.isInitialized())
            controllerBackgroundMusic.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        // audio
        if(controllerBackgroundMusic.isInitialized())
            controllerBackgroundMusic.resume();
        super.onResume();
    }


    enum ExitType {

        TYPE_ERROR(0),
        TYPE_DESTROY_SAVE(1),
        TYPE_SAVE(2);

        private int type;

        ExitType(int type) {
            this.type = type;
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
}
