package framework.controllers.wordActions;


import framework.annotations.*;

@Controller
@Service
public class AddB implements WordActions {


    public AddB() {
    }

    @Override
    @Path(path = "/AddBBegin")
    @POST
    public String addToBeginning(String word) {
        WordList.wordListB.add("B".concat(word));
        return "B".concat(word);
    }

    @Override
    @Path(path = "/AddBEnd")
    @POST
    public String addToEnd(String word) {
        WordList.wordListB.add(word.concat("B"));
        return word.concat("B");
    }

    @Override
    @Path(path = "/wordsB")
    @GET
    public String getWords() {
        return WordList.wordListB.toString();
    }


}
