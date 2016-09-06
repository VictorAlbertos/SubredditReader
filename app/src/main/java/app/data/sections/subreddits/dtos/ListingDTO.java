package app.data.sections.subreddits.dtos;

import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import java.util.List;

@AutoValue
public abstract class ListingDTO {
  public abstract DataDTO data();

  public static ListingDTO create(DataDTO data) {
    return new AutoValue_ListingDTO(data);
  }

  public static TypeAdapter<ListingDTO> typeAdapter(Gson gson) {
    return new AutoValue_ListingDTO.GsonTypeAdapter(gson);
  }

  @AutoValue
  public static abstract class DataDTO {
    public abstract List<PostDTO> children();

    @Nullable
    public abstract String after();

    @Nullable
    public abstract String before();

    public static DataDTO create(List<PostDTO> children, String after, String before) {
      return new AutoValue_ListingDTO_DataDTO(children, after, before);
    }

    public static TypeAdapter<DataDTO> typeAdapter(Gson gson) {
      return new AutoValue_ListingDTO_DataDTO.GsonTypeAdapter(gson);
    }
  }
}