package ztpai.proj.TrainGymAppCalendarBackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

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
    @Column(name="is_user_trainer", nullable = false, columnDefinition = "boolean default false")
    private boolean trainer;

    @NotNull
    @Column(name="is_admin", nullable = false, columnDefinition = "boolean default false")
    private boolean admin;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<UserGroup> asTrainer = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<UserGroup> asClient  = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private DataUser dataUser;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Training> trainings = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_recommended_trainings", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "recommended_training_id"))
    private Set<RecommendedTrainings> recommendedTrainings = new HashSet<>();

    public User(){}

    public User(Integer id, String name, String mail, boolean trainer, boolean admin){
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.trainer = trainer;
        this.admin = admin;
    }

    public DataUser getDataUser() {
        return dataUser;
    }
    public void setDataUser(DataUser dataUser) {
        this.dataUser = dataUser;
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

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Set<RecommendedTrainings> getRecommendedTrainings() {
        return recommendedTrainings;
    }

    public void setRecommendedTrainings(Set<RecommendedTrainings> recommendedTrainings) {
        this.recommendedTrainings = recommendedTrainings;
    }
}
