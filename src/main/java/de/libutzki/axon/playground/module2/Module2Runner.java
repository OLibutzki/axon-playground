package de.libutzki.axon.playground.module2;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Named;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import de.libutzki.axon.playground.module1.CreateModule1Aggregate;

@Named
public class Module2Runner {

	private static final Logger log = LoggerFactory.getLogger( Module2Runner.class );

	private final CommandGateway commandGateway;

	private final AtomicInteger counter = new AtomicInteger( );

	public Module2Runner( final CommandGateway commandGateway ) {
		this.commandGateway = commandGateway;
	}

	@Scheduled( fixedRate = 10000 )
	public void run( ) {
		final CreateModule1Aggregate command = new CreateModule1Aggregate( "Module1Aggregate-" + counter.getAndIncrement( ) );
		log.info( "Sending command: " + command );
		commandGateway.sendAndWait( command );
	}

}