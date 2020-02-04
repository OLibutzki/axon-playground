package de.libutzki.axon.playground.module2;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.libutzki.axon.playground.axon.client.AxonClientConfiguration;

@Configuration
@ComponentScan
@EnableScheduling
@Import( AxonClientConfiguration.class )
@PropertySource( "classpath:de/libutzki/axon/playground/module2/application.properties" )
public class Module2Configuration {

}