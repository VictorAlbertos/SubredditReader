package app.data.sections.subreddits;

import app.data.sections.subreddits.dtos.ListingDTO;
import app.data.sections.subreddits.models.Page;
import app.data.sections.subreddits.models.Post;
import javax.inject.Inject;
import rx.Observable;

final class ConvertDTOToModel {
  private final GetRelativeTimeSpan getRelativeTimeSpan;

  @Inject public ConvertDTOToModel(
      GetRelativeTimeSpan getRelativeTimeSpan) {
    this.getRelativeTimeSpan = getRelativeTimeSpan;
  }

  Observable<Page> react(ListingDTO listingDTO) {
    return Observable.just(listingDTO.data().children())
        .flatMapIterable(postDTOs -> postDTOs)
        .flatMap(postDTO ->
           getRelativeTimeSpan.react(postDTO.created_utc())
              .map(relativeTimeSpan ->
                 Post.create(relativeTimeSpan, postDTO.thumbnail(), postDTO.title(), postDTO.author(),
                    postDTO.url(), postDTO.score(), postDTO.num_comments())
              )
        )
        .toList()
        .map(posts -> {
          String after = listingDTO.data().after();
          String before = listingDTO.data().before();
          return Page.create(posts, after, before);
        });
  }
}
