package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.List;

/**
 * Mostra gli annunci attivi degli altri utenti.
 */
public class AnnunciGui {

    private final AnnuncioServiceAsync annuncioService =
            GWT.create(AnnuncioService.class);

    /**
     * Carica gli annunci del marketplace.
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
        panel.setSpacing(12);

        HTML titolo = new HTML("<h2>Skill Marketplace</h2>");
        Label messaggio = new Label("Caricamento annunci...");

        panel.add(titolo);
        panel.add(messaggio);
        panel.add(
                NavigationHelper.creaBottoneIndietroHomeUtente()
        );

        RootPanel.get().add(panel);

        annuncioService.trovaAttivi(
                new AsyncCallback<List<Annuncio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggio.setText("Errore durante il caricamento degli annunci.");
                    }

                    @Override
                    public void onSuccess(List<Annuncio> annunci) {
                        messaggio.setText("");

                        if (annunci == null || annunci.isEmpty()) {
                            messaggio.setText("Non ci sono annunci disponibili.");
                            return;
                        }

                        boolean trovato = false;

                        for (Annuncio annuncio : annunci) {
                            // Non mostra gli annunci dell'utente corrente.
                            if (!utente.getId().equals(
                                    annuncio.getIdAutore()
                            )) {
                                panel.add(creaSchedaAnnuncio(annuncio));
                                trovato = true;
                            }
                        }

                        if (!trovato) {
                            messaggio.setText("Non ci sono annunci di altri utenti.");
                        }
                    }
                }
        );
    }

    // Crea la scheda di un annuncio.
    private VerticalPanel creaSchedaAnnuncio(
            Annuncio annuncio
    ) {
        VerticalPanel scheda = new VerticalPanel();
        scheda.setSpacing(5);
        scheda.setWidth("400px");

        Label titolo = new Label(annuncio.getTitolo());

        Label descrizione = new Label(annuncio.getDescrizione());

        Label categoria = new Label("Categoria: " + annuncio.getCategoria());

        Label disponibilita = new Label("Disponibilità: " + annuncio.getDisponibilita());

        Label controprestazione = new Label("Controprestazione: " + annuncio.getControprestazione());

        scheda.add(titolo);
        scheda.add(descrizione);
        scheda.add(categoria);
        scheda.add(disponibilita);
        scheda.add(controprestazione);

        return scheda;
    }
}