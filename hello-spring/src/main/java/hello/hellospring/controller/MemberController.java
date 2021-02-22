package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.domain.Address;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class MemberController {

    private final MemberService memberService;

    //생성자가 호출될 때 스프링 컨테이너에 등록된 해당 의존성을 가지는 스프링 빈을 연결
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members/new")
    public String createForm(Model model) {
        // 모델을 넘기면 화면에서 객체에 접근할 수 있게 된다
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    // Vaild가 있으면 해당 클래스 내의 제약 어노테이션이 붙어 있는 변수를 검사한다 (NotEmpty)
    // 만약 에러가 있으면 튕기지 않고 BindingResult 객체에 에러코드가 담겨서 실행되고 이것으로 에러핸들링 가능
    public String create(@Valid MemberForm form, BindingResult result) { // Spring이 HTML의 키값을 보고 form의 setName을 호출
        // 이때 Post해서 받을 때 model의 key 값은 클래스명을 소문자로 바꾸고 가져다 쓴다 -> memberForm
        // 즉 html에서 날린 key 값과 같기 때문에 값을 가져다 쓸 수 있다
        if (result.hasErrors()){
            return "members/createMemberForm";
        }

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(new Address(form.getCity(), form.getStreet(), form.getZipcode()));
        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        // 지금은 그냥 Member 객체를 뿌려줬지만 로직이 복잡해지면 DTO 객체를 따로 만들어서 뿌리는 것이 좋다
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}

