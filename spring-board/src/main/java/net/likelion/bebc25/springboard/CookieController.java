package net.likelion.bebc25.springboard;

import jakarta.servlet.http.HttpServletResponse;
import net.likelion.bebc25.springboard.member.dto.MemberDto;
import net.likelion.bebc25.springboard.member.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Controller
@RequestMapping("/cookie")
public class CookieController {

    private MemberService memberService;
    public CookieController(MemberService memberService){
        this.memberService = memberService;
    }

    @GetMapping("/create") // http://localhost:8080/cookie/create?memberId=3 응답
    public String createCookie(@RequestParam String memberId, HttpServletResponse response){
        ResponseCookie memberIdCookie = ResponseCookie.from("memberId", memberId)
                .maxAge(Duration.ofHours(1))
                .path("/") // 경로 설정
                .httpOnly(true) // 자바스크립트 접근 불가
                .build();

        ResponseCookie usernameCookie = ResponseCookie.from("username", "haru")
                .maxAge(Duration.ofHours(1))
                .path("/") // 경로 설정
                .httpOnly(true) // 자바스크립트 접근 불가
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, memberIdCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, usernameCookie.toString());

        return "redirect:/";
    }

    @GetMapping("/view") // http://localhost:8080/cookie/view 응답
    @ResponseBody
    public String viewCookie(@CookieValue(name = "memberId", required = false) Integer memberId,
                             @CookieValue(name = "username", required = false) String username){
        if(memberId == null){
            return "<p>memberId 쿠키가 없습니다.</p>";
        }

        MemberDto memberInfo = memberService.getMember(memberId);
        return """
                <ul>
                    <li>번호: %s</li>
                    <li>이름: %s</li>
                    <li>권한: %s</li>
                </ul>
                """.formatted(memberInfo.getId(), memberInfo.getUsername(), memberInfo.getRole());
    }

    @GetMapping("/delete") // http://localhost:8080/cookie/delete 응답
    public String deleteCookie(){
        return "redirect:/";
    }
}
