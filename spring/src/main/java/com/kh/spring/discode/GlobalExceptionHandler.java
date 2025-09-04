package com.kh.spring.discode;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // 여러 컨트롤러에서 발생하는 예외를 전역으로 다 처리하겠다는 의미
public class GlobalExceptionHandler {

    // 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e){ // 오류가 들어옴 / TestController에서 일부러 오류 발생시킴
        // 에러 머시지를 Discord로 전송
        DiscodeNorifier.sendError(e.toString()); // sendError() 실행

        return "common/errorPage";
    }

}
