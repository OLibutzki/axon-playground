package de.libutzki.axon.playground.axon.embedded.outbox;

import java.util.Collections;

import org.axonframework.eventhandling.EventHandlerInvoker;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.Segment;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class GlobalEventPublisher implements EventHandlerInvoker {

	private static final Logger log = LoggerFactory.getLogger( OutboxEventStore.class );

	private final EventStore globalEventStore;
	private final String origin;

	public GlobalEventPublisher( final EventStore globalEventStore, final String origin ) {
		this.globalEventStore = globalEventStore;
		this.origin = origin;
	}

	@Override
	public boolean canHandle( final EventMessage<?> eventMessage, final Segment segment ) {
		final Class<?> payloadType = eventMessage.getPayloadType( );
		return !payloadType.isAnnotationPresent( LocalEvent.class );
	}

	@Override
	public void handle( final EventMessage<?> eventMessage, final Segment segment ) throws Exception {
		log.debug( "Event published to global event store: " + eventMessage );
		// How to handle exceptions? Transaction?
		globalEventStore.publish( eventMessage.andMetaData( Collections.singletonMap( MetadataKeys.ORIGIN, origin ) ) );

	}

}
