package hello.hellospring.domain.item;

import hello.hellospring.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("M")
@Getter @Setter
public class Movie extends Item {
    private String dircetor;
    private String actor;
}
