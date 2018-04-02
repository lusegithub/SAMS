package com.jacky.sams.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class StudentAssociation implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "association_id")
    @JsonIgnore
    private AssociationDetail association;

    @Id
    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;

    //状态{1:已加入；2：待审核}
    private int status;

    //申请时间
    @Column(length = 50)
    private String applyTime;

    //加入时间
    @Column(length = 50)
    private String enterTime;

    public AssociationDetail getAssociation() {
        return association;
    }

    public void setAssociation(AssociationDetail association) {
        this.association = association;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }
}
