package de.libutzki.axon.playground.module1;

import javax.inject.Named;

import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.libutzki.axon.playground.module2.Module2Event;

@Named
public class Module1EventHandler {

	private static final Logger log = LoggerFactory.getLogger( Module1EventHandler.class );

	@EventHandler
	public void on( final Module1Event someEvent ) {
		log.info( "Event handled: " + someEvent );
	}

	@EventHandler
	public void on( final Module2Event someEvent ) {
		log.info( "Event handled: " + someEvent );
	}
}