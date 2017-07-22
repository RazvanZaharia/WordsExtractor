package razvan.extractor.mvp;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import razvan.extractor.OnWordsListener;
import razvan.extractor.extraction.ExtractWordsThread;
import razvan.extractor.extraction.ExtractedWordsHandler;
import razvan.extractor.holders.Word;
import razvan.extractor.utils.FileUtils;

import static android.app.Activity.RESULT_OK;
import static razvan.extractor.utils.FileUtils.getFileChooserIntent;
import static razvan.extractor.utils.Utils.isPrime;

public class PresenterMainActivity extends BaseMvpPresenter<ContractMainActivity.View>
        implements ContractMainActivity.Presenter, OnWordsListener {
    private static final String TAG = "PresenterMainActivity";

    private static final int REQUEST_PERMISSION_CODE = 12;
    private static final int REQUEST_FILE_CODE = 13;

    private ExtractWordsThread mExtractWordsThread;
    private HashMap<String, Word> mWordsCount;
    private ExtractedWordsHandler mExtractedWordsHandler;
    private Word mMaxAppearCountsWord;
    private int mNumberOfWords = 0;

    @Override
    public void init() {
        mExtractedWordsHandler = new ExtractedWordsHandler(this);
    }

    @Override
    public void extractWordsFromLocalFile() {
        if (!isStoragePermissionGranted()) {
            getMvpView().requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_CODE);
        } else {
            selectFile();
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getMvpView().checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                return false;
            }
        } else {
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private void selectFile() {
        getMvpView().startAnotherActivityForResult(getFileChooserIntent(), REQUEST_FILE_CODE);
    }

    @Override
    public void extractWordsFromUrlFile(@NonNull String fileUrl) {
        if (URLUtil.isValidUrl(fileUrl)) {
            if (FileUtils.checkFileType(fileUrl, "txt")) {
                extractWords(fileUrl);
            } else {
                getMvpView().showUrlError("Wrong file type");
            }
        } else {
            getMvpView().showUrlError("Invalid URL");
        }
    }

    private void onFileSelected(@NonNull Uri uri) {
        String path = FileUtils.getPath(getMvpView().getContext(), uri);
        if (path != null && FileUtils.isLocal(path)) {
            File file = new File(path);
            if (FileUtils.checkFileType(file.getName(), "txt")) {
                extractWords(path);
            } else {
                getMvpView().showError("Wrong file type");
            }
        }
    }

    private void extractWords(String filePath) {
        getMvpView().toggleLoading(true);

        mWordsCount = new HashMap<>();
        mNumberOfWords = 0;
        mMaxAppearCountsWord = null;

        mExtractWordsThread = new ExtractWordsThread(filePath, mExtractedWordsHandler);
        mExtractWordsThread.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_FILE_CODE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    onFileSelected(data.getData());
                }
                break;
        }
    }

    @Override
    public void onPermissionGranted() {
        selectFile();
    }

    @Override
    public void onPermissionDenied() {
        getMvpView().showError("Permission Denied");
    }

    @Override
    public void onNext(List<String> words) {
        mNumberOfWords += words.size();
        for (String wordText : words) {
            Word word = mWordsCount.get(wordText);
            if (word == null) {
                word = new Word(wordText);
            }

            word.setWordCount(word.getWordCount() + 1);
            word.setCountIsPrime(isPrime(word.getWordCount()));
            mWordsCount.put(wordText, word);

            if (mMaxAppearCountsWord == null) {
                mMaxAppearCountsWord = word;
            } else {
                if (mMaxAppearCountsWord.getWordCount() < word.getWordCount()) {
                    mMaxAppearCountsWord = word;
                }
            }
        }
        Log.e(TAG, words.toString());
    }

    @Override
    public void onCompleted() {
        String output = mWordsCount.toString();
        Log.e("OnComplete", output);

        String numberOfWords = "Total Words: ".concat(String.valueOf(mNumberOfWords)).concat("\n");
        String maxNumberOfWords = "Max no of Appears-> ".concat(mMaxAppearCountsWord.getWordText()).concat(mMaxAppearCountsWord.toString()).concat("\n\n");

        getMvpView().showOutput(numberOfWords.concat(maxNumberOfWords).concat(output));
        getMvpView().toggleLoading(false);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mExtractWordsThread != null) {
            mExtractWordsThread.cancel();
        }
    }
}
