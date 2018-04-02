package com.jacky.sams.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
public class Activity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 50)
    private String id;

    //活动名称
    private String name;

    //活动简介
    private String description;

    //活动内容
    private String content;

    //开始时间
    private String startTime;

    //结束时间
    private String endTime;

    //举办地点
    private String location;

    //负责人及联系方式
    private String info;

    //申请时间
    private String applyTime;

    //发布时间
    private String sendTime;

    //报名截止时间
    private String overTime;

    //活动状态{0:未通过；1：已通过；2：报名中；3：报名结束；4：待审核}
    private Integer status;

    @OneToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "aid")
    private AssociationDetail detail;

    @OneToMany(mappedBy = "activity")
    private Set<StudentActivity> studentActivities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<StudentActivity> getStudentActivities() {
        return studentActivities;
    }

    public void setStudentActivities(Set<StudentActivity> studentActivities) {
        this.studentActivities = studentActivities;
    }

    public AssociationDetail getDetail() {
        return detail;
    }

    public void setDetail(AssociationDetail detail) {
        this.detail = detail;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getOverTime() {
        return overTime;
    }

    public void setOverTime(String overTime) {
        this.overTime = overTime;
    }
}
