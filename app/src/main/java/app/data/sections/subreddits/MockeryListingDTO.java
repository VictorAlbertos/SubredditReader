package app.data.sections.subreddits;

import app.data.sections.subreddits.dtos.ListingDTO;
import app.data.sections.subreddits.dtos.PostDTO;
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;

public final class MockeryListingDTO implements DTOArgs.Behaviour<ListingDTO> {

  @Override public ListingDTO legal(Object[] args) {
    String sort = args[1] + " ";
    String after = (String) args[4];
    int perPage = 25;
    int index = after == null ? 0 : Integer.valueOf(after);

    List<PostDTO> postDTOs = new ArrayList<>();
    for (int i = index; i < index + perPage; i++) {
      postDTOs.add(PostDTO
          .create(PostDTO.DataDTO.create(System.currentTimeMillis(),
              "https://camo.githubusercontent.com/b13830f5a9baecd3d83ef5cae4d5107d25cdbfbe/68747470733a2f2f662e636c6f75642e6769746875622e636f6d2f6173736574732f3732313033382f313732383830352f35336532613364382d363262352d313165332d383964312d3934376632373062646430332e706e67",
              "Title " +sort+ + i, "Author" + i, "http://setosa.io/ev/eigenvectors-and-eigenvalues/", i,
              i)));
    }

    ListingDTO.DataDTO dataDTO = ListingDTO.DataDTO
        .create(postDTOs, String.valueOf(index + perPage), "");
    return ListingDTO.create(dataDTO);
  }

  @Override public void validate(ListingDTO listing) throws AssertionError {
    assertThat(listing, notNullValue());
    assertThat(listing.data(), notNullValue());
    assertThat(listing.data().after(), notNullValue());

    List<PostDTO> postDTOs = listing.data().children();
    assertThat(postDTOs, notNullValue());

    for (PostDTO postDTO : postDTOs) {
      assertThat(postDTO.data().created_utc(), is(not(0)));
      assertThat(postDTO.data().thumbnail(), notNullValue());
      assertThat(postDTO.data().title().isEmpty(), is(not(true)));
      assertThat(postDTO.data().author().isEmpty(), is(not(true)));
      assertThat(postDTO.data().url().isEmpty(), is(not(true)));
    }
  }
}
