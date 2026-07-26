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


        panel.add(titolo);
        panel.add(benvenuto);
        panel.add(logoutButton);
        panel.add(profiloButton);

        RootPanel.get().add(panel);
    }
}