package de.libutzki.axon.playground.module1;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.libutzki.axon.playground.axon.client.AxonClientConfiguration;

@Configuration
@ComponentScan
@EnableScheduling
@EntityScan
@Import( AxonClientConfiguration.class )
@PropertySource( "classpath:de/libutzki/axon/playground/module1/application.properties" )
public class Module1Configuration {

}