package com.kh.spring.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {



    @Value("${file.upload-dir}") // application.properites 파일에서 키값 찾아서 value 주입!
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry ){
        registry.addResourceHandler( "/uploadFiles/**") // 브라우저에서 접근할 url 패턴을 열면
                .addResourceLocations("file:///" + uploadDir); // 브라우저에서 i파일 이렇게 떠있는게 file:/// 이랑 같은 거임 파일이란 의미. // 실제 os경로


    }
    // 요청 url : localhost:8083/uploadFiles/파일명.png을 보내도
    // 실제로는    localhost:8083/uploadFiles/ 가 요청되는 것이 아니라
    //            c:/09_spring-boot/spring/uploadFiles 경로로 요청이 된다. = uploadFiles


}
