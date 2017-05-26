package ontology.service;


import com.lark.ontology.service.InitService;

/**
 * 初始化工作，读取本体到内存
 * Created by skylark on 2017/4/15.
 */
public class InitServiceTest {

    public static void main(String[] args) {
        InitService.readOntModel();
    }
}