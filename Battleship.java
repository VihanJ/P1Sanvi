//Created by Vihan Jayaraman and Nakul Nandhakumar

import java.util.Scanner;
//required to modify elements of the board (not possible with arrays)
import java.util.ArrayList; 
import java.util.HashMap;
//may or may not be used
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.lang.Math;
 

public class Battleship {


    
    //private ArrayList<ArrayList<Integer>>  
    // initializing all public class variables
    public int boardWidth;
    public int boardLength;
    public int shipNumber; 
    public Board board;
    public Board playerBoard;
    public int totalShipLength = 0;

    public int minShipLength = 2;

    //public ArrayList<Ship.Status> shipStatuses = new ArrayList<Ship.Status>();
    //Arraylist that stores ship information
    public ArrayList<Ship> shipList = new ArrayList<Ship>();

    public Battleship() {
      this(8,8,3);
    }

    public Battleship(int boardWidth,int boardLength) {
      this(boardWidth, boardLength,1);
      
    }
    
    //Main constructor
    public Battleship(int boardWidth, int boardLength, int shipNumber) {
      this.boardWidth = boardWidth;
      this.boardLength = boardLength;
      this.shipNumber = shipNumber;

      this.board = new Board(boardWidth, boardLength);          //defines boards and playerboard as objects
      this.playerBoard = new Board(boardWidth, boardLength);

      board.createBoard();
      playerBoard.createBoard(); 


      
      //adds ships to list (each ship is as large as its number i)
      //this.shipList.add(new Ship(8,Ship.Orientation.HORIZONTAL));
      for (int i = 0; i < shipNumber; i ++) {
        //orientation flips every alternating ship (also checks to see that the ship's length doesn't exceed the board size)
        int tempLength = i + this.minShipLength;
        if (tempLength < boardWidth && tempLength < boardLength) {
          if (i % 2 == 0)
          {
              this.shipList.add(new Ship(tempLength, Ship.Orientation.VERTICAL));
          }  
          else 
          {
              this.shipList.add(new Ship(tempLength, Ship.Orientation.HORIZONTAL));
          }
        }        //calls Shio constructor to initalize ship info using "this." and then adds to arraylist

        else 
        {
          this.shipList.add(new Ship(boardWidth, Ship.Orientation.HORIZONTAL));
        }
      }

      for (Ship ship: this.shipList) {  //for loop that totals up enemy ship length to be later used
        this.totalShipLength = this.totalShipLength + ship.length;
      }
    }

    public void shipOverlapAdjust() {
      this.totalShipLength--;
    }

    public static void main(String[] args) {

        Battleship battleShip = new Battleship();     //creates object of class battleship to run game and call game methods
        battleShip.board.setAIShips(battleShip.shipList);
        battleShip.game(); 
    }
                          

  public void game() { //main active game method for attacking
    
        System.out.println("PlayerBoard:");
        playerBoard.printBoard();

        System.out.println("GameBoard");     //prints enemy board and board where you guess
        board.printBoard();                  //in real battleship game there would not be an enemy board

        System.out.println("Attack a point (ex. 1 1)");
        Scanner inputPos = new Scanner(System.in);

        int column = inputPos.nextInt()-1;  //takes user input and converts it to row column coords
        int row = inputPos.nextInt()-1;
            
        int valueOfGuess = board.getPoint(row,column);
        board.setPoint(row,column,2);

        System.out.print("\033[2J");

        switch (valueOfGuess) {
          //the player guesses the position of an enemy ship
          case 1:
            //checks if the player already has that ship part revealed/attacked
            if (playerBoard.getPoint(row,column) == 1) {
              System.out.println("You already attacked that point");
            } 
            else {
            //shows the ship on the player's board and decrements the enemy ship part counter
            playerBoard.setPoint(row, column, 1);
            System.out.println("Nice Hit");

            this.totalShipLength--;
            }
            break;
          //the player's guess is empty water
          case 0: 
            //changes the spot on the player's board to "already guessed (represented by 2)"
            playerBoard.setPoint(row,column, 2);
            System.out.println("You missed");
            break;
          //the player guesses a spot they already picked
          case 2:
            System.out.println("You already attacked that point");
            break;
        } 
        if (totalShipLength > 0) {
          game();
        }
        else {
          System.out.print("WINNER!\n");
        }
    }
    private static void help() {
      String[] priority_options = 
        {"-s number of ships (max 5)",
        "-b Board Size BOARDWIDTH BOARDLENGTH ex. -b 3 3",
        "-p number of players (max 2, default is 1) PLAYERNUMBER ex. -p 1",
        "-h Help Text"
        };

      for (String option: priority_options) {
        System.out.println(option);
      }
    }
}

