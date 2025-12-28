package ma.projet.rh.dto;

import ma.projet.rh.enums.Role;

import java.io.Serializable;

/**
 * DTO léger pour stocker les informations utilisateur dans la session HTTP
 * Évite les problèmes de sérialisation avec les entités JPA et leurs relations LAZY
 */
public class UserSessionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private Role role;
    private Long employeId;
    private String employeNom;
    private String employePrenom;

    public UserSessionDTO() {
    }

    public UserSessionDTO(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public UserSessionDTO(Long id, String username, Role role, Long employeId, String employeNom, String employePrenom) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.employeId = employeId;
        this.employeNom = employeNom;
        this.employePrenom = employePrenom;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public String getEmployeNom() {
        return employeNom;
    }

    public void setEmployeNom(String employeNom) {
        this.employeNom = employeNom;
    }

    public String getEmployePrenom() {
        return employePrenom;
    }

    public void setEmployePrenom(String employePrenom) {
        this.employePrenom = employePrenom;
    }

    public String getNomComplet() {
        if (employeNom != null && employePrenom != null) {
            return employePrenom + " " + employeNom;
        }
        return username;
    }
}

