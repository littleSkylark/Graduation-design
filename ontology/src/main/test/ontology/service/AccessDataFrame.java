package ontology.service;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.function.library.min;
import org.apache.jena.util.iterator.ExtendedIterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;

//import com.jgoodies.forms.layout.FormLayout;
//import com.jgoodies.forms.layout.ColumnSpec;
//import com.jgoodies.forms.layout.FormSpecs;
//import com.jgoodies.forms.layout.RowSpec;
//
//import net.miginfocom.swing.MigLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import java.awt.Panel;
import javax.swing.JEditorPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JList;

public class AccessDataFrame extends JFrame {

    GetConn connection = new GetConn();
    Connection conn = null;

    private JPanel contentPane;
    private JLabel lblNewLabel;
    private JLabel lblNewLabel_1;
    private JComboBox<String> comboBox;
    private JComboBox<String> comboBox_1;
    private JTextArea textArea_1;
    private JTextArea textArea;

    //构建本体模型
    public OntModel getontModel() {
        OntModel myOntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
        myOntModel.read("DQ.owl");
        OntDocumentManager ontDocumentManager = myOntModel.getDocumentManager();
        return myOntModel;
    }

    public OntClass getOntClass(String ontClassName) {
        for (Iterator<OntClass> i = getontModel().listClasses(); i.hasNext(); ) {
            OntClass oClass = i.next();
            if (oClass.isAnon())//是匿名类
                continue;
            if (oClass.getLocalName().equals(ontClassName)) {
                return oClass;
            }
        }
        return null;
    }

    //获取某类的实例
    public Individual getindividual(OntClass o) {
        ExtendedIterator<Individual> ex = getontModel().listIndividuals(o);
        while (ex.hasNext()) {
            Individual in = ex.next();
            return in;

        }
        return null;
    }

    /*
     * 定义查询数据库中的相应的字段
     */
    public List select(String tablename, String columnname) {
        conn = connection.getCon();
        List list = new ArrayList<String>();
        try {
            Statement statement = conn.createStatement();
            String sql = "select " + columnname + " from " + tablename;
            ResultSet rest = statement.executeQuery(sql);
            while (rest.next()) {
                if (rest == null) {
                    rest.deleteRow();
                } else {
                    list.add(rest.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
     * 非空约束
     */
    public int notnullcon() {
        String tablename = comboBox_1.getSelectedItem().toString();
        String columnname = comboBox.getSelectedItem().toString();
        AccessDataFrame aFrame = new AccessDataFrame();
        System.out.println(aFrame.select(tablename, columnname).toString());
        int allsize = aFrame.select(tablename, columnname).size();
        int nullsize = 0;
        for (int i = 0; i < allsize; i++) {
            if (aFrame.select(tablename, columnname).get(i) == null) {
                nullsize = nullsize + 1;
            }
        }
        return nullsize;
    }


    //获取实例的最小值
    public int getindividual(String indiname) {
        int minvalue = 0;
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("MIN_VALUE")) {
                        System.out.println("属性名：" + oProperty.getLocalName().toString());
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            String indprovalue = allindi.getPropertyValue(oProperty).toString();
                            minvalue = Integer.parseInt(indprovalue);
                            System.out.println("属性值：" + indprovalue);
                        }
                    }
                }
            }
        }
        return minvalue;
    }

