package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

// Schermata per inviare una richiesta di scambio relativa a un annuncio.
public class RichiediScambioGui {

    private final RichiestaScambioServiceAsync richiestaScambioService =
            GWT.create(RichiestaScambioService.class);

    private final Annuncio annuncio;

    public RichiediScambioGui(Annuncio annuncio) {
        this.annuncio = annuncio;
    }

    // Mostra il form per inviare la richiesta.
    public void mostra() {
        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        // Impedisce l'invio senza login.
        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        HTML titolo = new HTML("<h2>Invia richiesta di scambio</h2>");

        Label riepilogo = new Label(
                "Annuncio: " + annuncio.getTitolo()
        );

        TextArea messaggioBox = new TextArea();
        messaggioBox.setVisibleLines(5);
        messaggioBox.setCharacterWidth(40);
        messaggioBox.getElement().setPropertyString(
                "placeholder",
                "Scrivi un messaggio per proporre lo scambio"
        );

        Button inviaButton = new Button("Invia richiesta");
        Button annullaButton = new Button("Indietro");
        Label messaggioEsito = new Label();


        inviaButton.addClickHandler(event -> {
            messaggioEsito.setText("");

            String messaggio = messaggioBox.getText().trim();

            if (!annuncio.isAttivo() || "No".equalsIgnoreCase(annuncio.getDisponibilita())) {
                messaggioEsito.setText("Questo annuncio non è attualmente disponibile.");
                return;
            }
            if (messaggio.isEmpty()) {
                messaggioEsito.setText(
                        "Inserisci un messaggio per la richiesta."
                );
                return;
            }
            inviaButton.setEnabled(false);

            richiestaScambioService.invia(
                    annuncio.getId(),
                    utente.getId(),
                    messaggio,
                    new AsyncCallback<RichiestaScambio>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            inviaButton.setEnabled(true);

                            if (caught.getMessage() != null
                                    && !caught.getMessage().trim().isEmpty()) {
                                messaggioEsito.setText(
                                        caught.getMessage()
                                );
                            } else {
                                messaggioEsito.setText(
                                        "Errore durante l'invio della richiesta."
                                );
                            }
                        }

                        @Override
                        public void onSuccess(RichiestaScambio richiesta) {
                            inviaButton.setEnabled(true);
                            messaggioEsito.setText(
                                    "Richiesta inviata correttamente."
                            );
                            messaggioBox.setText("");
                        }
                    }
            );
        });

        annullaButton.addClickHandler(event -> new AnnunciGui().mostra());

        panel.add(titolo);
        panel.add(riepilogo);
        panel.add(new Label("Messaggio *"));
        panel.add(messaggioBox);
        panel.add(new Label("* Campo obbligatorio"));
        panel.add(inviaButton);
        panel.add(annullaButton);
        panel.add(messaggioEsito);

        RootPanel.get().add(panel);
    }
}
