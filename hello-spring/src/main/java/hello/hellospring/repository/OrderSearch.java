package hello.hellospring.repository;

import hello.hellospring.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {

    private String memberName; // 검색할 회원 이름
    private OrderStatus orderStatus; // 검색할 주문 상태 ORDER / CANCEL



}
