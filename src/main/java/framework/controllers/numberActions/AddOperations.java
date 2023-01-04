package framework.controllers.numberActions;

import framework.annotations.Autowired;
import framework.annotations.Controller;
import framework.services.Service1;

import java.util.HashMap;


public class AddOperations {

    @Autowired(verbose = true)
    HashMap<String, String> map;

    @Autowired(verbose = true)
    Service1 service1;

}
