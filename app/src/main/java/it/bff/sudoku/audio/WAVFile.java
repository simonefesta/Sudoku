package it.bff.sudoku.audio;

import android.app.Activity;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import it.bff.sudoku.R;


public  class WAVFile
{
    private int SAMPLERATE;
    private final int DEFAUL_SAMPLERATE=41000;
    private int BYTE_RATE;
    private int WAV_FILE_DIMENSION;
    private boolean PCM = false;
    private boolean MONO = false;
    private boolean MULTICHANNEL = false;
    private Activity activity;
    private int fileId;
    private String filePath;
    private int typePath; //0 if path is Int else 1

    public WAVFile(Activity activity,int fileId)
    {
        this.activity=activity;
        this.fileId=fileId;
        typePath=0;
    }
    public WAVFile(Activity activity,String filePath)
    {
        this.activity=activity;
        this.filePath=filePath;
        typePath=1;
    }

    private void getWavFileInformations(byte[] buffer)
    {
        //Dimensione
        WAV_FILE_DIMENSION = ((buffer[7] & 0xFF) << 24) | ((buffer[6] & 0xFF) << 16) | ((buffer[5] & 0xFF) << 8) | (buffer[4] & 0xFF) + 8;

        //Verifico metodo campionatura
        if((((buffer[21] & 0xFF) << 8) | ((buffer[20] & 0xFF)))==1)
            PCM = true;
        //Numero Canali
        if((((buffer[23] & 0xFF) << 8) | ((buffer[22] & 0xFF)))==1)
            MONO = true;
        else
            MULTICHANNEL = true;

        //Prelevo frequenza di campionamento
        SAMPLERATE=((buffer[27] & 0xFF) << 24) | ((buffer[26] & 0xFF) << 16) | ((buffer[25] & 0xFF) << 8) | (buffer[24] & 0xFF);
        if(SAMPLERATE == 0)
            SAMPLERATE=DEFAUL_SAMPLERATE;

        //Prelevo byte rate
        BYTE_RATE = ((buffer[31] & 0xFF) << 24) | ((buffer[30] & 0xFF) << 16) | ((buffer[29] & 0xFF) << 8) | (buffer[28] & 0xFF);

    }

    public boolean checkWavFromUri() throws IOException
    {
        InputStream inputStream1 = null;
        int bufferSize=0;
        inputStream1 = activity.getContentResolver().openInputStream(Uri.parse(filePath));
        bufferSize = inputStream1.available();
        byte[] buffer = new byte[bufferSize];
        inputStream1.read(buffer,0, bufferSize);
        inputStream1.close();
        return checkWavFile(buffer);
    }
    private boolean checkWavFile(byte[] buffer)
    {
        if(buffer[0]=='R' && buffer[1]=='I' && buffer[2]=='F' && buffer[3]=='F' && buffer[8]=='W' && buffer[9]=='A' && buffer[10]=='V' && buffer[11]=='E')
            return true;
        else
            return false;
    }
    public byte[] createWavItem() throws IOException
    {
        InputStream inputStream1 = null;
        int bufferSize=0;
        if(typePath == 0)
            inputStream1= activity.getResources().openRawResource(fileId);
        else if(typePath == 1)
            inputStream1 = activity.getContentResolver().openInputStream(Uri.parse(filePath));
        bufferSize = inputStream1.available();
        byte[] buffer = new byte[bufferSize];
        inputStream1.read(buffer,0, bufferSize);
        inputStream1.close();

        if(!this.checkWavFile(buffer))
            throw new IOException(activity.getResources().getString(R.string.exception_msg_corrupted_file));
        this.getWavFileInformations(buffer);
        return Arrays.copyOfRange(buffer,44, buffer.length);
    }

    public int getDimension()
    {
        return WAV_FILE_DIMENSION;
    }

    public int getSampleRate() { return SAMPLERATE; }

    public int getByteRate()
    {
        return BYTE_RATE;
    }

    public boolean getPCMInfo()
    {
        return PCM;
    }

    public boolean getChannelMonoInfo()
    {
        return MONO;
    }

    public boolean getMultiChannelInfo()
    {
        return MULTICHANNEL;
    }


}
