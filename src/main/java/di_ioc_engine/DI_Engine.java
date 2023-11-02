package di_ioc_engine;

import di_ioc_engine.anotations.*;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DI_Engine {
    private static DI_Engine instance;

    public List<Class<?>> beanClassesRegistry = new ArrayList<>();
    public List<Object> beanInstancesRegistry = new ArrayList<>();
    public HashMap<String, Class<?>> dependencyContainer = new HashMap<>();

    private DI_Engine() {
    }
    public static DI_Engine getInstance() {
        if (instance == null) {
            instance = new DI_Engine();
        }
        return instance;
    }

    public void printInstanceVerboseInfo(Object instance, String fieldName)
    {
        String out = "";
        out += "<" + instance.getClass().getSimpleName() + ">" + " ";
        out += "<" + fieldName + ">" + " ";
        out += "<" + instance.getClass().getSuperclass().getSimpleName() + ">" + " ";
        out += "<" + LocalDateTime.now().toString() + ">" + " ";
        out += "<" + instance.hashCode() + ">" + " ";
        out += "\n";

        System.out.println(out);
    }

    public void initializeDependencyContainer()
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
            Qualifier qualifier = clazz.getAnnotation(Qualifier.class);

            if(qualifier != null)
            {
                String qualifierID = qualifier.value();
                dependencyContainer.put(qualifierID,clazz);
            }
        }
    }

    public boolean isScopeSingleton(Class<?> clazz)
    {
        MyBean mb = clazz.getAnnotation(MyBean.class);
        Component comp = clazz.getAnnotation(Component.class);
        Service serv = clazz.getAnnotation(Service.class);

        if(mb != null) return mb.scope().equals("singleton");
        if(comp != null) return comp.scope().equals("singleton");
        if(serv != null) return serv.scope().equals("singleton");

        return false;
    }

    public boolean hasAutowiredFields(Class<?> clazz)
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

    public void printDependencyContainer()
    {
        for (String key : dependencyContainer.keySet()) {
            System.out.println(key + ": " + dependencyContainer.get(key).getSimpleName());
        }
    }

    public boolean isClassBean(Class<?> clazz)
    {
        MyBean mb = clazz.getAnnotation(MyBean.class);
        Component comp = clazz.getAnnotation(Component.class);
        Service serv = clazz.getAnnotation(Service.class);

        //if class is a bean, make instance of it and save it to clazz and instance registry
        return (mb != null || comp != null || serv != null);
    }



    /**
     * THIS IS THE OLD VERSION WITHOUT USING ASPECTS
     * ITS NOT IN USE IN THIS VERSION OF CODE (IN SOME PREVIOUS COMMITS IS IN USE)
     * */

//    public void initializeAllBeans()
//    {
//        // Define the base package(s) you want to scan
//        // Change this to your project's base package
//        String basePackage = "di_ioc_engine";
//
//        Reflections reflections = new Reflections(new ConfigurationBuilder()
//                .setUrls(ClasspathHelper.forPackage(basePackage))
//                .setScanners(new SubTypesScanner(false))
//        );
//
//        // Get all the classes in the base package and its subpackages
//        for (Class<?> clazz : reflections.getSubTypesOf(Object.class)) {
//            MyBean mb = clazz.getAnnotation(MyBean.class);
//            Component comp = clazz.getAnnotation(Component.class);
//            Service serv = clazz.getAnnotation(Service.class);
//
//            //if class is a bean, make instance of it and save it to clazz and instance registry
//            if(mb != null || comp != null || serv != null)
//            {
//                beanClassesRegistry.add(clazz);
//
//                //singleton beans can be initialized in advance
//                if(isScopeSingleton(clazz))
//                    initBean(clazz);
//            }
//        }
//    }
//
//
//    /**
//     * DEPENDENCY INJECTION LOGIC:
//     *
//     * if clazz has no @Autowired fields make and instance and mark it in beanInstanceRegistry
//     *
//     * if clazz has some @Autowired fields for each of them:
//     *      1. determent if field is interface and from dependencyContainer take appropriate clazz for injection
//     *      2. first try to inject them from beanInstanceRegistry if they are present
//     *      3. or if not call function recursively
//     *
//     * when all @Autowired fields are satisfied,
//     * mark clazzInstance in beanInstanceRegistry is class is bean whit scope="singleton"
//     *
//     * return clazz instance
//     * */
//    public Object initBean(Class<?> clazz)
//    {
//        //Make instance of clazz which needs to be processed
//        Object clazzInstance = null;
//        System.out.println("bio sam pre clazz.getDeclaredConstructor().newInstance();");
//        try { clazzInstance = clazz.getDeclaredConstructor().newInstance();
//            System.out.println("a sad sam posle");}
//        catch (Exception e) {e.printStackTrace();}
//
//        //If clazz has no @Autowired fields, mark it in beanInstancesRegistry and return
//        if(!hasAutowiredFields(clazz))
//        {
//            beanInstancesRegistry.add(clazzInstance);
//            return clazzInstance;
//        }
//        //else all @Autowired fields need to be injected first
//        //  if beanInstancesRegistry has that field just inject it
//        //  otherwise call function recursively
//        else
//        {
//            Field[] fields = clazz.getDeclaredFields();
//            for (Field f : fields)
//                analyzeAndInitField(f,clazzInstance);
//
//            //after all @Autowired fields are processed,
//            // mark clazzInstance in registry if its bean is scope=singleton
//            // and then return it
//            if(isScopeSingleton(clazz))
//                beanInstancesRegistry.add(clazzInstance);
//
//            return  clazzInstance;
//        }
//    }
//
//    /**
//     * if clazz has some @Autowired fields for each of them:
//     *      1. determent if field is interface and from dependencyContainer take appropriate clazz for injection
//     *      2. first try to inject them from beanInstanceRegistry if they are present
//     *      3. or if not call function recursively (by calling initBean())
//     * */
//    public void analyzeAndInitField(Field f, Object clazzInstance)
//    {
//        boolean isAccessible = f.isAccessible();
//        f.setAccessible(true);
//
//        Autowired a = f.getAnnotation(Autowired.class);
//
//        if (a != null)
//        {
//            Class<?> fieldInjectType = f.getType();
//            if(fieldInjectType.isInterface())
//            {
//                Qualifier qualifier = f.getAnnotation(Qualifier.class);
//                if(qualifier != null) fieldInjectType = dependencyContainer.get(qualifier.value());
//                else System.out.println("ERROR @Autowired interface need to have @Qualifier");
//            }
//
//            try
//            {
//                Object injectInstance = findInstanceByType(beanInstancesRegistry, fieldInjectType);
//                if(injectInstance == null) injectInstance = initBean(fieldInjectType);
//
//                f.set(clazzInstance,injectInstance);
//            }
//            catch (Exception e) {e.printStackTrace();}
//        }
//
//        f.setAccessible(isAccessible);
//    }
}
