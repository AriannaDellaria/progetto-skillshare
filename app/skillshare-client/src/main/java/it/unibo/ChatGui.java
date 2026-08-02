package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.List;

// Mostra la cronologia della chat associata a una richiesta accettata.
public class ChatGui {

    private final MessaggioServiceAsync messaggioService =
            GWT.create(MessaggioService.class);

    private final String idRichiesta;
    private final Runnable azioneIndietro;

    public ChatGui(String idRichiesta, Runnable azioneIndietro) {
        this.idRichiesta = idRichiesta;
        this.azioneIndietro = azioneIndietro;
    }

    public void mostra() {

        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        HTML titolo = new HTML("<h2>Chat</h2>");
        Label messaggioStato = new Label("Caricamento conversazione...");

        panel.add(titolo);
        panel.add(messaggioStato);

        VerticalPanel cronologiaPanel = new VerticalPanel();
        cronologiaPanel.setSpacing(8);
        panel.add(cronologiaPanel);

        Button indietroButton = new Button("Indietro");
        indietroButton.addClickHandler(event -> azioneIndietro.run());
        panel.add(indietroButton);

        RootPanel.get().add(panel);

        messaggioService.apriChat(
                idRichiesta,
                utente.getId(),
                new AsyncCallback<List<Messaggio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggioStato.setText(
                                caught.getMessage() != null
                                        ? caught.getMessage()
                                        : "Errore durante l'apertura della chat."
                        );
                    }

                    @Override
                    public void onSuccess(List<Messaggio> messaggi) {

                        if (messaggi == null || messaggi.isEmpty()) {
                            messaggioStato.setText(
                                    "Nessun messaggio in questa conversazione."
                            );
                            return;
                        }

                        messaggioStato.setText("");

                        for (Messaggio messaggio : messaggi) {
                            cronologiaPanel.add(
                                    new Label(messaggio.getTesto())
                            );
                        }
                    }
                }
        );
    }
}