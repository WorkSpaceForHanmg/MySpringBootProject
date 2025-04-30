package com.basic.myspringboot.runner;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.basic.myspringboot.property.MybootProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;

@Component()
public class MyRunner implements ApplicationRunner {
    @Value("${myboot.name}")
    private String name;

    @Value("${myboot.age}")
    private int age;

    @Autowired
    private Environment environment;

    @Autowired
    private MybootProperties properties;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("${myboot.name} = " + name);
        System.out.println("${myboot.age} = " + age);
        System.out.println("${myboot.fullName} = " + environment.getProperty("myboot.fullName"));

        System.out.println("MybootProperties getName() "+ properties.getName());
        System.out.println("MybootProperties getAge() "+ properties.getAge());
        System.out.println("MybootProperties getFullName()() "+ properties.getFullName());

        //foo 라는 VM 아규먼트가 있는지 확인
        System.out.println("VM 아규먼트 foo : " + args.containsOption("foo"));
        //bar 라는 Program 아규먼트가 있는지 확인
        System.out.println("Program 아규먼트 bar : " + args.containsOption("bar"));

        /*
            Iterable forEach(Consumer)
            Consumer는 함수형 인터페이스 void accept(T t)
            Consumer의 추상메서드를 오버라이딩 하는 구문을 람다식으로 작성
         */

        //Program 아규먼트 목록 출력
        args.getOptionNames()   //set<String>
                .forEach( name -> System.out.println(name));

    }//run
}//class
