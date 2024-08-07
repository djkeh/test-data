package uno.fastcampus.testdata.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uno.fastcampus.testdata.dto.security.GithubUser;

@Controller
public class UserAccountController {

    @GetMapping("/my-account")
    public String myAccount(
            @AuthenticationPrincipal GithubUser githubUser,
            Model model
    ) {
        model.addAttribute("nickname", githubUser.name());
        model.addAttribute("email", githubUser.email());

        return "my-account";
    }

}
