package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.domain.Order;
import hello.hellospring.domain.OrderStatus;
import hello.hellospring.domain.Address;
import hello.hellospring.domain.item.Book;
import hello.hellospring.domain.item.Item;
import hello.hellospring.exception.NotEnoughStockException;
import hello.hellospring.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired MemberService memberService;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    @Autowired ItemService itemService;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember("회원1", "서울", "상도로", "123123" );

        Item book = createItem("JPA", 15000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);


        //then
        Order order = orderRepository.findOne(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getOrderItems().size()).isEqualTo(1);
        assertThat(order.getTotalPrice()).isEqualTo(15000 * orderCount);
        assertThat(order.getMember().getId()).isEqualTo(member.getId());
        assertThat(order.getDelivery().getAddress()).isEqualTo(member.getAddress());
        assertThat(order.getOrderItems().get(0).getItem().getId()).isEqualTo(book.getId());
        assertThat(itemService.findOne(book.getId()).getStockQuantity()).isEqualTo(8);
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember("회원2", "서울", "상도동", "123" );

        Item book = createItem("JPA", 10000, 10);
        //when

        //then
        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), 11) );

    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember("name", "서울", "동네", "321");
        int stockQuantity = 2;
        Item book = createItem("Spring", 5000, stockQuantity);
        //when
        int count = 1;
        Long orderId = orderService.order(member.getId(), book.getId(), count);
        orderService.cancelOrder(orderId);
        Order order = orderRepository.findOne(orderId);

        //then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(itemService.findOne(book.getId()).getStockQuantity()).isEqualTo(stockQuantity);
    }


    private Item createItem(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        itemService.saveItem(book);
        return book;
    }

    private Member createMember(String name, String city, String street, String zipcode) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));
        memberService.join(member);
        return member;
    }


}
