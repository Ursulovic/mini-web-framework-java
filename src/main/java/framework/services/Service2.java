package framework.services;

import framework.annotations.*;

import java.util.HashMap;


@Service
public class Service2 {


    @Autowired(verbose = true)
    public HashMap<String, String> map;

    @Autowired(verbose = true)
    public Service1 service1;


}
