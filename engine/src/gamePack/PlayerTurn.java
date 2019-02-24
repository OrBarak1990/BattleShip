package gamePack;

import java.io.Serializable;

public class  PlayerTurn  implements Serializable {
    public enum Turn{ONE, TWO}
    private Turn turn;

    public Turn getTurn() {
        return turn;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public PlayerTurn(Turn turn){
        this.turn = turn;
    }

    public void updateTurn(){
        if(turn == Turn.ONE)
            turn = Turn.TWO;
        else
            turn = Turn.ONE;
    }

    public boolean equals(PlayerTurn other){
        return this.turn == other.turn;
    }

    public String toString(){
        if (turn == Turn.ONE)
            return "1";
        else
            return "2";
    }
}
