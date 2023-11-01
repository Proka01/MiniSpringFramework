package di_ioc_engine.anotations;

import java.lang.annotation.*;

/*
 * Na kom nivou ce anotacija biti dostupna: source/class/runtime
 */
@Retention(RetentionPolicy.RUNTIME)
/*
 * Samo klase/interfejs/enum se mogu anotiarti ovom anotacijom
 */
@Target(ElementType.TYPE)
/*
 * Oznacava da se anotacija automatski naledjuje
 */
@Inherited
public @interface MyBean {
    String scope() default "singleton";
}
