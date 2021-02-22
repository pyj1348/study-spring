package hello.hellospring;

import hello.hellospring.domain.*;
import hello.hellospring.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDB {

    private final InitService initService;

    @PostConstruct // 스프링 빈이 다 만들어진 뒤에 실행
    public void init() {
//        initService.init1();
//        initService.init2();

    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;

        public void init1(){
            Member member = createMember("userA", "서울", "상도동", "123");
            em.persist(member);

            Book book1 = createBook("JPA1", 1000, 10);
            em.persist(book1);

            Book book2 = createBook("JPA2", 1200, 10);
            em.persist(book2);

            Delivery delivery = createBook(member);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 1000, 2);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 1200, 4);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);

            em.persist(order);

        }

        public void init2(){
            Member member = createMember("userB", "부산", "대연동", "456");
            em.persist(member);

            Book book1 = createBook("Spring1", 1500, 20);
            em.persist(book1);

            Book book2 = createBook("Spring2", 1800, 10);
            em.persist(book2);

            Delivery delivery = createBook(member);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 1500, 5);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 1800, 3);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);

            em.persist(order);


        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }
        private Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }
        private Delivery createBook(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}
