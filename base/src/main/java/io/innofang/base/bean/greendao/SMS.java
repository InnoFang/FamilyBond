package io.innofang.base.bean.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: Inno Fang
 * Time: 2017/10/13 16:14
 * Description:
 */

@Entity
public class SMS {

    @Id(autoincrement = true)
    private Long id;

    private String time;
    private String address;
    private String content;
    private double probability;


    @Generated(hash = 1973675925)
    public SMS(Long id, String time, String address, String content,
            double probability) {
        this.id = id;
        this.time = time;
        this.address = address;
        this.content = content;
        this.probability = probability;
    }

    @Generated(hash = 2144275990)
    public SMS() {
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "SMS{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", address='" + address + '\'' +
                ", content='" + content + '\'' +
                ", probability=" + probability +
                '}';
    }
}
