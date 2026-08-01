package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

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

        Label stato = new Label(
                "Stato: " + richiesta.getStato().getEtichetta()
        );

        scheda.add(annuncio);
        scheda.add(mittente);
        scheda.add(messaggio);
        scheda.add(stato);

        if (richiesta.getStato() == StatoRichiesta.IN_ATTESA) {

            Label esitoOperazione = new Label();

            Button accettaButton = new Button("Accetta");
            Button rifiutaButton = new Button("Rifiuta");

            accettaButton.addClickHandler(event -> {

                boolean confermato = Window.confirm(
                        "Confermi di voler accettare questa richiesta di scambio?"
                );

                if (!confermato) {
                    return;
                }

                accettaButton.setEnabled(false);
                rifiutaButton.setEnabled(false);

                richiestaScambioService.accetta(
                        richiesta.getId(),
                        SessioneUtente.getUtenteCorrente().getId(),
                        new AsyncCallback<RichiestaScambio>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                accettaButton.setEnabled(true);
                                rifiutaButton.setEnabled(true);
                                esitoOperazione.setText(
                                        caught.getMessage() != null
                                                ? caught.getMessage()
                                                : "Errore durante l'accettazione della richiesta."
                                );
                            }

                            @Override
                            public void onSuccess(RichiestaScambio aggiornata) {
                                mostra();
                            }
                        }
                );
            });

            rifiutaButton.addClickHandler(event -> {

                boolean confermato = Window.confirm(
                        "Confermi di voler rifiutare questa richiesta di scambio?"
                );

                if (!confermato) {
                    return;
                }

                accettaButton.setEnabled(false);
                rifiutaButton.setEnabled(false);

                richiestaScambioService.rifiuta(
                        richiesta.getId(),
                        SessioneUtente.getUtenteCorrente().getId(),
                        new AsyncCallback<RichiestaScambio>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                accettaButton.setEnabled(true);
                                rifiutaButton.setEnabled(true);
                                esitoOperazione.setText(
                                        caught.getMessage() != null
                                                ? caught.getMessage()
                                                : "Errore durante il rifiuto della richiesta."
                                );
                            }

                            @Override
                            public void onSuccess(RichiestaScambio aggiornata) {
                                mostra();
                            }
                        }
                );
            });

            scheda.add(accettaButton);
            scheda.add(rifiutaButton);
            scheda.add(esitoOperazione);
        }
        return scheda;
    }
}