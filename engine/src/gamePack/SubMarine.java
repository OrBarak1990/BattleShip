package gamePack;

import java.io.Serializable;
import java.util.EventListener;
import java.util.List;

public class SubMarine implements Serializable{
    public enum Direction{ROW, COLUMN, DOWN_RIGHT, UP_RIGHT, RIGHT_UP, RIGHT_DOWN}
    public enum Category{REGULAR, L_SHAPE}
    private Cell start;
    private int length, numOfStrikes = 0;
    private boolean stillAlive = true;
    private final Direction direction;
    private final Category category;
    private final int score;
    private List<Cell> ship;

    public SubMarine(Direction direction, Category category, Cell theStart, int length, int score){
        this.direction = direction;
        this.category = category;
        this.length = length;
        start = theStart;
        this.score = score;
    }

    public boolean cellInShip(Cell target) {
        for (Cell cell: ship){
            if(cell.equals(target))
                return true;
        }
        return false;
    }


    public List<Cell> getShip() {
        return ship;
    }

    public void setShip(List<Cell> ship) {
        this.ship = ship;
    }

    public int getScore() {
        return score;
    }

    public Cell getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean attacked(Cell cell){
        boolean attacked = false;
        switch (direction) {
            case ROW:
                attacked = attackedToRight(cell);
                break;
            case COLUMN:
                attacked = attackedToDown(cell);
                break;
            case DOWN_RIGHT:
                attacked = attackedToUpp(cell);
                if(!attacked)
                    attacked = attackedToRight(cell);
                break;
            case UP_RIGHT:
                attacked = attackedToRight(cell);
                if(!attacked)
                    attacked = attackedToDown(cell);
                break;
            case RIGHT_UP:
                attacked = attackedToUpp(cell);
                if(!attacked)
                    attacked = attackedToLeft(cell);
                break;
            case RIGHT_DOWN:
                attacked = attackedToLeft(cell);
                if(!attacked)
                    attacked = attackedToDown(cell);
                break;
        }
        return attacked;
    }

    private boolean attackedToRight(Cell cell) {
        Cell currentSubMarineCell = new Cell(start.getX(), start.getY());
        int CubeIndex = this.length;
        while(CubeIndex-- > 0){
            if(cell.equals(currentSubMarineCell)){
                numOfStrikes++;
                updateLeaving();
                return true;
            }
            else{
                currentSubMarineCell.setY(currentSubMarineCell.getY() + 1);
            }
        }
        return false;
    }

    private boolean attackedToLeft(Cell cell) {
        Cell currentSubMarineCell = new Cell(start.getX(), start.getY());
        int CubeIndex = this.length;
        while(CubeIndex-- > 0){
            if(cell.equals(currentSubMarineCell)){
                numOfStrikes++;
                updateLeaving();
                return true;
            }
            else
                currentSubMarineCell.setY(currentSubMarineCell.getY() - 1);
        }
        return false;
    }

    private boolean attackedToDown(Cell cell) {
        Cell currentSubMarineCell = new Cell(start.getX(), start.getY());
        int CubeIndex = this.length;
        while(CubeIndex-- > 0){
            if(cell.equals(currentSubMarineCell)){
                numOfStrikes++;
                updateLeaving();
                return true;
            }
            else
                currentSubMarineCell.setX(currentSubMarineCell.getX() + 1);
        }
        return false;
    }

    private boolean attackedToUpp(Cell cell) {
        Cell currentSubMarineCell = new Cell(start.getX(), start.getY());
        int CubeIndex = this.length;
        while(CubeIndex-- > 0){
            if(cell.equals(currentSubMarineCell)){
                numOfStrikes++;
                updateLeaving();
                return true;
            }
            else
                currentSubMarineCell.setX(currentSubMarineCell.getX() - 1);
        }
        return false;
    }

    private void updateLeaving() {
        if((numOfStrikes == length * 2 - 1 && this.category == Category.L_SHAPE) ||
                (numOfStrikes == length && this.category == Category.REGULAR))
            stillAlive = false;
    }

    public boolean isAlive(){
        return this.stillAlive;
    }

    public Category getCategory() {
        return category;
    }
}
