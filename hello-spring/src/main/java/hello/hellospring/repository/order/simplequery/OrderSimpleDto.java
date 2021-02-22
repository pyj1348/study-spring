package hello.hellospring.repository.order.simplequery;

import hello.hellospring.domain.Order;
import hello.hellospring.domain.OrderStatus;
import hello.hellospring.domain.Address;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address; // delivery의 address

    public OrderSimpleDto(Order order) {
        orderId = order.getId();
        name = order.getMember().getName(); // Lazy 초기화
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress(); // Lazy 초기화
    }

    public OrderSimpleDto(Long orderId, String name, LocalDateTime
            orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}