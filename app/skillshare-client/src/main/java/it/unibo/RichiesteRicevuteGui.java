package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.List;

// Mostra le richieste di scambio ricevute per i propri annunci.
public class RichiesteRicevuteGui {

    private final RichiestaScambioServiceAsync richiestaScambioService =
            GWT.create(RichiestaScambioService.class);

    // Visualizza le richieste ricevute dall'utente autenticato.
    public void mostra() {

        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        HTML titolo = new HTML("<h2>Richieste ricevute</h2>");
        Label messaggio = new Label("Caricamento richieste...");

        panel.add(titolo);
        panel.add(messaggio);

        richiestaScambioService.richiesteRicevute(
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
                            messaggio.setText("Non hai ricevuto richieste di scambio.");
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

    // Crea la scheda di una singola richiesta ricevuta.
    private VerticalPanel creaSchedaRichiesta(RichiestaScambio richiesta) {
        VerticalPanel scheda = new VerticalPanel();
        scheda.setSpacing(5);
        scheda.setWidth("400px");

        Label annuncio = new Label(
                "Annuncio: " + richiesta.getTitoloAnnuncio()
        );

        Label mittente = new Label(
                "Da: " + richiesta.getNomeMittente()
        );

        Label messaggio = new Label(
                "Messaggio: " + richiesta.getMessaggio()
        );

        scheda.add(annuncio);
        scheda.add(mittente);
        scheda.add(messaggio);

        return scheda;
    }
}