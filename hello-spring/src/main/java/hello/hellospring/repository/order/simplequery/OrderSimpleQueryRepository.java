package hello.hellospring.repository.order.simplequery;

import hello.hellospring.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleDto> findAllWithDTO(OrderSearch orderSearch) {
        return em.createQuery("select new hello.hellospring.repository.order.simplequery.OrderSimpleDto(o.id, m.name, o.orderDate, o.status, d.address)"
                + " from Order o"
                + " join o.member m"
                + " join o.delivery d", OrderSimpleDto.class)
                .getResultList();

    }
}
