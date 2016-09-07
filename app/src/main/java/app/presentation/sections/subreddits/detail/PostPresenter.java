package app.presentation.sections.subreddits.detail;

import app.presentation.foundation.notifications.Notifications;
import app.presentation.foundation.presenter.Presenter;
import app.presentation.foundation.presenter.SyncView;
import app.presentation.foundation.presenter.ViewPresenter;
import app.presentation.foundation.transformations.Transformations;
import app.presentation.sections.subreddits.list.PostWireframe;
import javax.inject.Inject;

final class PostPresenter extends Presenter<PostPresenter.View> {
  private final PostWireframe postWireframe;

  @Inject public PostPresenter(Transformations transformations,
      Notifications notifications,
      SyncView syncView, PostWireframe postWireframe) {
    super(transformations, notifications, syncView);
    this.postWireframe = postWireframe;
  }

  @Override public void onBindView(View view) {
    super.onBindView(view);

    postWireframe.getPostScreen()
        .compose(transformations.safely())
        .compose(transformations.reportOnSnackBar())
        .subscribe(post -> view.loadUrl(post.url()));
  }

  interface View extends ViewPresenter {
    void loadUrl(String url);
  }
}
