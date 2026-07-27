package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.List;

/**
 * Mostra gli annunci pubblicati dall'utente autenticato.
 */
public class MieiAnnunciGui {

    private final AnnuncioServiceAsync annuncioService = GWT.create(AnnuncioService.class);

    /**
     * Visualizza gli annunci dell'utente.
     */
    public void mostra() {

        RootPanel.get().clear();

        Utente utente = SessioneUtente.getUtenteCorrente();

        if (utente == null) {
            new LoginGui().mostra();
            return;
        }

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);
        HTML titolo = new HTML("<h2>I miei annunci</h2>");
        Label messaggio = new Label();

        panel.add(titolo);

        annuncioService.mieiAnnunci(
                utente.getId(),
                new AsyncCallback<List<Annuncio>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        messaggio.setText("Errore durante il caricamento degli annunci.");
                    }


                    @Override
                    public void onSuccess(List<Annuncio> annunci) {

                        if (annunci == null || annunci.isEmpty()) {
                            messaggio.setText("Non hai ancora pubblicato annunci.");
                            return;
                        }

                        for (Annuncio annuncio : annunci) {

                            VerticalPanel box = new VerticalPanel();
                            box.setSpacing(5);

                            box.add(new Label("Titolo: " + annuncio.getTitolo()));

                            box.add(new Label("Categoria: " + annuncio.getCategoria()));

                            box.add(new Label("Disponibilità: " + annuncio.getDisponibilita()));

                            box.add(new Label("Controprestazione: " + annuncio.getControprestazione()));

                            panel.add(box);
                        }
                    }
                }
        );


        panel.add(messaggio);

        panel.add(
                NavigationHelper.creaBottoneIndietroHomeUtente()
        );

        RootPanel.get().add(panel);
    }
}