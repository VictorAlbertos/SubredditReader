package app.presentation.sections.subreddits.detail;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import app.presentation.foundation.views.BaseActivity;
import app.presentation.foundation.views.LayoutResActivity;
import butterknife.BindView;
import org.base_app_android.R;

@LayoutResActivity(R.layout.post_activity)
public class PostActivity extends BaseActivity<PostPresenter> implements PostPresenter.View {
  @BindView(R.id.wv_post) WebView wvPost;
  @BindView(R.id.toolbar) Toolbar toolbar;

  @Override protected void injectDagger() {
    getApplicationComponent().inject(this);
  }

  @Override protected void initViews() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(getString(R.string.app_name));
  }

  @Override public void loadUrl(String url) {
    wvPost.loadUrl(url);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) onBackPressed();
    return super.onOptionsItemSelected(item);
  }
}
