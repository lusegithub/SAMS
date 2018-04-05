package com.jacky.sams.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Student {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 50)
    private String id;

    //姓名
    private String name;

    //学号
    private String stuNo;

    //性别
    private String sex;

    //出生年月
    private String birthday;

    //学院
    private String institution;

    //专业
    private String profession;

    //年级
    private String grade;

    //班级
    private String className;

    //宿舍地址
    private String address;

    //联系电话
    private String phone;

    //个人描述
    private String description;

    @OneToOne(cascade = {CascadeType.REFRESH,CascadeType.REMOVE},fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private SysUser user;

    @OneToMany(mappedBy = "student",cascade={CascadeType.REMOVE})
    private Set<StudentAssociation> studentAssociations;

    @OneToMany(mappedBy = "student",cascade={CascadeType.REMOVE})
    private Set<StudentActivity> studentActivities;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<StudentAssociation> getStudentAssociations() {
        return studentAssociations;
    }

    public void setStudentAssociations(Set<StudentAssociation> studentAssociations) {
        this.studentAssociations = studentAssociations;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public Set<StudentActivity> getStudentActivities() {
        return studentActivities;
    }

    public void setStudentActivities(Set<StudentActivity> studentActivities) {
        this.studentActivities = studentActivities;
    }
}
