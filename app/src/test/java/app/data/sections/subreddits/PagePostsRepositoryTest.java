package app.data.sections.subreddits;

import app.data.foundation.net.NetworkResponse;
import app.data.sections.subreddits.dtos.ListingDTO;
import app.data.sections.subreddits.models.Page;
import app.data.sections.subreddits.models.Sort;
import io.reactivecache.ReactiveCache;
import io.rx_cache.Reply;
import io.rx_cache.RxCacheException;
import io.rx_cache.Source;
import io.victoralbertos.jolyglot.GsonSpeaker;
import io.victoralbertos.mockery.api.Mockery;
import io.victoralbertos.mockery.api.built_in_interceptor.RxRetrofit;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public final class PagePostsRepositoryTest {
  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Rule public TemporaryFolder testFolder = new TemporaryFolder();
  @Mock GetRelativeTimeSpan getRelativeTimeSpan;
  private PagePostsRepository pagePostsRepositoryUT;
  private final static String NOW = "now";

  @Test public void Verify_GetPagePosts_With_Previous_Page_Null() {
    mockApiForSuccess();

    TestSubscriber<Page> subscriber = new TestSubscriber<>();
    pagePostsRepositoryUT.getPagePosts(null, Sort.New, false).subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);

    assertNotNull(subscriber.getOnNextEvents().get(0));
  }

  @Test public void Verify_GetPagePosts_With_Previous_After_Null() {
    mockApiForSuccess();

    Page page = Page.create(Arrays.asList(), null, "before");
    checkSuccessPageResponse(pagePostsRepositoryUT.getPagePosts(page, Sort.New, false));
  }

  @Test public void Verify_GetPagePosts_With_Previous_Before_Null() {
    mockApiForSuccess();

    Page page = Page.create(Arrays.asList(), "after", null);
    checkSuccessPageResponse(pagePostsRepositoryUT.getPagePosts(page, Sort.New, false));
  }

  @Test public void Verify_GetPagePosts_Success() {
    mockApiForSuccess();

    Page page = Page.create(Arrays.asList(), "after", "before");
    checkSuccessPageResponse(pagePostsRepositoryUT.getPagePosts(page, Sort.New, false));
  }

  @Test public void Verify_GetPagePosts_Refresh() {
    mockApiForSuccess();

    Page page = Page.create(Arrays.asList(), "after", "before");
    TestSubscriber<Reply<ListingDTO>> subscriber1 = new TestSubscriber<>();
    pagePostsRepositoryUT.getListingDTO(page, Sort.New, false).subscribe(subscriber1);
    subscriber1.awaitTerminalEvent();

    assertEquals(Source.CLOUD,
        subscriber1.getOnNextEvents().get(0).getSource());

    TestSubscriber<Reply<ListingDTO>> subscriber2 = new TestSubscriber<>();
    pagePostsRepositoryUT.getListingDTO(page, Sort.New, false).subscribe(subscriber2);
    subscriber2.awaitTerminalEvent();

    assertEquals(Source.MEMORY,
        subscriber2.getOnNextEvents().get(0).getSource());

    TestSubscriber<Reply<ListingDTO>> subscriber3 = new TestSubscriber<>();
    pagePostsRepositoryUT.getListingDTO(page, Sort.New, true).subscribe(subscriber3);
    subscriber3.awaitTerminalEvent();

    assertEquals(Source.CLOUD,
        subscriber3.getOnNextEvents().get(0).getSource());
  }

  //TODO Test pagination
  @Test public void Verify_GetPagePosts_Cache_Pagination() {

  }

  @Test public void Verify_GetPagePosts_Failure() {
    mockApiForFailure();

    TestSubscriber<Page> subscriber = new TestSubscriber<>();
    pagePostsRepositoryUT.getPagePosts(null, Sort.New, false).subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertError(RxCacheException.class);
    subscriber.assertNoValues();

    Throwable error = subscriber.getOnErrorEvents().get(0);
    assertEquals("Mock failure!", error.getCause().getMessage());
  }

  @Test public void When_No_Subreddit_Updated_Then_Return_Default() {
    mockApiForFailure();

    TestSubscriber<String> subscriber = new TestSubscriber<>();
    pagePostsRepositoryUT.subreddit().subscribe(subscriber);
    subscriber.awaitTerminalEvent();

    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    assertEquals(PagePostsRepository.DEFAULT_SUBREDDIT, subscriber.getOnNextEvents().get(0));
  }

  @Test public void When_Subreddit_Updated_Then_Return_Value() {
    mockApiForFailure();

    TestSubscriber<Void> subscriberUpdate = new TestSubscriber<>();
    pagePostsRepositoryUT.updateSubreddit("updated").subscribe(subscriberUpdate);
    subscriberUpdate.awaitTerminalEvent();

    TestSubscriber<String> subscriber = new TestSubscriber<>();
    pagePostsRepositoryUT.subreddit().subscribe(subscriber);
    subscriber.awaitTerminalEvent();

    subscriber.assertCompleted();
    subscriber.assertNoErrors();
    assertEquals("updated", subscriber.getOnNextEvents().get(0));
  }

  private void checkSuccessPageResponse(Observable<Page> oPage) {
    TestSubscriber<Page> subscriber = new TestSubscriber<>();
    oPage.subscribe(subscriber);
    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValueCount(1);
    assertNotNull(subscriber.getOnNextEvents().get(0));
  }

  @RxRetrofit(delay = 0, failurePercent = 0, variancePercentage = 0) interface ApiSuccess
      extends SubredditApi {
  }

  @RxRetrofit(delay = 0, failurePercent = 100, variancePercentage = 0) interface ApiFailure
      extends SubredditApi {
  }

  private void mockApiForSuccess() {
    NetworkResponse networkResponse = new NetworkResponse();

    SubredditApi subredditApi = new Mockery.Builder<ApiSuccess>()
        .mock(ApiSuccess.class)
        .build();

    ReactiveCache reactiveCache = new ReactiveCache.Builder()
        .using(testFolder.getRoot(), new GsonSpeaker());

    when(getRelativeTimeSpan.react(any(Long.class)))
        .thenReturn(Observable.just(NOW));

    pagePostsRepositoryUT = new PagePostsRepository(subredditApi,
        networkResponse, reactiveCache, new ConvertDTOToModel(getRelativeTimeSpan));
  }

  private void mockApiForFailure() {
    NetworkResponse networkResponse = new NetworkResponse();

    SubredditApi subredditApi = new Mockery.Builder<ApiFailure>()
        .mock(ApiFailure.class)
        .build();

    ReactiveCache reactiveCache = new ReactiveCache.Builder()
        .using(testFolder.getRoot(), new GsonSpeaker());

    when(getRelativeTimeSpan.react(any(Long.class)))
        .thenReturn(Observable.just(NOW));

    pagePostsRepositoryUT = new PagePostsRepository(subredditApi,
        networkResponse, reactiveCache, new ConvertDTOToModel(getRelativeTimeSpan));
  }
}
