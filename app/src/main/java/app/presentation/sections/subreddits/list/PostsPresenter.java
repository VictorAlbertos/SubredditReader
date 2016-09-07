package app.presentation.sections.subreddits.list;

import android.view.MenuItem;
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
import org.base_app_android.R;
import rx.Observable;

final class PostsPresenter extends Presenter<PostsPresenter.View> {
  private final PostWireframe wireframe;
  private Page lastPage;
  private Sort lastSort;
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
    lastSort = Sort.New;

    view.setUpLoaderPager(new ArrayList<>(), lastItem ->
        callback -> nextPage(lastPage, callback)
    );

    view.setUpRefreshList(callback -> refreshList(callback, true));

    view.setOnPostSelectedListener((post, userViewGroup, position) ->
        wireframe.postScreen(post).subscribe()
    );

    view.onClickMenuItem()
        .compose(transformations.safely())
        .subscribe(menuItem -> {
          Sort sort = null;

          switch (menuItem.getItemId()) {
            case R.id.sort_by_new:
              sort = Sort.New;
              break;
            case R.id.sort_by_top:
              sort = Sort.Top;
              break;
            case R.id.sort_by_hot:
              sort = Sort.Hot;
              break;
            case R.id.sort_by_controversial:
              sort = Sort.Controversial;
              break;
          }

          if (sort == null) return;

          lastSort = sort;
          lastPage = null;
          view.resetPager(callback -> refreshList(callback, false));
        });
  }

  private void nextPage(Page prevPage, Pager.Callback<Post> callback) {
    repository.getPagePosts(prevPage, lastSort, false)
        .compose(transformations.safely())
        .subscribe(page -> {
          callback.supply(page.posts());
          lastPage = page;
        }, error -> {
          notifications.showSnackBar(Observable.just(error.getMessage()));
          view.setPagerStillLoading(false);
        });
  }

  private void refreshList(Pager.Callback<Post> callback, boolean evict) {
    repository.getPagePosts(lastPage, lastSort, evict)
        .compose(transformations.safely())
        .compose(transformations.loading())
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

    void resetPager(Pager.Call<Post> call);

    void setOnPostSelectedListener(OkRecyclerViewAdapter.Listener<Post,
        PostViewGroup> listener);

    void hideLoadingOnRefreshList();

    void setPagerStillLoading(boolean stillLoading);

    Observable<MenuItem> onClickMenuItem();
  }
}
