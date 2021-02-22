package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

//@Repository // Component Scan에 사용
@RequiredArgsConstructor // final 요소 자동 생성자주입 해주는 lombok 스타일 -> 이 스타일 추천
public class ShopMemberRepository implements MemberRepository{

//    @PersistenceContext // Component Scan 방식 쓸 때 사용?
    private final EntityManager em;

    @Override
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        // PK일 경우 그냥 쓰면 된다
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        // PK가 아니면 JPQL를 써야한다
        // 컬럼과 테이블을 대상으로 하는 것이 아니라 객체로 한다
        // :key -> 나중에 setParameter에서 key값에 변수 바인딩
        // Member 라는 클래스와 매핑시키고 (as) m이라는 별칭으로 사용
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)     // 이때 "name"은 위의 :name 을 의미
                .getResultList();
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

}
