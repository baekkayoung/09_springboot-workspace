package com.kh.spring.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/") // 우리의 contextPath
    public String index(){
        return "layout/layout"; // 포워딩이랑 마찬가지임
    }
}
