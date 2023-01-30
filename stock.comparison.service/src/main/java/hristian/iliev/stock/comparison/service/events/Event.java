package hristian.iliev.stock.comparison.service.events;

import com.google.gson.Gson;

public class Event {

  private String action;
  private String entityClass;
  private String username;
  private String message;

  public Event(String action, String entityClass, String username, String message) {
    this.action = action;
    this.entityClass = entityClass;
    this.username = username;
    this.message = message;
  }

  public enum EventAction {
    RETRIEVE, CREATE, DELETE, UPDATE;
  }

  public String toJson() {
    return new Gson().toJson(this);
  }

  public static class Builder {

    private EventAction action;
    private String entityClass;
    private String username;
    private String message;

    public Builder withAction(EventAction action) {
      this.action = action;

      return this;
    }

    public Builder withEntityClass(String entityClass) {
      this.entityClass = entityClass;

      return this;
    }

    public Builder withUsername(String username) {
      this.username = username;

      return this;
    }

    public Builder withMessage(String message) {
      this.message = message;

      return this;
    }

    public Event build() {
      return new Event(action.toString(), entityClass, username, message);
    }
  }

}
