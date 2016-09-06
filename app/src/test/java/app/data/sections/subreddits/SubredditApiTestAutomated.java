package app.data.sections.subreddits;

import app.data.foundation.net.ApiModule;

public class SubredditApiTestAutomated extends SubredditApiTest_ {

  @Override protected SubredditApi subredditApi() {
    return new ApiModule().provideSubredditApi();
  }
}
