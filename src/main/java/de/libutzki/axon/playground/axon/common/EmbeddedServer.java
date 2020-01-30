package de.libutzki.axon.playground.axon.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.NoHandlerForCommandException;
import org.axonframework.queryhandling.NoHandlerForQueryException;
import org.axonframework.queryhandling.QueryBus;
import org.axonframework.queryhandling.QueryMessage;
import org.axonframework.queryhandling.QueryResponseMessage;

/**
 * The {@code EmbeddedServer} replaces the real axon server in case that we do not want to distribute the application.
 * In this case all communication is performed within the JVM and via this server.<br>
 * <br>
 * The {@code EmbeddedServer} is threadsafe, as long as the registered {@link CommandBus CommandBusses} and
 * {@link QueryBus QueryBusses} are threadsafe.
 */
public interface EmbeddedServer {

	/**
	 * Dispatches the given command to the responsible command bus.
	 *
	 * @param <C>
	 *                 The type of payload of the command.
	 * @param <R>
	 *                 The type of result of the command handling.
	 * @param command
	 *                 The command to dispatch.
	 * @param callback
	 *                 The corresponding callback.
	 *
	 * @throws NoHandlerForCommandException
	 *                                      If there is currently no command bus registered to handle the command.
	 */
	<C, R> void dispatch( CommandMessage<C> command, CommandCallback<? super C, ? super R> callback );

	/**
	 * Registers the given command bus for the given command name.
	 *
	 * @param commandBus
	 *                    The command bus to be registered
	 * @param commandName
	 *                    The command name.
	 *
	 * @throws IllegalStateException
	 *                               If there is already a command bus registered for the given command name.
	 */
	void registerCommandBusForCommand( CommandBus commandBus, String commandName );

	/**
	 * Unregisters the command bus for the given command name.
	 *
	 * @param commandName
	 *                    The command name.
	 *
	 * @return true if and only if the command bus for the given command name has been removed.
	 */
	boolean unregisterCommandBusForCommand( String commandName );

	/**
	 * Registers the given query bus for the given query name.
	 *
	 * @param queryBus
	 *                  The query bus to be registered
	 * @param queryName
	 *                  The command name.
	 */
	void registerQueryBusForQuery( QueryBus queryBus, String queryName );

	/**
	 * Unregisters the given query bus for the given query name.
	 *
	 * @param queryBus
	 *                  The query bus to be registered
	 * @param queryName
	 *                  The command name.
	 *
	 * @return true if and only if the query bus has been removed.
	 */
	boolean unregisterQueryBusForQuery( QueryBus localQueryBus, String queryName );

	/**
	 * Executes the given query with the responsible query bus.
	 *
	 * @param <Q>
	 *              The type of payload of the query.
	 * @param <R>
	 *              The type of result of the query.
	 * @param query
	 *              The query to execute.
	 * 
	 * @return The result of the query execution.
	 *
	 * @throws NoHandlerForQueryException
	 *                                    If there is no or multiple handlers for the given query.
	 */
	<Q, R> CompletableFuture<QueryResponseMessage<R>> query( QueryMessage<Q, R> query );

	<Q, R> Stream<QueryResponseMessage<R>> scatterGather( QueryMessage<Q, R> query, long timeout, TimeUnit unit );

}