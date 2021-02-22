package hello.hellospring.api;

import hello.hellospring.domain.Address;
import hello.hellospring.domain.Order;
import hello.hellospring.domain.OrderItem;
import hello.hellospring.domain.OrderStatus;
import hello.hellospring.repository.OrderRepository;
import hello.hellospring.repository.OrderSearch;
import hello.hellospring.repository.order.query.OrderFlatDto;
import hello.hellospring.repository.order.query.OrderItemQueryDto;
import hello.hellospring.repository.order.query.OrderQueryDto;
import hello.hellospring.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * V1. 엔티티 직접 반환
     * 엔티티 노출 문제 발생
     * 트랜잭션 안에서 지연 로딩 필요
     * 양방향 연관관계 문제
     * N + 1 문제 발생
     * */
    @GetMapping("/api/v1/orders")
    public List<Order> orderV1(){
        List<Order> orders = orderRepository.findAllWithDSL(new OrderSearch());
        for (Order order : orders) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        return orders;
    }

    /**
     * V2. 엔티티를 DTO로 변환 (OrderDTO, OrderItemDTO)
     * DTO 내부의 또다른 엔티티도 DTO로 변환
     * N + 1 문제 발생 -> N = (2 * Order)* (1 * Member + 1 * Delivery + 2 * Item) = 10 -> 총 11번의 쿼리
     * */
    @GetMapping("/api/v2/orders")
    public WrapingDto orderV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                                .map(OrderDto::new).collect(toList());
        return new WrapingDto(collect);
    }

    /**
     * V3. 전체 Fetch join
     * N + 1 문제 해결 -> 총 1번의 쿼리
     * inner join으로 속성값을 연달아 붙여서 모든 속성값을 한 번에 조회
     * from 절에 fetch 구문 활용
     * 모든 속성값이 다 붙어 나오는 문제 발생
     * 1:N 관계에서 1 쪽의 데이터가 N만큼 중복된다 (어쨌든 join임으로) -> JPA의 "distinct" 활용
     * 재사용성은 높다
     * 놀라운 점은 v2와 SQL 부분만 다르고 나머진 똑같다 -> 유지보수, 생산성 대폭, 성능 대폭 증가
     * 치명적인 단점 : 1:다 관계에서 (fetch) join 하는 순간 paging이 불가능 해진다
     * 데이터가 1 x N이기 때문에 DB단에서 페이징할 수 없다 -> 그래서 메모리단에서 페이징하는데 이는 매우 위험한 상황
     * */
    @GetMapping("/api/v3/orders")
    public WrapingDto orderV3() {
        List<Order> orders = orderRepository.findAllWithItemFetch(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                                .map(OrderDto::new).collect(toList());
        return new WrapingDto(collect);
    }

    /***
     * V3.1 XToOne Fetch Join + XToMany where절 in 구문 (배치 사이즈 설정) + Collection Paging
     * N + 1 문제 해결 -> 1(root) + 1(collection) + 1(collection) -> 총 3번의 쿼리
     * 컬렉션의 개수만큼 쿼리 발생
     * 배치 사이즈 설정 -> hibernate.default_batch_fetch_size 설정 (100~1000 권장)
     * 프록시 객체의 id값을 저장하고 있다가 size 개수만큼 where절의 in 쿼리로 최적화해서 가져온다
     * 1:다 (컬렉션)을 이렇게 가져오면 좋다
     * 1:1도 가능하지만 1:1은 fetch로 가져오는 것이 효율이 더 좋다
     * 컬렉션은 페이징이 불가능하지만 이 방법은 페이징도 가능하다
     * 1 + N -> 1 + 1로 바뀐다
     * */
    @GetMapping("/api/v3.1/orders")
    public WrapingDto orderV3_batch_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                         @RequestParam(value = "limit", defaultValue = "100") int limit) {

        List<Order> orders = orderRepository.findAllWithOrderFetch(offset, limit, new OrderSearch());
        List<OrderDto> collect = orders.stream()
                                .map(OrderDto::new).collect(toList());
        return new WrapingDto(collect);
    }

    /**
     * V4. DTO로 직접 조회 + 컬렉션 부분 join
     * N + 1 문제 발생 -> N = 2 * OrderItem = 2 -> 3번의 쿼리
     * 결국 컬렉션의 Size만큼 또다시 N + 1 문제 발생
     * API 스펙에 맞춘 새로운 리포지토리 생성 (OrderQueryRepository)
     * */
    @GetMapping("/api/v4/orders")
    public WrapingDto orderV4(@RequestParam(value = "offset", defaultValue = "0") int offset,
                              @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return new WrapingDto(orderQueryRepository.findAllWithDto());
    }

    /**
     * V5. DTO로 직접 조회 + where절 in 구문 + Map 활용
     * N + 1 문제 해결 -> 1(root) + 1(collection) -> 총 2번의 쿼리
     * where절 in 구문으로 쿼리를 한 번만 날리고 메모리 위에서 Map 활용을 통한 값 세팅
     * Map을 이용한 매칭 성능 향상 -> O(1)
     * Entity Fetch Join (V3.1)과 비교했을 때 Trade-off가 발생
     * DB에서 Select 하는 양을 조절할 수 있다는 장점
     * 대부분의 쿼리 코드를 직접 작성해야 한다는 단점
     * */
    @GetMapping("/api/v5/orders")
    public WrapingDto orderV5(@RequestParam(value = "offset", defaultValue = "0") int offset,
                              @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return new WrapingDto(orderQueryRepository.findAllWithDto_opt());
    }

    /**
     * V6. DTO로 직접 조회 + 전체 join
     * N + 1 문제 해결 -> 총 1번의 쿼리
     * DB 단에서 전체를 join해서 쿼리 1번으로 해결 -> V3의 fetch join과 같은 개념
     * V3와 똑같은 문제 내재 (1쪽의 값이 N만큼 중복 -> paging 불가능)
     * 애플리케이션에서 추가 작업이 크다
     * 상황에 따라 V5 보다 더 느릴 수 도 있다
     * */
    @GetMapping("/api/v6/orders")
    public WrapingDto orderV6(@RequestParam(value = "offset", defaultValue = "0") int offset,
                              @RequestParam(value = "limit", defaultValue = "100") int limit) {

        List<OrderFlatDto> flats = orderQueryRepository.findAllWithDto_flat();
        List<OrderQueryDto> result = flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());

        return new WrapingDto(result);
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // delivery의 address
        private WrapingDto<List<OrderItemDto>> orderItems;
        // List<OrderItem>
        // 사실 OrderItem 자체도 엔티티이기 때문에 이것 또한 옳지 않다
        // 모든 엔티티와의 관계를 끊어야만 한다

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // Member Lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // Delivery Lazy 초기화
            orderItems = new WrapingDto<>(order.getOrderItems().stream()
                    .map(OrderItemDto::new).collect(toList())); // OrderItem Lazy 초기화
        }
    }

    //OrderItem에 대한 DTO
    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName(); // Item Lazy 초기화
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }
}
