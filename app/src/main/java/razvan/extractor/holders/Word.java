package razvan.extractor.holders;


import java.io.Serializable;

public class Word implements Serializable {
    private static final long serialVersionUID = 1596180287701719165L;

    private String mWordText;
    private int mWordCount;
    private boolean mCountIsPrime;

    public Word(String wordText) {
        mWordText = wordText;
    }

    public String getWordText() {
        return mWordText;
    }

    public void setWordText(String wordText) {
        mWordText = wordText;
    }

    public int getWordCount() {
        return mWordCount;
    }

    public void setWordCount(int wordCount) {
        mWordCount = wordCount;
    }

    public boolean isCountIsPrime() {
        return mCountIsPrime;
    }

    public void setCountIsPrime(boolean countIsPrime) {
        mCountIsPrime = countIsPrime;
    }

    @Override
    public String toString() { // not printing wordText because it will pe printed by HashMap.toString() in our case
        return ": "
                .concat(String.valueOf(mWordCount))
                .concat(" times; ")
                .concat(mCountIsPrime ? "Prime" : "Not prime")
                .concat("\n");
    }
}
