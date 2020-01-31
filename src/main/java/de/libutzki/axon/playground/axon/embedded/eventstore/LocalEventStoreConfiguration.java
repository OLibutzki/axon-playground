package de.libutzki.axon.playground.axon.embedded.eventstore;

import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.spring.config.AxonConfiguration;
import org.axonframework.springboot.autoconfig.AxonAutoConfiguration;
import org.axonframework.springboot.autoconfig.AxonServerAutoConfiguration;
import org.axonframework.springboot.autoconfig.EventProcessingAutoConfiguration;
import org.axonframework.springboot.autoconfig.JdbcAutoConfiguration;
import org.axonframework.springboot.autoconfig.JpaAutoConfiguration;
import org.axonframework.springboot.autoconfig.JpaEventStoreAutoConfiguration;
import org.axonframework.springboot.autoconfig.MetricsAutoConfiguration;
import org.axonframework.springboot.autoconfig.MicrometerMetricsAutoConfiguration;
import org.axonframework.springboot.autoconfig.NoOpTransactionAutoConfiguration;
import org.axonframework.springboot.autoconfig.ObjectMapperAutoConfiguration;
import org.axonframework.springboot.autoconfig.TransactionAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import( {
		ValidationAutoConfiguration.class,
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		MetricsAutoConfiguration.class,
		MicrometerMetricsAutoConfiguration.class,
		EventProcessingAutoConfiguration.class,
		JpaAutoConfiguration.class,
		JpaEventStoreAutoConfiguration.class,
		JdbcAutoConfiguration.class,
		TransactionAutoConfiguration.class,
		NoOpTransactionAutoConfiguration.class,
		AxonAutoConfiguration.class,
		ObjectMapperAutoConfiguration.class,
		AxonServerAutoConfiguration.class,
		PropertyPlaceholderAutoConfiguration.class,
} )
public class LocalEventStoreConfiguration {

	@Bean
	@Primary
	EventStore localEventStore( @Qualifier( "eventStore" ) final EventStore localEventStore, @Qualifier( "embeddedEventStore" ) final EventStore globalEventStore, final org.axonframework.config.Configuration configuration ) {
		return new LocalEventStore( localEventStore, globalEventStore, configuration );
	}

	@Qualifier( "eventStore" )
	@Bean( name = "eventBus" )
	public EmbeddedEventStore eventStore( final EventStorageEngine storageEngine, final AxonConfiguration configuration ) {
		return EmbeddedEventStore.builder( )
				.storageEngine( storageEngine )
				.messageMonitor( configuration.messageMonitor( EventStore.class, "eventStore" ) )
				.build( );
	}

}
