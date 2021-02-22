package hello.hellospring.api;

import hello.hellospring.domain.Order;
import hello.hellospring.repository.OrderRepository;
import hello.hellospring.repository.OrderSearch;
import hello.hellospring.repository.order.simplequery.OrderSimpleDto;
import hello.hellospring.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * XToOne (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * V1. 엔티티 직접 반환
     * 엔티티 노출 문제 발생
     * jackson 라이브러리는 기본적으로 프록시 객체를 json으로 어떻게 생성해야 하는지 모름 -> 예외 발생
     * LAZY 프록시 객체 조회 문제 발생 -> Hibernate5Module 모듈 등록 -> 프록시 객체는 null 값으로 출력
     * 양방향 관계 문제 발생 -> @JsonIgnore
     * */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        for (Order order : orders) {
            order.getMember().getName(); // Lazy 강제 초기화, 이때 DB에 쿼리 날림, 로딩
            order.getDelivery().getAddress(); // Lazy 강제 초기화, 이때 DB에 쿼리 날림, 로딩
        }
        return orders;
    }

    /**
     * V2. 엔티티를 DTO로 변환 (OrderSimpleDTO)
     * N + 1 문제 발생 -> N = (2 * Order) * (1 * Member + 1 * Delivery) = 4 -> 총 5번의 SQL
     * */
    @GetMapping("/api/v2/simple-orders")
    public WrapingDto ordersV2() {
        // 만약 같은 id라면 영속성 컨텍스트를 찌르기 때문에 조금 줄어들 순 있겠으나 그래도 최적은 아니다

        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderSimpleDto> collect = orders.stream().map(OrderSimpleDto::new).collect(toList());

        return new WrapingDto(collect);
    }

    /**
     * V3. Fetch join으로 쿼리수 최적화
     * inner join으로 속성값을 연달아 붙여서 모든 속성값을 한 번에 조회
     * from 절에 fetch 구문 활용
     * 모든 속성값이 다 붙어 나오는 문제 발생
     * 재사용성은 높다
     * */
    @GetMapping("/api/v3/simple-orders")
    public WrapingDto ordersV3() {
        // Fetch는 JPA 문법 : inner join으로 속성값 연달아 붙여서 한 번만 조회

        List<Order> orders = orderRepository.findAllWithOrderFetch(new OrderSearch());
        List<OrderSimpleDto> collect = orders.stream().map(OrderSimpleDto::new).collect(toList());

        return new WrapingDto(collect);
    }


    /**
     * V4. DTO로 직접 조회
     * 애초에 Query select 반환값을 DTO로 받기
     * inner join으로 속성값을 연달아 붙이지만 원하는 속성값들만 조회
     * select 절에 new 구문 활용
     * 쿼리도 최적화하고 필요한 속성들만 DTO 설정을 통해 가져올 수 있음
     * 재사용성은 떨어진다, API 스펙에 맞춘 코드가 리포지토리에 들어감
     * -> 이 방법이 필요하다면 이것만을 위한 새로운 전용 리포지토리를 새로 만들자 (OrderSimpleQueryRepository)
     * */
    @GetMapping("/api/v4/simple-orders")
    public WrapingDto ordersV4() {
        List<OrderSimpleDto> orders = orderSimpleQueryRepository.findAllWithDTO(new OrderSearch());

        return new WrapingDto(orders);
    }

}
