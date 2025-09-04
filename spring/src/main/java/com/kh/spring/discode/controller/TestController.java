package com.kh.spring.discode.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test-error")
    public String testError(){
        // 강제로 예외를 발생시켜보는 테스트
        throw new RuntimeException("테스트용 강제 예외 발생! ");
    }
}
