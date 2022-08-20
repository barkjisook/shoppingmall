package com.shop.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

@Value("${uploadPath}")  //application.properties에설정한 uploadpath프로퍼티 값을 읽어온다
    String uploadPath;

@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
    registry.addResourceHandler("/images/**")
            .addResourceLocations(uploadPath); //로컬컴퓨터에 저장된 파일을 읽어올root경로로 설정
}





}
