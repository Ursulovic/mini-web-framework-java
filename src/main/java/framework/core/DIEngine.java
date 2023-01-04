package framework.core;

import framework.annotations.*;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;

public class DIEngine {

    // beand and services singleton instancesces
    public static HashMap<Class, Object> beanInstances = new HashMap<>();


    public static void indexBeans() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (String pack : InitFramework.allPackages) {
            Reflections reflections = new Reflections(pack);
            Set<Class<?>> beans = reflections.getTypesAnnotatedWith(Bean.class);
            Set<Class<?>> services = reflections.getTypesAnnotatedWith(Service.class);



            for (Class c : beans) {
                Bean bean = (Bean) c.getDeclaredAnnotation(Bean.class);
                Scope scope = bean.scope();
                if (scope.equals(Scope.SINGLETON)) {
                    Constructor constructor = c.getDeclaredConstructor();
                    Object o = constructor.newInstance();
                    beanInstances.put(c, o);
                }
            }

            for (Class c : services) {
                Constructor constructor = c.getDeclaredConstructor();
                Object o = constructor.newInstance();
                beanInstances.put(c, o);
            }



        }
    }

    public static void autowireBeans() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        for (Class c : beanInstances.keySet()) {
            autowire(beanInstances.get(c));
        }
    }

    public static void autowire(Object o) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {

        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {

            if (field.isAnnotationPresent(Autowired.class)) {


                Autowired aw = field.getAnnotation(Autowired.class);
                field.setAccessible(true);

                if (field.getType().isInterface()) {

                    if (!field.isAnnotationPresent(Qualifier.class)) {
                        //ako nema Qualifer nasetuj iz dependency containera

                        Class cls = DependencyContainer.dependencyContainer.get(field.getType().toString());



                        if (cls == null) {
                            System.out.println("Put implementation in container or add it in annotation!");
                            System.exit(-1);
                        }

                        field.set(o, DIEngine.beanInstances.get(cls));
                        continue;
                    }

                    Qualifier qualifier = field.getAnnotation(Qualifier.class);
                    String value = qualifier.value();


                    Class classs = Class.forName(value);
                    if (!beanInstances.containsKey(classs)) {

                        System.out.println("Bean is not implementation of interface!");
                        System.exit(-1);
                    }

                    

                    Class c = DependencyContainer.dependencyContainer.get(value);
                    Constructor constructor = c.getDeclaredConstructor();
                    Object obj = constructor.newInstance();
                    field.set(o, obj);


                    autowire(field.get(o));

                }

                if (InitFramework.controllerInstances.containsKey(field.getType())) {
                    field.set(o, InitFramework.controllerInstances.get(field.getType()));
                    if (aw.verbose()) {
                        injectLog(field, o);
                    }
                    continue;
                }
                else if (beanInstances.containsKey(field.getType())) {
                    field.set(o, beanInstances.get(field.getType()));
                    if (aw.verbose()) {
                        injectLog(field, o);
                    }
                    continue;
                } else {
                    Constructor constructor = field.getType().getDeclaredConstructor();
                    Object obj = constructor.newInstance();
                    field.set(o, obj);
                }
                if (aw.verbose()) {
                    injectLog(field, o);
                }

                autowire(field.get(o));
            }

        }



    }

    public static void injectLog(Field f, Object parent) throws IllegalAccessException {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder str = new StringBuilder();
        str.append("Initialized ");
        str.append(f.getType() + " " + f.getName() + " in ");
        str.append( parent.getClass().getName() + " on " + now);
        str.append(" with " + f.get(parent).hashCode());
        System.out.println(str);
        System.out.println("-------------------------");
    }




}

