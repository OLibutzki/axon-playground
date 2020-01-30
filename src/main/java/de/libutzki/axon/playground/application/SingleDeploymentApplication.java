package de.libutzki.axon.playground.application;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;

import de.libutzki.axon.playground.axon.embedded.server.EmbeddedServerConfiguration;
import de.libutzki.axon.playground.module1.Module1Configuration;
import de.libutzki.axon.playground.module2.Module2Configuration;

@Configuration
public class SingleDeploymentApplication {

	public static void main( final String[] args ) {
		new SpringApplicationBuilder( )
				.parent( SingleDeploymentApplication.class )
				.logStartupInfo( false )
				.web( WebApplicationType.NONE )

				.child( EmbeddedServerConfiguration.class )
				.web( WebApplicationType.NONE )

				.sibling( Module1Configuration.class )
				.web( WebApplicationType.NONE )

				.sibling( Module2Configuration.class )
				.web( WebApplicationType.NONE )

				.run( args );

	}

}