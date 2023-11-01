package di_ioc_engine.anotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Autowired {
    boolean verbose() default false;
}


