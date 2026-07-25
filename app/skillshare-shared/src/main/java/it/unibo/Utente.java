package it.unibo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un utente registrato sulla piattaforma.
 */
public class Utente implements Serializable {

    private String id;
    private String nomeUtente;
    private String email;
    private String password;
    private String bio;
    private String fotoProfilo;
    private List<String> tagCompetenze;

    /**
     * Costruttore vuoto richiesto da GWT RPC.
     */
    public Utente() {
        this.tagCompetenze = new ArrayList<>();
    }

    /**
     * Crea un utente con i dati della registrazione.
     */
    public Utente(
            String id,
            String nomeUtente,
            String email,
            String password
    ) {
        this.id = id;
        this.nomeUtente = nomeUtente;
        this.email = email;
        this.password = password;
        this.bio = "";
        this.fotoProfilo = "";
        this.tagCompetenze = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public String getFotoProfilo() {
        return fotoProfilo;
    }

    public List<String> getTagCompetenze() {
        return tagCompetenze;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setFotoProfilo(String fotoProfilo) {
        this.fotoProfilo = fotoProfilo;
    }

    public void setTagCompetenze(List<String> tagCompetenze) {
        this.tagCompetenze = tagCompetenze;
    }
}