package de.libutzki.axon.playground.module1;

import javax.inject.Named;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import de.libutzki.axon.playground.module2.Module2Command;

@Named
public class Module1Runner {

	private static final Logger log = LoggerFactory.getLogger( Module1Runner.class );

	private final CommandGateway commandGateway;
	private final EventGateway eventGateway;

	public Module1Runner( final CommandGateway commandGateway, final EventGateway eventGateway ) {
		this.commandGateway = commandGateway;
		this.eventGateway = eventGateway;
	}

	@Scheduled( fixedRate = 10000 )
	public void run( ) {
		final Module2Command command = new Module2Command( "Command sent by module 1" );
		log.info( "Sending command: " + command );
		commandGateway.sendAndWait( command );
		final Module1Event event = new Module1Event( "Event published by module 1" );
		log.info( "Publishing event: " + event );
		eventGateway.publish( event );
	}

}