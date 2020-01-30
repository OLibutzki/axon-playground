package de.libutzki.axon.playground.infra.export;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.type.StandardMethodMetadata;

/**
 * Dieser {@link BeanPostProcessor} scannt die Beans danach, ob sie mit {@link Exported} markiert sind oder ihr Name im
 * <i>exportedbeannames</i> Property enthalten ist. Falls dem so ist, so wird das Bean in den übergeordneten
 * Parent-Context mit übernommen.
 *
 * @see Exported
 */
@Named
class ExportingBeanPostProcessor implements BeanPostProcessor {

	private final ConfigurableListableBeanFactory beanFactory;
	private final List<String> exportedBeanNames;
	private final String moduleName;

	@Inject
	ExportingBeanPostProcessor( final ConfigurableListableBeanFactory beanFactory, @Value( "${modulename}" ) final String moduleName, @Value( "${exportedbeannames:}#{T(java.util.Collections).emptyList()}" ) final List<String> exportedBeanNames ) {
		this.beanFactory = beanFactory;
		this.exportedBeanNames = exportedBeanNames;
		this.moduleName = moduleName;
	}

	@Override
	public Object postProcessBeforeInitialization( final Object bean, final String beanName ) {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization( final Object bean, final String beanName ) {
		if ( exportedBeanNames.contains( beanName ) ) {
			exportBeanToParentApplicationContext( bean, beanName );
		} else {
			final BeanDefinition bd;
			try {
				bd = beanFactory.getBeanDefinition( beanName );
			} catch ( final NoSuchBeanDefinitionException exception ) {
				return bean;
			}

			if ( bd.getSource( ) instanceof StandardMethodMetadata ) {
				final StandardMethodMetadata metadata = ( StandardMethodMetadata ) bd.getSource( );

				if ( metadata.isAnnotated( Exported.class.getName( ) ) ) {
					exportBeanToParentApplicationContext( bean, beanName );
				}
			}
		}

		return bean;
	}

	private void exportBeanToParentApplicationContext( final Object bean, final String beanName ) {
		final ConfigurableListableBeanFactory parentBeanFactory = getParentBeanFactory( );
		if ( parentBeanFactory == null ) {
			throw new IllegalStateException( "Das Bean " + beanName + " kann nicht exportiert werden, da kein Parent Application Context vorhanden ist." );
		}

		final String uniqueBeanName = moduleName + "." + beanName;
		parentBeanFactory.registerSingleton( uniqueBeanName, bean );
	}

	private ConfigurableListableBeanFactory getParentBeanFactory( ) {
		final BeanFactory parent = beanFactory.getParentBeanFactory( );
		return parent instanceof ConfigurableListableBeanFactory ? ( ConfigurableListableBeanFactory ) parent : null;
	}
}
