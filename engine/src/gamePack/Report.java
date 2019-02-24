package gamePack;

import java.io.Serializable;

public class Report  implements Serializable {
    private Cell target;
    private boolean computerMove;
    private ApiGame.Result result;
    private PlayerTurn.Turn player;

    Report(Cell theTarget, boolean computerMove, ApiGame.Result theResult, PlayerTurn.Turn thePlayer){
        target = new Cell(theTarget.getX(), theTarget.getY());
        result = theResult;
        this.computerMove = computerMove;
        player = thePlayer;
    }

    Report(boolean computerMove, ApiGame.Result theResult, PlayerTurn.Turn thePlayer){
        result = theResult;
        this.computerMove = computerMove;
        player = thePlayer;
    }


    public Cell getTarget() {
        return target;
    }

    public boolean isComputerMove() {
        return computerMove;
    }

    public ApiGame.Result getResult() {
        return result;
    }

    public PlayerTurn.Turn getPlayer() {
        return player;
    }

    public void setPlayer(PlayerTurn.Turn player) {
        this.player = player;
    }

    public void setResult(ApiGame.Result result) {
        this.result = result;
    }
}
