package app.data.sections.subreddits.models;

public enum Sort {
  Top("top"), New("new"), Hot("hot"), Controversial("controversial");

  private final String text;


  Sort(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }
}
