package app.data.sections.subreddits;

import app.data.sections.subreddits.dtos.ListingDTO;
import app.data.sections.subreddits.dtos.PostDTO;
import io.victoralbertos.mockery.api.built_in_mockery.DTO;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;

public final class MockeryListingDTO implements DTO.Behaviour<ListingDTO> {

  @Override public ListingDTO legal() {
    String after = "after";
    String before = "before";
    List<PostDTO> postDTOs = Arrays.asList(
        PostDTO.create(PostDTO.DataDTO.create(1, "t1", "t1", "a1", "u1", 1, 1)),
        PostDTO.create(PostDTO.DataDTO.create(2, "t2", "t2", "a2", "u2", 2, 2)),
        PostDTO.create(PostDTO.DataDTO.create(3, "t3", "t3", "a3", "u3", 3, 3))
    );
    ListingDTO.DataDTO dataDTO = ListingDTO.DataDTO.create(postDTOs, after, before);
    ListingDTO listingDTO = ListingDTO.create(dataDTO);
    return listingDTO;
  }

  @Override public void validate(ListingDTO listing) throws AssertionError {
    assertThat(listing, notNullValue());
    assertThat(listing.data(), notNullValue());
    assertThat(listing.data().after(), notNullValue());

    List<PostDTO> postDTOs = listing.data().children();
    assertThat(postDTOs, notNullValue());

    for (PostDTO postDTO: postDTOs) {
      assertThat(postDTO.data().created_utc(), is(not(0)));
      assertThat(postDTO.data().thumbnail(), notNullValue());
      assertThat(postDTO.data().title().isEmpty(), is(not(true)));
      assertThat(postDTO.data().author().isEmpty(), is(not(true)));
      assertThat(postDTO.data().url().isEmpty(), is(not(true)));
    }
  }
}
