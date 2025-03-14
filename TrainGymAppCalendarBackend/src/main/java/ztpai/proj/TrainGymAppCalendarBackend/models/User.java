package ztpai.proj.TrainGymAppCalendarBackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotEmpty
    @Column(name="user_name", length = 50, nullable = false, unique = false)
    private String name;

    @NotEmpty
    @Column(name="user_mail", length = 100, nullable = false, unique = false)
    private String mail;

    @NotNull
    @Column(name="is_user_trainer", nullable = false, unique = false)
    private boolean trainer;

    public User(){}

    public User(Integer id, String name, String mail, boolean trainer){
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.trainer = trainer;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setMail(String mail){
        this.mail = mail;
    }

    public String getMail(){
        return mail;
    }

    public void setTrainer(Boolean trainer){
        this.trainer = trainer;
    }

    public boolean getTrainer(){
        return trainer;
    }
}
