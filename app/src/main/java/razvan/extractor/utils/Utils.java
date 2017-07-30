package razvan.extractor.utils;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    @NonNull
    public static List<String> getLowerCaseWords(String input) {
        List<String> wordsList = new ArrayList<>();

        Pattern p = Pattern.compile("[\\w']+"); // word regex
        Matcher m = p.matcher(input);

        while (m.find()) {
            wordsList.add(input.substring(m.start(), m.end()).toLowerCase());
        }

        return wordsList;
    }

    @Nullable
    public static BufferedReader getBufferedReader(String filePath) {
        if (FileUtils.isLocal(filePath)) {
            return getBufferedReader(new File(filePath));
        } else {
            try {
                return getBufferedReader(new URL(filePath));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Nullable
    private static BufferedReader getBufferedReader(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);

            InputStreamReader inputStreamReader = new InputStreamReader(in);
            return new BufferedReader(inputStreamReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    private static BufferedReader getBufferedReader(URL fileUrl) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(fileUrl.openStream());
            return new BufferedReader(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isPrime(int number) {
        boolean prime = true;
        if (number <= 2) {
            prime = false;
        } else {
            double sqrtOfNumber = Math.sqrt((double) number);
            for (int p = 2; p <= sqrtOfNumber; p++) {
                if (number % p == 0) {
                    prime = false;
                    break;
                }
            }
        }
        return prime;
    }
}
