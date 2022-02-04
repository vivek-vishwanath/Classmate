package com.vvishwanath.fbla.objects;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFile extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... strings) {
        String fileUrl = strings[0];
        String fileName = strings[1];
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "testthreepdf");
        folder.mkdir();

        File pdfFile = new File(folder, fileName);

        try{
            pdfFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        FileDownloader.downloadFile(fileUrl, pdfFile);
        return null;
    }

    static class FileDownloader {

        private static final int MEGABYTE = 1024 * 1024;

        public static void downloadFile(String fileUrl, File directory) {
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(directory);
                int totalSize = urlConnection.getContentLength();

                byte[] buffer = new byte[MEGABYTE];
                int bufferLength;
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bufferLength);
                }
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}