package razvan.extractor;

import java.util.List;

public interface OnWordsListener {
    void onNext(List<String> words);

    void onCompleted();
}
