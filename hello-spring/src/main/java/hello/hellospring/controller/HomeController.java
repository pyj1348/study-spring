package hello.hellospring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

//    Logger logger = LoggerFactory.getLogger(getClass()); // 어노테이션으로 대체

    @GetMapping("/")
    public String home() {
        log.info("home controller");
        return "home";
    }
}
