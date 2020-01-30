package de.libutzki.axon.playground.axon.embedded.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import de.libutzki.axon.playground.axon.common.EmbeddedServer;
import de.libutzki.axon.playground.infra.SpringInfraConfiguration;

@Configuration
@PropertySource( "classpath:de/libutzki/axon/playground/axon/embedded/server/application.properties" )
@Import( {
		SpringInfraConfiguration.class } )
public class EmbeddedServerConfiguration {

	@Bean
	public EmbeddedServer embeddedServer( ) {
		return new DefaultEmbeddedServer( );
	}

}