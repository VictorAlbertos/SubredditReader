package app.presentation.sections.subreddits.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import app.data.sections.subreddits.models.Post;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import miguelbcr.ok_adapters.recycler_view.OkRecyclerViewAdapter;
import org.base_app_android.R;

public final class PostViewGroup extends FrameLayout implements OkRecyclerViewAdapter.Binder<Post> {
  @BindView(R.id.iv_avatar) ImageView ivAvatar;
  @BindView(R.id.tv_time) TextView tvTime;
  @BindView(R.id.tv_title) TextView tvTitle;
  @BindView(R.id.tv_author) TextView tvAuthor;
  @BindView(R.id.tv_comments) TextView tvComments;
  @BindView(R.id.tv_score) TextView tvScore;

  public PostViewGroup(Context context) {
    super(context);
    init();
  }

  public PostViewGroup(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    View view = LayoutInflater.from(getContext()).inflate(R.layout.post_view_group, this, true);
    ButterKnife.bind(this, view);
  }

  @Override public void bind(Post post, int position, int count) {
    tvTime.setText(post.relativeTimeSpan());
    tvTitle.setText(post.title());
    tvAuthor.setText(post.author());
    tvComments.setText(String.valueOf(post.numberOfComments()));
    tvScore.setText(String.valueOf(post.score()));

    if (post.urlThumbnail().isEmpty()) return;

    Picasso.with(getContext()).load(post.urlThumbnail())
        .centerCrop()
        .fit()
        .into(ivAvatar);
  }

}