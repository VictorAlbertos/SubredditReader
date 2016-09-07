package app.presentation.sections.subreddits.list;

import app.data.sections.subreddits.PagePostsRepository;
import app.data.sections.subreddits.models.Page;
import app.data.sections.subreddits.models.Post;
import app.data.sections.subreddits.models.Sort;
import app.presentation.foundation.notifications.Notifications;
import app.presentation.foundation.presenter.Presenter;
import app.presentation.foundation.presenter.SyncView;
import app.presentation.foundation.presenter.ViewPresenter;
import app.presentation.foundation.transformations.Transformations;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import miguelbcr.ok_adapters.recycler_view.OkRecyclerViewAdapter;
import miguelbcr.ok_adapters.recycler_view.Pager;
import rx.Observable;

final class PostsPresenter extends Presenter<PostsPresenter.View> {
  private final PostWireframe wireframe;
  private Page lastPage;
  private final PagePostsRepository repository;

  @Inject public PostsPresenter(Transformations transformations,
      Notifications notifications,
      SyncView syncView,
      PostWireframe wireframe, PagePostsRepository repository) {
    super(transformations, notifications, syncView);
    this.wireframe = wireframe;
    this.repository = repository;
  }

  @Override public void onBindView(View view) {
    super.onBindView(view);

    lastPage = null;

    view.setUpLoaderPager(new ArrayList<>(), lastItem ->
        callback -> nextPage(lastPage, callback)
    );

    view.setUpRefreshList(this::refreshList);

    view.setOnPostSelectedListener((post, userViewGroup, position) ->
        wireframe.postScreen(post).subscribe()
    );
  }

  private void nextPage(Page prevPage, Pager.Callback<Post> callback) {
    repository.getPagePosts(prevPage, Sort.New, false)
        .compose(transformations.safely())
        .subscribe(page -> {
          callback.supply(page.posts());
          lastPage = page;
        }, error -> {
          notifications.showSnackBar(Observable.just(error.getMessage()));
          view.setPagerStillLoading(false);
        });
  }

  private void refreshList(Pager.Callback<Post> callback) {
    repository.getPagePosts(null, Sort.New, true)
        .compose(transformations.safely())
        .subscribe(page -> {
          callback.supply(page.posts());
          lastPage = page;
          view.hideLoadingOnRefreshList();
        }, error -> {
          view.hideLoadingOnRefreshList();
          notifications.showSnackBar(Observable.just(error.getMessage()));
        });
  }

  interface View extends ViewPresenter {
    void setUpLoaderPager(List<Post> initialLoad, Pager.LoaderPager<Post> loaderPager);

    void setUpRefreshList(Pager.Call<Post> call);

    void setOnPostSelectedListener(OkRecyclerViewAdapter.Listener<Post,
        PostViewGroup> listener);

    void hideLoadingOnRefreshList();

    void setPagerStillLoading(boolean stillLoading);
  }
}
