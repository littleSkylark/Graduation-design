package com.lark.ontology.service;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Iterator;

/**
 * 初始化工作，读取本体到内存
 * Created by skylark on 2017/4/15.
 */
@Service
public class InitService {

    private static String ont;

    @PostConstruct
    private static void readOntModel() {
        OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM);
        ontModel.read("DQ.owl");

        String str;
        for (Iterator allClass = ontModel.listClasses(); allClass.hasNext();) {

            OntClass ontClass = (OntClass) allClass.next();
            if(!ontClass.isAnon()){
                String classStr = ontClass.toString();
                System.out.print("类URI：" + classStr + "   ");
                str = classStr.substring(classStr.indexOf("#") + 1);
                System.out.print("类名：" + str + "   ");
                
                if (!ontClass.listSuperClasses().hasNext()) {
                    System.out.println("类描述类型：无");
                } else {
                    for (Iterator supClasses = ontClass.listSuperClasses(); supClasses.hasNext();) {
                        OntClass supClass = (OntClass) supClasses.next();
                        String supClassStr = supClass.toString();
                        str = supClassStr.substring(supClassStr.indexOf("#") + 1);
                        System.out.print("类描述类型：subClassOf   ");
                        System.out.println("类描述值：" + str);


                    }
                }
            }
        }
    }

    public static String get(){
        return ont;
    }
}