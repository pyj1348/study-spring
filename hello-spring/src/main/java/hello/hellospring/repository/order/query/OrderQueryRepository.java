package hello.hellospring.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    /**
     * V4. DTO로 직접 조회 (N + 1 문제 내재)
     */
    public List<OrderQueryDto> findAllWithDto() {

        List<OrderQueryDto> result = findOrdersWithDto(); // root query 1번

        // collection query N번
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItemsWithDto(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    // V4, V5 root query
    private List<OrderQueryDto> findOrdersWithDto() {
        return em.createQuery("select new hello.hellospring.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                + " from Order o"
                + " join o.member m"
                + " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    // V4 collection query
    private List<OrderItemQueryDto> findOrderItemsWithDto(Long orderId) {
        return em.createQuery("select new hello.hellospring.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                + " from OrderItem oi"
                + " join oi.item i"
                + " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }


    /**
     * V5. DTO로 직접 조회 (N + 1 문제 해결 -> 쿼리 1(root) + 1(collection))
     */
    public List<OrderQueryDto> findAllWithDto_opt() {

        List<OrderQueryDto> result = findOrdersWithDto(); // root query 1번

        List<Long> orderIds = toOrderIds(result);

        // collection query 1번
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
    }

    // V5 collection query
    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new hello.hellospring.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                        + " from OrderItem oi"
                        + " join oi.item i"
                        + " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        return orderItems.stream().collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

    }

    /**
     * V6. DTO로 직접 조회 (N + 1 문제 해결 -> 쿼리 1)
     */
    public List<OrderFlatDto> findAllWithDto_flat() {
        return em.createQuery(
                "select new hello.hellospring.repository.order.query.OrderFlatDto" +
                        "(o.id, m.name, o.orderDate,o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
