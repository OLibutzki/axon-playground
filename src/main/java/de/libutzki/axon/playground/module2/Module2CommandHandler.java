package de.libutzki.axon.playground.module2;

import javax.inject.Named;

import org.axonframework.commandhandling.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class Module2CommandHandler {

	private static final Logger log = LoggerFactory.getLogger( Module2CommandHandler.class );

	@CommandHandler
	public void handle( final Module2Command someCommand ) {
		log.info( "Command handled: " + someCommand );
	}

}