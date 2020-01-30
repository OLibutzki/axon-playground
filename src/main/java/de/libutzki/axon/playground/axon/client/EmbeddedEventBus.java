package de.libutzki.axon.playground.axon.client;

import java.util.List;
import java.util.function.Consumer;

import org.axonframework.common.Registration;
import org.axonframework.common.stream.BlockingStream;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.MessageDispatchInterceptor;

import de.libutzki.axon.playground.axon.common.EmbeddedServer;

final class EmbeddedEventBus implements EventStore {

	private final EmbeddedServer embeddedServer;
	private final EventBus localEventBus;

	public EmbeddedEventBus( final EmbeddedServer embeddedServer, final EventBus localEventBus ) {
		this.embeddedServer = embeddedServer;
		this.localEventBus = localEventBus;
	}

	@Override
	public Registration subscribe( final Consumer<List<? extends EventMessage<?>>> messageProcessor ) {
		final Registration delegateRegistration = localEventBus.subscribe( messageProcessor );
		embeddedServer.registerEventBusForEvent( localEventBus, messageProcessor );
		return ( ) -> {
			final boolean delegateCancelationSuccessful = delegateRegistration.cancel( );
			final boolean wrapperCancelationSuccessful = embeddedServer.unregisterEventBusForEvent( localEventBus, messageProcessor );
			return delegateCancelationSuccessful && wrapperCancelationSuccessful;
		};
	}

	@Override
	public void publish( final List<? extends EventMessage<?>> events ) {
		embeddedServer.publish( events );

	}

	@Override
	public Registration registerDispatchInterceptor( final MessageDispatchInterceptor<? super EventMessage<?>> dispatchInterceptor ) {
		return localEventBus.registerDispatchInterceptor( dispatchInterceptor );
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
