package de.libutzki.axon.playground.infra;

import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import( PropertyPlaceholderAutoConfiguration.class )
public class SpringInfraConfiguration {

}
