package com.jacky.sams.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class StudentActivity implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "activity_id")
    @JsonIgnore
    private Activity activity;

    @Id
    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;

    //报名时间
    @Column(length = 50)
    private String applyTime;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }
}
