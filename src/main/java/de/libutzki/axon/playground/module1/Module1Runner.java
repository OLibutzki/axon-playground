package de.libutzki.axon.playground.module1;

import javax.inject.Named;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;

import de.libutzki.axon.playground.module2.Module2Command;

@Named
public class Module1Runner implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger( Module1Runner.class );

	private final CommandGateway commandGateway;
	private final EventGateway eventGateway;

	public Module1Runner( final CommandGateway commandGateway, final EventGateway eventGateway ) {
		this.commandGateway = commandGateway;
		this.eventGateway = eventGateway;
	}

	@Override
	@Async
	public void run( final String... args ) throws Exception {
		Thread.sleep( 2000 );
		final Module2Command command = new Module2Command( "Command sent by module 1" );
		log.info( "Sending command: " + command );
		commandGateway.sendAndWait( command );
		final Module1Event event = new Module1Event( "Event publised by module 1" );
		log.info( "Publishing event: " + event );
		eventGateway.publish( event );
	}

}