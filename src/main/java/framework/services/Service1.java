package framework.services;

import framework.annotations.*;
import framework.controllers.wordActions.AddA;
import framework.controllers.wordActions.WordActions;

import java.util.HashMap;


@Service
public class Service1 {


    @Autowired(verbose = true)
    public Service2 service2;

    @Autowired(verbose = true)
    public WordActions wordActions;




    public Service1() {
    }
}
