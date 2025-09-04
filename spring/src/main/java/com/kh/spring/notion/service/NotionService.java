package com.kh.spring.notion.service;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

public interface NotionService {
	
	// 1. 경조사 게시판 조회
	String getDatabase();
	
	// 2. 경조사 게시판 등록
	HttpResponse<JsonNode> addPage(String category, String title, String writer);
	

	

}
