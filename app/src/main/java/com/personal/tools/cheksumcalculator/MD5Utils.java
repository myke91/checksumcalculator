package com.personal.tools.cheksumcalculator;


import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Gabriela.Angelova on 26.9.2017 г..
 */

public class MD5Utils {

    public String checkMD5( File updateFile) {


        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(Constants.TAG,"calculatedDigest null");
            return "";
        }

        Log.w(Constants.TAG,"Calculated digest: " + calculatedDigest);

        return calculatedDigest;
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(Constants.TAG,"Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(Constants.TAG,"Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(Constants.TAG,"Exception on closing MD5 input stream", e);
            }
        }
    }
}
