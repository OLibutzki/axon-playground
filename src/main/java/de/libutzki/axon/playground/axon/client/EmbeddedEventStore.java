package de.libutzki.axon.playground.axon.client;

import java.util.List;
import java.util.function.Consumer;

import org.axonframework.common.Registration;
import org.axonframework.common.stream.BlockingStream;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.MessageDispatchInterceptor;

import de.libutzki.axon.playground.axon.common.EmbeddedServer;

public final class EmbeddedEventStore implements EventStore {

	private final EmbeddedServer embeddedServer;

	public EmbeddedEventStore( final EmbeddedServer embeddedServer ) {
		this.embeddedServer = embeddedServer;
	}

	@Override
	public Registration subscribe( final Consumer<List<? extends EventMessage<?>>> eventProcessor ) {
		return embeddedServer.registerEventProcessor( eventProcessor );
	}

	@Override
	public void publish( final List<? extends EventMessage<?>> events ) {
		embeddedServer.publish( events );

	}

	@Override
	public Registration registerDispatchInterceptor( final MessageDispatchInterceptor<? super EventMessage<?>> dispatchInterceptor ) {
		return embeddedServer.registerDispatchInterceptor( dispatchInterceptor );
	}

	@Override
	public BlockingStream<TrackedEventMessage<?>> openStream( final TrackingToken trackingToken ) {
		return embeddedServer.openStream( trackingToken );
	}

	@Override
	public DomainEventStream readEvents( final String aggregateIdentifier ) {
		return embeddedServer.readEvents( aggregateIdentifier );
	}

	@Override
	public void storeSnapshot( final DomainEventMessage<?> snapshot ) {
		embeddedServer.storeSnapshot( snapshot );
	}

}
