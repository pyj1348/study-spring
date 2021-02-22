package hello.hellospring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders") // 예약어를 피하기위한 이름 변경
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY) // Member 테이블을 참조하는 FK
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL) // one-to-one일 때 어디 쪽에 둘 것인가? -> 조회가 많은 곳에
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // @BatchSize(size = 1000)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 (ORDER, CANCELED)

    private LocalDateTime orderDate; // 주문시간

//    // 생성 메소드 외에 생성자 막는 용도 = @NoArgsConstructor(access = AccessLevel.PROTECTED)
//    protected Order(){
//
//    }

    //==연관관계 편의 메소드==//
    // Setter가 있음에도 '양방향 세팅을 원자적'으로 관리하기 위해 커스터마이징
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    //==생성 메소드==//
    // 생성 초기화해야하는 것이 단순하지 않으니까 하나의 메소드에서 관계되는 것들을 다 처리하기 -> 응집도 상승
    // 모든 멤버 변수 설정
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        //...문법 공부하기
        Order order = new Order(); // 이 순간에 order_ID 생성?
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){
        if (delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCELED);
        // 재고 원복
        for (OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
