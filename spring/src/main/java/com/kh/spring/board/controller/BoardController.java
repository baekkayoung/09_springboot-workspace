package com.kh.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import javax.websocket.Session;

import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.log.UserDataHelper.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.kh.spring.board.model.service.BoardServiceImpl;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.Reply;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BoardController {

    @Value("${file.upload-dir}")
    private String uploadDir;

	@Autowired
	private BoardServiceImpl bService;


	// 메뉴바 클릭시 /list.bo (기본적으로 1번 페이징 요청)
	// 페이징바 클릭시 /list.bo?cpage=요청하는 페이지수
	/*
	@RequestMapping("list.bo")
	public String selectList(@RequestParam(value="cpage", defaultValue="1")int currentPage, Model model) {
		
		int listCount = bService.selectListCount();
		
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, 10, 5);
		
		ArrayList<Board> list = bService.selectList(pi);
		
		model.addAttribute("pi",pi);
		model.addAttribute("list",list);
		
		// 포워딩할 뷰 (web-inf/views/board/boardListView.jsp)
		return "board/boardListView";
		
	}
	*/
	@GetMapping("enrollForm.bo")
	public String enrollForm() {
        return "board/boardEnrollForm";
	}

	@GetMapping("list.bo")
	public ModelAndView selectList(@RequestParam(value="cpage", defaultValue="1")int currentPage, ModelAndView mv) {

		int listCount = bService.selectListCount();

		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, 10, 5);
		ArrayList<Board> list = bService.selectList(pi);

		mv.addObject("pi",pi)
		  .addObject("list",list)
		  .setViewName("board/boardListView");

		return mv;

	}

	@PostMapping("insert.bo")
	public String insertBoard(Board b, MultipartFile upfile, Model model, RedirectAttributes redirectAttributes)  {

		if(!upfile.getOriginalFilename().equals("")) {

			String changeName = saveFile(upfile);

			b.setOriginName(upfile.getOriginalFilename());
			b.setChangeName(changeName);
		}

		int result = bService.insertBoard(b);

		if (result > 0 ) {
            redirectAttributes.addFlashAttribute("alertMsg","성공적으로 게시글이 등록되었습니다.");
//			session.setAttribute("alertMsg", "게시글이 성공적으로 등록되었습니다"); 이전버전
			return "redirect:list.bo";

		} else {
			model.addAttribute("errorMsg", "게시글 등록 실패");
			return "common/errorPage";
		}

	}

	@GetMapping("detail.bo")
	public String selectBoard(int bno, Model model) {

		int result = bService.increaseCount(bno);

		if(result > 0) { // 조회수 증가 성공 시
	        Board b = bService.selectBoard(bno);
	        model.addAttribute("b", b);
	        return "board/boardDetailView";

	    } else { // 조회수 증가 실패 시
	        model.addAttribute("errorMsg", "게시글 상세조회 실패"); // ModelAndView로 해도 됨
	        return "common/errorPage";
	    }
	}

	@RequestMapping("delete.bo") // 첨부파일도 삭제
	public String deleteBoard(int bno, String changeName, Model model, RedirectAttributes redirectAttributes) {

		int result = bService.deleteBoard(bno); // jsp에서 name값이 bno 4

		if(result > 0 ) {

            if(!changeName.equals("")){
                File oldFile= new File(uploadDir, changeName);
                if(oldFile.exists()){
                    oldFile.delete();
                }

			}
            redirectAttributes.addFlashAttribute("alert","게시글이 성공적으로 삭제되었습니다.");
            return "redirect:list.bo";
		}else { // 삭제 실패
			model.addAttribute("errorMsg","게시글 삭제 실패");
			return "common/errorPage";

		}
	}

	@PostMapping("updateForm.bo") // Model -> 기존데이터를 가지고 화면에 가야 가지고올 수 있으니까 담아서가려고
	public String updateForm(int bno, Model model) {
		model.addAttribute("b", bService.selectBoard(bno));// b라는 키값에 정보가 담김
		return  "board/boardUpdateForm";

	}
	
	@PostMapping("update.bo") // 인젝션 해주려면 jsp 네임이랑 여기랑 이름이 똑같아야함
	public String updateBoard(Board b, MultipartFile reupfile, Model model, RedirectAttributes redirectAttributes) {
		
		// 새로 넘어온 첨부파일이 있을 경우
		if(!reupfile.getOriginalFilename().equals("")) { // 첨부를 안 했다는 의미
		
			// 기존에 첨부파일이 있었을 경우 => 기존의 첨부파일을 지워야 함
			if(b.getChangeName() != null){
                File oldFile= new File(uploadDir, b.getChangeName()); // 우리 경로에 찾아가서 차즘
                if(oldFile.exists()){
                    oldFile.delete();
                }
            }


			// 방금 새로 넘어온 첨부파일을 서버에 업로드 시켜야 함
			String changeName = saveFile(reupfile);
			
			// b에 새로 넘어온 첨부파일의 원본명, 첨부파일에 대한 체인지네임에 저장경로 담기
			b.setOriginName(reupfile.getOriginalFilename());
			b.setChangeName(changeName);
			
		}
		/*
		 * b 에 boardNo, boardTitle, boardContent 무조건 담겨 있음
		 * 
		 * 1. 새로 첨부된 파일이 x, 기존 첨부 파일 x
		 * 	  => originName : null / chagneName : null
		 *    
		 * 2. 새로 첨부된 파일 x, 기존 첨부 파일 o
		 * 	  => originName : 기존파일원본명, changeName: 기존파일경로
		 * 
		 * 3. 새로 첨부된 파일 o, 기존 첨부 파일 x
		 * 	  => 새로 전달된 파일 서버에 업로드
		 *    => originName : 새로전달된파일원본명, changeName : 새로전달된파일경로
		 * 
		 * 4. 새로 첨부된 파일 o, 기존 첨부 파일 o
		 * 	  => 기존의 파일 삭제 , 새로 전달된 파일 서버에 업로드
		 *    => originName : 새로전달된파일원본명, changeName : 새로전달된파일경로
		 */
		
		int result = bService.updateBoard(b);
		
		if(result > 0) {
			// 수정 성공 => 상세페이지(detail.bo)로 url 재요청 -> 한 번 가서 봐봐 => bno라는 변수를 넘겨야 하겠네
			// detail.bo?bno=해당게시글번호 url 재요청 그러니까 bno 있어야겠찌
			redirectAttributes.addFlashAttribute("alert","성공적으로 게시글이 수정되었습니다");
			return "redirect:detail.bo?bno=" + b.getBoardNo();
			
		} else {
			// 수정 실패 => 에러메세지
			model.addAttribute("errorMsg", "게시글 수정 실패");
			return "common/errorPage";
		}
		
	}	
	
	@ResponseBody
	@RequestMapping(value="rlist.bo",produces="application/json; charset=utf-8")
	public String ajaxSelectReplyList(int bno) {
		ArrayList<Reply> list = bService.selectReplyList(bno);
		
		return new Gson().toJson(list);
	}
	
	@ResponseBody
	@RequestMapping(value="rinsert.bo")
	public String ajaxInsertReply(Reply r) {
		
		int result = bService.insertReply(r);
		
		return result > 0 ? "success" : "fail";
		// 응답뷰 리턴인지 데이터인지? 데이터면 바디추가
		
	}
	
	@ResponseBody
	@RequestMapping(value="topList.bo", produces="application/json; charset=utf-8")	
	public String ajaxTopBoardList() {
		ArrayList<Board> list = bService.selectTopBoardList();
//		System.out.println(new Gson().toJson(list));
		return new Gson().toJson(list);
	
	} 
	
	
	
	// 현재 넘어온 첨부파일 그 자체를 서버의 폴더에 저장시키는 역할
	// uploadFiles에 저장시키는
	
	public String saveFile(MultipartFile upfile) {
		
	String originName = upfile.getOriginalFilename();
	
	//"20250825123555"
	String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); // "2025082132555"
	int ranNum = (int)(Math.random() * 90000 + 10000); // 85236 5자리 랜덤값
	String ext = originName.substring(originName.lastIndexOf(".")); // ".png"; 이 셋 결합
	
	String changeName = currentTime + ranNum + ext;
	
	// 업로드 경로 준비
    File savePath = new File(uploadDir);
    if(!savePath.exists()){
        savePath.mkdir(); // 폴더 없으면 생성! 폴더가 이미 있으면 지나감.
    }

	try {
		upfile.transferTo(new File(savePath,changeName)); // 바뀐 파일명으로 변경
	} catch (IllegalStateException | IOException e) {
		e.printStackTrace();
	} //트라이캐치
		return changeName;
	}
	
	
}

