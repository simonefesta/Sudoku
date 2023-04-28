package it.bff.sudoku.audio;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import it.bff.sudoku.R;


public class ControllerAudio
{
    private Activity activity;
    private WAVFile wavFile;
    private AudioListener audioListener;
    private int idSession;
    private AudioTrack audioTrack;
    private boolean isPrepared=false;
    private short[] wav;

    public ControllerAudio(Activity activity, String filePath, AudioListener audioListener, int idSession)
    {
        this.activity=activity;
        idSession=-1;
        this.audioListener=audioListener;
        wavFile = new WAVFile(activity, filePath);
        audioTrack=null;
    }

    public ControllerAudio(Activity activity, int fileId, AudioListener audioListener)
    {

        this.activity=activity;
        idSession=-1;
        this.audioListener=audioListener;
        wavFile = new WAVFile(activity, fileId);
        audioTrack=null;
    }

    public ControllerAudio(Activity activity, int fileId, AudioListener audioListener, int idSession)
    {
        this.activity=activity;
        this.audioListener=audioListener;
        this.idSession=idSession;
        wavFile = new WAVFile(activity, fileId);
        audioTrack=null;
    }

    public void prepareSoundAndPlay() throws IOException
    {
        if(!isPrepared)
        {
            byte[] byteWav = wavFile.createWavItem();
            wav = new short[(wavFile.getDimension() - 44) / 2];
            ByteBuffer.wrap(byteWav).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(wav);
            isPrepared=true;
        }
        playShortModeStatic(wav);
    }

    public void stopAndRelase()
    {
        audioTrack.stop();
        audioTrack.release();
        audioTrack=null;
    }

    public void pause()
    {
        audioTrack.pause();
    }

    public void resume()
    {
        audioTrack.play();
    }

    public boolean isInitialized()
    {
        if(audioTrack!=null)
            return true;
        else
            return false;
    }
    private void playShortModeStatic(short [] shortBuffer) throws IOException
    {
        if(wavFile.getPCMInfo())
        {
            if(wavFile.getChannelMonoInfo())
            {
                if (idSession != -1)
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, wavFile.getSampleRate(), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, shortBuffer.length * 2, AudioTrack.MODE_STATIC, idSession);
                else
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, wavFile.getSampleRate(), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, shortBuffer.length * 2, AudioTrack.MODE_STATIC);
            }
            else if(wavFile.getMultiChannelInfo())
            {
                if(idSession!=-1)
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, wavFile.getSampleRate(), AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, shortBuffer.length * 2, AudioTrack.MODE_STATIC, idSession);
                else
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, wavFile.getSampleRate(), AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, shortBuffer.length * 2, AudioTrack.MODE_STATIC);

            }
            else
                throw new IOException(activity.getResources().getString(R.string.exception_msg_wav_incompatible));
            audioTrack.write(shortBuffer, 0, (shortBuffer.length - 250));
            audioTrack.setNotificationMarkerPosition(shortBuffer.length);
            audioTrack.setPlaybackPositionUpdateListener(audioListener);
            try {
                audioTrack.play();
            } catch (IllegalStateException e) {
                Toast.makeText(activity.getApplicationContext(), R.string.toast_audiotrack, Toast.LENGTH_SHORT);
                audioTrack.stop();
                audioTrack.release();
                e.printStackTrace();
            }
        }
        else
            throw new IOException(activity.getResources().getString(R.string.exception_msg_wav_incompatible));
    }



}

