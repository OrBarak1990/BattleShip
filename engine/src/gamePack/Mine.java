package gamePack;

import java.io.Serializable;

public class Mine implements Serializable{
    private Cell cell;
    private boolean attacked;

    Mine(Cell theCell) {
        cell = theCell;
        attacked = false;
    }

    public Cell getCell() {
        return cell;
    }

    boolean attacked(Cell other){
        if(cell.getX() == other.getX() && cell.getY() == other.getY())
            attacked = true;
        return attacked;
    }
}
