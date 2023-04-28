package it.bff.sudoku.storage;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class StoreManager {

    public static List<String> loadTextFile(String fileName, FileFormat format, FileType fileType, Activity activity) {

        File path = buildPath(fileType, activity);
        if (path == null)
            return null;

        List<String> lines = new ArrayList<>();
        try {

            InputStream is = new FileInputStream(path + "/" + fileName + format.getExtName());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            is.close();
        } catch (IOException e) {
            return null;
        }

        return lines;
    }

    public static List<String> loadTextFileFromAsset(String fileName, FileFormat format, FileType fileType, Activity activity) {

        List<String> lines = new ArrayList<>();
        try {

            InputStream is = activity.getAssets().open(fileType.getName() + "/" + fileName + format.getExtName());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null) {
                lines.add(line);
            }

            is.close();
        } catch (IOException e) {
            return null;
        }

        return lines;
    }

    public static boolean storeTextFile(String fileName, String text, FileFormat format, FileType fileType, Activity activity) {

        File path = buildPath(fileType, activity);
        if (path == null)
            return false;

        try {
            FileOutputStream fos = new FileOutputStream(path + "/" + fileName + format.getExtName());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(text);
            outputStreamWriter.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static boolean deleteFile(String fileName, FileFormat format, FileType fileType, Activity activity)
    {
        File path = buildPath(fileType, activity);
        if (path == null)
            return false;

        File file = new File(path, fileName + format.getExtName());
        return file.delete();
    }

    private static File buildPath(FileType fileType, Activity activity) {

        File path;
        //  /data/data/it.bff.sudoku/files/saveLocation
        switch(fileType) {
            case TYPE_PICTURES:
                path = new File(activity.getFilesDir() + "/" + FileType.TYPE_PICTURES.getName());
                break;
            case TYPE_DOWNLOAD:
                path = new File(activity.getFilesDir() + "/" + FileType.TYPE_DOWNLOAD.getName());
                break;
            case TYPE_DOCUMENT:
                path = new File(activity.getFilesDir() + "/" + FileType.TYPE_DOCUMENT.getName());
                break;
            default:
                return null;
        }

        if(!path.exists()) {
            if(!path.mkdir())
                return null;
        }

        return path;
    }

    public enum FileType {
        TYPE_PICTURES("Pictures"),
        TYPE_DOWNLOAD("Download"),
        TYPE_DOCUMENT("Documents");

        private String name;

        FileType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public enum FileFormat {
        FORMAT_TXT(".txt"),
        FORMAT_DATA(".data");

        String extName;

        FileFormat(String extName) {
            this.extName = extName;
        }

        public String getExtName() {
            return extName;
        }
    }

}
