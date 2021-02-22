package hello.hellospring.repository;

import hello.hellospring.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item) {
        // null -> jpa에 저장하기 전까지 ID값이 없음 -> 방금 생성된 객체, 이미 등록된 객체 X
        // not null -> 이미 등록된 개체를 가져온 것 -> 업데이트를 의미
        if (item.getId() == null) {
            em.persist(item);
        } else {
            System.out.println("---------------");
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}

