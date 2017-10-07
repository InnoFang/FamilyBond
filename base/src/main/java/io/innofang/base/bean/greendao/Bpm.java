package io.innofang.base.bean.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author: Inno Fang
 * Time: 2017/10/5 11:23
 * Description:
 */

@Entity
public class Bpm {

    @Id(autoincrement = true)
    private Long id;

    private String mBpm;
    private String mTime;
    private String mDescription;
    @Generated(hash = 2100335612)
    public Bpm(Long id, String mBpm, String mTime, String mDescription) {
        this.id = id;
        this.mBpm = mBpm;
        this.mTime = mTime;
        this.mDescription = mDescription;
    }
    @Generated(hash = 931508249)
    public Bpm() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getBpm() {
        return this.mBpm;
    }
    public void setBpm(String mBpm) {
        this.mBpm = mBpm;
    }
    public String getTime() {
        return this.mTime;
    }
    public void setTime(String mTime) {
        this.mTime = mTime;
    }
    public String getDescription() {
        return this.mDescription;
    }
    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
    public String getMBpm() {
        return this.mBpm;
    }
    public void setMBpm(String mBpm) {
        this.mBpm = mBpm;
    }
    public String getMTime() {
        return this.mTime;
    }
    public void setMTime(String mTime) {
        this.mTime = mTime;
    }
    public String getMDescription() {
        return this.mDescription;
    }
    public void setMDescription(String mDescription) {
        this.mDescription = mDescription;
    }

}
