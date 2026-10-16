package kz.iitu.springlab.audit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) // must be visible at run time, otherwise @annotation(..) finds nothing
@Documented
public @interface Audited {

    String action();

    boolean logArguments() default false;
}
