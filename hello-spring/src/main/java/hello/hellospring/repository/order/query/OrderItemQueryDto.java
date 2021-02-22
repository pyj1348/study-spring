package hello.hellospring.repository.order.query;

import lombok.Data;

@Data
public class OrderItemQueryDto {

    private Long orderId;
    private String itemName;
    private int orderPride;
    private int count;

    public OrderItemQueryDto(Long orderId, String itemName, int orderPride, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPride = orderPride;
        this.count = count;
    }
}
