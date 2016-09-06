package app.data.sections.subreddits.dtos;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class PostDTO {
  public abstract long created_utc();

  public abstract String thumbnail();

  public abstract String title();

  public abstract String author();

  public abstract String url();

  public abstract int score();

  public abstract int num_comments();

  public static PostDTO create(long created_utc, String thumbnail, String title, String author,
      String url, int score, int num_comments) {
    return new AutoValue_PostDTO(created_utc, thumbnail, title, author, url, score, num_comments);
  }

  public static TypeAdapter<PostDTO> typeAdapter(Gson gson) {
    return new AutoValue_PostDTO.GsonTypeAdapter(gson);
  }
}