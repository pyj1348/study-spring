package hello.hellospring.domain.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hello.hellospring.domain.Category;
import hello.hellospring.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 한 테이블에 상속관계 데이터를 다 넣는다
@DiscriminatorColumn(name = "dtype") // dtype으로 구분
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;
    // Setter와 Getter로 외부에서 비즈니스 로직을 실행한 다음 값을 바꿔주는 것이 아니라
    // 애초에 해당 엔티티에서 비즈니스 로직을 실행하는 방식 -> 객체 지향적

    @JsonIgnore
    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    // 왜 엔티티 안에 비즈니스 로직?
    // 도메인 주도 설계 -> 엔티티 자체가 해결할 수 있는 것은 엔티티 안에 비즈니스 로직을 넣는 것이 객체지향적이다

    /**
     * stock 증가
     * */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     * */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("not enough stock");
        }
        this.stockQuantity = restStock;

    }

}
