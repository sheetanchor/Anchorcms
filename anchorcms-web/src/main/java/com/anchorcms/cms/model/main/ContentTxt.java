package com.anchorcms.cms.model.main;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author 阁楼麻雀
 * @Email netuser.orz@icloud.com
 * @Date 2016-11-2
 * @Desc CMS内容文本表
 */
@Entity
@Table(name = "c_content_txt", schema = "db_cms")
public class ContentTxt implements Serializable{
    private static final long serialVersionUID = 5091176700006302477L;
    private int contentId;
    private String txt;
    private String txt1;
    private String txt2;
    private String txt3;

    @Id
    @Column(name = "content_id")
    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    @Basic
    @Column(name = "txt")
    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    @Basic
    @Column(name = "txt1")
    public String getTxt1() {
        return txt1;
    }

    public void setTxt1(String txt1) {
        this.txt1 = txt1;
    }

    @Basic
    @Column(name = "txt2")
    public String getTxt2() {
        return txt2;
    }

    public void setTxt2(String txt2) {
        this.txt2 = txt2;
    }

    @Basic
    @Column(name = "txt3")
    public String getTxt3() {
        return txt3;
    }

    public void setTxt3(String txt3) {
        this.txt3 = txt3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentTxt that = (ContentTxt) o;

        if (contentId != that.contentId) return false;
        if (txt != null ? !txt.equals(that.txt) : that.txt != null) return false;
        if (txt1 != null ? !txt1.equals(that.txt1) : that.txt1 != null) return false;
        if (txt2 != null ? !txt2.equals(that.txt2) : that.txt2 != null) return false;
        if (txt3 != null ? !txt3.equals(that.txt3) : that.txt3 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contentId;
        result = 31 * result + (txt != null ? txt.hashCode() : 0);
        result = 31 * result + (txt1 != null ? txt1.hashCode() : 0);
        result = 31 * result + (txt2 != null ? txt2.hashCode() : 0);
        result = 31 * result + (txt3 != null ? txt3.hashCode() : 0);
        return result;
    }
}
