package loader;

public class GameValidationException extends Exception{
    private String message;
    GameValidationException(String theMessage){
        super(theMessage);
        message = theMessage;
    }

    @Override
    public String getMessage(){return message;}
}
