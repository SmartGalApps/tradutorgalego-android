package es.galapps.android.tradutorgalego.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class VolgaChecker {

    public static boolean exists(Context context, String verb)
            throws IOException {

        try {
            InputStream is = context.getResources().getAssets()
                    .open("verbosVolga.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.equalsIgnoreCase(verb)) {
                    is.close();
                    return true;
                }
            }
            is.close();

        } catch (IOException e) {
            throw e;
        }
        return false;
    }

}
