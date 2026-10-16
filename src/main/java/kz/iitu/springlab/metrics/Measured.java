package kz.iitu.springlab.metrics;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a method whose call count and execution time must be collected (variant 11). */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Measured {

    /** Metric name; if empty, the short method signature is used. */
    String value() default "";
}
