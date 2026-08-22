package net.likelion.bebc25.springboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/post/list"; // 게시글 목록으로 이동
    }
}
