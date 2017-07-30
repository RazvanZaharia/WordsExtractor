package razvan.extractor;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import razvan.extractor.mvp.ContractMainActivity;
import razvan.extractor.mvp.PresenterMainActivity;

public class TestPresenterMainActivity {
    @Mock
    private Context mContext;

    private MockView mView;
    private ContractMainActivity.Presenter mPresenter;

    @Before
    public void setUp() {
        mView = new MockView();

        mPresenter = new PresenterMainActivity();
        mPresenter.attachView(mView);
        mPresenter.init();
    }

    @Test
    public void testPresenterInvalidUrlInput() {
        String urlToTest = "http://urlToTest";
        mPresenter.extractWordsFromUrlFile(urlToTest);
        Assert.assertTrue(mView.showUrlError);
    }

    @Test
    public void testPresenterWrongFileTypeInput() {
        String urlToTest = "https://github.com/";
        mPresenter.extractWordsFromUrlFile(urlToTest);
        Assert.assertTrue(mView.showUrlError);
    }

    @Test
    public void testPresenterOnPermissionDenied() {
        mPresenter.onPermissionDenied();
        Assert.assertTrue(mView.showError);
    }

    private class MockView implements ContractMainActivity.View {
        boolean showUrlError = false;
        boolean showError = false;
        boolean isLoadingVisible = false;
        boolean showOutput = false; // true if some content was sent to view

        @Override
        public int checkPermission(@NonNull String permission) {
            return 0;
        }

        @Override
        public void requestPermission(@NonNull String permission, int requestCode) {
        }

        @Override
        public void startAnotherActivityForResult(@NonNull Intent intent, int requestCode) {
        }

        @Override
        public void showUrlError(@NonNull String error) {
            showUrlError = true;
        }

        @Override
        public void showError(@NonNull String error) {
            showError = true;
        }

        @Override
        public Context getContext() {
            return mContext;
        }

        @Override
        public void toggleLoading(boolean show) {
            isLoadingVisible = show; // the last state of loading to test if it was hidden or not
        }

        @Override
        public void showOutput(@NonNull String output) {
            showOutput = !TextUtils.isEmpty(output);
        }
    }
}
