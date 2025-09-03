package com.kh.spring.board.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Reply {
	
	
	
	private int replyNo;
	private String replyContent;
	private int refBoardNo;
	private String replyWriter;
	private String createDate;
	private String status;
	
	
	
	

}
