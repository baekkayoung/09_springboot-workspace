package com.kh.spring.member.controller;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {

    @Autowired
    private MemberService mService;

    @Autowired // bean으로 등록되어있는가, Autiwured가 붙어있는가 이 2개ㄱ ㅏ있어야 주입
    private PasswordEncoder bcryptPasswordEncoder;


    @PostMapping("login.me")
    public ModelAndView loginMember(Member m, HttpSession session, ModelAndView mv ) {

        // 암호화 작업 후에 해야되는 과정
        // Member m userId 필드 : 사용자가 입력한 아이디
        // Member m userPwd 필드: 사용자가 입력한 비밀번호(평문)
        Member loginUser = mService.loginMember(m);
        // loginUser : 오로지 아이디만을 가지고 조회한 회원 객체
        // loginUser userPwd 필드 : db에 기록된 비번(암호문)


        // 매치(평문,암호문)-> t/f
        if(loginUser != null && bcryptPasswordEncoder.matches(m.getUserPwd(), loginUser.getUserPwd())) {
            // 로그인 성공
            session.setAttribute("loginUser", loginUser);
            mv.setViewName("redirect:/");

        } else {
            // 로그인 실패
            mv.addObject("errorMsg","로그인 실패");
            mv.setViewName("common/errorPage");
        }

        return mv;

    }

    @GetMapping("logout.me")
    public String logoutMember(HttpSession session) {
        session.invalidate();
        return "redirect:/";

    }

    @RequestMapping("enrollForm.me")
    public String enrollForm() {
        // /WEB-INF/views/     "member/memberEnrollForm"      .jsp 포워딩
        return "member/memberEnrollForm";

    }

    @PostMapping("insert.me")
    public String insertMember(Member m, Model model, RedirectAttributes redirectAttributes) {
        // 커맨드 방식. vo랑 jsp의 네임값이 동일해야 주입이 가능함!

//		System.out.println(m);
        // 1. 한글이 깨짐 -> 인코딩 설정 필요 -> 스프링에서 제공하는 인코딩 필터 등록 (web.xml에 등록)
        // 2. 나이를 입력하지 않았을 경우 "" 빈 문자열이 넘어오는데 int형 필드에 담을 수 없어서 400 에러 발생
        //	  => Member 클래스의 age 필드를 int형 --> String 형으로 변경
        // 3. 비밀번호가 사용자가 입력한 있는 그대로의 평문
        // 	  => Bcrypt 방식으로 암호화를 통해서 암호문으로 변경
        //	  => 1. spring 시큐리티 모듈에서 제공 => 라이브러리 필요 => pom.xml에 추가
        //	  => 2. bcryptPassWordEncoder라는 클래스를 빈으로 등록
        //	  => 3. web.xml에 spring-security.xml 파일을 pre-loading 할 수 있도록 작성

        // 암호화 작업 (암호문을 만들어내는 과정)
//		bcryptPasswordEncoder.encode(rawPassword); 평문!
        String encPwd =  bcryptPasswordEncoder.encode(m.getUserPwd());
//		System.out.println(encPwd);

        m.setUserPwd(encPwd); // MEmber객체에 userPwd에 평문이 아닌 암홈누으ㅜ로 변ㄱ여
        int result = mService.insertMember(m);

        if(result >0) { // 성공 => 메인페이지 url 재요청

            // 타임 리프에서는 세션 값을 자동으로 지우지 않기 때문에 1회성 메시지를 처리할 수 있는 것으로 사용
            redirectAttributes.addFlashAttribute("alertMsg", "회원가입에 성공했습니다.");
            // 한 번 세팅하고 삭제해줌. session.addAttribute랑 다름
            return "redirect:/";

        } else { // 실패 => 에러페이지
            model.addAttribute("errorMsg", "회원가입실패");
            return "common/errorPage";
        }

    }

    @GetMapping("myPage.me")
    public String myPage() {
        return "member/myPage"; // myPage.jsp
    }


    @PostMapping("update.me")
    public String updateMemer(Member m, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        int result = mService.updateMember(m);

        if(result > 0) {
            // db로 부터 수정된 회원정보를 다시 조회해와서
            // session에 loginUser라는 키값으로 덮어 씌워야 함!
//			Member updateMem = mService.loginMember(m); +
//			session.setAttribute("loginUser", updateMem); =
            session.setAttribute("loginUser", mService.loginMember(m));

            // alert 띄워줄 문구 세팅
            redirectAttributes.addFlashAttribute("alertMsg","성공적으로 회원정보가 수정되었습니다.");

            // 마이페이지 url 재요청
            return "redirect:myPage.me";


        }else {
            model.addAttribute("errorMsg","회원 정보 수정 실패");
            return "common/errorPage";
        }
    }



    @PostMapping("delete.me")
    public String deleteMember(String userId, String userPwd, HttpSession session, Model model) {

        Member loginUser = (Member)session.getAttribute("loginUser");
        userId = loginUser.getUserId();

        if(loginUser == null || !loginUser.getUserId().equals(userId)) {
            model.addAttribute("errorMsg", "잘못된 접근입니다.");
            return "common/errorPage";
        }

        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        if(!bcrypt.matches(userPwd, loginUser.getUserPwd())) {
            model.addAttribute("errorMsg", "비밀번호가 일치하지 않습니다.");
            return "common/errorPage";
        }

        int result = mService.deleteMember(userId);
        if(result > 0) {
            session.invalidate();
            return "redirect:/"; // 탈퇴 성공
        } else {
            model.addAttribute("errorMsg","회원삭제 실패");
            return "common/errorPage";
        }
    }

    @ResponseBody // 응답뷰를 찾게돼서 이상해짐. 내가 보내는 데이터는 응답뷰가 아니라 글자 자체의 데이터다 !!
    @RequestMapping("idCheck.me")
    public String idCheck(String checkId) {
        int count = mService.idCheck(checkId);

		/* 3항 연산자로 하면 더 가능
		if( count > 0 ) { // 이미 존재하는 아이디 => 사용 불가능(NNNNN)
			return "NNNNN";
		}else { // 사용 가능(NNNNY)
			return "NNNNY";
		}
		*/
        return count > 0 ? "NNNNN" : "NNNNY";
    }

}
