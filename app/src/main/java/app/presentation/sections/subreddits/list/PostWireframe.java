package app.presentation.sections.subreddits.list;

import android.content.Intent;
import app.data.foundation.WireframeRepository;
import app.data.sections.subreddits.models.Post;
import app.presentation.foundation.BaseApp;
import app.presentation.sections.subreddits.detail.PostActivity;
import javax.inject.Inject;
import rx.Observable;

public class PostWireframe {
  private final WireframeRepository wireframeRepository;
  private final BaseApp baseApp;

  @Inject public PostWireframe(BaseApp baseApp,
      WireframeRepository wireframeRepository) {
    this.baseApp = baseApp;
    this.wireframeRepository = wireframeRepository;
  }

  public Observable<Void> postScreen(Post post) {
    return wireframeRepository
        .put(PostActivity.class.getName(), post)
        .doOnNext(_I ->
            baseApp.getLiveActivity()
                .startActivity(new Intent(baseApp, PostActivity.class))
        );
  }

  public Observable<Post> getPostScreen() {
    return wireframeRepository
        .<Post>get(PostActivity.class.getName());
  }
}
