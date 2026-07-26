package it.unibo;

import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {


	/**
	 * Mostra la schermata di registrazione all'avvio.
	 */
	@Override
	public void onModuleLoad() {
		new LoginGui().mostra();
	}
}
