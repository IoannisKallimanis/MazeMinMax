public class Player {

	protected int playerId;
	protected String name;
	protected Board board;
	protected int score;
	protected int x;
	protected int y;

	public Player()
	{
		playerId = -1;	//properties of the player not yet specified
		name = "";
		board = new Board();
		score = 0;	//starts with 0 score
		x = -1;	// not a valid coord
		y = -1;	//not a valid coord
	}

	public Player(int playerId, String name, Board board, int score, int x, int y) {
		this.playerId = playerId;
		this.name = name;
		this.board = new Board(board);
		this.score = score;
		this.x = x;
		this.y = y;
	}

	public Player(Player player) {
		playerId = player.getPlayerId();
		name = player.getName();
		board = new Board(player.getBoard());
		score = player.getScore();
		x= player.getx();
		y = player.gety();
	}

	public void setPlayerId(int playerId)
	{
		this.playerId = playerId;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setBoard(Board board)
	{
		this.board = new Board(board);
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public void setx(int x)
	{
		this.x = x;
	}

	public void sety(int y)
	{
		this.y = y;
	}

	public int getPlayerId()
	{
		return playerId;
	}

	public String getName()
	{
		return name;
	}

	public Board getBoard()
	{
		return board;
	}

	public int getScore()
	{
		return score;
	}

	public int getx()
	{
		return x;
	}

	public int gety()
	{
		return y;
	}

	//extra getter
	public int getPlayerTile(){
		return x * board.getN() + y;
	  }
	
	//extra setter
	public void setPayerTile(int tileId) 
	{
		x = tileId / board.getN();
		y = tileId % board.getN();
	}
	
	/**
	 * @param id the tileId of the randomly moving player
	*/
	 public int diceAndJudge(int id){
		int randomMove;
		int tempx;
		int tempy;
		do {
			randomMove = (int) (Math.random() * 101);
			randomMove %= 4;
			randomMove = 2*randomMove + 1;
			// we don't want to do an invalid move(e.g. out of bounds)
			// Thus we use tempx and tempy so as to apply the changes
			// and check them for validity
			tempx = x;
			tempy = y;

			// if randomMove == 1, then he moves up
			// if randomMove == 3, then he moves right
			// if randomMove == 5, then he moves down
			// if randomMove == 7, then he moves left


			//shows if he can move
			boolean canHeMove = true;
			switch(randomMove) {
				case 1:
					//if there is a wall up he cannot move
					if(board.getTile(id).getUp()) canHeMove = false;
					break;
				case 3:
					//if there is a right wall he cannot move
					if(board.getTile(id).getRight()) canHeMove = false;
					break;
				case 5:
					//if there is a wall down he cannot move
					if(board.getTile(id).getDown()) canHeMove = false;
					break;
				case 7:
					//if there is a left wall he cannot move
					if(board.getTile(id).getLeft()) canHeMove = false;
					break;
				default:
			  	System.out.println("Error in the code!");
			}

			if(canHeMove) {
				switch(randomMove) {
					case 1:	//up
						tempx++;
						break;
					case 3:	//right
						tempy++;
						break;
					case 5:	//down
						tempx--;
						break;
					case 7:	//left
						tempy--;
						break;

					default:
				     System.out.println("Error in the code!");
				}
			}
			//if he cannot move
			else {
				switch(randomMove) {
					case 1:	//up
						System.out.println(name + " can't move up, so he miss his turn");
						break;
					case 3:	//right
						System.out.println(name + " can't move right, so he miss his turn");
						break;
					case 5:	//down
						System.out.println(name + " can't move down, so he miss his turn");
						break;
					case 7:	//left
						System.out.println(name + " can't move left, so he miss his turn");
						break;

					default:
				     System.out.println("Error in the code!");
			}
		}
			//we do this process while we don't move out of bounds
		}while((tempx < 0) || (tempx >= board.getN()) || (tempy < 0) || (tempy >= board.getN()));
		//If we exit the move was valid so return the tileId of the newPos
		return tempx*board.getN()+tempy;	
		//if the player could not move tempx = x_previous and tempy = y_previous
	}

	/**
	@param id the tileId of the player
	@param newPos the tileId of the newPosition the Player has chosen(randomly or not)
	@return an array that has all the info needed about the move
	*/
	public int[] move(int id, int newPos)
	{
		//create the array that holds the info we want to return
		int[] moveInfo = new int[4];
		x = newPos/board.getN();
		y = newPos%board.getN();

		int temp = -1;	//holds possibly the supplyId of the supply that Theseus has collected(or -1 if he does not)
		//we check if Theseus collects a supply in this move
		if(name == "Theseus") {
			for(int i  = 0; i < board.getS(); i++) {
				if(board.getSupply(i).getSupplyTileId() == (x * board.getN() + y)) {
					temp = i;
					board.getSupply(i).setSupplyTileId(-1);
					increaseScore();
				}
			}
		}
		//store the Information of the current move
		moveInfo[0] = x * board.getN() + y;
		moveInfo[1] = x;
		moveInfo[2] = y;
		moveInfo[3] = temp;
		
		//update the info in the board as well
		board.setPlayerTile(playerId, moveInfo[0]);
		
		//return moveInfo
		return moveInfo;
	}

	//a function that increases Theseus score by 1 when he finds a supply
	public void increaseScore() {
		score++;
	}
}