package de.libutzki.axon.playground.axon.embedded.outbox;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.axonframework.common.Registration;
import org.axonframework.common.stream.BlockingStream;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configuration;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventsourcing.MultiStreamableMessageSource;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.FilteringDomainEventStream;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutboxEventStore implements EventStore {

	@SuppressWarnings( "unused" )
	private static final Logger log = LoggerFactory.getLogger( OutboxEventStore.class );

	private final EventStore localEventStore;
	private final EventStore globalEventStore;

	private final MultiStreamableMessageSource messageSource;
	private final TrackingEventProcessor trackingEventProcessor;

	private final GlobalEventPublisher localEventHandlerInvoker;

	private final Predicate<? extends EventMessage<?>> filter;

	public OutboxEventStore( final EventStore localEventStore, final EventStore globalEventStore, final Configuration configuration, final String origin ) {
		this.localEventStore = localEventStore;
		this.globalEventStore = globalEventStore;
		filter = eventMessage -> !eventMessage.getMetaData( ).getOrDefault( MetadataKeys.ORIGIN, "" ).equals( origin );
		messageSource = MultiStreamableMessageSource.builder( )
				.addMessageSource( "globalEventStore", globalEventStore )
				.addMessageSource( "localEventStore", localEventStore )
				.build( );
		localEventHandlerInvoker = new GlobalEventPublisher( globalEventStore, origin );
		trackingEventProcessor = TrackingEventProcessor.builder( )
				.name( "localEventStoreTracker" )
				.eventHandlerInvoker( localEventHandlerInvoker )
				.messageMonitor( configuration.messageMonitor( TrackingEventProcessor.class, "localEventStoreTracker" ) )
				.messageSource( localEventStore )
				.tokenStore( configuration.getComponent( TokenStore.class ) )
				.transactionManager( configuration.getComponent( TransactionManager.class ) )
				.build( );
		trackingEventProcessor.start( );
	}

	public void shutdown( ) {
		trackingEventProcessor.shutDown( );
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
		final BlockingStream<TrackedEventMessage<?>> delegateStream = messageSource.openStream( trackingToken );
		@SuppressWarnings( "unchecked" )
		final BlockingStream<TrackedEventMessage<?>> blockingStream = new FilteringBlockingStream<>( delegateStream, ( Predicate<TrackedEventMessage<?>> ) filter );
		return blockingStream;
	}

	@Override
	public DomainEventStream readEvents( final String aggregateIdentifier ) {
		final DomainEventStream localEventStoreEventStream = localEventStore.readEvents( aggregateIdentifier );
		final DomainEventStream globalEventStoreEventStream = globalEventStore.readEvents( aggregateIdentifier );
		@SuppressWarnings( "unchecked" )
		final FilteringDomainEventStream filteredGlobalEventStoreEventStream = new FilteringDomainEventStream( globalEventStoreEventStream, ( Predicate<? super DomainEventMessage<?>> ) filter );
		return DomainEventStream.concat( localEventStoreEventStream, filteredGlobalEventStoreEventStream );
	}

	@Override
	public void storeSnapshot( final DomainEventMessage<?> snapshot ) {
		localEventStore.storeSnapshot( snapshot );
		globalEventStore.storeSnapshot( snapshot );
	}
}
