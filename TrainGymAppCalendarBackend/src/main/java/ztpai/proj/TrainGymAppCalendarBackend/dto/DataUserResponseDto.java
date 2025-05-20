package ztpai.proj.TrainGymAppCalendarBackend.dto;

public class DataUserResponseDto {
    private Integer id;
    private Double weight;
    private Double height;
    private Integer age;
    private Double bmi;
    private Double bp;
    private Double sq;
    private Double dl;
    private Double sum;
    private UserDataDto user;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Double getBmi() { return bmi; }
    public void setBmi(Double bmi) { this.bmi = bmi; }
    public Double getBp() { return bp; }
    public void setBp(Double bp) { this.bp = bp; }
    public Double getSq() { return sq; }
    public void setSq(Double sq) { this.sq = sq; }
    public Double getDl() { return dl; }
    public void setDl(Double dl) { this.dl = dl; }
    public Double getSum() { return sum; }
    public void setSum(Double sum) { this.sum = sum; }
    public UserDataDto getUser() { return user; }
    public void setUser(UserDataDto user) { this.user = user; }
}
