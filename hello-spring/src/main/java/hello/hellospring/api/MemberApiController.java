package hello.hellospring.api;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Controller @ResponseBody
// @RestController // 위의 두개를 합친 어노테이션
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> memberV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public WrapingDto memberV2(){
        List<Member> members = memberService.findMembers();
        List<MemberDto> collect = members.stream().map(m -> new MemberDto(m.getName())).collect(Collectors.toList());
        return new WrapingDto(collect);
        // list를 바로 반환하면 배열 []에 감싸져서 반환이 되기 때문에 유연성이 떨어진다.
    }


    @PostMapping("/api/v1/members")
    // @RequestBody 는 JSON으로 온 데이터를 member 객체에 다 매핑
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    // @RequestBody 는 JSON으로 온 데이터를 request 객체에 다 매핑
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request){

        memberService.updateMember(id, request.getName());
        // 변경과 조회 SQL은 구분해서 사용하는 것이 좋다
        Member member = memberService.findOne(id).get();
        return new UpdateMemberResponse(member.getId(), member.getName());
    }

    @Data // JSON 요청의 응답으로 보낼 데이터 클래스
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data // 별도의 DTO
    static class CreateMemberRequest {
        // 여기의 이름으로 JSON을 주고 받기 때문에 엔티티의 변수이름이 변해도 API 스펙이 바뀌지 않는다
        @NotEmpty // Validation을 API DTO 계층에서 할 수 있다
        private String name;

    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

}
