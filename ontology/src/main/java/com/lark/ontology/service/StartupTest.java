package com.lark.ontology.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by skylark on 2017/4/15.
 */
@Service
public class StartupTest {

    private static String ont;

    @PostConstruct
    public static void otherWork() {
        ont=new String("hello");
        System.out.println("other work");
    }

    public static String get(){
        return ont;
    }
}