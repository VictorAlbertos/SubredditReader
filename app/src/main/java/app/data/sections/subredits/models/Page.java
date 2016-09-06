package app.data.sections.subredits.models;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import java.util.List;

@AutoValue
public abstract class Page {
  public abstract List<Post> posts();

  public abstract String after();

  public abstract String before();

  public static Page create(List<Post> posts, String after, String before) {
    return new AutoValue_Page(posts, after, before);
  }

  public static TypeAdapter<Page> typeAdapter(Gson gson) {
    return new AutoValue_Page.GsonTypeAdapter(gson);
  }
}