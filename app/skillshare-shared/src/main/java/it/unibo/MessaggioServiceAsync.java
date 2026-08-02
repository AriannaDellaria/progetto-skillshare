package it.unibo;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface MessaggioServiceAsync {

    void apriChat(
            String idRichiesta,
            String idUtenteCorrente,
            AsyncCallback<List<Messaggio>> callback
    );
}
