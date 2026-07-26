package it.unibo;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Mostra il profilo dell'utente autenticato.
 */
public class ProfiloGui {

    /**
     * Visualizza le informazioni del profilo.
     */
    public void mostra() {

        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        // Impedisce l'accesso senza login.
        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        HTML titolo = new HTML("<h2>Il mio profilo</h2>");

        Label nomeLabel = new Label("Nome: " + utente.getNomeUtente());
        Label emailLabel = new Label("Email: " + utente.getEmail());
        Label bioLabel = new Label("Bio: " + utente.getBio());
        Label fotoLabel;

        if (utente.getFotoProfilo() != null && !utente.getFotoProfilo().isEmpty()) {
            fotoLabel = new Label("Foto profilo presente");
        } else {
            fotoLabel = new Label("Nessuna foto profilo");
        }

        Label tagLabel = new Label(
                "Competenze: "
                        + String.join(
                        ", ",
                        utente.getTagCompetenze()
                )
        );

        panel.add(titolo);
        panel.add(nomeLabel);
        panel.add(emailLabel);
        panel.add(bioLabel);
        panel.add(fotoLabel);
        panel.add(tagLabel);
        panel.add(NavigationHelper.creaBottoneIndietroHomeUtente());

        RootPanel.get().add(panel);
    }
}