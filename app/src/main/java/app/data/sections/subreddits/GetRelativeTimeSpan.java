package app.data.sections.subreddits;

import rx.Observable;

interface GetRelativeTimeSpan {
  Observable<String> react(long startTimeInSeconds);
}
