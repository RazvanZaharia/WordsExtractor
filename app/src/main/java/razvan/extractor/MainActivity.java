package razvan.extractor;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import razvan.extractor.extraction.ExtractWordsThread;
import razvan.extractor.extraction.ExtractedWordsHandler;
import razvan.extractor.holders.Word;
import razvan.extractor.utils.FileUtils;

import static razvan.extractor.utils.FileUtils.getFileChooserIntent;
import static razvan.extractor.utils.Utils.isPrime;

public class MainActivity extends AppCompatActivity implements OnWordsListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 13;

    private ExtractWordsThread mExtractWordsThread;
    private HashMap<String, Word> mWordsCount;
    private ProgressDialog mProgressDialog;
    private ExtractedWordsHandler mExtractedWordsHandler;
    private int mNumberOfWords = 0;

    @BindView(R.id.et_file_url)
    EditText mEtFileUrl;
    @BindView(R.id.tv_output)
    TextView mTvOutput;

    @OnClick(R.id.btn_file_from_storage)
    void selectFileFromStorage() {
        if (!isStoragePermissionGranted()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            selectFile();
        }
    }

    @OnClick(R.id.btn_file_from_url)
    void selectFileFromUrl() {
        String fileUrl = mEtFileUrl.getText().toString();

        if (TextUtils.isEmpty(fileUrl)) {
            fileUrl = mEtFileUrl.getHint().toString();
        }

        if (URLUtil.isValidUrl(fileUrl)) {
            extractWords(fileUrl);
        } else {
            mEtFileUrl.setError("Invalid URL");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        setActions();
    }

    private void init() {
        mWordsCount = new HashMap<>();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Extracting words");

        mExtractedWordsHandler = new ExtractedWordsHandler(this);
    }

    private void setActions() {
        mEtFileUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    selectFileFromUrl();
                    return true;
                }
                return false;
            }
        });
    }

    private void selectFile() {
        startActivityForResult(getFileChooserIntent(), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    onFileSelected(data.getData());
                }
                break;
        }
    }

    private void onFileSelected(@NonNull Uri uri) {
        String path = FileUtils.getPath(this, uri);
        if (path != null && FileUtils.isLocal(path)) {
            File file = new File(path);
            if (FileUtils.checkFileType(file.getName(), "txt")) {
                extractWords(path);
            } else {
                Toast.makeText(this, "Wrong file type", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void extractWords(String filePath) {
        mProgressDialog.show();

        mExtractWordsThread = new ExtractWordsThread(filePath, mExtractedWordsHandler);

        mExtractWordsThread.start();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFile();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mExtractWordsThread.cancel();
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
        }
        Log.e(TAG, words.toString());
    }

    @Override
    public void onCompleted() {
        String output = mWordsCount.toString();
        Log.e(TAG + " OnComplete", output);

        String numberOfWords = "Total Words: ".concat(String.valueOf(mNumberOfWords)).concat("\n\n");
        mTvOutput.setText(numberOfWords.concat(output));
        mProgressDialog.dismiss();
    }
}
