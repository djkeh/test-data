package uno.fastcampus.testdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserAccountController {

    @GetMapping("/my-account")
    public String myAccount(Model model) {
        model.addAttribute("nickname", "홍길동");
        model.addAttribute("email", "홍길동@email.com");

        return "my-account";
    }

}
