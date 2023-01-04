package framework.core;

import java.util.HashMap;

public class DependencyContainer {

    public static HashMap<String, Class> dependencyContainer = new HashMap<>();

    public static void putImplementation(String ifc, Class cls) {
        dependencyContainer.put(ifc, cls);
    }

    public static void main(String[] args) {
    }

}
