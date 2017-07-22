package razvan.extractor.extraction;

import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import razvan.extractor.utils.Utils;

import static razvan.extractor.utils.Utils.getLowerCaseWords;

public class ExtractWordsThread extends Thread {
    static final int NEW_WORDS = 1;
    static final int ON_COMPLETED = 2;

    private String mFilePath;
    private Handler mResponseHandler;
    private boolean mIsCanceled = false;

    public ExtractWordsThread(String filePath, Handler responseHandler) {
        this.mResponseHandler = responseHandler;
        this.mFilePath = filePath;
    }

    public void cancel() {
        mIsCanceled = true;
    }

    @Override
    public void run() {
        try {
            BufferedReader textReader = Utils.getBufferedReader(mFilePath);
            if (textReader != null) {
                String receiveString;
                while ((receiveString = textReader.readLine()) != null) {
                    List<String> words = getLowerCaseWords(receiveString);
                    if (words.size() > 0) {
                        if (!mIsCanceled) {
                            mResponseHandler.obtainMessage(NEW_WORDS, -1, -1, words).sendToTarget();
                        } else {
                            return;
                        }
                    }
                }
            }
            mResponseHandler.obtainMessage(ON_COMPLETED, -1, -1, null).sendToTarget();
        } catch (IOException e) {
            mResponseHandler.obtainMessage(ON_COMPLETED, -1, -1, null).sendToTarget();
        }
    }
}