package app.data.sections.subreddits;

import app.data.sections.subreddits.dtos.ListingDTO;
import app.data.sections.subreddits.dtos.PostDTO;
import app.data.sections.subreddits.models.Page;
import app.data.sections.subreddits.models.Post;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public final class ConvertDTOToModelTest {
  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Mock GetRelativeTimeSpan getRelativeTimeSpan;
  private ConvertDTOToModel convertDTOToModelUT;
  private final static String NOW = "now";

  @Before public void init() {
    when(getRelativeTimeSpan.react(any(Long.class)))
        .thenReturn(Observable.just(NOW));
    convertDTOToModelUT = new ConvertDTOToModel(getRelativeTimeSpan);
  }

  @Test public void Verify_Convert_DTO_To_Model() {
    String after = "after";
    String before = "before";
    List<PostDTO> postDTOs = Arrays.asList(
        PostDTO.create(PostDTO.DataDTO.create(1, "t1", "t1", "a1", "u1", 1, 1)),
        PostDTO.create(PostDTO.DataDTO.create(2, "t2", "t2", "a2", "u2", 2, 2)),
        PostDTO.create(PostDTO.DataDTO.create(3, "t3", "t3", "a3", "u3", 3, 3))
    );
    ListingDTO.DataDTO dataDTO = ListingDTO.DataDTO.create(postDTOs, after, before);
    ListingDTO listingDTO = ListingDTO.create(dataDTO);

    TestSubscriber<Page> subscriber = new TestSubscriber<>();
    convertDTOToModelUT.react(listingDTO).subscribe(subscriber);
    subscriber.awaitTerminalEvent();

    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    Page page = subscriber.getOnNextEvents().get(0);
    assertThat(page.after(), is(after));
    assertThat(page.before(), is(before));
    assertThat(page.posts().size(), is(3));

    Post post1 = page.posts().get(0);
    assertThat(post1.relativeTimeSpan(), is(NOW));
    assertThat(post1.urlThumbnail(), is(postDTOs.get(0).data().thumbnail()));
    assertThat(post1.title(), is(postDTOs.get(0).data().title()));
    assertThat(post1.author(), is(postDTOs.get(0).data().author()));
    assertThat(post1.url(), is(postDTOs.get(0).data().url()));
    assertThat(post1.score(), is(postDTOs.get(0).data().score()));
    assertThat(post1.numberOfComments(), is(postDTOs.get(0).data().num_comments()));

    Post post2 = page.posts().get(1);
    assertThat(post2.relativeTimeSpan(), is(NOW));
    assertThat(post2.urlThumbnail(), is(postDTOs.get(1).data().thumbnail()));
    assertThat(post2.title(), is(postDTOs.get(1).data().title()));
    assertThat(post2.author(), is(postDTOs.get(1).data().author()));
    assertThat(post2.url(), is(postDTOs.get(1).data().url()));
    assertThat(post2.score(), is(postDTOs.get(1).data().score()));
    assertThat(post2.numberOfComments(), is(postDTOs.get(1).data().num_comments()));

    Post post3 = page.posts().get(2);
    assertThat(post3.relativeTimeSpan(), is(NOW));
    assertThat(post3.urlThumbnail(), is(postDTOs.get(2).data().thumbnail()));
    assertThat(post3.title(), is(postDTOs.get(2).data().title()));
    assertThat(post3.author(), is(postDTOs.get(2).data().author()));
    assertThat(post3.url(), is(postDTOs.get(2).data().url()));
    assertThat(post3.score(), is(postDTOs.get(2).data().score()));
    assertThat(post3.numberOfComments(), is(postDTOs.get(2).data().num_comments()));
  }


}
