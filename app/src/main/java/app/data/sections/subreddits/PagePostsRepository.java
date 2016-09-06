package app.data.sections.subreddits;

import app.data.foundation.net.NetworkResponse;
import app.data.sections.subreddits.dtos.ListingDTO;
import app.data.sections.subreddits.models.Page;
import app.data.sections.subreddits.models.Sort;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.fernandocejas.frodo.core.annotations.VisibleForTesting;
import io.reactivecache.Provider;
import io.reactivecache.ProviderGroup;
import io.reactivecache.ReactiveCache;
import io.rx_cache.Reply;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;

public class PagePostsRepository {
  private final SubredditApi subredditApi;
  private final ProviderGroup<ListingDTO> cacheProviderPages;
  private final Provider<String> cacheProviderSubreddit;
  private final NetworkResponse networkResponse;
  private final ConvertDTOToModel convertDTOToModel;
  private static final int POSTS_PER_PAGE = 100;
  private static final String KEY_FIRST_PAGE = "key_first_page";
  @VisibleForTesting
  static final String DEFAULT_SUBREDDIT = "programming";

  @Inject public PagePostsRepository(SubredditApi subredditApi, NetworkResponse networkResponse,
      ReactiveCache reactiveCache, ConvertDTOToModel convertDTOToModel) {
    this.subredditApi = subredditApi;
    this.networkResponse = networkResponse;
    this.convertDTOToModel = convertDTOToModel;
    this.cacheProviderPages = reactiveCache.<ListingDTO>providerGroup()
        .lifeCache(15, TimeUnit.MINUTES)
        .withKey("users");
    this.cacheProviderSubreddit = reactiveCache.<String>provider()
        .withKey("subreddit");
  }

  @RxLogObservable
  public Observable<Page> getPagePosts(Page previousPage, Sort sort, final boolean refresh) {
    return getListingDTO(previousPage, sort, refresh)
        .flatMap(reply -> convertDTOToModel.react(reply.getData()));
  }

  @VisibleForTesting Observable<Reply<ListingDTO>> getListingDTO(Page previousPage, Sort sort,
      final boolean refresh) {
    return subreddit()
        .flatMap(subreddit -> {
          String before = previousPage == null ? null : previousPage.before();
          String after = previousPage == null ? null : previousPage.after();
          String keyGroup = (before == null && after == null) ? KEY_FIRST_PAGE : after + before;
          keyGroup += sort.toString();

          Observable<ListingDTO> apiCall = subredditApi.getPostsPage(subreddit, sort.toString(),
              POSTS_PER_PAGE, before, after)
              .compose(networkResponse.process());

          return refresh ? apiCall.compose(cacheProviderPages.replaceAsReply(keyGroup))
              : apiCall.compose(cacheProviderPages.readWithLoaderAsReply(keyGroup));
        });
  }

  @VisibleForTesting Observable<String> subreddit() {
    return cacheProviderSubreddit.read()
        .onErrorResumeNext(throwable -> Observable.just(DEFAULT_SUBREDDIT));
  }

  @RxLogObservable
  public Observable<Void> updateSubreddit(String subreddit) {
    return Observable.just(subreddit)
        .compose(cacheProviderSubreddit.replace())
        .map(ignore -> null);
  }
}
