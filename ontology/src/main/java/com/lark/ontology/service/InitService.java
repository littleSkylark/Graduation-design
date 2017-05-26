package com.lark.ontology.service;

import com.lark.ontology.dao.pojo.Ontology;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
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

    private static Ontology ont;

    @PostConstruct
    public static void readOntModel() {
        String SOURCE = "http://www.semanticweb.org/lenovo/ontologies/2016/4/untitled-ontology-78";
        String NS = SOURCE + "#";
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        //ontModel.read("DQ.owl");
        OntDocumentManager dm = ontModel.getDocumentManager();
        dm.addAltEntry(SOURCE, "classpath:DQ.owl");
        ontModel.read(SOURCE, "RDF/XML");

        System.out.println(ontModel.getOntClass(NS + "Task"));

        //列出model中所包含的所有的类
        for (Iterator<OntClass> i = ontModel.listClasses(); i.hasNext(); ) {
            OntClass c = i.next();
            System.out.println("ontClass:" + c.getLocalName());
        }
    }

    public static Ontology get() {
        return ont;
    }
}