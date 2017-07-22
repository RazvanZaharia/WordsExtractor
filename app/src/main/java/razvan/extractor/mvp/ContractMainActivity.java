package razvan.extractor.mvp;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public interface ContractMainActivity {

    interface View extends MvpView {
        int checkPermission(@NonNull String permission);

        void requestPermission(@NonNull String permission, int requestCode);

        void startAnotherActivityForResult(@NonNull Intent intent, int requestCode);

        void showUrlError(@NonNull String error);

        void showError(@NonNull String error);

        Context getContext();

        void toggleLoading(boolean show);

        void showOutput(@NonNull String output);
    }

    interface Presenter extends MvpPresenter<View> {
        void init();

        void extractWordsFromLocalFile();

        void extractWordsFromUrlFile(@NonNull String fileUrl);

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onPermissionGranted();

        void onPermissionDenied();
    }
}
