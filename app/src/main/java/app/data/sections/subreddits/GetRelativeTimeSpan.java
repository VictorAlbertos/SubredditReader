package app.data.sections.subreddits;

import rx.Observable;

public interface GetRelativeTimeSpan {
  Observable<String> react(long startTimeInSeconds);
}
