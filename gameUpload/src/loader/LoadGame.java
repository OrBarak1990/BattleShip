package loader;

import file.generated.BattleShipGame;
import file.generated.BattleShipGame.Boards.Board.Ship;
import gamePack.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoadGame {
    private BattleShipGame game = null;
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "file.generated";


    public void gameLoader(ApiGame theGame){
        theGame.setHaveWorkingFile(false);
        List<SubMarine> subMarineOne = makeSubMarineList(0);
        List<SubMarine> subMarineTwo = makeSubMarineList(1);
        int mineAmount = 0;
        if(game.getMine() != null)
            mineAmount = game.getMine().getAmount();
        Player playerOne = new Player(subMarineOne, this.buildTracker(),this.game.getBoardSize(), mineAmount);
        Player playerTwo = new Player(subMarineTwo, this.buildTracker(), this.game.getBoardSize(), mineAmount);
        theGame.initializeGame(playerOne, playerTwo, game.getBoardSize());
    }

    public void generate(String path) throws LoadingException, PathIsNotXmlFileException {
        xmlFileValidation(path);
        File file = new File(path);
        try {
            game = deserializeFrom(file);
        } catch (JAXBException e) {
            throw new LoadingException();
        }
    }


    public void generate(InputStream gameStream) throws LoadingException {
        try {
            game = deserializeFrom(gameStream);
        } catch (JAXBException e) {
            throw new LoadingException();
        }
    }

    public void validGame() throws GameValidationException {
        boardSizeValidation();
        validShipType();
        boardsValidation();
    }

    public int getBoarsSize(){
        return game.getBoardSize();
    }

    public String getBoardType() {
        return game.getGameType();
    }

    private void xmlFileValidation(String path) throws PathIsNotXmlFileException {
        if(!path.endsWith("XML") && !path.endsWith("xml"))
            throw new PathIsNotXmlFileException();
    }

    private List<SubMarine> makeSubMarineList(int boardIndex) {
        List<SubMarine> player = new ArrayList<>(game.getBoards().getBoard().get(boardIndex).getShip().size());
        for (Ship ship : game.getBoards().getBoard().get(boardIndex).getShip()) {
            int length = finedShipLength(ship);
            String categoryStr = finedShipCategory(ship);
            SubMarine.Category category;
            int score = finedShipScore(ship);
            if(categoryStr.equals("REGULAR"))
                category = SubMarine.Category.REGULAR;
            else
                category = SubMarine.Category.L_SHAPE;
            SubMarine.Direction direction;
            if(ship.getDirection().equals("ROW"))
                direction = SubMarine.Direction.ROW;
            else if(ship.getDirection().equals("COLUMN"))
                direction = SubMarine.Direction.COLUMN;
            else if(ship.getDirection().equals("DOWN_RIGHT"))
                direction = SubMarine.Direction.DOWN_RIGHT;
            else if(ship.getDirection().equals("UP_RIGHT"))
                direction = SubMarine.Direction.UP_RIGHT;
            else if(ship.getDirection().equals("RIGHT_UP"))
                direction = SubMarine.Direction.RIGHT_UP;
            else
                direction = SubMarine.Direction.RIGHT_DOWN;
            player.add(new SubMarine(direction, category, new Cell(ship.getPosition().getX() - 1, ship.getPosition().getY() - 1), length, score ));
        }
        return player;
    }



    private static BattleShipGame deserializeFrom(File in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (BattleShipGame) u.unmarshal(in);
    }

    private BattleShipGame deserializeFrom(InputStream gameStream) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(BattleShipGame.class);
        Unmarshaller u = jc.createUnmarshaller();
        return (BattleShipGame) u.unmarshal(gameStream);
    }


    private void boardSizeValidation() throws GameValidationException {
        if(game.getBoardSize() < 5 || game.getBoardSize() > 20)
            throw new GameValidationException("invalid board size = " + game.getBoardSize());
    }

    private void validShipType() throws GameValidationException {
        for(BattleShipGame.ShipTypes.ShipType shipType: game.getShipTypes().getShipType()){
            if(shipType.getAmount() <= 0)
                throw new GameValidationException("ship type " + shipType.getId() + " must have more then 0 ships");
            else if(shipType.getLength() <= 0)
                throw new GameValidationException("ship type " + shipType.getId() + " Length of ship must be positive number");
        }
    }

    private void boardsValidation() throws GameValidationException {
        haveTwoBoards();
        shipsValidation();
    }

    private void haveTwoBoards() throws GameValidationException {
        if(game.getBoards().getBoard().size() != 2)
            throw new GameValidationException("wrong amount of boards = " +
                    game.getBoards().getBoard().size());
    }

    private void shipsValidation() throws GameValidationException {
        validShipsStructure();
        sameShipAmountAndTypeAsDeclared();
        allShipsCorrectlyPosition();
    }

    private boolean haveShipType(String shipTypeId) {
        boolean found = false;
        for (BattleShipGame.ShipTypes.ShipType shipType: game.getShipTypes().getShipType()){
            if(shipTypeId.equals(shipType.getId())) {
                found = true;
                break;
            }
        }
        return found;
    }

    private void validShipsStructure() throws GameValidationException {
        for (int boardIndex = 0; boardIndex < game.getBoards().getBoard().size(); ++boardIndex) {
            for (Ship ship : game.getBoards().getBoard().get(boardIndex).getShip()) {
                if(!validShipStructure(ship))
                    throw new GameValidationException("wrong direction in: " +
                            ship.getShipTypeId() + " in board " + (boardIndex + 1) +
                            " = " + ship.getDirection());
                if(!haveShipType(ship.getShipTypeId()))
                    throw new GameValidationException(ship.getShipTypeId() +
                            " not declared has ship type");
            }
        }
    }

    private void sameShipAmountAndTypeAsDeclared() throws GameValidationException {
        for (BattleShipGame.ShipTypes.ShipType shipType: game.getShipTypes().getShipType()) {
            int shipTypeAmount = shipType.getAmount();
            for (int boardIndex = 0; boardIndex < game.getBoards().getBoard().size(); ++boardIndex) {
                int boardShipsAsType = 0;
                for (Ship ship: game.getBoards().getBoard().get(boardIndex).getShip()) {
                    if (ship.getShipTypeId().equals(shipType.getId())) {
                        boardShipsAsType++;
                    }
                }
                if (boardShipsAsType != shipTypeAmount)
                    throw  new  GameValidationException(shipType.getId() +
                    " declared to have " + shipType.getAmount() + " ships. but in board " +
                            (boardIndex + 1) + " have " + boardShipsAsType + " ships");
            }
        }
    }

    private void allShipsCorrectlyPosition() throws GameValidationException {
        for(int boardIndex = 0; boardIndex < game.getBoards().getBoard().size(); ++boardIndex) {
            int allShipsInBoard = game.getBoards().getBoard().get(boardIndex).getShip().size();
            for (int i = 0; i < allShipsInBoard; ++i) {
                Ship ship = game.getBoards().getBoard().get(boardIndex).getShip().get(i);
                makeCorrectlyChecksForShip(ship, allShipsInBoard, boardIndex, i);
            }
        }
    }

    private boolean validShipStructure(Ship ship) {
        boolean result = false;
        if(ship.getDirection().equals("ROW") || ship.getDirection().equals("COLUMN") ||
                ship.getDirection().equals("DOWN_RIGHT") || ship.getDirection().equals("UP_RIGHT") ||
                ship.getDirection().equals("RIGHT_UP") || ship.getDirection().equals("RIGHT_DOWN"))
            result = true;
        return result;
    }

    private void makeCorrectlyChecksForShip(Ship ship, int allShipsInBoard,int boardIndex, int shipIndex) throws GameValidationException {
        if(shipOutOfBoardsLimits(ship))
            throw new GameValidationException(ship.getShipTypeId() +
                    " is not in boards limit, in board: " + (boardIndex + 1));
        for (int j = shipIndex + 1; j <     allShipsInBoard; ++j){
            Ship other = game.getBoards().getBoard().get(boardIndex).getShip().get(j);
            if(!correctlyPosition(ship, other))
                throw new GameValidationException(ship.getShipTypeId() +
                        " located in (" + ship.getPosition().getX() + "," +  ship.getPosition().getY() + ")" +
                        " is not correctly position, in board: " + (boardIndex + 1) +
                " with " + other.getShipTypeId() +
                        " located in (" + other.getPosition().getX() + "," +  other.getPosition().getY() + ")" );
        }
    }

    private boolean shipOutOfBoardsLimits(Ship ship) {
        boolean result = false;
        int shipLength = finedShipLength(ship);
        if(ship.getPosition().getX() < 1 || ship.getPosition().getX() > game.getBoardSize() ||
                ship.getPosition().getY() < 1 || ship.getPosition().getY() > game.getBoardSize())
            result = true;
        else{
            int lastSquareX = ship.getPosition().getX(), lastSquareY = ship.getPosition().getY();
            if(ship.getDirection().equals("ROW"))
                lastSquareY += shipLength - 1;
            else
                lastSquareX += shipLength - 1;
            if(lastSquareX < 1 || lastSquareX > game.getBoardSize() ||
                    lastSquareY < 1 || lastSquareY > game.getBoardSize())
                result = true;
        }
        return result;
    }

    private boolean correctlyPosition(Ship ship, Ship other) {
        int shipLength = finedShipLength(ship), otherLength = finedShipLength(other);
        Cell currentShipCell = new Cell(ship.getPosition().getX(), ship.getPosition().getY());
        Cell currentOtherCell = new Cell(other.getPosition().getX(), other.getPosition().getY());
        boolean correct = true;
        for(int i = 0; i < shipLength && correct; ++i){
            for(int j = 0; j < otherLength; ++j){
                if(squaresAroundEachOther(currentShipCell, currentOtherCell)){
                    correct = false;
                    break;
                }
                if(other.getDirection().equals("ROW"))
                    currentOtherCell.setY(currentOtherCell.getY() + 1);
                else
                    currentOtherCell.setX(currentOtherCell.getX() + 1);
            }
            if(ship.getDirection().equals("ROW"))
                currentShipCell.setY(currentShipCell.getY() + 1);
            else
                currentShipCell.setX(currentShipCell.getX() + 1);
            currentOtherCell.setY(other.getPosition().getY());
            currentOtherCell.setX(other.getPosition().getX());
        }
        return correct;
    }

    private boolean squaresAroundEachOther(Cell currentShipCell, Cell currentOtherCell) {
        return (Math.abs(currentShipCell.getX() - currentOtherCell.getX()) <= 1 &&
                Math.abs(currentShipCell.getY() - currentOtherCell.getY()) <= 1);
    }

    private int finedShipLength(Ship ship) {
        int length = 0;
        for (BattleShipGame.ShipTypes.ShipType shipType: game.getShipTypes().getShipType()) {
            if(ship.getShipTypeId().equals(shipType.getId())) {
                length = shipType.getLength();
                break;
            }
        }
        return length;
    }

    private List<SubTracker> buildTracker(){
        List<SubTracker> tracker = new LinkedList<>();
        SubMarine.Category category;
        for (BattleShipGame.ShipTypes.ShipType shipType: game.getShipTypes().getShipType()) {
            if(shipType.getCategory().equals("REGULAR"))
                category = SubMarine.Category.REGULAR;
            else
                category = SubMarine.Category.L_SHAPE;
            tracker.add(new SubTracker(category, shipType.getLength(), shipType.getAmount()));
        }
        return tracker;
    }

    private String finedShipCategory(Ship ship) {
        String category = "";
        for (BattleShipGame.ShipTypes.ShipType shipType: game.getShipTypes().getShipType()) {
            if(ship.getShipTypeId().equals(shipType.getId())) {
                category = shipType.getCategory();
                break;
            }
        }
        return category;
    }

    private int finedShipScore(Ship ship) {
        int score = 0;
        for (BattleShipGame.ShipTypes.ShipType shipType: game.getShipTypes().getShipType()) {
            if(ship.getShipTypeId().equals(shipType.getId())) {
                score = shipType.getScore();
                break;
            }
        }
        return score;
    }
}
