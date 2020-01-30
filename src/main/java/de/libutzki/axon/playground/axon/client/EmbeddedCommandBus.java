package de.libutzki.axon.playground.axon.client;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.common.Registration;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.messaging.MessageHandlerInterceptor;

import de.libutzki.axon.playground.axon.common.EmbeddedServer;

/**
 * The {@code EmbeddedCommandBus} connects a local {@link CommandBus} with an {@link EmbeddedServer}. This makes sure
 * that commands can be executed across bounded context borders. All commands are basically forwarded to the embedded
 * server.
 */
final class EmbeddedCommandBus implements CommandBus {

	private final EmbeddedServer embeddedServer;
	private final CommandBus localCommandBus;

	public EmbeddedCommandBus( final EmbeddedServer embeddedServer, final CommandBus localCommandBus ) {
		this.embeddedServer = embeddedServer;
		this.localCommandBus = localCommandBus;
	}

	@Override
	public Registration registerHandlerInterceptor( final MessageHandlerInterceptor<? super CommandMessage<?>> handlerInterceptor ) {
		return localCommandBus.registerHandlerInterceptor( handlerInterceptor );
	}

	@Override
	public Registration registerDispatchInterceptor( final MessageDispatchInterceptor<? super CommandMessage<?>> dispatchInterceptor ) {
		return localCommandBus.registerDispatchInterceptor( dispatchInterceptor );
	}

	@Override
	public <C, R> void dispatch( final CommandMessage<C> command, final CommandCallback<? super C, ? super R> callback ) {
		embeddedServer.dispatch( command, callback );
	}

	@Override
	public Registration subscribe( final String commandName, final MessageHandler<? super CommandMessage<?>> handler ) {
		final Registration delegateRegistration = localCommandBus.subscribe( commandName, handler );
		embeddedServer.registerCommandBusForCommand( localCommandBus, commandName );
		return ( ) -> {
			final boolean delegateCancelationSuccessful = delegateRegistration.cancel( );
			final boolean wrapperCancelationSuccessful = embeddedServer.unregisterCommandBusForCommand( commandName );
			return delegateCancelationSuccessful && wrapperCancelationSuccessful;
		};
	}

	@Override
	public <C> void dispatch( final CommandMessage<C> command ) {
		dispatch( command, LoggingCallback.INSTANCE );
	}

}
