package gamePack;

public class WrongInputException extends Exception{
    private String massage;
    public WrongInputException(String theMassage){
        super(theMassage);
        massage = theMassage;
    }

    @Override
    public String getMessage(){
        return massage;
    }
}
