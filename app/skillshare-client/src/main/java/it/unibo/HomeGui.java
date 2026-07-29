package it.unibo;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Schermata principale dell'utente autenticato.
 */
public class HomeGui {

    /**
     * Mostra la home dopo l'accesso.
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

        HTML titolo = new HTML("<h1>SkillShare</h1>");
        HTML benvenuto = new HTML(
                "<h2>Benvenuto, "
                        + utente.getNomeUtente()
                        + "!</h2>"
        );

        Button logoutButton = new Button("Logout");
        logoutButton.addClickHandler(event -> {
            SessioneUtente.esci();
            new LoginGui().mostra();
        });


        Button profiloButton = new Button("Profilo");
        profiloButton.addClickHandler(event -> {
            new ProfiloGui().mostra();
        });

        Button creaAnnuncioButton = new Button("Crea annuncio");
        creaAnnuncioButton.addClickHandler(
                event -> new CreaAnnuncioGui().mostra()
        );

        Button mieiAnnunciButton = new Button("I miei annunci");
        mieiAnnunciButton.addClickHandler(
                event -> new MieiAnnunciGui().mostra()
        );

        Button annunciButton = new Button("Esplora annunci");
        annunciButton.addClickHandler(
                event -> new AnnunciGui().mostra()
        );

        panel.add(titolo);
        panel.add(benvenuto);
        panel.add(logoutButton);
        panel.add(profiloButton);
        panel.add(creaAnnuncioButton);
        panel.add(mieiAnnunciButton);
        panel.add(annunciButton);

        RootPanel.get().add(panel);
    }
}