package de.libutzki.axon.playground.infra.export;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Diese Annotation wird verwendet um ein Spring-Bean zu markieren, welches nicht nur in dem eigenen Spring-Kontext
 * vorhanden ist, sondern auch in der übergeordneten Parent-Context übernommen wird. Falls kein solcher Context
 * existiert, wird die Annotation ignoriert. Diese Annotation sollte nur bei Singleton-Beans verwendet werden und auch
 * nur bei solchen, die innerhalb einer Configuration mittels einer Methode deklariert werden.
 *
 * @author nils-christian.ehmke
 *
 * @see ExportingBeanPostProcessor
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface Exported {
}
