package hello.hellospring;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import hello.hellospring.aop.TimeTraceAop;
import hello.hellospring.repository.*;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class SpringConfig {
/* Spring-Data-JPA 구문
    private final MemberRepository memberRepository;

    @Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
*/
// 기본 JPA 구문
    @PersistenceContext // 영속성, 1차 DB 캐시 사용
    private EntityManager em;

//    public SpringConfig(EntityManager em) {
//        this.em = em;
//    }


//    ** JdbcTemplate 쓸 때
//    private DataSource dataSource;
//
//    @Autowired
//    public SpringConfig(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

    @Bean
    public MemberService memberService() {
//        DI -> DI
        return new MemberService(memberRepository());
        // 밑에 있는 memberRepository 빈을 또 참조
    }

    @Bean
    public MemberRepository memberRepository() {
//        DI
//        return new MemoryMemberRepository();
//        return new JdbcMemberRepository(dataSource);
//        return new JdbcTemplateMemberRepository(dataSource);
//        return new JpaMemberRepository(em);
        return new ShopMemberRepository(em);
    }

    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }
}
