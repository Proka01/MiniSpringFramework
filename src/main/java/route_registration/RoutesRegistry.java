package route_registration;

import di_ioc_engine.DI_Engine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoutesRegistry {

    private static RoutesRegistry instance;

    public HashMap<String, Method> routesRegistryMap = new HashMap<>();
    public HashMap<String, Object> methodClassMap = new HashMap<>();
    public List<Object> controllerInstancesRegistry = new ArrayList<>();
    private RoutesRegistry() {
    }
    public static RoutesRegistry getInstance() {
        if (instance == null) {
            instance = new RoutesRegistry();
        }
        return instance;
    }

    public void printControllers()
    {
        System.out.println();
        for(Object o : controllerInstancesRegistry)
            System.out.println(o.getClass().getSimpleName());
    }

    public void printRouteMethods()
    {
        System.out.println();
        for(String key : routesRegistryMap.keySet())
            System.out.println(key + ": " + routesRegistryMap.get(key));
    }



}
