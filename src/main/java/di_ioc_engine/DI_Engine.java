package di_ioc_engine;

import di_ioc_engine.anotations.Autowired;
import di_ioc_engine.anotations.Component;
import di_ioc_engine.anotations.MyBean;
import di_ioc_engine.anotations.Service;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DI_Engine {
    private static DI_Engine instance;

    public List<Class<?>> beanClassesRegistry = new ArrayList<>();
    public List<Object> beanInstancesRegistry = new ArrayList<>();


    private DI_Engine() {
    }
    public static DI_Engine getInstance() {
        if (instance == null) {
            instance = new DI_Engine();
        }
        return instance;
    }

    private boolean isScopeSingleton(Class<?> clazz)
    {
        MyBean mb = clazz.getAnnotation(MyBean.class);
        Component comp = clazz.getAnnotation(Component.class);
        Service serv = clazz.getAnnotation(Service.class);

        if(mb != null) return mb.scope().equals("singleton");
        if(comp != null) return comp.scope().equals("singleton");
        if(serv != null) return serv.scope().equals("singleton");

        return false;
    }

    public void initializeAllBeans()
    {
        // Define the base package(s) you want to scan
        // Change this to your project's base package
        String basePackage = "di_ioc_engine";

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(new SubTypesScanner(false))
        );

        // Get all the classes in the base package and its subpackages
        for (Class<?> clazz : reflections.getSubTypesOf(Object.class)) {
            MyBean mb = clazz.getAnnotation(MyBean.class);
            Component comp = clazz.getAnnotation(Component.class);
            Service serv = clazz.getAnnotation(Service.class);

            //if class is a bean, make instance of it and save it to clazz and instance registry
            if(mb != null || comp != null || serv != null)
            {
                beanClassesRegistry.add(clazz);
                initBean(clazz);
            }
        }
    }


    /**
     * if clazz has no @Autowired fields make and instance and mark it in beanInstanceRegistry
     *
     * if clazz has some @Autowired fields for each of them first try to inject them from beanInstanceRegistry if
     * they are present, or if not call function recursively
     *
     * when all @Autowired fields are satisfied, mark clazzInstance in beanInstanceRegistry and return
     * */
    private Object initBean(Class<?> clazz)
    {
        //Make instance of clazz which needs to be processed
        Object clazzInstance = null;
        try { clazzInstance = clazz.getDeclaredConstructor().newInstance();}
        catch (Exception e) {e.printStackTrace();}

        //If clazz has no @Autowired fields, mark it in beanInstancesRegistry and return
        if(!hasAutowiredFields(clazz))
        {
            beanInstancesRegistry.add(clazzInstance);
            return clazzInstance;
        }
        //else all @Autowired fields need to be injected first
        //  if beanInstancesRegistry has that field just inject it
        //  otherwise call function recursively
        else
        {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields)
            {
                boolean isAccessible = f.isAccessible();
                f.setAccessible(true);

                Autowired a = f.getAnnotation(Autowired.class);

                if (a != null)
                {
                    try
                    {
                        Object injectInstance = findInstanceByType(beanInstancesRegistry, f.getType());
                        if(injectInstance == null) injectInstance = initBean(f.getType());

                        f.set(clazzInstance,injectInstance);
                    }
                    catch (Exception e) {e.printStackTrace();}
                }

                f.setAccessible(isAccessible);
            }

            //after all @Autowired fields are processed, mark clazzInstance in registry and return it
            beanInstancesRegistry.add(clazzInstance);
            return  clazzInstance;
        }
    }

    private boolean hasAutowiredFields(Class<?> clazz)
    {
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            Autowired a = f.getAnnotation(Autowired.class);

            if (a != null) return true;
        }

        return false;
    }

    public Object findInstanceByType(List<Object> instances, Class<?> desiredClass) {
        for (Object instance : instances) {
            if (instance.getClass().equals(desiredClass)) {
                return instance;
            }
        }
        return null;
    }


    public void printAllBeans()
    {
        for(Class<?> clazz : beanClassesRegistry)
            System.out.println(clazz.getSimpleName());
    }

}
