package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello") // String은 url을 의미 get(url) 방식
    // 즉, hello.html에 대한 controller
    public String hello(Model model){
        model.addAttribute("data", "hello!!");
        // attributeName은 html에서 가져다 쓰는 이름 (key 값)
        return "hello-template";
        /*
        * 이때 hello는 templates의 html 파일 이름을 의미 > resources:templates/(ViewName).html
        * Controller가 반환하면 Spring이 받아서 viewResolver를 호출 후에 html 템플릿엔진과 연결
        * 동적 컨텐츠가 포함되어 있다면 템플릿 엔진이 이것을 변환 후에 반환
        * ctrl + 클릭하면 이동가능
        */
    }

    @GetMapping("hello-mvc")
    public String helloMVC(@RequestParam("name") String name, Model model){
        model.addAttribute("name", name);
        return "hello-mvc-template";
    }

    @GetMapping("hello-string")
    @ResponseBody // html의 body부분에 데이터를 직접 넣어주겠다
    public String helloString(@RequestParam("name") String name){
        return "hello " + name;
    }

    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloAPI(@RequestParam("name") String name){
        Hello hello = new Hello();
        hello.setName(name);
        return hello;

    }

    private static class Hello{
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
