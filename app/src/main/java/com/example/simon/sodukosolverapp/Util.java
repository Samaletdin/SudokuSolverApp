package com.example.simon.sodukosolverapp;

import java.util.ArrayList;
import java.util.Scanner;

public final class Util{


    /**
     * Finds the values that 2 arrays has in common and returns a new array containing them
     * @param array1
     * @param array2
     * @return Array containing the values array1 & array2 has in common
     */
    public static ArrayList<Integer> CompareHelper(ArrayList<Integer> array1, ArrayList<Integer> array2){
        ArrayList<Integer> same = new ArrayList<>();
        for(int int1 : array1){
            for(int int2 : array2){
                if(int1 == int2){
                    same.add(int1);
                }
            }
        }

        return removeDuplicate(same);
    }

    /**
     * fills a matrix with given value
     * @param matrix
     * @param value
     * @return
     */
    public static int[][] fillMatrix(int[][] matrix, int value){
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                matrix[i][j] = value;
            }
        }
        return matrix;
    }

    /**
     * finds the index of a minimum value in a matrix
     * @param matrix
     * @return
     */
    public static int[] findMinMatrix(int[][] matrix){
        int minVal = matrix[0][0];
        int[] retVal = new int[]{0,0};
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                if(matrix[i][j] < minVal && matrix[i][j] != 1){
                    retVal = new int[]{i,j};
                    minVal = matrix[i][j];
                }
            }
        }
        return retVal;
    }

    /**
     * removes the duplicate values in an array
     * @param list
     * @return
     */
    private static ArrayList<Integer> removeDuplicate(ArrayList<Integer> list){
        int i = 0;
        int j;
        while(i < list.size()){
            j = i+1;
            while(j < list.size()){
                if(list.get(i) == list.get(j)){
                    list.remove(j);
                    j--;
                }
                j++;
            }
            i++;
        }
        return list;
    }


    public static int convertToPrime(int number){
        switch(number){
            case 1: return 2;
            case 2: return 3;
            case 3: return 5;
            case 4: return 7;
            case 5: return 11;
            case 6: return 13;
            case 7: return 17;
            case 8: return 19;
            case 9: return 23;
            default: return 1; //this is for the 0:s
        }
    }

    public static int convertToRegular(int number){
        switch(number){
            case 2: return 1;
            case 3: return 2;
            case 5: return 3;
            case 7: return 4;
            case 11: return 5;
            case 13: return 6;
            case 17: return 7;
            case 19: return 8;
            case 23: return 9;
            default: return 0;
        }
    }

    /**
     * Reduces the input into the corresponding prime numbers and returns them as an array.
     *
     * @param num number to be reduced into prime factors
     * @return an array of the prime factors
     */
    public static ArrayList<Integer> reducer(int num){
        int i = 2;
        ArrayList primeFactors = new ArrayList();
        while(i < num){
            for(i = 2; i < num; i++){
                if(num%i == 0){
                    num = num/i;
                    primeFactors.add(i);
                    break;
                }
            }
        }
        primeFactors.add(num);
        return primeFactors;
    }

    public static int relevantData(int i){
        if(i<3)return 3;
        if(i<6)return 6;
        return 9;

    }

    /**
     * To convert the input to an integer array
     * @return the board
     */
    public static int[][] stringToIntegerArray(){
        Scanner scan;
        int[][] retVal = new int[9][9];
        try
        {
            String[] line;
            scan = new Scanner(System.in);
            int i = 0;
            int j;
            while (scan.hasNextLine()){
                line = scan.nextLine().split(" ");
                j = 0;
                for(String value : line){
                    retVal[i][j++] = Integer.parseInt(value);
                }
                i++;
            }
        }catch (Exception e){
            System.out.print(e.toString());
        }
        return retVal;
    }
}