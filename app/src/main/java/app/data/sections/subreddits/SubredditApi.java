package app.data.sections.subreddits;

import app.data.foundation.net.ErrorAdapter;
import app.data.sections.subreddits.dtos.ListingDTO;
import io.victoralbertos.mockery.api.built_in_interceptor.RxRetrofit;
import io.victoralbertos.mockery.api.built_in_mockery.DTOArgs;
import io.victoralbertos.mockery.api.built_in_mockery.Optional;
import io.victoralbertos.mockery.api.built_in_mockery.Valid;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static io.victoralbertos.mockery.api.built_in_mockery.Valid.Template.STRING;

/**
 * Definition for Retrofit and Mockery of every endpoint required by the Api.
 */
@RxRetrofit(delay = 2000, errorResponseAdapter = ErrorAdapter.class, failurePercent = 75)
public interface SubredditApi {


  @DTOArgs(MockeryListingDTO.class)
  @GET("/r/{subreddit}/{sort}.json")
  Observable<Response<ListingDTO>> getPostsPage(
      @Valid(value = STRING, legal = "programming") @Path("subreddit") String subreddit,
      @Valid(value = STRING, legal = "new") @Path("sort") String sort,
      @Optional @Query("limit") int limit,
      @Optional @Query("before") String before,
      @Optional @Query("after") String after);
}
