package aspects;

import di_ioc_engine.DI_Engine;
import di_ioc_engine.anotations.Autowired;
import di_ioc_engine.anotations.Qualifier;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import route_registration.RoutesRegistry;
import route_registration.anotations.Controller;
import route_registration.anotations.GET;
import route_registration.anotations.POST;
import route_registration.anotations.Path;
import tmp.ProkicClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Aspect
public class BeforeMain {
    @Before("execution(public static void server.Server.main(String[]))")
    public void beforeMainMethod() {
        System.out.println("Aspect is running before the main method");
        RoutesRegistry routesRegistry = RoutesRegistry.getInstance();
        scanAndInitializeControllers();
        routesRegistry.printControllers();
        routesRegistry.printRouteMethods();
    }

    public void scanAndInitializeControllers()
    {
        RoutesRegistry routesRegistry = RoutesRegistry.getInstance();

        // Define the base package(s) you want to scan
        // Change this to your project's base package
        String basePackage = "aspects";

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(new SubTypesScanner(false))
        );

        // Get all the classes in the base package and its subpackages
        for (Class<?> clazz : reflections.getSubTypesOf(Object.class)) {
            Controller cont = clazz.getAnnotation(Controller.class);

            if(cont != null)
            {
                //Make instance of clazz which needs to be processed
                Object clazzInstance = null;
                try { clazzInstance = clazz.getDeclaredConstructor().newInstance();}
                catch (Exception e) {e.printStackTrace();}

                routesRegistry.controllerInstancesRegistry.add(clazzInstance);
                extractAndMapAnotatedMethodsFromControllerClass(clazz, clazzInstance);

                //look for @Autowired if injection is needed
                Field[] fields = clazz.getDeclaredFields();
                for (Field f : fields)
                    analyzeAndInitField(f,clazzInstance);
            }
        }
    }

    private void extractAndMapAnotatedMethodsFromControllerClass(Class<?> clazz, Object clazzInstance)
    {
        RoutesRegistry routesRegistry = RoutesRegistry.getInstance();

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Path p = method.getAnnotation(Path.class);

            if (p != null) {
                String path_url = p.path_url();
                String httpMethod = null;

                GET get = method.getAnnotation(GET.class);
                POST post = method.getAnnotation(POST.class);

                if(get != null) httpMethod = "GET";
                if(post != null) httpMethod = "POST";

                String routeKey = httpMethod+":"+path_url;
                routesRegistry.routesRegistryMap.put(routeKey,method);
                routesRegistry.methodClassMap.put(routeKey, clazzInstance);
            }
        }
    }

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

                if(a.verbose()) di_engine.printInstanceVerboseInfo(fieldInjectInstance,f.getName());
            }
            catch (Exception e) {e.printStackTrace();}
        }

        f.setAccessible(isAccessible);
    }

}
