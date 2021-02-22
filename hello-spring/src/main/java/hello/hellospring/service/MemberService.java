package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Transactional(readOnly = true) // 조회만 하는 서비스에선 readOnly = true 사용 권장, 성능이 좋다
// 생성자주입 때 lombok을 쓸 거면 @AllArgsConstructor | @RequiredArgsConstructor(final 요소만)를 사용
public class MemberService {
    private final MemberRepository memberRepository;

    @Autowired // 생성자가 하나일 때는 생략가능
//  @RequiredArgsConstructor를 쓰면 생성자조차도 생략가능(final만) -> 권장
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원가입
     */
    // 쓰기를 해야하는 서비스는 readOnly = false, default이면서 우선권도 false가 높다
    @Transactional
    public Long join(Member member) {
        // 이름도 중복 안됨
        validateDuplicateMember(member); // 중복검사
        memberRepository.save(member);
        return member.getId();
    }

    /**
     *중복회원검사
     * */
    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다");
                });
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /**
     * 아이디로 개별 회원 조회
     */
    public Optional<Member> findOne(Long memberId){
        return memberRepository.findById(memberId);
    }

    @Transactional
    public void updateMember(Long id, String name) {
        Member member = memberRepository.findById(id).get();
        member.setName(name);
        // 변경감지를 이용하므로 자동 commit
    }
}
