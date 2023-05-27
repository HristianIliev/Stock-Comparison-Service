package hristian.iliev.stock.comparison.service.comparison.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hristian.iliev.stock.comparison.service.users.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Comparison {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  @JsonIgnore
  private Long id;

  private String firstStockName;

  private String secondStockName;

  @ManyToOne
  @JsonIgnore
  private User user;

  @ManyToOne
  private Tag tag;

  @OneToMany(mappedBy = "comparison")
  private List<Note> notes;

  @JsonIgnore
  public User getUser() {
    return user;
  }

}
