package de.libutzki.axon.playground.module2;

import javax.inject.Named;

import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.libutzki.axon.playground.module1.Module1Event;

@Named
public class Module2EventHandler {

	private static final Logger log = LoggerFactory.getLogger( Module2EventHandler.class );

	@EventHandler
	public void on( final Module1Event someEvent ) {
		log.info( "Event handled: " + someEvent );
	}

	@EventHandler
	public void on( final Module2Event someEvent ) {
		log.info( "Event handled: " + someEvent );
	}
}