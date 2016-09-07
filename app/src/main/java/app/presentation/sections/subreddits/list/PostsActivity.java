package app.presentation.sections.subreddits.list;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import app.data.sections.subreddits.models.Post;
import app.presentation.foundation.views.BaseActivity;
import app.presentation.foundation.views.LayoutResActivity;
import butterknife.BindView;
import java.util.List;
import miguelbcr.ok_adapters.recycler_view.OkRecyclerViewAdapter;
import miguelbcr.ok_adapters.recycler_view.Pager;
import org.base_app_android.R;
import rx.Observable;
import rx.subjects.PublishSubject;

@LayoutResActivity(R.layout.posts_activity)
public class PostsActivity extends BaseActivity<PostsPresenter> implements PostsPresenter.View {
  @BindView(R.id.rv_posts) RecyclerView rvPosts;
  @BindView(R.id.srl_posts) SwipeRefreshLayout swipeRefreshPosts;
  @BindView(R.id.toolbar) Toolbar toolbar;
  private OkRecyclerViewAdapter<Post, PostViewGroup> adapter;
  private PublishSubject<MenuItem> menuItemPublishSubject = PublishSubject.create();

  @Override protected void injectDagger() {
    getApplicationComponent().inject(this);
  }

  @Override protected void initViews() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(getString(R.string.app_name));

    adapter = new OkRecyclerViewAdapter<Post, PostViewGroup>() {
      @Override protected PostViewGroup onCreateItemView(ViewGroup parent, int viewType) {
        return new PostViewGroup(PostsActivity.this);
      }
    };

    rvPosts.setLayoutManager(new LinearLayoutManager(this));
    rvPosts.setAdapter(adapter);
  }

  @Override
  public void setUpLoaderPager(List<Post> initialLoad, Pager.LoaderPager<Post> loaderPager) {
    adapter.setPager(R.layout.srv_progress, initialLoad, loaderPager);
  }

  @Override public void setUpRefreshList(Pager.Call<Post> call) {
    swipeRefreshPosts.setOnRefreshListener(() -> adapter.resetPager(call));
  }

  @Override public void resetPager(Pager.Call<Post> call) {
    adapter.resetPager(call);
  }

  @Override public void setPagerStillLoading(boolean stillLoading) {
    adapter.setStillLoadingPager(stillLoading);
  }

  @Override public void setOnPostSelectedListener(
      OkRecyclerViewAdapter.Listener<Post, PostViewGroup> listener) {
    adapter.setOnItemClickListener(listener);
  }

  @Override public void hideLoadingOnRefreshList() {
    swipeRefreshPosts.setRefreshing(false);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.sort_menu, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    menuItemPublishSubject.onNext(item);
    return super.onOptionsItemSelected(item);
  }

  @Override public Observable<MenuItem> onClickMenuItem() {
    return menuItemPublishSubject;
  }
}
