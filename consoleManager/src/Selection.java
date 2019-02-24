public enum  Selection {
    LOAD_GAME(1), PLAY_AGAINST_COMPUTER(11), START_GAME(2),
    GAME_STATUS(3), PLAY_MOVE(4), SHOW_STAT(5), END_CURRENT_GAME(6), ADD_MINE(7),
    EXIT_GAME(8), SAVE_GAME(9), LOAD_SAVING(10);

    private int selectionValue;

    Selection(int theSelectionValue){
        selectionValue = theSelectionValue;
    }

    public int getSelectionValue(){return selectionValue;}
}
