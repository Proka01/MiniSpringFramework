package aspects;

import di_ioc_engine.DI_Engine;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import tmp.ProkicClass;

@Aspect
public class BeforeMain {
    @Before("execution(public static void server.Server.main(String[]))")
    public void beforeMainMethod() {
        System.out.println("Aspect is running before the main method");

        DI_Engine di_engine = DI_Engine.getInstance();
        di_engine.initializeAllBeans();
        ProkicClass prokicClass = new ProkicClass();
        prokicClass = (ProkicClass) di_engine.findInstanceByType(di_engine.beanInstancesRegistry, prokicClass.getClass());

        prokicClass.f();
    }
}
