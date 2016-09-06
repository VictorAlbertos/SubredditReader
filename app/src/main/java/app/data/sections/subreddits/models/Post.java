package app.data.sections.subreddits.models;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Post {
  public abstract String relativeTimeSpan();

  public abstract String urlThumbnail();

  public abstract String title();

  public abstract String author();

  public abstract String url();

  public abstract int score();

  public abstract int numberOfComments();

  public static Post create(String relativeTimeSpan, String urlThumbnail, String title,
      String author, String url, int score, int num_comments) {
    return new AutoValue_Post(relativeTimeSpan, urlThumbnail, title, author, url, score,
        num_comments);
  }

  public static TypeAdapter<Post> typeAdapter(Gson gson) {
    return new AutoValue_Post.GsonTypeAdapter(gson);
  }
}