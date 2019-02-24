import gamePack.ApiGame;
import gamePack.Game;
import gamePack.PlayerTurn;

class GameController {
    private ApiGame game = new Game();

    ApiGame getEngine(){
        return game;
    }

    PlayerTurn getLoosePlayer(){
        PlayerTurn loosePlayer;
        if(game.getWinPlayer().getTurn() == PlayerTurn.Turn.ONE)
            loosePlayer = new PlayerTurn(PlayerTurn.Turn.TWO);
        else
            loosePlayer = new PlayerTurn(PlayerTurn.Turn.ONE);

        return loosePlayer;
    }

    int getWinnerScore(){
        return game.getWinPlayer().getTurn() == PlayerTurn.Turn.ONE?
                game.getScoreNumber(PlayerTurn.Turn.ONE): game.getScoreNumber(PlayerTurn.Turn.TWO);
    }

    int getLooseScore(){
        return game.getWinPlayer().getTurn() == PlayerTurn.Turn.ONE?
                game.getScoreNumber(PlayerTurn.Turn.TWO): game.getScoreNumber(PlayerTurn.Turn.ONE);
    }
}
