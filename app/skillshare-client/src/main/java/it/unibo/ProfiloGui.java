package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

/**
 * Mostra il profilo dell'utente autenticato.
 */
public class ProfiloGui {

    private final UtenteServiceAsync utenteService = GWT.create(UtenteService.class);

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
        Label bioLabel = new Label("Biografia ");
        TextArea bioBox = new TextArea();
        bioBox.setVisibleLines(4);
        bioBox.setCharacterWidth(40);
        bioBox.setText(
                utente.getBio()
        );

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

        Button salvaButton = new Button("Salva modifiche");
        Label messaggio = new Label();

        salvaButton.addClickHandler(event -> {

                    utenteService.aggiornaBio(
                            utente.getId(),
                            bioBox.getText(),
                            new AsyncCallback<Utente>() {

                                @Override
                                public void onFailure(Throwable caught) {
                                    messaggio.setText("Errore durante il salvataggio");
                                }

                                @Override
                                public void onSuccess(Utente utenteAggiornato) {
                                    if (utenteAggiornato == null) {
                                        messaggio.setText(
                                                "Impossibile aggiornare la biografia"
                                        );
                                        return;
                                    }

                                    // Aggiorna la sessione con i nuovi dati.
                                    SessioneUtente.accedi(utenteAggiornato);
                                    messaggio.setText("Aggiornamento avvenuto con successo");
                                }
                            }
                    );
                }
        );

        panel.add(titolo);
        panel.add(nomeLabel);
        panel.add(emailLabel);
        panel.add(bioLabel);
        panel.add(bioBox);
        panel.add(fotoLabel);
        panel.add(tagLabel);
        panel.add(salvaButton);
        panel.add(messaggio);
        panel.add(NavigationHelper.creaBottoneIndietroHomeUtente());

        RootPanel.get().add(panel);
    }
}