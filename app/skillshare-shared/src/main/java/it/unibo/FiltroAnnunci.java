package it.unibo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Rappresenta i criteri di filtro selezionati dall'utente nella
 * schermata dei filtri: un insieme di categorie tra quelle
 * consigliate, un'eventuale categoria libera aggiuntiva, e un
 * valore di disponibilità.
 */
public class FiltroAnnunci implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<String> categorieSelezionate = new HashSet<>();
    private String altroCategoria;
    private String disponibilitaSelezionata;

    // Necessario per la serializzazione GWT.
    public FiltroAnnunci() {
    }

    public Set<String> getCategorieSelezionate() {
        return categorieSelezionate;
    }

    public void setCategorieSelezionate(Set<String> categorieSelezionate) {
        this.categorieSelezionate = categorieSelezionate;
    }

    public String getAltroCategoria() {
        return altroCategoria;
    }

    public void setAltroCategoria(String altroCategoria) {
        this.altroCategoria = altroCategoria;
    }

    public String getDisponibilitaSelezionata() {
        return disponibilitaSelezionata;
    }

    public void setDisponibilitaSelezionata(String disponibilitaSelezionata) {
        this.disponibilitaSelezionata = disponibilitaSelezionata;
    }

    /**
     * Indica se non è stato selezionato alcun criterio, cioè se il
     * filtro corrisponde a "mostra tutti gli annunci".
     */
    public boolean isVuoto() {
        boolean nessunaCategoria = categorieSelezionate == null
                || categorieSelezionate.isEmpty();

        boolean nessunAltro = altroCategoria == null
                || altroCategoria.trim().isEmpty();

        boolean nessunaDisponibilita = disponibilitaSelezionata == null
                || disponibilitaSelezionata.trim().isEmpty();

        return nessunaCategoria && nessunAltro && nessunaDisponibilita;
    }
}