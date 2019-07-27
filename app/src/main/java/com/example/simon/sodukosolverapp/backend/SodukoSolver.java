package com.example.simon.sodukosolverapp.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SodukoSolver {

    public Status getStatus() {
        return currentStatus;
    }

    public enum Status{
        PRIME_NUMBER_CONVERSION,
        CALCULATING,
        UPDATING_BOARD,
        COMPARING,
        CHANCING,
        COMPLETED,
        NORMALISING_NUMBERS,
        MULTIPLE_ANSWERS,
        ERROR;
    }

    private final int[][] board;
    private final int[] productRow;
    private final int[] productColumn;
    private final int[][] productQuadrant;
    private int[][] availableOptions;
    private static final int[][] possibleSolution = new int[9][];

    private boolean stillWorkToDo;
    private boolean hasUpdatedThisIteration;
    private boolean noSolution;
    private boolean running;
    private static boolean completedOnce = false;
    private static boolean multipleAnswers = false;

    private final int fullBoardValue = 2*3*5*7*11*13*17*19*23;

    private static Status currentStatus;

    public SodukoSolver(int[][] matrix){
        board = matrix;
        noSolution = false;
        productQuadrant = new int[3][3];
        productColumn = new int[9];
        productRow = new int[9];
        running = true;
        availableOptions = new int[9][9];
        availableOptions = Util.fillMatrix(availableOptions, fullBoardValue);
        currentStatus = Status.PRIME_NUMBER_CONVERSION;
    }

    /**
     * state machine for the SodukoSolver
     */
    public void run(){
        switch(currentStatus){
            case PRIME_NUMBER_CONVERSION:
                convertNumbers();
                currentStatus = Status.CALCULATING;
                break;

            case CALCULATING:
                calculateCurrent();
                currentStatus = Status.UPDATING_BOARD;
                break;

            case UPDATING_BOARD:
                hasUpdatedThisIteration = false;
                updateBoard();
                if(!stillWorkToDo){
                    currentStatus = Status.NORMALISING_NUMBERS;
                }else if(!hasUpdatedThisIteration){
                    currentStatus = Status.COMPARING;
                }else {
                    currentStatus = Status.CALCULATING;
                }
                if(noSolution){
                    currentStatus = Status.ERROR;
                }
                break;

            case COMPARING:
                compareBoard();
                if(hasUpdatedThisIteration){
                    currentStatus = Status.CALCULATING;
                }else{
                    currentStatus = Status.CHANCING;
                }
                break;

            case CHANCING:
                running = false;
                break;

            case NORMALISING_NUMBERS:
                convertNumbersBack();
                currentStatus = Status.COMPLETED;
                break;

            case COMPLETED:
                System.out.println("SUCCESS!");
                if(completedOnce){
                    currentStatus = Status.MULTIPLE_ANSWERS;
                    break;
                }
                handleSuccessCase();
                running = false;
                break;

            case MULTIPLE_ANSWERS:
                System.out.println("Non-unique");
                multipleAnswers = true;
                running = false;
                return;

            case ERROR:
                System.out.println("Find another job");
                running = false;
                return;
        }
    }

    /**
     * Execute method, takes a sudoku matrix as parameter and solves the sudoku.
     * @param board 9*9 matrix with sudoku
     * @return 9*9 matrix with solved sudoku or empty if no solution available.
     */
    public int[][] execute(int[][] board){
        final SodukoSolver sodukoSolver = new SodukoSolver(copyMatrix(board));
        resetStaticVariables();
        while(sodukoSolver.running){
            sodukoSolver.run();
        }

        handeSpecialIteration(sodukoSolver);

        if(multipleAnswers) {
            currentStatus = Status.MULTIPLE_ANSWERS;
            return board;
        } else if (completedOnce) {
          currentStatus = Status.COMPLETED;
          printFinalSolution();
          return possibleSolution.clone();
        } else {
            return board;
        }

    }

    private void resetStaticVariables() {
        currentStatus = Status.PRIME_NUMBER_CONVERSION;
        noSolution = false;
        multipleAnswers = false;
        completedOnce = false;
    }

    /**
     * Reads a sudoku as string input and solves
     * @return 9*9 matrix with solved sudoku.
     */
    public int[][] execute(){
      final int[][] initalBoard = new int[9][9];
        try
        {
            parseInput(initalBoard);
            final SodukoSolver sodsolv = new SodukoSolver(copyMatrix(initalBoard));
            while(sodsolv.running){
                sodsolv.run();
            }
            handeSpecialIteration(sodsolv);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        if (!multipleAnswers && completedOnce) {
            printFinalSolution();
            return possibleSolution;
        } else {
            return initalBoard;
        }

    }

    private static void handeSpecialIteration(final SodukoSolver sodsolv) {
        if (sodsolv.currentStatus == Status.COMPLETED) {
            return;
        } else {
            executeHelper(new SodukoSolver(sodsolv.board));
        }
    }

    private static void parseInput(final int[][] board) {
        final Scanner scan;
        scan = new Scanner(System.in);
        int row = 0;
        int column;
        while (scan.hasNextLine()){
            if(row==9) {
                break;
            }
            for(column = 0; column < 9;column++){
                board[row][column] = scan.nextInt();
            }
            row++;
        }
    }

    /**
     * Recursive method that tries to solve a sudoku using match and compare and then
     * guesses the inputs recursively (using the slots with least alternatives) checks all to see if
     * there are multiple solutions.
     * @param sodukoSolver new sudokusolver with updated board.
     * @return true if solved
     */
    private static void executeHelper(SodukoSolver sodukoSolver){
        System.out.println("NEW ITERATION");
        sodukoSolver.currentStatus = Status.CALCULATING;
        while(sodukoSolver.running){
            sodukoSolver.run();
        }
        if(sodukoSolver.currentStatus == Status.CHANCING){
            final int[] indexWithLeastOptions = Util.findLeastAmountOfSLotOptions(sodukoSolver.availableOptions);
            for(int i = 0; i < sodukoSolver.availableOptions[indexWithLeastOptions[0]][indexWithLeastOptions[1]]; i++){

                if(sodukoSolver.multipleAnswers){
                    break;
                }
                sodukoSolver.updateSingle(indexWithLeastOptions[0], indexWithLeastOptions[1], i);
                executeHelper(new SodukoSolver(copyMatrix(sodukoSolver.board)));
            }
        }
    }

    private static int[][] copyMatrix(final int[][] initalBoard) {
        final int[][] tempB = new int[9][];
        for (int j = 0; j < 9; j++) {
            tempB[j] = Arrays.copyOf(initalBoard[j], initalBoard[j].length);
        }
        return tempB;
    }

    private void savePossibleSolution() {
        for (int j = 0; j < 9; j++) {
            possibleSolution[j] = Arrays.copyOf(board[j], board[j].length);
        }
    }

    private void handleSuccessCase() {
        System.out.println("SUCCESS!");
        savePossibleSolution();
        boardPrinter();
        completedOnce = true;
    }


    /**
     * Prints the board as answer
     *
     */
    private void boardPrinter(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                System.out.print(possibleSolution[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    private static void printFinalSolution(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                System.out.print(possibleSolution[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    /**
     * General update method, checks corresponding row, column and quadrant to
     * see if there is only one feasible answer to put in and updates the board.
     * @param row
     * @param column
     */
    private void updateSingle(int row, int column){
        updateSingle(row, column, -1);
    }

    private void updateSingle(int row, int column, int index){
        final ArrayList<Integer> rowPrimes;
        final ArrayList<Integer> columnPrimes;
        final ArrayList<Integer> quadrantPrimes;

        columnPrimes = Util.reducer(fullBoardValue/productColumn[column]);
        rowPrimes = Util.reducer(fullBoardValue/productRow[row]);
        quadrantPrimes = matchNumbersQuadrant(row,column);
        final ArrayList<Integer> sameVal = matchNumbersHelper(rowPrimes, columnPrimes, quadrantPrimes);
        if(index == -1){
            stillWorkToDo = true;
            availableOptions[row][column] = sameVal.size();
            if(sameVal.size() == 0){
                noSolution = true;
            }
            if(sameVal.size()==1){
                availableOptions[row][column] = fullBoardValue;
                hasUpdatedThisIteration = true;
                board[row][column] = sameVal.get(0);
            }
        }
        else{
            board[row][column] = sameVal.get(index);
        }

    }

    /**
     * Iterates through the board, looking for open slots and evaluates them. If a
     * relevant number is found it will then update it.
     */
    private void updateBoard(){
        stillWorkToDo = false;
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(board[i][j] == 1){
                    updateSingle(i,j);
                }
            }
        }
    }

    /**
     * Iterates through the board to compare the feasible inputs for each quadrant. E.g. if
     * a number is excluded from all open slots except one it will update the only option for
     * given slot.
     */
    private void compareBoard(){
        hasUpdatedThisIteration = false;
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(board[i][j] == 1){
                    board[i][j] = compare(i,j);
                }
            }
        }
    }

    private int compare(int row, int column){
        return compare(row,column,-1);
    }

    private int compare(int row, int column, int index){

        ArrayList<Integer> available = matchNumbersQuadrant(row, column);
        available = Util.compareHelper(available, Util.reducer(fullBoardValue/productColumn[column]));
        available = Util.compareHelper(available, Util.reducer(fullBoardValue/productRow[row]));
        available = excluder(row,column,available);

        if(index == -1) {
            if (available.size() == 1) {
                hasUpdatedThisIteration = true;
                availableOptions[row][column] = fullBoardValue;
                return available.get(0);
            } else return 1;
        }
        else return available.get(index);
    }

    /**
     * Help method for compare(), compares the available numbers to the unavailable to
     * the other slots in given quadrant.
     * @param row
     * @param column
     * @param available
     * @return
     */
    private ArrayList<Integer> excluder(int row, int column, ArrayList<Integer> available){
        final int rows = Util.relevantData(row);
        final int columns = Util.relevantData(column  );
        for(int x = rows-3; x < rows; x++){
            for(int y = columns-3; y < columns; y++){
                if(board[x][y] == 1){
                    if(x != row || y != column){
                        available = Util.compareHelper(available, matcher(x,y));
                    }
                }

            }
        }
        return available;
    }

    /**
     * Helper method for excluder, checks which numbers are unavailable to given row and column
     * @param row
     * @param column
     * @return Array with the unavailable inputs for nearby slots
     */
    private ArrayList<Integer> matcher(int row, int column){
        final ArrayList<Integer> filled = Util.reducer(productRow[row]);
        filled.addAll(Util.reducer(productColumn[column]));
        return filled;
    }


    /**
     * Helper method for updating the board. Matches the arraylists with available prime numbers
     * for the row, column and quadrant relating to a slot and checks if there is only one answer
     * this is then used to update the board.
     * @param row
     * @param column
     * @param quadrant
     * @return  the only number available for the slot which then updates the board
     */
    private ArrayList<Integer> matchNumbersHelper(ArrayList<Integer> row, ArrayList<Integer> column, ArrayList<Integer> quadrant){
        ArrayList<Integer> sameVal;

        sameVal =  Util.compareHelper(row, column);
        sameVal = Util.compareHelper(sameVal, quadrant);

        return sameVal;
    }


    /**
     * Helper method to match numbers, used for readability
     * @param i row
     * @param j column of slot to be evaluated
     * @return  arraylist with available prime numbers to match
     */
    private ArrayList<Integer> matchNumbersQuadrant(int i, int j){
        final int quadrantX;
        final int quadrantY;
        if(i<3) quadrantX = 0;
        else if(i<6)quadrantX = 1;
        else quadrantX = 2;
        if(j<3) quadrantY = 0;
        else if(j<6)quadrantY = 1;
        else quadrantY = 2;

        return Util.reducer(fullBoardValue/productQuadrant[quadrantX][quadrantY]);
    }


    /**
     * Calculates the products of the rows and columns and quadrants for
     * every part of the board. This is so we can use the prime number finder
     * to evaluate which numbers are available for every slot and match.
     *
     */
    private void calculateCurrent(){

        //These loops calculate the row and column products separately
        for(int i = 0 ; i < 9; i++){
            productColumn[i] = 1;
            productRow[i] = 1;
            for(int j = 0; j < 9; j++){
                productRow[i] *= board[i][j];
                productColumn[i] *= board[j][i];
                //System.out.println("BOARD VALUE: " + board[i][j]+ "\nAND OTHER: " + board[j][i]);
            }
        }

        //OBS I know this looks really messy but I feel like 9 for loops is even messier...
        //efficiency wise it is about as heavy as the double loop above.
        for(int i = 0; i < 3; i++){
            final int addX = i*3;
            for(int j = 0; j < 3; j++){
                final int addY = j*3;
                productQuadrant[i][j] = 1;
                for(int x = 0; x < 3; x++){
                    for(int y = 0; y < 3; y ++){
                        productQuadrant[i][j] *= board[x + addX][y + addY];
                    }
                }
            }
        }

    }





    /**
     * I'm using the Goedels approach in which we use prime numbers
     * to deduce which integers are missing from each row/column or quadrant.
     *
     */
    private void convertNumbers(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                board[i][j] = Util.convertToPrime(board[i][j]);
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void convertNumbersBack(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                board[i][j] = Util.convertToRegular(board[i][j]);
            }
        }
    }
}


