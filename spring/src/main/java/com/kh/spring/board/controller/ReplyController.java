package com.kh.spring.board.controller;

import com.google.gson.Gson;
import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ReplyController {

    @Autowired
    private BoardService bService;

    /*
        @RestController 두가지 어노테이션의 조합임!
        => @Controller : 컨트러 역할 +  @ResponseBody : 메소드가 반환하는 값을 응답 본문에 담아라! (이게 없으면 주는 데이터를 스프링은 view 이름으로 인식!)

        최근 백엔드는 데이터만 제공하고, 프론트엔드는(클라이언트)는 그 데이터를 받아서 화면에 그리는 구조가 일반적임!
        이를 API 서버라고 부름!

        @Restcontroller가 바로 이런 API 서버를 만드는데 최적화 된 어노테이션이다!
        심지어 데이터도 알아서 JSON으로 변경해줘서 GSON 사용 안 해도 됨!


        * 기존 방식 (JSP / 서블릿)은 음식 주문을 받았을 때 "음식 + 예쁜 그릇 + 식탁보"를 전부 차려서하는 배달 방식
        * @RestController는 "음식 재료(JSON)"만 깔끔하게 포장해서 배달하고, 손님(클라이언트)이 알아서 예쁘게 차려먹는 방식

        서버는 데이터 제공이라는 핵심 역할에만 집중할 수 있고,
        클라이언트는 그 데이터를 가지고 화면을 다양한 형태로 자유롭게 만들 수 있음!
        그래서 현대 웹 개발에서는 @RestController가 핵심적인 역할을 한다!

        */



    @GetMapping(value="rlist.bo")
    public ArrayList<Reply> ajaxSelectReplyList(int bno) {

//        ArrayList<Reply> list = bService.selectReplyList(bno);
//        return list;

        return bService.selectReplyList(bno); // 바로 list를 리턴해도 알아서 json 으로 줌!
    }


    @RequestMapping(value="rinsert.bo")
    public String ajaxInsertReply(Reply r) {
        int result = bService.insertReply(r);
        return result > 0 ? "success" : "fail";

    }




}
