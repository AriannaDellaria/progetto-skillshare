package it.unibo;

import com.google.gwt.user.client.ui.Button;

/**
 * Crea i pulsanti di navigazione tra le schermate.
 */
public final class NavigationHelper {

    private NavigationHelper() {
    }

    /**
     * Pulsante per aprire la schermata di login.
     */
    public static Button creaBottoneVaiAlLogin() {
        Button bottone = new Button("Hai già un account? Accedi");
        bottone.addClickHandler(event -> new LoginGui().mostra());
        return bottone;
    }

    /**
     * Pulsante per aprire la schermata di registrazione.
     */
    public static Button creaBottoneVaiAllaRegistrazione() {
        Button bottone = new Button("Non hai un account? Registrati");
        bottone.addClickHandler(event -> new RegistrazioneGui().mostra());
        return bottone;
    }
}