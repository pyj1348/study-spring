package hello.hellospring.service;

import hello.hellospring.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // Spring 위에서 테스트할 거면 써야함
@Transactional // Test에서는 DB에 Commit하지 않는 모드 -> 테스트에 용이하다
class MemberServiceIntegrationTest {

    @Autowired MemberService memberService;
    @Test
    void 회원가입() {
        // given
        Member member = new Member();
        member.setName("spring");

        // when
        Long saveId = memberService.join(member);

        // then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 회원 () throws Exception {
        //given
        assertThat("A").isEqualTo("A");
        //when

        //then
    }

    @Test
    public void 중복_회원_예외(){
        // given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // when
        memberService.join(member1);

        // then
        // 에러클래스가 같은지 아닌지
        assertThrows(IllegalStateException.class, () -> memberService.join(member2));


        /* 메시지 검사
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다");
         */

    }

    @Test
    public void 영속성테스트(){
        // given
        Member member = new Member();
        member.setName("Jin");
        memberService.join(member);

        // when
        Member member2 = memberService.findOne(member.getId()).get();

        // then
        assertThat(member).isEqualTo(member2);
    }
}