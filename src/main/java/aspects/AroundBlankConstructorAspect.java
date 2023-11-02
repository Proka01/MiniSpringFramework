package aspects;

import di_ioc_engine.DI_Engine;
import di_ioc_engine.anotations.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;

@Aspect
public class AroundBlankConstructorAspect {

    /**
     * DEPENDENCY INJECTION LOGIC:
     *
     * if clazz is singleton bean and present in registry return it from registry
     *
     * if clazz has no @Autowired fields make and instance and mark it in beanInstanceRegistry if it is singleton bean
     *
     * if clazz has some @Autowired fields for each of them:
     *      1. determent if field is interface and from dependencyContainer take appropriate clazz for injection
     *      2. first if they are scope=singleton try to inject them from beanInstanceRegistry if they are present
     *      3. or if not call function aspect recursively with .getDeclaredConstructor().newInstance();
     *
     * when all @Autowired fields are satisfied,
     * mark clazzInstance in beanInstanceRegistry if class is bean whit scope="singleton"
     *
     * return clazz instance
     * */
    @Around("execution(*.new(..)) && ( within(tmp.*) || within(server.*) || within(framework.*))")
    public Object aroundClassConstructor(ProceedingJoinPoint joinPoint) throws Throwable {
        DI_Engine di_engine = DI_Engine.getInstance();

        joinPoint.proceed(); // Proceed with the constructor execution and get the newly created instance

        //Make instance of clazz which needs to be processed
        Object clazzInstance = joinPoint.getTarget(); // Capture the newly created instance using joinPoint.getTarget()
        Class<?> clazz = clazzInstance.getClass();

        //if clazz is a singleton bean and is already instanced in registry, return it
        if(di_engine.isClassBean(clazz))
        {
            if(di_engine.isScopeSingleton(clazz))
            {
                Object injectInstance = di_engine.findInstanceByType(di_engine.beanInstancesRegistry,clazz);
                if(injectInstance != null) return injectInstance;
            }
        }

        //if clazz is not in registry and
        //If clazz has no @Autowired fields and is singleton, mark it in beanInstancesRegistry and return
        if(!di_engine.hasAutowiredFields(clazz))
        {
            if(di_engine.isClassBean(clazz) && di_engine.isScopeSingleton(clazz))
                 di_engine.beanInstancesRegistry.add(clazzInstance);
        }
        //else all @Autowired fields need to be injected first
        //  if beanInstancesRegistry has that field just inject it
        //  otherwise call function recursively
        else
        {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields)
                analyzeAndInitField(f,clazzInstance);

            //after all @Autowired fields are processed,
            // mark clazzInstance in registry if its bean is scope=singleton
            // and then return it
            if(di_engine.isClassBean(clazz) && di_engine.isScopeSingleton(clazz))
                di_engine.beanInstancesRegistry.add(clazzInstance);
        }

        return  clazzInstance;
    }

    /** analyzeAndInitField() - injects @Autowired fields
     * - it just a wrapper for code, in order to have cleaner aspect
     *
     * if clazz has some @Autowired fields for each of them:
     *      1. determent if field is interface and from dependencyContainer take appropriate clazz for injection
     *      2. first try to inject them from beanInstanceRegistry if they are present
     *      3. or if not call function recursively (by calling initBean())
     * */
    private void analyzeAndInitField(Field f, Object clazzInstance)
    {
        DI_Engine di_engine = DI_Engine.getInstance();
        boolean isAccessible = f.isAccessible();
        f.setAccessible(true);

        Autowired a = f.getAnnotation(Autowired.class);

        if (a != null)
        {
            Class<?> fieldInjectType = f.getType();
            if(fieldInjectType.isInterface())
            {
                Qualifier qualifier = f.getAnnotation(Qualifier.class);
                if(qualifier != null) fieldInjectType = di_engine.dependencyContainer.get(qualifier.value());
                else System.out.println("ERROR @Autowired interface need to have @Qualifier");
            }

            try
            {
                Object fieldInjectInstance = di_engine.findInstanceByType(di_engine.beanInstancesRegistry, fieldInjectType);
                if(fieldInjectInstance == null) fieldInjectInstance = fieldInjectType.getDeclaredConstructor().newInstance();

                f.set(clazzInstance,fieldInjectInstance);
            }
            catch (Exception e) {e.printStackTrace();}
        }

        f.setAccessible(isAccessible);
    }
}
