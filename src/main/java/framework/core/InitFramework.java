package framework.core;


import framework.annotations.Controller;
import framework.annotations.GET;
import framework.annotations.POST;
import framework.annotations.Path;
import framework.controllers.wordActions.AddA;
import framework.controllers.wordActions.AddB;
import framework.controllers.wordActions.WordActions;
import framework.services.Service1;
import framework.services.Service2;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class InitFramework {

    public static Set<String> allPackages = new HashSet<>();
    public static Set<Class<?>> allControllers = new HashSet<>();

    public static Map<String, Method> getMethods = new HashMap<>();
    public static Map<String, Method> postMethods = new HashMap<>();

    public static Map<Class, Object> controllerInstances = new HashMap<>();

    public static void loadPackages() {
        loadPacks("src/");
    }

    private static void loadPacks(String path) {
        File directory = new File(path);
        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isFile()) {
                String p = file.getPath();
                String packName=p.substring(p.indexOf("src")+4, p.lastIndexOf('/'));
                allPackages.add(packName.replace('/', '.').replace("main.java.", ""));
            } else if (file.isDirectory()) {
                loadPacks(file.getAbsolutePath());
            }
        }
    }
    public static void findAllControllers() {

        for (String pack : allPackages) {
            Reflections reflections = new Reflections(pack);
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Controller.class);

            for (Class<?> controller : annotated) {
                allControllers.add(controller);
            }
        }
    }

    public static void findAllMethods() {
        for (Class cls : allControllers) {
            Method[] methods = cls.getDeclaredMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(Path.class)) {
                    Path p = m.getAnnotation(Path.class);
                    String url = p.path();
                    if (getMethods.containsKey(url) || postMethods.containsKey(url)) {
                        System.out.println("Path: " + url + " declared twice");
                    }
                    if (m.isAnnotationPresent(GET.class)) {
                        getMethods.put(url, m);
                    }
                    else if (m.isAnnotationPresent(POST.class)) {
                        postMethods.put(url, m);
                    } else {
                        System.out.println("Method " + m.getName() + " must be annotated with method annotation");
                    }
                }
            }
        }
    }

    public static void initControllers() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Class cls : allControllers) {
            if (!controllerInstances.containsKey(cls)) {
                Constructor c = cls.getDeclaredConstructor();
                Object o = c.newInstance();
                //DIEngine.autowire(o);
                controllerInstances.put(cls, o);
            }
        }
    }

    public static void  autowireControllers() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        for (Class c : controllerInstances.keySet()) {
            DIEngine.autowire(controllerInstances.get(c));
        }
    }

    public static void init() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {


        DependencyContainer.dependencyContainer.put(WordActions.class.toString(), AddB.class);

        loadPackages();
        findAllControllers();
        findAllMethods();
        initControllers();
        autowireControllers();
        DIEngine.indexBeans();
        DIEngine.autowireBeans();

        //testiranje injecotavanje polja
        Service1 service1 = (Service1) DIEngine.beanInstances.get(Service1.class);
        System.out.println(service1.service2 == DIEngine.beanInstances.get(Service2.class));

      //testiranje injecotvanje interfejsa
        System.out.println(service1.wordActions.getClass().getName());


    }





    public static String findMethod(framework.request.enums.Method method, String path, HashMap<String, String> params) throws InvocationTargetException, IllegalAccessException {
        if (method.equals(framework.request.enums.Method.GET))
            return getRequest(path);
        return postRequest(path, params);
    }


    public static String getRequest(String path) throws InvocationTargetException, IllegalAccessException {
        if (getMethods.containsKey(path)) {

            Method m = getMethods.get(path);
            Class c = m.getDeclaringClass();
            Object o = controllerInstances.get(c);
            String response = m.invoke(o).toString();
            return response;
        } else {
            return "Wrong route";
        }
    }

    public static String postRequest(String path, HashMap<String, String > params) throws InvocationTargetException, IllegalAccessException {
        if (postMethods.containsKey(path)) {

            Method m = postMethods.get(path);
            Class c = m.getDeclaringClass();
            Object o = controllerInstances.get(c);
            String response = m.invoke(o).toString();
            System.out.println(params);
            return response;

        } else {
            return "Wrong route";
        }
    }


}
