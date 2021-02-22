package hello.hellospring.service;

import hello.hellospring.domain.Delivery;
import hello.hellospring.domain.Member;
import hello.hellospring.domain.Order;
import hello.hellospring.domain.OrderItem;
import hello.hellospring.domain.item.Item;
import hello.hellospring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    /**
     * 주문 생성
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Optional<Member> member = memberRepository.findById(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.get().getAddress());

        // 주문상품 생성
        // 복수의 아이템은?? => 예제를 위해 단순화 했음
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        // 복수의 아이템은 여기에서 연달아 적어주면 됨
        Order order = Order.createOrder(member.get(), delivery, orderItem);

        // 주문 저장
        orderRepository.save(order); //order를 persist해줄 때 cascade 문법 때문에 delivery와 orderItem도 같이 persist 된다.
        return order.getId();

    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order =orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch){

        return orderRepository.findAllWithDSL(orderSearch);
    }

}
