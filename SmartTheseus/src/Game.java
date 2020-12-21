/*
	Group 100:
		Kallimanis Ioannis     10007  6945466230    ikallima@ece.auth.gr
		Melissaris Christos    9983   6907596710    cpmeliss@ece.auth.gr
*/
import java.util.ArrayList;
import java.util.Scanner;

public class Game {

	int round;

	public Game()
	{
		round = 0;	//no one has played in the beginning of the Game
	}

	public Game(int round)
	{
		this.round = round;
	}

	public Game(Game game)
	{
		round = game.getRound();
	}

	public void setRound(int round)
	{
		this.round = round;
	}

	public int getRound()
	{
		return round;
	}
	/**
		This function is used so as one can observe the whole game
		We have to press a key in order to go to the next move
	*/
	public void promptEnterKey(){
		   System.out.println("Press \"ENTER\" to continue...");
		   Scanner scanner = new Scanner(System.in);
		   scanner.nextLine();
	}

	/**	A quick intro of our project
		A night in the musuem
		with credits
	*/
	public void credits(){
			//This loop prints a series of messages
			//and pauses for a small period of time according to the index i
				for (int i = 0; i < 16; i++) {
		      if(i == 0){
		        System.out.println("A long time ago in a galaxy\n");

		      }
		      else if(i == 1)
		        System.out.println("far far away...\n");
		      else if(i == 2){
		          System.out.println("Team 100 welcomes you to...\n");
		       }
		      else if(i == 3){
		         System.out.println("\"A night in the musuem\"\n");
		      }
		      else if(i == 5){
		       System.out.println("The content creators:");
		      }
		      else if(i == 6){
		      System.out.println("Ioannis Kallimanis");
		      System.out.println("Christos Melissaris");
		      }
		     try {
		        // thread to sleep for 1000 milliseconds
		        if(i == 2 || i == 3)
		          Thread.sleep(2000);
		        else if(i == 0 || i == 1)
		          Thread.sleep(1000);
		        else if(i == 4 || i == 5)
		          Thread.sleep(1000);
		        else{
		          Thread.sleep(200);
		          System.out.println();
		        }
		     }
		     catch (Exception e) {
		        System.out.println(e);
		     }
		    }
		  }
		/**
	 @return an integer in {0,1,2}
	2 means Minotaurus won
	1 means Theseus won
	0 means nobody won
	*/
	public int checkGameOver(int S, Player p1, Player p2){
		int p1Tile = p1.getPlayerTile();
		int p2Tile = p2.getPlayerTile();
		if(p1.getScore() == S || p2.getScore() == S){
		return 1; //  Theseus has won
		}
		if(p1Tile == p2Tile){
		return 2;  //Minotaurus has won
		}
		return 0; //Draw
	}

