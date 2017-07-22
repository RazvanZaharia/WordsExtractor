package razvan.extractor.mvp;


public class BaseMvpPresenter<T extends MvpView> implements MvpPresenter<T> {

    private T mMvpView;

    @Override
    public void attachView(T mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mMvpView = null;
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public T getMvpView() {
        checkViewAttached();
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        private static final long serialVersionUID = 621052920134273073L;

        public MvpViewNotAttachedException() {
            super("Please call MvpPresenter.attachView(MvpView) before" +
                    " requesting data to the MvpPresenter");
        }
    }
}
