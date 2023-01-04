package framework.controllers.wordActions;

import framework.annotations.*;

@Controller
@Service
public class AddA implements WordActions {


    @Autowired(verbose = true)
    public String test1;

    @Autowired(verbose = true)
    private String test2;

//    @Autowired(verbose = true)
//    private AddB addB;


    public AddA() {
    }



    @Override
    @Path(path = "/addABegin")
    @POST
    public String addToBeginning(String word) {
        WordList.wordListA.add("A".concat(word));
        return "A".concat(word);
    }

    @Override
    @Path(path = "/addAEnd")
    @POST
    public String addToEnd(String word) {
        WordList.wordListA.add(word.concat("A"));
        return word.concat("A");
    }

    @Override
    @Path(path = "/wordsA")
    @GET
    public String getWords() {
        return WordList.wordListA.toString();
    }

}
