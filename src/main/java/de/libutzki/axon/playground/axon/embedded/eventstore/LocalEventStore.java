package de.libutzki.axon.playground.axon.embedded.eventstore;

import java.util.List;
import java.util.function.Consumer;

import org.axonframework.common.Registration;
import org.axonframework.common.stream.BlockingStream;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventsourcing.MultiStreamableMessageSource;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.MessageDispatchInterceptor;

public class LocalEventStore implements EventStore {

	private final EventStore localEventStore;
	private final EventStore globalEventStore;

	private final MultiStreamableMessageSource messageSource;

	public LocalEventStore( final EventStore localEventStore, final EventStore globalEventStore ) {
		this.localEventStore = localEventStore;
		this.globalEventStore = globalEventStore;
		messageSource = MultiStreamableMessageSource.builder( )
				.addMessageSource( "localEventStore", localEventStore )
				.addMessageSource( "globalEventStore", globalEventStore )
				.build( );
	}

	@Override
	public void publish( final List<? extends EventMessage<?>> events ) {
		localEventStore.publish( events );
	}

	@Override
	public Registration registerDispatchInterceptor( final MessageDispatchInterceptor<? super EventMessage<?>> dispatchInterceptor ) {
		final Registration localEventStoreRegistration = localEventStore.registerDispatchInterceptor( dispatchInterceptor );
		final Registration globalEventStoreRegistration = globalEventStore.registerDispatchInterceptor( dispatchInterceptor );
		return ( ) -> {
			final boolean localEventStoreCancelationSuccessful = localEventStoreRegistration.cancel( );
			final boolean globalEventStoreCancelationSuccessful = globalEventStoreRegistration.cancel( );
			return localEventStoreCancelationSuccessful && globalEventStoreCancelationSuccessful;
		};
	}

	@Override
	public Registration subscribe( final Consumer<List<? extends EventMessage<?>>> messageProcessor ) {
		final Registration localEventStoreRegistration = localEventStore.subscribe( messageProcessor );
		final Registration globalEventStoreRegistration = globalEventStore.subscribe( messageProcessor );
		return ( ) -> {
			final boolean localEventStoreCancelationSuccessful = localEventStoreRegistration.cancel( );
			final boolean globalEventStoreCancelationSuccessful = globalEventStoreRegistration.cancel( );
			return localEventStoreCancelationSuccessful && globalEventStoreCancelationSuccessful;
		};
	}

	@Override
	public BlockingStream<TrackedEventMessage<?>> openStream( final TrackingToken trackingToken ) {

		return messageSource.openStream( trackingToken );
	}

	@Override
	public DomainEventStream readEvents( final String aggregateIdentifier ) {
		final DomainEventStream localEventStoreEventStream = localEventStore.readEvents( aggregateIdentifier );
		final DomainEventStream globalEventStoreEventStream = globalEventStore.readEvents( aggregateIdentifier );
		return DomainEventStream.concat( localEventStoreEventStream, globalEventStoreEventStream );
	}

	@Override
	public void storeSnapshot( final DomainEventMessage<?> snapshot ) {
		localEventStore.storeSnapshot( snapshot );
		globalEventStore.storeSnapshot( snapshot );
	}

}
