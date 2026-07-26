package it.unibo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Schermata per l'accesso degli utenti registrati.
 */
public class LoginGui {

    private final UtenteServiceAsync utenteService =
            GWT.create(UtenteService.class);

    /**
     * Mostra il form di accesso.
     */
    public void mostra() {
        RootPanel.get().clear();

        VerticalPanel panel = new VerticalPanel();
        panel.setSpacing(10);

        Label titolo = new Label("Accesso SkillShare");

        TextBox nomeUtenteBox = new TextBox();
        nomeUtenteBox.getElement().setPropertyString(
                "placeholder",
                "Nome utente"
        );

        PasswordTextBox passwordBox = new PasswordTextBox();
        passwordBox.getElement().setPropertyString(
                "placeholder",
                "Password"
        );

        Button loginButton = new Button("Accedi");

        Label messaggio = new Label();

        loginButton.addClickHandler(event -> {
            String nomeUtente = nomeUtenteBox.getText().trim();
            String password = passwordBox.getText();

            if (nomeUtente.isEmpty() || password.isEmpty()) {
                messaggio.setText(
                        "Inserisci nome utente e password"
                );
                return;
            }

            utenteService.login(
                    nomeUtente,
                    password,
                    new AsyncCallback<Utente>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            messaggio.setText(
                                    "Errore di comunicazione con il server"
                            );
                        }

                        @Override
                        public void onSuccess(Utente utente) {
                            if (utente == null) {
                                messaggio.setText(
                                        "Nome utente o password non validi"
                                );
                                return;
                            }

                            SessioneUtente.accedi(utente);
                            new HomeGui().mostra();
                        }
                    }
            );
        });

        panel.add(titolo);
        panel.add(nomeUtenteBox);
        panel.add(passwordBox);
        panel.add(loginButton);
        panel.add(messaggio);
        panel.add(NavigationHelper.creaBottoneVaiAllaRegistrazione());

        RootPanel.get().add(panel);
    }
}