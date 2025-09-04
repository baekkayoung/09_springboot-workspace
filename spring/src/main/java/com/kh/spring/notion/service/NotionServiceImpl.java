package com.kh.spring.notion.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//import com.kh.spring.notion.config.NotionConfig;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;


@Service
public class NotionServiceImpl implements NotionService {

    @Value("${notion.token}")
    private String token;
    @Value("${notion.database.id}")
    private String database_id;
    
	
	/**
	 * 1. Notion 데이터베이스에서 데이터를 가져오는 메소드
	 */
	@Override
	public String getDatabase() {
		/*
		 * 1단계 : API URL 주소 만들기
		 * URL 구성 : 노션 API 기본 주소 + 우리가 접근하려는 특정 DB 아이디 + /query
		 * 결국은 우리가 접근하려는 노션 디비에 가서 데이터를 조회해줘! 라는 의미
		 * 
		 * 2단계 : HTTP 요청 보내기
		 * 우리는 Unirest 라는 라이브러리 사용할거임! (pom.xml에 추가)
		 * 노션은 데이터 조회도 post로 요청 (.post() 메서드 사용 예정)
		 * 
		 * 3단계 : 인증 정보 추가
		 * API에게 "나는 토큰을 가진 정당한 사용자야!" 라는 것을 증명하는 거임! (은행가서 신분증 보여주듯)
		 * Bearer: "이 토큰을 가진 사람이야..!" 라는 뜻!
		 * 이때 header()라는 메서드를 사용한다
		 * HTTP의 Header라는 공간은 웹에서 데이터를 주고받을 때 추가 정보를 담는 곳!
		 * 
		 * 예를 들어...
		 * 편지를 쓸 때 받는 이, 보내는 이, 우편번호 <= 이런거 추가정보임! 이런 걸 담는 공간(헤더)!
		 * 안녕? 잘 지내니? => 몸통! 중요한 내용 (바디)
		 * 
		 * 4단계 : 요청 보내기
		 * .asJson() 메소드를 통해서 데이터를 JSON 형태로 보낼거임!
		 * 
		 *  
		 */
		String url = "https://api.notion.com/v1/databases/" + database_id + "/query";
		
		HttpResponse<JsonNode> response = Unirest.post(url)
						.header("Authorization", "Bearer " + token) // 인증 정보 줄게. 베럴 뒤에 하나 띄우기
						.header("Notion-Version", "2022-06-28")
						.header("Content-Type","application/json")
						.asJson(); // 요청을 보냄
	
		return response.getBody().toPrettyString(); // 몸통 부분만 뽑아서 스트링으로 리턴하겠다
		// json 데이터를 보기 좋게 문자열로 변환하는 과정!
		
		
		
	}

	/**
	 * 2. 노션 데이터베이스에 페이지(데이터)를 추가하는 메소드
	 */
	@Override
	public HttpResponse<JsonNode> addPage(String category, String title, String writer) {
		
		String url = "https://api.notion.com/v1/pages";
		
		// 1. 부모 설정(어떤 데이터 베이스에다가 추가할건지?)
		JSONObject parent = new JSONObject(); // {}
		parent.put("database_id", database_id);
		
		// 2. properties 설정
		JSONObject properties = new JSONObject(); // {}
		
		// Notion에서 title 속성은 배열 형태의 "title:[{"text":{"content":{..}}}] 구조를 가져야 함 json 배열
		// 제목 (title이라는 속성은 JSONArray(배열) 필요) 만들기
		// page.제목.title[0].text.content = 이글의 제목
		JSONArray titleArray = new JSONArray(); // []
		JSONObject titleContent = new JSONObject(); // {}
		titleContent.put("text", new JSONObject().put("content", title)); 
		titleArray.add(titleContent); // "title:[{"text":{"content":{..}}}]
		properties.put("제목", new JSONObject().put("title", titleArray));
		
		// 구분(select 속성)
		// page.구분.select.name
		JSONObject selectObject = new JSONObject(); //{}
		selectObject.put("name", category);
		properties.put("구분", new JSONObject().put("select", selectObject));
		
		// 작성자(JSONArray 필요)
		// page.작성자.rich_text[0].text.content
		JSONArray writerArray = new JSONArray(); // []
		JSONObject writerContent = new JSONObject(); //{}
		writerContent.put("text", new JSONObject().put("content", writer));
		writerArray.add(writerContent);
		properties.put("작성자", new JSONObject().put("rich_text", writerArray));
		
		
		// 작성일(현재날짜)
		// page.작성일.date.start
		String dateOnly = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		JSONObject dateObject = new JSONObject(); // {}
		dateObject.put("start", dateOnly);
		properties.put("작성일", new JSONObject().put("date",dateObject));
		
		// 3. 최종 body
		JSONObject body = new JSONObject(); // {}
		body.put("parent", parent);
		body.put("properties", properties);
		
		// 4. HTTP로 요청
		HttpResponse<JsonNode> response = Unirest.post(url)
			.header("Authorization", "Bearer " + token)
			.header("Notion-Version", "2022-06-28")
			.header("Content-Type","application/json")
			.body(body)
			.asJson();
		
		return response;
	}

	private void properties(String string, Object put) {
	}

}
