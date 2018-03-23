package com.xhs.qa;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by zren on 2018/1/3.
 */
@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class Application extends SpringApplication {
  public static void main(String[] args){
    Application.run(Application.class,args);
  }
}
