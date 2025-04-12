package ztpai.proj.TrainGymAppCalendarBackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class DataUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "double precision default 0")
    private Double weight;

    @Column(columnDefinition = "double precision default 0")
    private Double height;

    @Column(columnDefinition = "integer default 0")
    private Integer age;

    private Double bmi;

    @Column(columnDefinition = "double precision default 0")
    private Double bp;

    @Column(columnDefinition = "double precision default 0")
    private Double sq;

    @Column(columnDefinition = "double precision default 0")
    private Double dl;

    private Double sum;

    @OneToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    public DataUser(){
    }

    public DataUser(Integer id, Double weight, Double height, Integer age, Double bmi, Double bp, Double sq, Double dl, Double sum) {
        this.id = id;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.bmi = bmi;
        this.bp = bp;
        this.sq = sq;
        this.dl = dl;
        this.sum = sum;
    }

    @PrePersist
    @PreUpdate
    public void calculateFields(){
        if(weight != null && height != null && height > 0){
            this.bmi = weight / (height*height) * 10000;
        } else {
            this.bmi = 0.0;
        }

        if(bp != null && sq != null && dl != null){
            this.sum = bp + sq  + dl;
        } else {
            this.sum = 0.0;
        }
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getBMI() {
        return bmi;
    }

    public Double getBP() {
        return bp;
    }

    public void setBP(Double bp) {
        this.bp = bp;
    }

    public Double getSQ() {
        return sq;
    }

    public void setSQ(Double sq) {
        this.sq = sq;
    }

    public Double getDL() {
        return dl;
    }

    public void setDL(Double dl) {
        this.dl = dl;
    }

    public Double getSum() {
        return sum;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
