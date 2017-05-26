package ontology.dao.pojo;

import java.util.Date;

/**
 * Created by skylark on 2017/4/15.
 */
public class Ontology {
    private String str;
    private Date date;

    public Ontology(String s, Date date) {
        this.str=s;
        this.date=date;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