	public static void printEndScreen(){
		System.out.print("\n");
		int h = 6;
		int n = 2*h;	//n is even && n>=2
		int width = 6 + n;
		for(int k = 0; k < h; k++){
			for(int i = 0; i < width; i++){           // *     * 
				if(i == k || i == (width - k - 1))
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.print("\n");
		}
		for(int i = 0; i < width; i++){            
			if(i == h || i == (h+5))
				System.out.print("*");
			else if(i > h && i < (h+5)){
				System.out.print("-");
			}
			else
				System.out.print(" ");
		}
		System.out.print("\n");
		for(int i = 0; i < width; i++){            
			if(i == h || i == h+5)
				System.out.print("|");
			else if(i == (h+1)){
				System.out.print("G");
			}
			else if(i == (h+2)){
				System.out.print("A");
			}
			else if(i == (h+3)){
				System.out.print("M");
			}
			else if(i == (h+4)){
				System.out.print("E");
			}
			else
				System.out.print(" ");
		}
		System.out.print("\n");
		for(int i = 0; i < width; i++){            
			if(i == h || i == (h+5))
				System.out.print("|");
			else if(i == (h+1)){
				System.out.print("O");
			}
			else if(i == (h+2)){
				System.out.print("V");
			}
			else if(i == (h+3)){
				System.out.print("E");
			}
			else if(i == (h+4)){
				System.out.print("R");
			}
			else
				System.out.print(" ");
		}
		System.out.print("\n");
		for(int i = 0; i < width; i++){            
			if(i == h || i == (h+5))
				System.out.print("*");
			else if(i > h && i < (h+5)){
				System.out.print("-");
			}
			else
				System.out.print(" ");
		}
		System.out.print("\n");
		for(int k = h - 1; k >= 0; k--){
			for(int i = 0; i < width; i++){           // *     * 
				if(i == k || i == (width - k - 1))
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
	}

	public static void main(String[] args) {
		//make a new game
		Game game = new Game();
		//show credits
		//game.credits();
		//set parameters
		//length/width of board
		int n = 15;
		//number of supplies
		int s = 4;
		//number of walls
		int w = 199;
		//holds who has won
		int winner = 0; //if winner = 0, draw, if winner = 1, winner is Theseus, if winner = 2, winner is Minotaur
		//create a new board
		Board board = new Board(n, s, w);
		board.createBoard();
		//create 2 players
		MinMaxPlayer[] players = new MinMaxPlayer[2];
		players[0] = new MinMaxPlayer(1, "Theseus", board, 0, 0, 0, 0);
		players[1] = new MinMaxPlayer(2, "Minotaur", board, 0, (n / 2), (n / 2), 0);
		
		int[] tiles = new int[2]; //store the tileId of each player in every round
		//@ind = 0 theseusTile
		//@ind = 1 minotaurTile
		tiles[0] = players[0].getBoard().getPlayerTile(players[0].getPlayerId()); //TheseusTile
		tiles[1] = players[1].getBoard().getPlayerTile(players[1].getPlayerId()); //MinoTile

		int[] moveInfo = new int[2];
		//represent the board
		String[][] array = new String[2 * n + 1][n];
		//get and print initial state
		array = board.getStringRepresentation(tiles[0], tiles[1]);
		//note that we have included the change of line in the function getStringRepresentation
		for(int j = (2 * n); j >= 0; j--) {
			for(int k = 0; k < n; k++) {
				System.out.print(array[j][k]);
			}
		}
	
		game.setRound(0);
		//Let the game begin
		for(int i = 0; i < 200; i++){
			game.setRound(game.getRound() + 1);
			game.promptEnterKey();
			//Theseus playes first
			
			System.out.println("Round: " + game.getRound());
			for(int j = 0; j < 2; j++) {
				moveInfo[j] = players[i % 2].getNextMove(tiles[i % 2], tiles[(i + 1) % 2])[j];
				if(j == 0)
					players[i % 2].getPath().remove(players[i % 2].getPath().size() - 1);
			}
			tiles[i % 2] = moveInfo[0];
			players[i % 2].setPayerTile(moveInfo[0]);
			if(moveInfo[1] != -1) {
				board.getSupply(moveInfo[1]).setFounded(true);
				board.getSupply(moveInfo[1]).setSupplyTileId(-1);
				board.getSupply(moveInfo[1]).setx(-1);
				board.getSupply(moveInfo[1]).sety(-1);
			}
			players[i % 2].statistics(false);
			board.setPlayerTile((i % 2) + 1, tiles[i % 2]);
			//get and print current state
			array = board.getStringRepresentation(tiles[0], tiles[1]);
			//note that we have included the change of line in the function getStringRepresentation
	
			
			for(int j = (2 * n); j >= 0; j--) {
				for(int k = 0; k < n; k++) {
					System.out.print(array[j][k]);
				}
			}
			//If theseus has not collected all supplies
			//and minotaur has not caught theseus,
			// the game continues until we reach 200 dices in total
			winner = game.checkGameOver(s, players[0], players[1]);	//ADD CONDITIONS
			if(winner!=0){
				break;
			}
		}
		printEndScreen();
		System.out.println("Statistics of the Game:");
		game.promptEnterKey();
		for(int i = 0; i < 2; i++) {
			players[i].statistics(true);
		}
		
		//print the final status who wins or draw
		switch(winner) {
			case 0:	//Draw
				System.out.println("Nobody is the winner. Draw!!");
				break;
			case 1: //Theseus wins
				System.out.println("Theseus is the winner!!!");
				break;
			case 2:	//Minotaur wins
				System.out.println("Minotaur is the winner!!!");
				break;
		}
		System.out.println("Thanks for your participation. See you next time ;)");


		
	}
}

