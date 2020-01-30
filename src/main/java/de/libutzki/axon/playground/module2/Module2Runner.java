package de.libutzki.axon.playground.module2;

import javax.inject.Named;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;

import de.libutzki.axon.playground.module1.Module1Command;

@Named
public class Module2Runner implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger( Module2Runner.class );

	private final CommandGateway commandGateway;
	private final EventGateway eventGateway;

	public Module2Runner( final CommandGateway commandGateway, final EventGateway eventGateway ) {
		this.commandGateway = commandGateway;
		this.eventGateway = eventGateway;
	}

	@Override
	@Async
	public void run( final String... args ) throws Exception {
		Thread.sleep( 2000 );
		final Module1Command command = new Module1Command( "Command sent by module 2" );
		log.info( "Sending command: " + command );
		commandGateway.sendAndWait( command );
		final Module2Event event = new Module2Event( "Event published by module 2" );
		log.info( "Publishing event: " + event );
		eventGateway.publish( event );
	}

}