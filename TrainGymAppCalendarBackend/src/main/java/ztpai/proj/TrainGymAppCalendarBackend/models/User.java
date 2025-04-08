package ztpai.proj.TrainGymAppCalendarBackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    @Column(name="user_name", length = 50, nullable = false, unique = false)
    private String name;

    @NotEmpty
    @Column(name="user_mail", length = 100, nullable = false, unique = true)
    private String mail;

    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "user_password", nullable = false)
    private String password;

    @NotNull
    @Column(name="is_user_trainer", nullable = false, unique = false)
    private boolean trainer;

    public User(){}

    public User(Integer id, String name, String mail, boolean trainer){
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTrainer(Boolean trainer){
        this.trainer = trainer;
    }

    public boolean getTrainer(){
        return trainer;
    }
}