class Ship {
  
  public static enum Orientation {VERTICAL,HORIZONTAL}
  public static enum Status {ALIVE, DEAD}
  
  public Orientation orientation;
  public Status status;

  public ArrayList<ArrayList<Integer>> position;

  public int length;

  
  
  //no parameters
  public Ship() {
    this(1,Orientation.VERTICAL, Status.ALIVE);
  }

  //setting only length
  public Ship(int length) {
    this(length, Orientation.VERTICAL, Status.ALIVE);
  }

  //setting orientation and length
  public Ship(int length, Orientation orientation) {
    this(length, orientation, Status.ALIVE);
  }

  //fully defined constructor
  public Ship(int length, Orientation orientation, Status status) {
    this.orientation = orientation;
    this.length = length;
    this.status = status;
  }
}


class Board {

  //Battleship ship;
  public int boardWidth;
  public int boardLength;
  public int shipOverlapCount;

  public static final String ANSI_BLUE = "\u001B[34m";     //color schemes for UI of game
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_BLACK = "\u001b[30;1m";

  public ArrayList<ArrayList<Integer>> board = new ArrayList<ArrayList<Integer>>();
    
  public Board() {
    this(8,8);      //set size of board
  } 

  public Board(int boardWidth, int boardLength) {   //constructor for defining size of board
    this.boardWidth = boardWidth;
    this.boardLength = boardLength; 
  }

  public void createBoard() {

    for (int i = 0; i < this.boardLength; i++) {     //Uses iteration to set 0 values for all positions of Arraylists. Adds arraylist inside arraylist to create 2D arraylist
      board.add(new ArrayList<Integer>());      
      for (int j = 0; j < this.boardWidth; j++) {
        board.get(i).add(0);  
      }
    }
  }

  public void printBoard() {

      //Prints Top row of indices and board
      System.out.print("  ");
      for (int i = 0; i < this.boardWidth; i++) {
        System.out.print(ANSI_GREEN + (i+1) + " ");
          }
      System.out.print("\n");  

      
      for (int i = 0; i < this.boardLength; i++) {
        //Side Row of indices and board
        System.out.print(ANSI_GREEN+ (i+1) + ANSI_RESET+ " ");
        for (int point: board.get(i)) {
          switch (point) {
            case 0:
              System.out.print(ANSI_BLUE + point + ANSI_RESET + " ");   //game checks for different numbers which mean different things before choosing color to print
              break;
            case 1:
              System.out.print(ANSI_RED + point + ANSI_RESET + " ");
              break;
            case 2:
              System.out.print(ANSI_BLACK + point + ANSI_RESET + " ");
              break;
              }
        }
        System.out.print("\n");
      }
    System.out.print("\n");
  } 

  public void setPoint(int row, int column, int value) {  //standard method for setting value @ coordinate
    board.get(row).set(column, value); 
  }

  public int getPoint(int row, int column) {   //gives coordinate so above method can be performed
    return board.get(row).get(column); 
  }

  public void setPlayerShips(ArrayList<Ship> shipList) { //work in progress for player ships

  }

  public void setAIShips(ArrayList<Ship> shipList) { //method for setting enemy ships
    
    int randomY; 
    int randomX;

    for (Ship i: shipList) {    //for each loop makes sure that each ship in list gets placed on board
      switch(i.orientation) {
        case VERTICAL:

          randomY = (int) (Math.random() * (boardLength-i.length+1));    //random math integer for Y axis to randomly move ships veritcally
          System.out.println(randomY);

          randomX = (int) (Math.random()* (boardWidth));  //random math integer for X axis to move ships randomly horizontally
          System.out.println(randomX);
        
          for (int j = 0; j < i.length; j++) {  //prints enemy ships on board vertically
            board.get(randomY+j).set(randomX,1);
            /*if (board.get(randomY).get(randomX+j) == 1) {
              ship.shipOverlapAdjust();
            }*/
          }
        break;

        case HORIZONTAL:
          
          randomY = (int) (Math.random() * (boardLength));   
          System.out.println(randomY);

          randomX = (int) (Math.random()* (boardWidth-i.length+1));
          System.out.println(randomX);
          
          for (int j = 0; j < i.length; j++) {   //prints enemy ships horizontally
            /*if (board.get(randomY).get(randomX+j) == 1) {
              ship.shipOverlapAdjust();
            }*/
            board.get(randomY).set(randomX+j,1);
          }  
        break;
      }

    } 
  }
    
} 

 


