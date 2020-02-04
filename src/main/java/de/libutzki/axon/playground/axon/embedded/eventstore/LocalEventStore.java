package de.libutzki.axon.playground.axon.embedded.eventstore;

import java.util.List;
import java.util.function.Consumer;

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
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalEventStore implements EventStore {

	private static final Logger log = LoggerFactory.getLogger( LocalEventStore.class );

	private final EventStore localEventStore;
	private final EventStore globalEventStore;
	private final String moduleName;

	private final MultiStreamableMessageSource messageSource;
	private final TrackingEventProcessor trackingEventProcessor;

	private final GlobalEventPublisher localEventHandlerInvoker;

	public LocalEventStore( final EventStore localEventStore, final EventStore globalEventStore, final Configuration configuration, final String moduleName ) {
		this.localEventStore = localEventStore;
		this.globalEventStore = globalEventStore;
		this.moduleName = moduleName;
		messageSource = MultiStreamableMessageSource.builder( )
				.addMessageSource( "localEventStore", localEventStore )
				.addMessageSource( "globalEventStore", globalEventStore )
				.build( );
		localEventHandlerInvoker = new GlobalEventPublisher( globalEventStore, moduleName );
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
		final BlockingStream<TrackedEventMessage<?>> blockingStream = new FilteringBlockingStream<>( delegateStream, candidate -> !candidate.getMetaData( ).getOrDefault( "moduleName", "" ).equals( moduleName ) );
		return blockingStream;
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
