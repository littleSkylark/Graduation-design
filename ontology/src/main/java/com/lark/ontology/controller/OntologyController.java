package com.lark.ontology.controller;

import com.lark.ontology.dao.pojo.Ontology;
import com.lark.ontology.service.StartupTest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by skylark on 2017/4/15.
 */

@Controller
public class OntologyController {

    @RequestMapping("hello.do")
    @ResponseBody
    public Ontology test(){
        return new Ontology("hello world",new Date());
    }
}
