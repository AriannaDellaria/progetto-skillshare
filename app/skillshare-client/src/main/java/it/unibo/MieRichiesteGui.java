package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.List;

// Mostra le richieste di scambio inviate dall'utente autenticato.
public class MieRichiesteGui {

    private final RichiestaScambioServiceAsync richiestaScambioService =
            GWT.create(RichiestaScambioService.class);

    // Visualizza le richieste inviate dall'utente autenticato.
    public void mostra() {

        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        HTML titolo = new HTML("<h2>Le mie richieste inviate</h2>");
        Label messaggio = new Label("Caricamento richieste...");

        panel.add(titolo);
        panel.add(messaggio);

        richiestaScambioService.richiesteInviate(
                utente.getId(),
                new AsyncCallback<List<RichiestaScambio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggio.setText(
                                "Errore durante il caricamento delle richieste."
                        );
                    }

                    @Override
                    public void onSuccess(List<RichiestaScambio> richieste) {
                        if (richieste == null || richieste.isEmpty()) {
                            messaggio.setText(
                                    "Non hai inviato alcuna richiesta di scambio."
                            );
                        } else {
                            messaggio.setText("");

                            for (RichiestaScambio richiesta : richieste) {
                                panel.add(creaSchedaRichiesta(richiesta));
                            }
                        }

                        panel.add(NavigationHelper.creaBottoneIndietroHomeUtente());
                    }
                }
        );

        RootPanel.get().add(panel);
    }

    // Crea la scheda di una singola richiesta inviata.
    private VerticalPanel creaSchedaRichiesta(RichiestaScambio richiesta) {
        VerticalPanel scheda = new VerticalPanel();
        scheda.setSpacing(5);
        scheda.setWidth("400px");

        Label annuncio = new Label(
                "Annuncio: " + richiesta.getTitoloAnnuncio()
        );

        Label messaggioRichiesta = new Label(
                "Messaggio inviato: " + richiesta.getMessaggio()
        );

        StatoRichiesta statoRichiesta = richiesta.getStato() != null
                ? richiesta.getStato()
                : StatoRichiesta.IN_ATTESA;

        Label stato = new Label(
                "Stato: " + statoRichiesta.getEtichetta()
        );

        scheda.add(annuncio);
        scheda.add(messaggioRichiesta);
        scheda.add(stato);

        if (statoRichiesta == StatoRichiesta.ACCETTATA) {
            Button apriChatButton = new Button("Apri chat");
            apriChatButton.addClickHandler(event ->
                    new ChatGui(
                            richiesta.getId(),
                            () -> new MieRichiesteGui().mostra()
                    ).mostra()
            );
            scheda.add(apriChatButton);
        }

        return scheda;
    }
}