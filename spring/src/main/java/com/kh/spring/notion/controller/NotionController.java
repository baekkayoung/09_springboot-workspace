package com.kh.spring.notion.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kh.spring.notion.service.NotionServiceImpl;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

@Controller
public class NotionController {
	
	@Autowired // null 에러 뜸
	private NotionServiceImpl nService;
	
	@GetMapping("list.notion")
	public String selectNotion(Model model) {
		
		String dbData = nService.getDatabase();
		model.addAttribute("dbData",dbData);
		return "notion/notionListView";
	}
	
	@GetMapping("enrollForm.notion")
	public String enrollForm() {
		return "notion/notionEnrollForm";
	}
	
	@ResponseBody
	@PostMapping(value="add.notion", consumes ="application/json")
	// 나는 POST 요청일때만 실행하게 됨! 클라이언트가 json 데이터를 보내야만함!
	public String addNotion(@RequestBody Map<String, String> map){ // JSON 데이터를 받아오기 위해 쓰는 어노테이션
		// 데이터를 json으로 보내면 map으로 받을 수 있음. 
		// jsp 에서 바꿔서 보낸 것도 Controller를 map으로 받기 위해서 한거임.
		
		String title = map.get("title");
		String category = map.get("category");
		String writer = map.get("writer");

		
		HttpResponse<JsonNode> response = nService.addPage(category, title, writer);
		
		int status = response.getStatus();
		//System.out.println(status);
		
		if(status == 200) {
			return "success";
		}else {
			return "fail";
		}
	
	}

}
