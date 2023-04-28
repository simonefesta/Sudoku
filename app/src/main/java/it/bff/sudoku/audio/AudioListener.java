package it.bff.sudoku.audio;

import android.app.Activity;
import android.media.AudioTrack;


public class AudioListener implements AudioTrack.OnPlaybackPositionUpdateListener
{
    private Activity activity;
    public AudioListener(Activity activity)
    {
        this.activity=activity;
    }

    @Override
    public void onMarkerReached(AudioTrack audioTrack) {
        //Marcatore afferrato
        if(audioTrack.getAudioSessionId() == 1)
        {
            audioTrack.stop();
            audioTrack.reloadStaticData();
            audioTrack.play();
        }
        else {
            audioTrack.stop();
            audioTrack.release();
        }

    }

    @Override
    public void onPeriodicNotification(AudioTrack audioTrack) {

    }
}
