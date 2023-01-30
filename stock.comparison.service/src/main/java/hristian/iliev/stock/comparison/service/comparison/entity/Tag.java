package hristian.iliev.stock.comparison.service.comparison.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
public class Tag {

  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  @JsonIgnore
  private Long id;

  private String name;

  private String color;

  private Long userId;

}
