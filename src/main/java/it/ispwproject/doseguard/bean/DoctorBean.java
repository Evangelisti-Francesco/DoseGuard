package it.ispwproject.doseguard.bean;

public class DoctorBean {

    private int id;
    private String name;
    private String surname;
    private String email;
    private boolean favourite;

    public DoctorBean(int id, String name, String surname, String email, boolean favourite) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.favourite = favourite;
    }

    public String getFullName() { return name + " " + surname;   }

    public int getId() { return id;}
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isFavourite() { return favourite; }
    public void setFavourite(boolean favourite) { this.favourite = favourite; }

}
