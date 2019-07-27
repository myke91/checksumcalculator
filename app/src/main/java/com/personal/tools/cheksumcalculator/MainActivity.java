package com.personal.tools.cheksumcalculator;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String downloadUrl;
    EditText checksumResult;
    EditText download;
    Button button;
    File downloadedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download = findViewById(R.id.downloadUrl);
        download.setText("generated google drive file id here");
        downloadUrl = "https://drive.google.com/uc?export=download&confirm=no_antivirus&id="+download.getText().toString();
        button = findViewById(R.id.button);
        checksumResult = findViewById(R.id.checksumResult);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process();
            }
        });
    }

    private void process() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                checksumResult.setText("downloading...");
            }

            @Override
            protected String doInBackground(Void... voids) {
                Log.d(Constants.TAG, "started...");
                MD5Utils md5Checker = new MD5Utils();
                downloadApk(downloadUrl);
                String checksum = md5Checker.checkMD5(downloadedFile);
                return checksum;
            }

            @Override
            protected void onPostExecute(String value) {
                if (value == null) {
                    checksumResult.setText("CHECKSUM CALCULATION FAILED.");
                }
                checksumResult.setText(value);
            }
        }.execute();

    }


    private void downloadApk(String outputDir) {

        HttpURLConnection connection = null;
        try {
            if (downloadUrl != null) {
                URL url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
            }

            // download the file
            ContextWrapper cw = new ContextWrapper(this);
            File directory = cw.getDir("media", Context.MODE_PRIVATE);
            String pathToFile = String.format("%s/%s.apk", directory, "checksumfile");

            downloadedFile = new File(pathToFile);
            FileOutputStream fos = new FileOutputStream(new File(pathToFile));
            copyStream(connection.getInputStream(), fos);
        } catch (IOException e) {
            Log.e(Constants.TAG, e.getLocalizedMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte data[] = new byte[1024];
        int read = 0;
        int totalRead = 0;

        while ((read = inputStream.read(data)) != -1) {
            totalRead += read;
            outputStream.write(data, 0, read);
        }
    }
}
