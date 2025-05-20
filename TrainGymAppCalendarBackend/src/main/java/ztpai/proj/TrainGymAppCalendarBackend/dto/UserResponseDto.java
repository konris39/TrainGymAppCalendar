package ztpai.proj.TrainGymAppCalendarBackend.dto;

public class UserResponseDto {
    private Integer id;
    private String name;
    private String mail;
    private boolean trainer;
    private boolean admin;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public boolean isTrainer() { return trainer; }
    public void setTrainer(boolean trainer) { this.trainer = trainer; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
}
