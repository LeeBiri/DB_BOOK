package bookDB.demo.Controller;


import bookDB.demo.Domain.Member;
import bookDB.demo.Service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MemberController {

    @Autowired
    private final MemberService memberService;

    public MemberController (MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "/member/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam Long id, @RequestParam String password, HttpSession session, Model model) {
        try {
            Member member = memberService.login(id, password);
            System.out.println("Login successful, member: " + member);
            session.setAttribute("loginMember", member);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "/member/login";
        }
    }

    @GetMapping("/register")
    public String registerForm() {
        return "/member/register";
    }

    @PostMapping("/register")
    public String register(Member member, Model model) {
        try {
            memberService.addMember(member);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "/member/register";
        }
    }

    @GetMapping("/deleteAccount")
    public String deleteAccountForm(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", loginMember);
        return "member/deleteAccount";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember != null) {
            try {
                memberService.deleteMember(loginMember.getId());
                session.invalidate();
                return "redirect:/";
            } catch (DataIntegrityViolationException e) {
                model.addAttribute("error", "대출 중이거나 예약 중인 경우 탈퇴할 수 없습니다.");
                return "member/deleteAccount";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


    @GetMapping("/memberInfo")
    public String memberInfo(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        System.out.println("Session loginMember: " + loginMember);
        if (loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", loginMember);
        return "member/memberInfo";
    }
}
