package app.data.sections.subreddits;

import android.content.Context;
import android.text.format.DateUtils;
import javax.inject.Inject;
import rx.Observable;

public class GetRelativeTimeSpanBehaviour implements GetRelativeTimeSpan {
  private final Context context;

  @Inject public GetRelativeTimeSpanBehaviour(Context context) {
    this.context = context;
  }

  @Override public Observable<String> react(long startTimeInSeconds) {
    long startTimeInMilli = startTimeInSeconds * 1000;
    String relativeTimeSpan = DateUtils.getRelativeDateTimeString(context, startTimeInMilli, DateUtils.MINUTE_IN_MILLIS,
        DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    return Observable.just(relativeTimeSpan);
  }
}
