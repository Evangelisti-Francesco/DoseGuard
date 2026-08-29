package it.ispwproject.doseguard.bean;

import it.ispwproject.doseguard.enumerator.Role;

import java.util.List;

public class RegistrationBean {

    private String name;
    private String surname;
    private String email;
    private String password;
    private String confirmPassword;
    private Role role;
    private String pharmacyName;

    //Solo per paziente
    private String fiscalCode;
    // Solo per medico
    private List<SpecializationBean> specializations;

    public RegistrationBean() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getFiscalCode() { return fiscalCode; }
    public void setFiscalCode(String fiscalCode) { this.fiscalCode = fiscalCode; }

    public List<SpecializationBean> getSpecializations() { return specializations; }
    public void setSpecializations(List<SpecializationBean> specializations) { this.specializations = specializations; }

    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) {this.pharmacyName = pharmacyName;}
}
