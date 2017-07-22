package razvan.extractor.extraction;

import android.os.Handler;
import android.os.Message;

import java.util.List;

import razvan.extractor.OnWordsListener;

public class ExtractedWordsHandler extends Handler {
    private OnWordsListener mOnWordsListener;

    public ExtractedWordsHandler(OnWordsListener onWordsListener) {
        mOnWordsListener = onWordsListener;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case ExtractWordsThread.NEW_WORDS:
                List<String> words = (List<String>) msg.obj;
                mOnWordsListener.onNext(words);
                break;
            case ExtractWordsThread.ON_COMPLETED:
                mOnWordsListener.onCompleted();
                break;
        }
    }
}
