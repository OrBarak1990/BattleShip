class Print {
    static void printBoards(char[][] subMarinBoard, char[][] trackingBoard) {
        printTopic(subMarinBoard.length);
        printFirstRowNumbers(subMarinBoard.length);
        printLineSeparator(subMarinBoard.length);
        for(int i = 0; i < subMarinBoard.length; ++i) {
            printNumber(i+1, subMarinBoard.length);
            for (int j = 0; j < subMarinBoard.length; ++j) {
                System.out.print(subMarinBoard[i][j] + " |");
            }
            for (int j = 0; j < 7; ++j)
                System.out.print(" ");
            printNumber(i+1, subMarinBoard.length);
            for (int j = 0; j < trackingBoard.length; ++j) {
                System.out.print(trackingBoard[i][j] + " |");
            }
            System.out.println();
            printLineSeparator(subMarinBoard.length);
        }
    }

    static void printNumber(int i, int boardSize) {
        if(boardSize >= 10) {
            if (i < 10)
                System.out.print(" " + i + "|");
            else
                System.out.print(i + "|");
        }
        else{
            System.out.print(" " + i + "|");
        }
    }

    private static void printTopic(int boardSize){
        int measure = boardSize * 3;
        for(int i = 0; i < measure / 2 - 5; ++i)
            System.out.print(" ");
        System.out.print("SUBMARINE BOARD");
        int spacing = measure + 7 -(measure + 7 - (measure / 2 - 2));
        while (spacing-- >0)
            System.out.print(" ");
        for(int i = 0; i < measure / 2 - 2; ++i)
            System.out.print(" ");
        System.out.println("TRACKING BOARD");
    }

    private static void printLineSeparator(int boardSize) {
        System.out.print(" ");
        for(int i = 0; i < boardSize * 3 + 2; ++i)
            System.out.print("-");
        for(int j = 0; j < 7; ++j)
            System.out.print(" ");
        for(int j = 0; j < boardSize * 3 + 3; ++j)
            System.out.print("-");
        System.out.println();
    }

    static void printSmallLineSeparator(int boardSize){
        for(int i = 0; i < boardSize * 3; ++i){
            System.out.print("---");
        }
        System.out.println();
    }

    private static void printFirstRowNumbers(int boardSize) {
        System.out.print("  |");
        for(int i = 0; i < boardSize; ++i)
            printNumber(i+1, boardSize);
        for(int i = 0; i < 9; ++i)
            System.out.print(" ");
        System.out.print("|");
        for(int i = 0; i < boardSize; ++i)
            printNumber(i+1, boardSize);
        System.out.println();
    }
}
