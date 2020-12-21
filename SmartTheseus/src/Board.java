public class Board {

	int N;
	int S;
	int W;
	Tile[] tiles;
	Supply[] supplies;
	int[] playerTiles;	//stores the tileId of each player i.e. x * N + y
					//@ position 0 the tileId of the player with playerId = 0 (+ 1)
					//@ position 1 the tileId of the player with playerId = 1 (+ 1)
	

	
	public Board()
	{
		N = 0;
		S = 0;
		W = 0;
		tiles = new Tile[0];
		supplies = new Supply[0];
		playerTiles = new int[2];
		playerTiles[0] = -1;
		playerTiles[1] = -1;
	}

	public Board(int N, int S, int W)
	{
		this.N = N;
		this.S = S;
		this.W = W;
		this.tiles = new Tile[N * N];
		for(int i = 0; i < (N * N); i++) {
			this.tiles[i] = new Tile(i, (i / N), (i % N), false, false, false, false);
		}
		this.supplies = new Supply[S];
		for(int i = 0; i < S; i++) {
			this.supplies[i] = new Supply(i, -1, -1, -1, false);
		}
		playerTiles = new int[2];
		playerTiles[0] = 0;					//Theseus initial position
		playerTiles[1] = (N/2) * N + N/2;	//Minotaur initial position
	}

	public Board(Board b)
	{
		N = b.getN();
		S = b.getS();
		W = b.getW();
		tiles = b.getTiles();
		
		supplies = b.getSupplies();
		
		this.playerTiles = b.getPlayerTile();
		
	}

	public void setN(int N)
	{
		this.N = N;
	}

	public void setS(int S)
	{
		this.S = S;
	}

	public void setW(int W)
	{
		this.W = W;
	}

	public void setTile(Tile tiles, int i) // i is the position that this Tile should take in the vector tiles
	{
		this.tiles[i] = new Tile(tiles);
	}

	public void setSupply(Supply supplies, int i)
	{
		this.supplies[i] = new Supply(supplies);
	}

	public void setPlayerTile(int id, int tileId) {
		if(id == 1 || id == 2)
			playerTiles[id-1] = tileId;
		else {
			System.out.println("Error in setPlayerTile");
			System.exit(1);
		}
	}
	
	public int getN()
	{
		return N;
	}

	public int getS()
	{
		return S;
	}

	public int getW()
	{
		return W;
	}

	public Tile getTile(int i) //i the position of the tile in the vector tiles
	{
		return tiles[i];
	}
	public Tile[] getTiles() 
	{
		return tiles;
	}
	public Supply getSupply(int i)
	{
		return supplies[i];
	}
	public Supply[] getSupplies()
	{
		return supplies;
	}
	
	public int getPlayerTile(int id) {
		if(id == 1 || id == 2)
			return playerTiles[id-1];
		else {
			System.out.println("Error in getPlayerTile");
			System.exit(1);
			return -1;
		}
	}
	public int[] getPlayerTile() {
		return playerTiles;
	}
	
	public void createTile()
	{
		//initialize with an address in memory
		tiles = new Tile[N * N];

		//This loop creates the wall of the perimeter of the maze
		//index i refers to the tileId of each Tile of the maze
		for(int i = 0; i < (N * N); i++) {
			//if we refer to the bottom line except for the corners(down = true)
			if((i < N) && (i != 0) && (i != (N - 1))) {
				tiles[i] = new Tile(i, (i / N), (i % N), false, true, false, false);
			}
			//else if we refer to the upper line except for the corners(up = true)
			else if((i > (N * (N - 1))) && (i != ((N * N) - 1))) {
				tiles[i] = new Tile(i, (i / N), (i % N), true, false, false, false);
			}
			//else if we refer to the 1st column and  not the upper left corner(left = true)
			else if(((i % N) == 0) && (i != N * (N - 1))) {
				tiles[i] = new Tile(i, (i / N), (i % N), false, false, true, false);
			}
			//else if we refer to the last column except for the 2 corners(right = true)
			else if((i % N == (N - 1)) && (i != (N - 1)) && (i != (N * N - 1))) {
				tiles[i] = new Tile(i, (i / N), (i % N), false, false, false, true);
			}
			//else if we refer to the down right corner
			else if(i == (N - 1)) {
				tiles[i] = new Tile(i, i, 0, false, true, false, true);
			}
			//else if we refer to the up left corner
			else if(i == N * (N - 1)) {
				tiles[i] = new Tile(i, (N - 1), 0, true, false, true, false);
			}
			//else if we refer to the up right corner
			else if(i == N * N -1) {
				tiles[i] = new Tile(i, N - 1, N - 1, true, false, false, true);
			}
			//any tile in the inside of the maze
			else {
				tiles[i] = new Tile(i, (i / N), (i % N), false, false, false, false);
			}
		}

		//this counter holds the total number of walls placed up to now
		//for every new wall added we increment it by 1
		int counter = 4 * N - 1;
		
		// is a number that checks how many repeats happened after our last wall placement
		int maxWalls = 0; 
		//we loop randomly over the maze in order to find available positions for placing the walls
		while(counter < W) {
			//a possiible tileId to add an extra wall
			int randomId = ((int) (Math.random() * 101 * N * N)) % (N * N);

			//this counter shows us how many available positions for placing walls exist for the specific  tileId = randomId
			// max available positions are 4 (up, right, down, left)
			// the counter decreases every time we have a wall to one of the 4 available positions
			//                                  or we go out of bounds
			int walls = 4;

			/*this counter shows us for every available position for placing walls(i.e. walls counter) 
				if the nearby tiles can accept a wall
				e.g. if a nearby wall has 2 walls already then we cannot place another one.
			cnt increases every time a nearby tile does not have a wall next to the tile with randomId
			given that the nearby tile has already 2 walls*/
			int cnt = 0;

			/*	So,
			 	if for every empty border of the tile with randomId	(walls counter)
				we cannot place a wall because the neighboor tile, on each empty side, has 2 walls (cnt counter)
				<=>
				walls = cnt 
				then,
				the randomId corresponds to a tile in which we cannot place another wall.
			*/
			
			//So we check every possibly available position

			//up
			if((randomId + N) >= (N * N)) walls--;
			else {
				if(tiles[randomId].getUp()) walls--;
				else
					if(tiles[randomId + N].getNumberOfWalls() > 1) cnt++;
			}

			//down
			if((randomId - N) < 0) walls--;
			else {
				if(tiles[randomId].getDown()) walls--;
				else
					if(tiles[randomId - N].getNumberOfWalls() > 1) cnt++;
			}

			//left
			if((randomId % N) == 0) walls--;
			else {
				if(tiles[randomId].getLeft()) walls--;
				else
					if(tiles[randomId - 1].getNumberOfWalls() > 1) cnt++;
			}

			//right
			if((randomId % N) == (N - 1)) walls--;
			else {
				if(tiles[randomId].getRight()) walls--;
				else
					if(tiles[randomId + 1].getNumberOfWalls() > 1) cnt++;
			}
			
			//Here we check the rest conditions
			// the tile with id = randomId must have less than 2 walls
			// also empty wall positions must not be equal to the number of nearby tiles with more than 2 walls
			//(see the explanation lines 164-171)
			if((tiles[randomId].getNumberOfWalls() > 1) || (walls == cnt)) {
				//if we already repeated this process more than 1001 times
				//we reach in most of the times the max number of walls
				//that we can place so it's okey to exit the while loop
				if(maxWalls < 1001) {
					maxWalls++;
				}
				else if(counter >= N*N){
					counter = W;
				}

			}
			else {
				//our last placement occurs now, so we restore this number with his initial value
				maxWalls = 0; 
				//Where will we try to place a wall ?
				int dice;
				//dice = 0, wall up
				//dice = 1, wall down
				//dice = 2, wall left
				//dice = 3, wall right
				//can we add a wall to the side of the Tile indicated by the dice value
				boolean canAddWalls = false;
				//Because we know there is at least one side available for wall placing we try every spot randomly
				do {
					//generate a random side
					dice = ((int) (Math.random() * 101)) % 4;
					switch(dice) {
						//checks for upper side
						case 0:
							if((randomId + N) >= (N * N)) canAddWalls = false;	//out - of - bounds
							else {
								//do we have a wall up already
								//or
								//does the nearby tile have more than 1 wall
								if((tiles[randomId].getUp()) || (tiles[randomId + N].getNumberOfWalls() > 1)) canAddWalls = false;
								else canAddWalls = true;
							}
							break;
							//checks for down side
						case 1:
							if((randomId - N) < 0) canAddWalls = false;	//out of bounds
							else {
								//do we have a wall down already
								//or
								//does the nearby tile have more than 1 wall
								if((tiles[randomId].getDown()) || (tiles[randomId - N].getNumberOfWalls() > 1)) canAddWalls = false;
								else canAddWalls = true;
							}
							break;
							//checks for left side
						case 2:
								if((randomId % N) == 0) canAddWalls = false;	//we are in the 1st column(there is already a wall in the perimeter)
								else {
									//do we have a wall left already
									//or
									//does the nearby tile have more than 1 wall
									if((tiles[randomId].getLeft()) || (tiles[randomId - 1].getNumberOfWalls() > 1)) canAddWalls = false;
									else canAddWalls = true;
								}
								break;
								//checks for right side
						case 3:
								if((randomId % N) == (N - 1)) canAddWalls = false;	//we are in the last column(there is already a wall in the perimeter)
								else {
									//do we have a wall up already
									//or
									//does the nearby tile have more than 1 wall
									if((tiles[randomId].getRight()) || (tiles[randomId + 1].getNumberOfWalls() > 1)) canAddWalls = false;
									else canAddWalls = true;
								}
								break;
						}
				}while(!canAddWalls);	//stop if we can add a wall
				//Now we are placing a wall
				switch(dice) {
					//up
					case 0:
						tiles[randomId].setUp(true);
						tiles[randomId + N].setDown(true);
						break;
					//down
					case 1:
						tiles[randomId].setDown(true);
						tiles[randomId - N].setUp(true);
						break;
					//left
					case 2:
						tiles[randomId].setLeft(true);
						tiles[randomId - 1].setRight(true);
						break;
					//right
					case 3:
						tiles[randomId].setRight(true);
						tiles[randomId + 1].setLeft(true);
						break;
				}
				//increment counter by 1
				counter++;
			}
		}
	}

	public void createSupply()
	{
		//initialize with an address in memory
		supplies = new Supply[S];
		int x;
		int y;
		boolean flag;
		for(int i = 0; i < S; i++) {
			//repeat the process of giving random x,y and tileId while ...(see end of do while)
			do {
				x = ((int) (Math.random() * 10*N)) % N;
				y = ((int) (Math.random() * 10*N)) % N;
				supplies[i] = new Supply(i, x, y, (x * N + y), false);
				//
				flag = false;
				//if we find a tile that has already a supply on it
				//set flag to true
				for(int j = 0; j < i; j++){
					if(supplies[j].HasId(supplies[i].getSupplyTileId())){
						flag = true;
						break;
					}
				}
				//... tileId does not have already a supply on it and is not theseusTile or minotaurTile
			}while( flag || (supplies[i].HasId(0)) || (supplies[i].HasId(N * N / 2)));
		}

	}

	public void createBoard()
	{
		createTile();
		createSupply();
	}

	public String[][] getStringRepresentation(int theseusTile, int minotaurTile)
	{
		//create the board that will represent the maze
		String[][] s = new String[2 * N + 1][N];

		//checks the 1st row needed to represent each tile
		//we fill the board from bottom to top
		for(int i = 0; i < (2 * N + 1); i += 2) {
			for(int j = 0; j < N; j++) {
				s[i][j] = "+";	//starts with +
				//if we are not in the upper row
				if(i != (2 * N)) {
					//id is the tileId
					int id = (i / 2) * N + j;	//i is always even so id is an integer
					if(tiles[id].getDown()) s[i][j] += " - - - ";	//if it has a wall down
					else s[i][j] += "       ";	//if it does not leave a space
					if(j == N - 1) s[i][j] += "+\n";	//if we are at the last column add + and \n(next line)
				}
				//top row
				else {
					if(j != (N - 1)) s[i][j] += " - - - ";	//if not in the last column of the top row
					else s[i][j] += " - - - +\n";	//else add + and \n to change line
				}
			}
		}
		//checks the second row needed to represent each tile
		for(int i = 1; i < (2 * N + 1); i += 2) {
			for(int j = 0; j < N; j++){
				//id is the tileId
				int id = ((i - 1) / 2) * N + j;
				if(tiles[id].getLeft()) s[i][j] = "|";	//if we tile has a left wall add |
				else s[i][j] = " ";	//else leave a spacee

				int supId = -1;	//the id of supply  that is located on the specific tile
				int numOfFounded = 0;	//the number of supplies collected i.e. Theseus score

				//note if there exists a supply on the specific tile
				//and simultaneously find the number of collected supplies
				for(int k = 0; k < S; k++) {
					if(supplies[k].getSupplyTileId() == id) supId = k;
					if(supplies[k].getFounded()) numOfFounded++;
				}
				//if Theseus and Minotaur are on the same tile
				if((theseusTile == id) && (minotaurTile == id))
					//if theseus has found all supplies he wins(so only Theseus is printed)
					if(countSupplies() == 0) s[i][j] += "   T   ";
					//else Minotaur stays alone as the winner
					else s[i][j] += "   M   ";
				//the case that nobody is on this tile or the supply that is in this tile has been found already
				else if((((supId == -1)) || ((supId != -1) && (supplies[supId].getFounded()))) && (theseusTile != id) && (minotaurTile != id))
					s[i][j] += "       ";
				//Theseus is on this tile alone
				else if((theseusTile == id) && (minotaurTile != id))
					s[i][j] += "   T   ";
				//only Minotaur is on this tile
				else if((((supId == -1)) || ((supId != -1) && (supplies[supId].getFounded()))) && (theseusTile != id) && (minotaurTile == id))
					s[i][j] += "   M   ";
				// We only have a supply on this tile
				else if((supId != -1) && (!supplies[supId].getFounded()) && (theseusTile != id) && (minotaurTile != id))
					s[i][j] += "   S" + (supId + 1) + "  ";
				//we have Minotaur together with a supply
				else if((supId != -1) && (!supplies[supId].getFounded()) && (theseusTile != id) && (minotaurTile == id))
					s[i][j] += " M, S" + (supId + 1) + " ";

				//to finish we also add a | to every tile in the last column succesively
				if(j == (N - 1)) s[i][j] += "|\n";
			}
		}
		//finally return the representation
		return s;
	}
	/**
	  *@param tileId :possible location of the supply
	  *@return
	  the index of the supplies matrix if the given tileId holds a supply
	  or
	  -1 if no supply is positioned in the given tileId
	  */
	  public int searchSupply(int tileId){
	    int index = -1;
	    for(int i = 0; i < S; i++){
	      if(supplies[i].getSupplyTileId() == tileId){
	        index = i;
	        return index;
	      }
	    }
	    return index;
	  }


	  /**This method counts the total number of supplies
	  that are currently placed in the maze
	  @return the number of supplies currently on the maze
	  */
	  public int countSupplies(){
	    int count = 0;
	    for(int i = 0; i < S; i++){
	      if(getSupply(i).getSupplyTileId() > 0 && getSupply(i).getSupplyTileId() <= (N*N - 1)){
	        count++;
	      }
	    }
	    return count;
	  }
}

