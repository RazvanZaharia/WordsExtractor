package razvan.extractor;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import razvan.extractor.mvp.ContractMainActivity;
import razvan.extractor.mvp.PresenterMainActivity;

public class MainActivity extends AppCompatActivity implements ContractMainActivity.View {

    @BindView(R.id.et_file_url)
    EditText mEtFileUrl;
    @BindView(R.id.tv_output)
    TextView mTvOutput;

    private ProgressDialog mProgressDialog;
    private ContractMainActivity.Presenter mPresenter;

    @OnClick(R.id.btn_file_from_storage)
    void selectFileFromStorage() {
        mPresenter.extractWordsFromLocalFile();
    }

    @OnClick(R.id.btn_file_from_url)
    void selectFileFromUrl() {
        String fileUrl = mEtFileUrl.getText().toString();
        if (TextUtils.isEmpty(fileUrl)) {
            fileUrl = mEtFileUrl.getHint().toString();
        }
        mPresenter.extractWordsFromUrlFile(fileUrl);
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
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Extracting words");

        mPresenter = new PresenterMainActivity();
        mPresenter.attachView(this);
        mPresenter.init();
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

    @Override
    public void startAnotherActivityForResult(@NonNull Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void toggleLoading(boolean show) {
        if (show) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showOutput(@NonNull String output) {
        mTvOutput.setText(output);
    }

    @Override
    public void showUrlError(@NonNull String error) {
        mEtFileUrl.setError(error);
    }

    @Override
    public void showError(@NonNull String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public int checkPermission(@NonNull String permission) {
        return checkSelfPermission(permission);
    }

    @Override
    public void requestPermission(@NonNull String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPresenter.onPermissionGranted();
        } else {
            mPresenter.onPermissionDenied();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