    //获取值域的最大值
    public int getmaxvalue(String indiname) {
        int maxvalue = 0;
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("MAX_VALUE")) {
                        System.out.println("属性名：" + oProperty.getLocalName().toString());
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            String indprovalue = allindi.getPropertyValue(oProperty).toString();
                            maxvalue = Integer.parseInt(indprovalue);
                            System.out.println("属性值：" + indprovalue);
                        }
                    }
                }
            }
        }
        return maxvalue;
    }

    //获取逻辑约束的比较字段:大于
    public String getmorethancom(String indiname) {
        String column = "";
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("HAS_MORE_THAN")) {
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            String columnname = allindi.getPropertyValue(oProperty).toString();
                            column = columnname.substring(columnname.indexOf("#") + 1);
                        } else {
                            column = null;
                        }
                    }
                }
            }
        }
        return column;
    }

    //获取逻辑约束的比较字段:小于
    public String getlessthancom(String indiname) {
        String column = "";
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("HAS_LESS_THAN")) {
                        System.out.println("属性名：" + oProperty.getLocalName().toString());
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            column = allindi.getPropertyValue(oProperty).toString();
                        }
                    }
                }
            }
        }
        return column.substring(column.indexOf("#") + 1);
    }

    //获取存在一致性
    public String getexitcom(String indiname) {
        String column = "";
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    //System.out.println(oProperty);
                    if (oProperty.getLocalName().toString().equals("HAS_EXIST")) {
                        //System.out.println("属性名："+oProperty.getLocalName().toString());
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            column = allindi.getPropertyValue(oProperty).toString();
                            //System.out.println(column);
                        }
                    }
                }
            }
        }
        return column.substring(column.indexOf("#") + 1);
    }

    //获取逻辑一致性
    public String getlogiccon(String indiname) {
        String column = "";
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("HAS_LOGIC_CON")) {
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            String columnname = allindi.getPropertyValue(oProperty).toString();
                            column = columnname.substring(columnname.indexOf("#") + 1);
                        } else {
                            column = null;
                        }
                    }
                }
            }
        }
        return column;
    }

    //获取等值约束
    public String getequone(String indiname) {
        String column = "";
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("HAS_EQU_ONE")) {
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            String columnname = allindi.getPropertyValue(oProperty).toString();
                            column = columnname.substring(columnname.indexOf("#") + 1);
                        } else {
                            column = null;
                        }
                    }
                }
            }
        }
        return column;
    }

    public String getequtwo(String indiname) {
        String column = "";
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("HAS_EQU_TWO")) {
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            String columnname = allindi.getPropertyValue(oProperty).toString();
                            column = columnname.substring(columnname.indexOf("#") + 1);
                        } else {
                            column = null;
                        }
                    }
                }
            }
        }
        return column;
    }

    //获取等值一致性约束
    public String getequconone(String indiname) {
        String column = "";
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("HAS_EQU_CON_ONE")) {
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            String columnname = allindi.getPropertyValue(oProperty).toString();
                            column = columnname.substring(columnname.indexOf("#") + 1);
                        } else {
                            column = null;
                        }
                    }
                }
            }
        }
        return column;
    }

    public String getequcontwo(String indiname) {
        String column = "";
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                //列出实例所属的属性
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("HAS_EQU_CON_TWO")) {
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            String columnname = allindi.getPropertyValue(oProperty).toString();
                            column = columnname.substring(columnname.indexOf("#") + 1);
                        } else {
                            column = null;
                        }
                    }
                }
            }
        }
        return column;
    }

    //获取特定实例所属的类
    public String getindiclass(String indiname) {
        String indclass = null;
        for (Iterator<Individual> indi = getontModel().listIndividuals(); indi.hasNext(); ) {
            Individual allindi = indi.next();
            if (allindi.getLocalName().equals(indiname)) {
                OntClass indiclass = allindi.getOntClass();
                for (Iterator<OntProperty> allobjpro = indiclass.listDeclaredProperties(); allobjpro.hasNext(); ) {
                    OntProperty oProperty = allobjpro.next();
                    if (oProperty.getLocalName().toString().equals("IS_COLUMN_OF")) {
                        //获取实例的属性值
                        if (allindi.getPropertyValue(oProperty) != null) {
                            String value = allindi.getPropertyValue(oProperty).toString();
                            indclass = value.substring(value.indexOf("#") + 1);
                        }
                    }
                }
            }
        }
        return indclass;
    }

    //获取需要评估的数据
    public ArrayList<String> selectdata(String tablename) {
        conn = connection.getCon();
        ArrayList<String> list = new ArrayList<String>();
        try {
            Statement statement = conn.createStatement();
            String sql = "select  *  from " + tablename;
            ResultSet rest = statement.executeQuery(sql);
            ResultSetMetaData data = rest.getMetaData();
            for (int i = 1; i <= data.getColumnCount(); i++) {
                // 获得指定列的列名
                String columnName = data.getColumnName(i);
                list.add(columnName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        AccessDataFrame frame = new AccessDataFrame();
        //System.out.println(frame.select(tablename, columnname));
        //	System.out.println(frame.select("Student", "ST_ID").size());
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AccessDataFrame frame = new AccessDataFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Create the frame.
     */
    public AccessDataFrame() {
        setTitle("数据评估窗口");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 873, 611);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        comboBox = new JComboBox();
        comboBox.addItem("请选择");
        comboBox.setAutoscrolls(true);
        comboBox_1 = new JComboBox<String>();
        comboBox_1.addItem("请选择");
        JRadioButton equal = new JRadioButton("等值约束");
        JRadioButton equalcon = new JRadioButton("等值一致性约束");
        JRadioButton logiccon = new JRadioButton("逻辑一致性约束");
        JButton btnNewButton = new JButton("获取类中的实例");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String value = "";
                ArrayList<String> list = new ArrayList<String>();
                AccessDataFrame aFrame = new AccessDataFrame();
                textArea_1.setText(null);
                if (comboBox.getItemAt(1) != null) {
                    comboBox.removeAllItems();
                    comboBox.addItem("请选择");
                }

                String tablename = comboBox_1.getSelectedItem().toString();
                if (!tablename.equals("请选择")) {
                    for (int i = 0; i < aFrame.selectdata(tablename).size(); i++) {
                        comboBox.addItem(aFrame.selectdata(tablename).get(i));
                        value = value + aFrame.selectdata(tablename).get(i) + "\n";
                    }
                } else {
                    textArea_1.setText("还没有选择要评估的数据表");
                }
                textArea_1.setText(value);
            }
        });
        textArea = new JTextArea();
        lblNewLabel = new JLabel("选择需要评估的字段");

        lblNewLabel_1 = new JLabel("评估规则");
        JRadioButton exist = new JRadioButton("存在约束");
        JRadioButton notnull = new JRadioButton("非空");
        JEditorPane editorPane_1 = new JEditorPane();
        JRadioButton range = new JRadioButton("值域约束");
        JRadioButton logic = new JRadioButton("逻辑约束");
        notnull.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if (notnull.isSelected()) {
                    range.setEnabled(false);
                    logic.setEnabled(false);
                    exist.setEnabled(false);
                    equal.setEnabled(false);
                    equalcon.setEnabled(false);
                    logiccon.setEnabled(false);
                } else {
                    range.setEnabled(true);
                    logic.setEnabled(true);
                    exist.setEnabled(true);
                    equal.setEnabled(true);
                    equalcon.setEnabled(true);
                    logiccon.setEnabled(true);
                }
            }
        });

        range.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if (range.isSelected()) {
                    notnull.setEnabled(false);
                    logic.setEnabled(false);
                    exist.setEnabled(false);
                    equal.setEnabled(false);
                    equalcon.setEnabled(false);
                    logiccon.setEnabled(false);
                } else {
                    notnull.setEnabled(true);
                    logic.setEnabled(true);
                    exist.setEnabled(true);
                    equal.setEnabled(true);
                    equalcon.setEnabled(true);
                    logiccon.setEnabled(true);
                }
            }
        });

        logic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if (logic.isSelected()) {
                    notnull.setEnabled(false);
                    range.setEnabled(false);
                    exist.setEnabled(false);
                    equal.setEnabled(false);
                    equalcon.setEnabled(false);
                    logiccon.setEnabled(false);
                } else {
                    notnull.setEnabled(true);
                    range.setEnabled(true);
                    exist.setEnabled(true);
                    equal.setEnabled(true);
                    equalcon.setEnabled(true);
                    logiccon.setEnabled(true);
                }
            }
        });

        exist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if (exist.isSelected()) {
                    notnull.setEnabled(false);
                    range.setEnabled(false);
                    logic.setEnabled(false);
                    equal.setEnabled(false);
                    equalcon.setEnabled(false);
                    logiccon.setEnabled(false);
                } else {
                    notnull.setEnabled(true);
                    range.setEnabled(true);
                    logic.setEnabled(true);
                    equal.setEnabled(true);
                    equalcon.setEnabled(true);
                    logiccon.setEnabled(true);
                }
            }
        });

        equal.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if (equal.isSelected()) {
                    notnull.setEnabled(false);
                    range.setEnabled(false);
                    logic.setEnabled(false);
                    exist.setEnabled(false);
                    equalcon.setEnabled(false);
                    logiccon.setEnabled(false);
                } else {
                    notnull.setEnabled(true);
                    range.setEnabled(true);
                    logic.setEnabled(true);
                    exist.setEnabled(true);
                    equalcon.setEnabled(true);
                    logiccon.setEnabled(true);
                }
            }
        });

        equalcon.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if (equalcon.isSelected()) {
                    notnull.setEnabled(false);
                    range.setEnabled(false);
                    logic.setEnabled(false);
                    exist.setEnabled(false);
                    equal.setEnabled(false);
                    logiccon.setEnabled(false);
                } else {
                    notnull.setEnabled(true);
                    range.setEnabled(true);
                    logic.setEnabled(true);
                    exist.setEnabled(true);
                    equal.setEnabled(true);
                    logiccon.setEnabled(true);
                }
            }
        });

        logiccon.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                if (logiccon.isSelected()) {
                    notnull.setEnabled(false);
                    range.setEnabled(false);
                    logic.setEnabled(false);
                    exist.setEnabled(false);
                    equalcon.setEnabled(false);
                    equal.setEnabled(false);
                } else {
                    notnull.setEnabled(true);
                    range.setEnabled(true);
                    logic.setEnabled(true);
                    exist.setEnabled(true);
                    equalcon.setEnabled(true);
                    equal.setEnabled(true);
                }
            }
        });

        JButton btnNewButton_1 = new JButton("开始评估");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tablename = comboBox_1.getSelectedItem().toString();
                String columnname = comboBox.getSelectedItem().toString();
                AccessDataFrame aFrame = new AccessDataFrame();
                int value = 0;
                int value1 = 0;
                int allsize = aFrame.select(tablename, columnname).size();
                String gainvalue = aFrame.select(tablename, columnname).toString() + "\n";
                //非空约束
                if (notnull.isSelected()) {
                    System.out.println(allsize);
                    int nullsize = 0;
                    for (int i = 0; i < allsize; i++) {
                        if (aFrame.select(tablename, columnname).get(i) == null) {
                            nullsize = nullsize + 1;
                            //System.out.println(nullsize);
                        }
                        textArea.setText(gainvalue + "一共有" + nullsize + "个空值");
                    }
                }
                //值域评估
                if (range.isSelected()) {
                    int range = 0;
                    int minvalue = aFrame.getindividual(columnname);
                    int maxvalue = aFrame.getmaxvalue(columnname);
                    if (minvalue != maxvalue) {
                        ArrayList<Integer> aList = new ArrayList<Integer>();
                        for (int i = 0; i < allsize; i++) {
                            value = Integer.parseInt(aFrame.select(tablename, columnname).get(i).toString());
                            if (value >= maxvalue || (int) value <= minvalue) {
                                range = range + 1;
                                aList.add(value);
                            }
                        }
                        textArea.setText("查询结果：\n" + gainvalue + "评估结果：\n" + aList.toString() + "\n" + "一个有" + range + "个值超出范围");
                    } else {
                        textArea.setText("改字段没有值域约束");
                    }
                }
                //逻辑约束
                if (logic.isSelected()) {
                    int count = 0;
                    String cname = aFrame.getmorethancom(columnname);
                    if (cname != null) {
                        String gainvalue1 = aFrame.select(tablename, columnname).toString() + "\n";
                        String gainvalue2 = aFrame.select(tablename, cname).toString() + "\n";
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        for (int i = 0; i < allsize; i++) {
                            value = Integer.parseInt(aFrame.select(tablename, columnname).get(i).toString());
                            value1 = Integer.parseInt(aFrame.select(tablename, cname).get(i).toString());
                            if (value < value1) {
                                count = count + 1;
                                list.add(value);
                            }
                        }
                        textArea.setText("查询结果：\n" + gainvalue1 + gainvalue2 + "评估结果：\n" + list + "\n一共有" + count + "个小于上面的值");
                    } else {
                        textArea.setText("该字段没有逻辑约束");
                    }
                }
                //存在性约束
                if (exist.isSelected()) {
                    String convalue = "";
                    String convalue1 = "";
                    int count = 0;
                    ArrayList<String> aList = new ArrayList<String>();
                    String coname = aFrame.getexitcom(columnname);
                    String taname = aFrame.getindiclass(coname);
                    if (coname != null && taname != null) {
                        String gainvalue1 = aFrame.select(tablename, columnname).toString() + "\n";
                        String gainvalue2 = aFrame.select(taname, coname).toString() + "\n";
                        for (int i = 0; i < allsize; i++) {
                            convalue = aFrame.select(tablename, columnname).get(i).toString();
                            if (gainvalue2.contains(convalue)) {
                                count = count + 1;
                                aList.add(convalue);
                            }
                        }
                        int aa = allsize - count;
                        textArea.setText("原字段：" + gainvalue1 + "参照字段：" + gainvalue2 + "评估结果：" + aList.toString() + "\n一共有" + aa + "个记录不在参照表中");
                    } else {
                        textArea.setText("该字段没有存在行约束");
                    }
                }
                //逻辑一致性约束
                if (logiccon.isSelected()) {
                    int convalue = 0;
                    int convalue1 = 0;
                    int count = 0;
                    ArrayList<String> aList = new ArrayList<String>();
                    String coname = aFrame.getlogiccon(columnname);
                    String taname = aFrame.getindiclass(coname);
                    if (coname != null && taname != null) {
                        String gainvalue1 = aFrame.select(tablename, columnname).toString() + "\n";
                        String gainvalue2 = aFrame.select(taname, coname).toString() + "\n";
                        for (int i = 0; i < allsize; i++) {
                            convalue = Integer.parseInt(aFrame.select(tablename, columnname).get(i).toString());
                            convalue1 = Integer.parseInt(aFrame.select(taname, coname).get(i).toString());
                            if (convalue < convalue1) {
                                count = count + 1;
                                aList.add(aFrame.select(tablename, columnname).get(i).toString());
                            }
                        }
                        textArea.setText("原字段：" + gainvalue1 + "参照字段：" + gainvalue2 + "评估结果：" + aList.toString() + "\n一共有" + count + "个记录不满足逻辑约束");
                    } else {
                        textArea.setText("该字段没有存在逻辑一致性约束");
                    }
                }

                //等值约束
                if (equal.isSelected()) {
                    int convalue = 0;
                    int convalue1 = 0;
                    int convalue2 = 0;
                    int count = 0;
                    ArrayList<String> aList = new ArrayList<String>();
                    String coname1 = aFrame.getequone(columnname);
                    String coname2 = aFrame.getequtwo(columnname);
                    if (coname1 != null && coname2 != null) {
                        String gainvalue1 = aFrame.select(tablename, columnname).toString() + "\n";
                        String gainvalue2 = aFrame.select(tablename, coname1).toString() + "\n";
                        String gainvalue3 = aFrame.select(tablename, coname2).toString() + "\n";
                        for (int i = 0; i < allsize; i++) {
                            convalue = Integer.parseInt(aFrame.select(tablename, columnname).get(i).toString());
                            convalue1 = Integer.parseInt(aFrame.select(tablename, coname1).get(i).toString());
                            convalue2 = Integer.parseInt(aFrame.select(tablename, coname2).get(i).toString());
                            if (convalue1 + convalue2 != convalue) {
                                count = count + 1;
                                aList.add(aFrame.select(tablename, columnname).get(i).toString());
                            }
                        }
                        textArea.setText("原字段：" + gainvalue1 + "参照字段1：" + gainvalue2 + "参照字段2：" + gainvalue3 + "评估结果：" + aList.toString() + "\n一共有" + count + "个记录不满足等值约束");
                    } else {
                        textArea.setText("该字段没有存在等值约束");
                    }
                }

                //等值一致性约束
                if (equalcon.isSelected()) {
                    int convalue = 0;
                    int convalue1 = 0;
                    int convalue2 = 0;
                    int count = 0;
                    ArrayList<String> aList = new ArrayList<String>();
                    String coname1 = aFrame.getequconone(columnname);
                    String coname2 = aFrame.getequcontwo(columnname);
                    String taname1 = aFrame.getindiclass(coname1);
                    String taname2 = aFrame.getindiclass(coname2);
                    if (coname1 != null && coname2 != null && taname1 != null && taname2 != null) {
                        String gainvalue1 = aFrame.select(tablename, columnname).toString() + "\n";
                        String gainvalue2 = aFrame.select(taname1, coname1).toString() + "\n";
                        String gainvalue3 = aFrame.select(taname2, coname2).toString() + "\n";
                        for (int i = 0; i < allsize; i++) {
                            convalue = Integer.parseInt(aFrame.select(tablename, columnname).get(i).toString());
                            convalue1 = Integer.parseInt(aFrame.select(taname1, coname1).get(i).toString());
                            convalue2 = Integer.parseInt(aFrame.select(taname2, coname2).get(i).toString());
                            if (convalue1 + convalue2 != convalue) {
                                count = count + 1;
                                aList.add(aFrame.select(tablename, columnname).get(i).toString());
                            }
                        }
                        textArea.setText("原字段：" + gainvalue1 + "参照字段1：" + gainvalue2 + "参照字段2：" + gainvalue3 + "评估结果：" + aList.toString() + "\n一共有" + count + "个记录不满足等值约束");
                    } else {
                        textArea.setText("该字段没有存在等值一致性约束");
                    }
                }
            }
        });

        JList list = new JList();
        textArea_1 = new JTextArea();


        JButton btnNewButton_2 = new JButton("获取本体中的数据表");
        btnNewButton_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editorPane_1.setText(null);
                ArrayList<String> data = new ArrayList<String>();
                String individual = "";
                String classname = "DATATABLE";
                AccessDataFrame accessDataFrame = new AccessDataFrame();
                OntClass oo = accessDataFrame.getOntClass(classname);
                for (Iterator<Individual> ind = getontModel().listIndividuals(oo); ind.hasNext(); ) {
                    Individual indi = ind.next();
                    if (indi.getLocalName() != null) {
                        individual = individual + indi.getLocalName() + "\n";
                        comboBox_1.addItem(indi.getLocalName());
                    }
                }
                editorPane_1.setText(individual);
            }
        });


        JLabel lblNewLabel_2 = new JLabel("选择要评估的数据表");


        JLabel lblNewLabel_3 = new JLabel("评估结果：");


        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addComponent(editorPane_1, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnNewButton_2, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE))
                                .addGap(41)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(textArea_1, GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                                        .addComponent(btnNewButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(14)
                                                .addComponent(lblNewLabel_3)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                                .addComponent(textArea, GroupLayout.PREFERRED_SIZE, 395, GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap())
                                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                                                .addComponent(lblNewLabel_2)
                                                                                .addGap(118)
                                                                                .addComponent(list, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(lblNewLabel)
                                                                        .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 138, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                                        .addComponent(notnull)
                                                                        .addComponent(lblNewLabel_1)
                                                                        .addComponent(logic)
                                                                        .addComponent(range)
                                                                        .addComponent(exist)
                                                                        .addComponent(equal)
                                                                        .addComponent(equalcon)
                                                                        .addComponent(logiccon))
                                                                .addContainerGap())))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                                                .addGap(165))))
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addComponent(btnNewButton_2, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                        .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblNewLabel_1)
                                                .addComponent(lblNewLabel_2)))
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGap(10)
                                                .addComponent(notnull))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                                        .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                                                .addComponent(textArea_1, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                                                                .addGroup(gl_contentPane.createSequentialGroup()
                                                                        .addComponent(list, GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
                                                                        .addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(13)))
                                                        .addComponent(editorPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                                                .addGap(18))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(range)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                                .addComponent(lblNewLabel)
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                                .addComponent(logic)
                                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                                .addComponent(exist)
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(equal)))
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(equalcon)
                                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                                .addComponent(logiccon)
                                                .addGap(78)))
                                .addGap(17)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(textArea, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblNewLabel_3))
                                .addContainerGap())
        );
        contentPane.setLayout(gl_contentPane);
    }
}
