package de.libutzki.axon.playground.axon.embedded.eventstore;

import org.axonframework.springboot.autoconfig.AxonAutoConfiguration;
import org.axonframework.springboot.autoconfig.AxonServerAutoConfiguration;
import org.axonframework.springboot.autoconfig.EventProcessingAutoConfiguration;
import org.axonframework.springboot.autoconfig.InfraConfiguration;
import org.axonframework.springboot.autoconfig.JdbcAutoConfiguration;
import org.axonframework.springboot.autoconfig.JpaAutoConfiguration;
import org.axonframework.springboot.autoconfig.JpaEventStoreAutoConfiguration;
import org.axonframework.springboot.autoconfig.MetricsAutoConfiguration;
import org.axonframework.springboot.autoconfig.MicrometerMetricsAutoConfiguration;
import org.axonframework.springboot.autoconfig.NoOpTransactionAutoConfiguration;
import org.axonframework.springboot.autoconfig.ObjectMapperAutoConfiguration;
import org.axonframework.springboot.autoconfig.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
		InfraConfiguration.class,
		PropertyPlaceholderAutoConfiguration.class,
} )
public class EmbeddedEventStoreConfiguration {

}
