package de.libutzki.axon.playground.axon.client;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.messaging.interceptors.CorrelationDataInterceptor;
import org.axonframework.queryhandling.LoggingQueryInvocationErrorHandler;
import org.axonframework.queryhandling.QueryBus;
import org.axonframework.queryhandling.QueryInvocationErrorHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.queryhandling.SimpleQueryBus;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.libutzki.axon.playground.axon.common.EmbeddedServer;

@Configuration
@ConditionalOnBean( EmbeddedServer.class )
class EmbeddedServerClientConfiguration {

	@Bean
	public CommandBus embeddedCommandBus( final EmbeddedServer embeddedServer, final AxonConfiguration axonConfiguration, final TransactionManager transactionManager ) {
		// We create the bus here to avoid that we have multiple busses in the spring conect.
		final SimpleCommandBus simpleCommandBus = SimpleCommandBus.builder( )
				.transactionManager( transactionManager )
				.messageMonitor( axonConfiguration.messageMonitor( CommandBus.class, "commandBus" ) )
				.build( );
		simpleCommandBus.registerHandlerInterceptor( new CorrelationDataInterceptor<>( axonConfiguration.correlationDataProviders( ) ) );

		return new EmbeddedCommandBus( embeddedServer, simpleCommandBus );
	}

	@Bean
	public QueryBus embeddedQueryBus( final EmbeddedServer embeddedServer, final AxonConfiguration axonConfiguration, final TransactionManager transactionManager ) {
		// We create the bus here to avoid that we have multiple busses in the spring conect. Also in the case of the query bus,
		// this is for some reason analogous to how the real axon server query bus is created.
		final SimpleQueryBus simpleQueryBus = SimpleQueryBus.builder( )
				.messageMonitor( axonConfiguration.messageMonitor( QueryBus.class, "queryBus" ) )
				.transactionManager( transactionManager )
				.queryUpdateEmitter( axonConfiguration.getComponent( QueryUpdateEmitter.class ) )
				.errorHandler( axonConfiguration.getComponent(
						QueryInvocationErrorHandler.class,
						( ) -> LoggingQueryInvocationErrorHandler.builder( ).build( ) ) )
				.build( );
		simpleQueryBus.registerHandlerInterceptor( new CorrelationDataInterceptor<>( axonConfiguration.correlationDataProviders( ) ) );

		return new EmbeddedQueryBus( embeddedServer, simpleQueryBus );
	}

}
