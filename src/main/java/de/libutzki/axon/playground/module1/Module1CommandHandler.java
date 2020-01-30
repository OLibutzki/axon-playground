package de.libutzki.axon.playground.module1;

import javax.inject.Named;

import org.axonframework.commandhandling.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class Module1CommandHandler {

	private static final Logger log = LoggerFactory.getLogger( Module1CommandHandler.class );

	@CommandHandler
	public void handle( final Module1Command someCommand ) {
		log.info( "Command handled: " + someCommand );
	}

}