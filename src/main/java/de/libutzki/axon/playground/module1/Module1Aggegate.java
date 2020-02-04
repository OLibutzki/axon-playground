package de.libutzki.axon.playground.module1;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
@Entity
public class Module1Aggegate {

	private static final Logger log = LoggerFactory.getLogger( Module1Aggegate.class );
	@Id
	@AggregateIdentifier
	private String id;

	@CommandHandler
	private Module1Aggegate( final CreateModule1Aggregate createModule1Aggregate ) {
		id = createModule1Aggregate.getId( );

		final Module1CreatedLocalEvent module1CreatedLocalEvent = new Module1CreatedLocalEvent( id );
		log.info( "Publishing event: " + module1CreatedLocalEvent );
		AggregateLifecycle.apply( module1CreatedLocalEvent );

		final Module1CreatedGlobalEvent module1CreatedGlobalEvent = new Module1CreatedGlobalEvent( id );
		log.info( "Publishing event: " + module1CreatedGlobalEvent );
		AggregateLifecycle.apply( module1CreatedGlobalEvent );
	}
}
