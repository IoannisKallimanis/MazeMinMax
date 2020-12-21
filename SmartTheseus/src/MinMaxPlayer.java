import java.util.ArrayList;

public class MinMaxPlayer extends Player{
		
		private ArrayList<ArrayList<Integer>> path;
		

		public MinMaxPlayer() {
			super();
			path = new ArrayList<ArrayList<Integer>>(0);	//initial size = 0
		}
		
		public MinMaxPlayer(int playerId, String name, Board board, int score, int x, int y, int size) {
			super(playerId, name, board, score, x, y);
			path = new ArrayList<ArrayList<Integer>>(size);	//We give as a parameter the initial size of the ArrayList
		}

		public MinMaxPlayer(MinMaxPlayer hero) {
			super(hero.getPlayerId(), hero.getName(), hero.getBoard(), hero.getScore(), hero.getx(), hero.gety());
			//Attention: this is not a copy initialization, the HeuristicPlayer we are creating will control the same path as the hero
			this.path = hero.getPath();	
		}
		
		public void setPath(ArrayList<Integer> moveInfo) {
			path.add(moveInfo);
		}
		
		//this returns the full path
		public ArrayList<ArrayList<Integer>> getPath(){
			return path;
		}
		//we only get information about a single move
		public ArrayList<Integer> getPath(int index){
			if(index >= path.size()) {
				System.out.println("Wrong index in getPath");
				System.exit(1);
			}
			return path.get(index);
		}
		
		public double evaluate(int currentPos, int dice) {
			//We must check in the direction pointed by dice
			//For all this cases apply the corresponding if Minotaur calls this function
			/*
			 1)If there is a wall next to us we return the smallest value(we defined) 
			 	so as to completely avoid making an illegal move (we could return Double.NEGATIVE_INFINITY)
			   If there is a wall in the our path that is visible we also take proper care.
			   
			2)only opponent
			 
			3)only supply
			
			4)else if Minotaur and supply are on the same tile
			 	make Theseus run for his life
			5)If  Minotaur and LAST supply are on the same tile
			  	go for the win

			 We may also have an empty tile
			*/
			
			// i is the iterator of the do-while loop and indicates the distance Theseus has moved in the direction of dice
			int i = 0;
			
			//we store all the info needed from cases 1 to 5 regarding all the tiles we can see from our spot
			//rows means distance in tiles (we can see up to 3 tiles in one direction)
			//columns hold flags for the 5 different cases we examine 
			//For Minotaur 4th and 5th case are almost the same, there is no difference, so he needs a column less than Theseus
			int rec = 0;
			switch(playerId) {
				case 1:
					rec = 5; //Theseus needs 5 columns
					break;
				case 2:
					rec = 4;
					break;
				default:
					System.out.println("I made a mistake  during 3rd part. Check evaluate in MinMaxPlayer");
			}
			boolean[][] cases = new boolean[3][rec];
			for(int j = 0; j < 3; j++) {
				for(int k = 0; k < rec; k++) {
					cases[j][k] = false;
				}
			}
			do {
				
				
				//1st case if we find a wall in our vision scope
				switch(dice) {
					case 1: if(board.getTile(currentPos).getUp()) {
								cases[i][0] = true;
								if(i == 0) {	
									return -100000;
								}
							}
					break;
					case 3: if(board.getTile(currentPos).getRight()){
								cases[i][0] = true;
								if(i == 0) {
									return -100000;
								}
							}
					break;
					case 5: if((board.getTile(currentPos).getDown() || currentPos == 0)){
								cases[i][0] = true;
								if(i == 0) {
									return -100000;
								}
							}
					break;
					case 7: if(board.getTile(currentPos).getLeft()){
								cases[i][0] = true;
								if(i == 0) {
									return -100000;
								}
							}
					break;
					default: 
						System.out.println("Error in evaluate. Unknown dice value!");
						System.exit(1);
				}
				//Stop the loop if you find a Wall
				if(cases[i][0]) {
					break;
				}
				int closeId = closeTileId(currentPos, dice);
				

				//5th case Minotaur and last supply, no need for minotaur
					
					// playerId % 2 + 1 gives the opponent's id
					if((board.getPlayerTile(playerId % 2 + 1) == closeId) && 	
						(board.searchSupply(closeId) != -1) &&
						(board.countSupplies() == 1) &&
						(playerId == 1)) {
						cases[i][4] = true;
						if(i == 0) {
							return 1000.0;	//go for the win
						}
					}
					
				//4th case: if opponent and supply are on the same tile
				 	//make Theseus run for his life, or Minotaur hunt him
					else if((board.getPlayerTile(playerId % 2 + 1) == closeId) && 	
							(board.searchSupply(closeId) != -1)){
						cases[i][3] = true;
						if(i == 0)
							if(playerId == 1)
								return -100.0;	//run for your life
							else 
								return +100.0; //take him
					}
				//2nd only opponent
					else if((board.getPlayerTile(playerId % 2 + 1) == closeId)) {
						cases[i][1] = true;
						if(i != 2) {
							if(playerId == 1) //Theseus should to go away
								return -1000.0;
							else
								return +1000.0; //Minotaur is hunting, so he should get closer
						}
							
					}
				//3rd only Supply
				//Bot  of them should stay close to a supply, each of them for different reason
					else if((board.searchSupply(closeId) != -1)) {
						cases[i][2] = true;
					}
				//update virtual position
				currentPos = closeId;
				
				
				i++;
				//Stop if you check through all your visible area in the specified direction
			}while((i < 3));
			
			//What is the overall evaluation of the move
			double eval = 0;	//hold the result of the final evaluation
			double NearSupplies = 0;	//the coefficient that shows the existence of supplies
			double NearOpponent = 0;	//the coefficient that shows the presence of Minotaur near us
			//do the calculations
			for(int k = 0; k < 3; k++) {
				if(cases[k][0] == true) {
					break;
				}
				if(cases[k][1] == true) {
					if(playerId == 1)
						NearOpponent = NearOpponent - measureStep(k);
					else
						NearOpponent = NearOpponent + measureStep(k);
				}
				else if(cases[k][2] == true) {
					NearSupplies = NearSupplies + measureStep(k);
				}				
				if(cases[k][3] == true) {
					//Opponent steps on the same tile with a supply
					if(playerId == 1)
						eval = -6;
					else
						eval = 6;
					return eval;
				}
				if(rec == 5) { //only Theseus plays
					if(cases[k][4] == true) {
						//if k = 0 i.e. the tile with Minotaur and the last supply 
						//is next to Theseus, we have already handled this case in the do-while loop
						//now it is dangerous to play , so we prefer to do another move if possible
						eval = -5;//
						return eval;
					}
				}
			}
			eval = NearSupplies*0.46 + NearOpponent*0.54;
			return eval;
			
		}
		/**
		 * 
		 * @param currentPos the initial tileId of the player before moving in this round
		 * 
		 * @return Array with 
		 * @ind = 0 new TileId of the player
		 * @ind = 1 The supplyId of the supply that was founded, -1 elsewhere(always -1 for Minotaur)
		 * In the variable path we save an ArrayList<Integer> that contains
		 * @ ind = 0  the direction of the move i.e. {up,right,down,left} encoded TOGETHER with the tileId of the new move
		 * @ ind = 1  if the player collected a supply or not (that is not exist for Minotaur)
		 * @ ind = 2  the supplyId of the supply (if it was collected) or -1 (nothing was collected) (Not exist for Minotaur)
		 * @ ind = 3  the distance from the opponent measured in number of tiles or -1(opponent not visible) (ind = 1 for Mino)
		 * @ ind = 4,...,size-1 possibly(may not exist) the distance from nearby supplies TOGETHER with the direction (ind = 2,..., size-1 for Mino)
		 * We change it to coresponds in the demands of the 3rd devirable
		 */
		public int[] getNextMove(int currentPos, int opponentCurrentPos) {
			//We make the assumption that the player can move in any tile that is located up down left or right
			//In the above function(evaluate) we check if there is a wall to any of those directions
			// and we take care of the case in which the player would go out of bounds
			//So for a given tileId we evaluate 4 moves
			int move[] = new int[2];
			int bestMove = chooseMinMaxMove(currentPos, opponentCurrentPos);
			
			int newPos = closeTileId(currentPos, bestMove);	//the tileId of the newPosition
			move[0] = newPos;
			if(newPos < 0 || newPos>=board.getN()*board.getN()) {
				System.out.println("Wrong Move");
				System.exit(1);
			}
			
			ArrayList<Integer> moveInfo = new ArrayList<Integer>(0);
			
			moveInfo.add((bestMove+newPos)*prime(bestMove));	//save the dice together with the tileId
			if(playerId == 1) { //only Theseus collects supplies. No need this parametr for Minotaur
				int temp = -1;
				int collectedSupply = 0;
				if(board.searchSupply(newPos) != -1){
					collectedSupply = 1;
				}
				moveInfo.add(collectedSupply);	//save 1 if he collected a supply or 0 otherwise 
				//we also add the supplyId to the matrix we create or -1 if no supplies are collected
				if(collectedSupply == 1) {
					for(int i  = 0; i < board.getS(); i++) {
						if(board.getSupply(i).getSupplyTileId() == newPos) {
							temp = i;
						}
					}
				}
				move[1] = temp;
				moveInfo.add(temp);
			}
			else {
				move[1] = -1;
			}
			
			moveInfo.add(opponentDist(newPos));	//save the distance from the opponent	
			ArrayList<Integer> supD = supplyDist(newPos);
			if(supD.size() > 0) {
				for(int i = 0; i < supD.size(); i++) {
					moveInfo.add(supD.get(i));	//save the distance from a supply (or supplies) in the newPos in an encoded form
				}
			}
			
			setPath(moveInfo);
			return move;
		}
		
		/**
		 * 
		 * @param flag is false if called for printing the statistics of a round
		 * 			   is true if called for printing the statistics of the game in the end
		 * 
		 */

		public void statistics(boolean flag) {
			

			for(int i = 0; i < path.size(); i++){
				//if flag = false we want to print the info of the last round
				if(!flag){
					i = path.size() -1;
				}
				System.out.print("\n");
				//the round of the game is given by 2*i + 1 for Theseus
				// 2 * (i + 1) for Minotaur
				if(flag && i == (path.size() - 1)) {
					System.out.print(getName() + " in final round");
				}
				else{
					if(playerId == 1)
						System.out.print(getName() + " in round: "+ (2*i + 1));
					else
						System.out.print(getName() + " in round: "+ (2 * (i + 1)));
				}

				//
				//in coded form the tileId and the dice of the round
				int coded = getPath(i).get(0);
				//the dice of the round
				int dice1 = (decode(coded))[1];
				//the tileId of the round
				int tileId = (decode(coded))[0];
				System.out.print(" ,has set the dice to: "+dice1);
				if(playerId == 1) {	
					if(getPath(i).get(1) == 1) {
							int supId = getPath(i).get(2);
							System.out.print(" and collected supply S"+(supId + 1));
					}
				}	
				System.out.print(".\nHe is located at (x,y) = ("+tileId/board.getN()+","+tileId%board.getN()+").\n");

				//inform about score
				if(playerId == 1) {
					if(roundScore(i+1) == 1) {
						System.out.println("Theseus has collected : "+roundScore(i+1)+" supply until now!");
					}
					else {
						System.out.println("Theseus has collected : "+roundScore(i+1)+" supplies until now!");
					}
				}
				//check the distance from opponent 
				//get the i-th ArrayList<Integer> contained in the path. And from this arraylist take the element @ ind = 3
				if(playerId == 1) {
					if(getPath(i).get(3) != -1) {
						System.out.println("Theseus is : "+getPath(i).get(3)+" tiles far from the Minotaur!");
					}
					else
						System.out.println("Theseus cannot see Minotaur from his position.");
				}
				else {
					if(getPath(i).get(1) != -1) {
						System.out.println("Minotaur is : "+getPath(i).get(1)+" tiles far from the Theseus!");
					}
					else
						System.out.println("Minotaur cannot see Theseus from his position.");
				}
				//check for the nearest supply and its relative distance to the player's new location
				if(playerId == 1) {
					if(getPath(i).size() > 4) {
						int encoded = getPath(i).get(4);
						int dice = (decode(encoded))[1];
						int tileDist = (decode(encoded))[0];
						switch(dice) {
							case 1: System.out.println("Theseus can see a supply "+tileDist+" tile(s) up from his position."); break;
							case 3: System.out.println("Theseus can see a supply "+tileDist+" tile(s) right from his position."); break;
							case 5: System.out.println("Theseus can see a supply "+tileDist+" tile(s) down from his position."); break;
							case 7: System.out.println("Theseus can see a supply "+tileDist+" tile(s) far from his position."); break;
						}
					}
					else{
						System.out.println("Theseus cannot see any supplies from his position.");
					}
				}
				else {
					if(getPath(i).size() > 2) {
						int encoded = getPath(i).get(2);
						int dice = (decode(encoded))[1];
						int tileDist = (decode(encoded))[0];
						switch(dice) {
							case 1: System.out.println("Minotaur can see a supply "+tileDist+" tile(s) up from his position."); break;
							case 3: System.out.println("Minotaur can see a supply "+tileDist+" tile(s) right from his position."); break;
							case 5: System.out.println("Minotaur can see a supply "+tileDist+" tile(s) down from his position."); break;
							case 7: System.out.println("Minotaur can see a supply "+tileDist+" tile(s) far from his position."); break;
						}
					}
					else{
						System.out.println("Minotaur cannot see any supplies from his position.");
					}
				}
			}
			
			//if we are in the end of the game, we also print the final statistics
			if(flag) {
				System.out.println("\nFinal Statistics:");
				//gather the required information from each move and
				int countUp = 0;
				int countRight = 0;
				int countDown = 0;
				int countLeft = 0;
				for(int i = 0; i < path.size(); i++) {
					int direction = decode(path.get(i).get(0))[1];
					switch(direction) {
						case 1: countUp++;    break;
						case 3: countRight++; break;
						case 5: countDown++;  break;
						case 7: countLeft++;  break;
						default:
							System.out.println("Error in statistics!");
							System.exit(1);
					}
				}
				if(countUp != 1) {
					System.out.println(getName()+" decided to move up(1) " + countUp + " times.");
				}
				else {
					System.out.println(getName()+" decided to move up(1) " +  "once.");
				} 
				if(countRight != 1) {
					System.out.println(getName()+" decided to move right(3) " + countRight + " times.");
				}
				else {
					System.out.println(getName()+" decided to move right(3) " + "once.");
				}
				if(countDown != 1) {
					System.out.println(getName()+" decided to move down(5) " + countDown + " times.");
				}
				else {
					System.out.println(getName()+" decided to move down(5) " + "once.");
				}
				if(countLeft != 1) {
					System.out.println(getName()+" decided to move left(7) " + countLeft + " times.");
				}
				else {
					System.out.println(getName()+" decided to move left(7) " + "once.");
				}
			}
		}
		
		//extra methods
		
		
		/**
		 * *
		 * @param currentPos the tileId of the Player's hypothetical or actual spot
		 * @param dice the direction {up,right,down,left} <-> {1,3,5,7} we want to check
		 * @return the tileId of the neighboring tile (specified by dice). If the neighboring tile is not valid -1 is returned 
		 */
		
		public int closeTileId(int currentPos, int dice) {
			int N = board.getN();
			int x = currentPos / N;
			int y = currentPos % N;
			
			switch(dice) {
			case 1: if((board.getTile(currentPos).closeIsValid(x+1, N)) && (!board.getTile(currentPos).getUp())){ 
						return (x+1)*N + y;
			}
			break;
			case 3: if((board.getTile(currentPos).closeIsValid(y+1, N)) && (!board.getTile(currentPos).getRight())){ 
						return x*N + y+1;
					}	
			break;
			case 5: if((board.getTile(currentPos).closeIsValid(x-1, N)) && (!board.getTile(currentPos).getDown()) && (currentPos != 0)){
						return (x-1)*N + y;
					}
			break;
			case 7:	if((board.getTile(currentPos).closeIsValid(y-1, N)) && (!board.getTile(currentPos).getLeft())){
						return x*N + y-1;
					} 
			break;
			default:
				System.out.println("Error in closeTileId. Unknown dice value!");
				System.exit(1);
				return -10000;
			}
			return -1;
		}
		
		/**
		 * 
		 * @param k = distance - 1 where distance refers to the number of tiles that "line up" (I can see 3 at max)
		 * next to the tile that the player is located in one of the 4 directions as presented in the task 
		 * @return part of the cost of the path if the player had to take the particular step 
		 */
		
		public double measureStep(int k) {
			switch(k) {
				case 0: return 1;	
				case 1: return 0.5;	
				case 2: return 0.3;	
				default: 
					System.out.println("Unknown Value in measureStep");
					System.exit(1);
					return Double.NEGATIVE_INFINITY;
			}
		}
		
		/**
		 * Depth First Search Algorithm
		 * @return the distance in number of tiles of the opponent, given that he is visible
		 * If the opponent is not visible in our vision scope (the cross) we return -1
		 * else the distance in tiles is returned
		 */
		public int opponentDist(int currentPos) {
			int dist = -1;
			int closeId;
			
			
			//keep track of the steps you are taking in every direction by saving the currentPosition of the player in each step
			int initialPos =  currentPos;
			
			//j = d - 1 shows where d is the distance in tiles from the initialPos
			for(int i = 0; i < 4; i++) {
	            int dice = 2*i+1;
	            currentPos = initialPos;	//after checking 3 steps in the direction indicated by dice,
	            //start checking step triplets in other directions
				for(int j = 0; j < 3; j++) {
					
					boolean flag = false;
					//if you stumble in a wall continue searching in another direction
					switch(dice) {
						case 1: if(board.getTile(currentPos).getUp()) {
							flag = true;
						}
						break;
						case 3: if(board.getTile(currentPos).getRight()) {
							flag = true;
						}
						break;
						case 5: if((board.getTile(currentPos).getDown() || currentPos == 0)) {
							flag = true;
						}
			        	break;
						case 7: if(board.getTile(currentPos).getLeft()) {
							flag = true;
						}
						break;
						default: 
				        	System.out.println("Error in opponentDist. Unknown dice value!");
							System.exit(1);
					}
					if(flag) break;
					closeId = closeTileId(currentPos, dice);
						
					// playerId % 2 + 1 gives the opponent's id
					if((board.getPlayerTile(playerId % 2 + 1) == closeId)) {
						dist = j + 1;
						return dist;
					}
					//update virtual position
					currentPos = closeId;
					
				}
			}
			return dist;
		}
		
		// return all visible supplies from our new position
		/**
		 * Breadth First Search is used in order to save the closest supplies first
		 * @param currentPos the current tileId 
		 * @return a list that contains all distances from supplies.
		 */
		/*
		 * dice:        1(up)             3(right)       5(down)        7(left)
		 *                |                  |              |              |
		 * distance:    1,2,3              1,2,3          1,2,3          1,2,3
		 * 
		 * I will multiply the sum of (location + distance) with a relative (to the output values of the sum) big PRIME Number
		 * so as to encode ,simultaneously, BOTH the information of the distance from the supply AND the direction(up,right,down,left)
		 */
		public ArrayList<Integer> supplyDist(int currentPos) {
			int dist = -1;
			int closeId;
			
			boolean stumbledInWall[] = new boolean[4];	//holds true if we find a wall in the direction 2*i+1 
			for(int i = 0; i < 4; i++) {
				stumbledInWall[i] = false;
			}
			
			//create the structure that will hold our results
			ArrayList<Integer> result = new ArrayList<Integer>(0);
			
			//keep track of the steps you are taking in every direction by saving the currentPosition of the player in each step
			int[] currentP =  new int[4];
			for(int i = 0; i < 4; i++) {
				currentP[i] = currentPos;
			}
			
			//i = d - 1 shows where d is the distance in tiles from the currentPos
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 4; j++) {
					int dice = 2*j+1;
					
					if(stumbledInWall[j] == false) {
						switch(dice) {
							case 1: if(board.getTile(currentP[j]).getUp()) {
										stumbledInWall[0] = true;
										continue;
							}
							break;
							case 3: if(board.getTile(currentP[j]).getRight()){
										stumbledInWall[1] = true;
										continue;
							}
							break;
							case 5: if((board.getTile(currentP[j]).getDown() || currentP[j] == 0)){
										stumbledInWall[2] = true;
										continue;
							}
							break;
							case 7: if(board.getTile(currentP[j]).getLeft()){
										stumbledInWall[3] = true;
										continue;
							}
							break;
							default: 
								System.out.println("Error in supplyDist. Unknown dice value!");
								System.exit(1);
						}
						
						closeId = closeTileId(currentP[j], dice);
						
						
						if((board.searchSupply(closeId) != -1)) {
							//we encode the distance = i+1 WITH the direction-dice as described in the beginning of the function
							dist = ((i + 1) + dice)*prime(dice);
							result.add(dist);
						}
						//update virtual position in direction 2*j+1
						currentP[j] = closeId;
					}
				}
			}
			return result;
		}
		/**
		 * 
		 * @param dice the direction {up,right,down,left} <-> {1,3,5,7}
		 * @return the prime number that will be used for encoding
		 */
		public int prime(int dice) {
			switch(dice) {
				case 1: return 401; 
				case 3: return 421; 
				case 5: return 431; 
				case 7: return 461; 
			}
			System.out.println("Wrong call prime!");
			System.exit(1);
			return 1;
		}
		/**
		 * 
		 * @param dist the encoded distance using the formula (distance+dice)*prime_number(dice)
		 * @return an Integer[] array containing in the #0 spot the distance in tiles from the supply
		 * 									and in the  #1 spot the dice-direction(up,right,down,left)
		 * 
		 * ALTERNATIVELY this can be used:
		 * @param dist the encoded dice in the form (dice+ TileId of New Position)*prime(dice)
		 * @return an Integer[] array containing in the #0 spot the tileId of the move
		 * 													(The Player only knows the x,y coords so in order for him to 
		 * 													use this info we must first decompose it to x and y)
		 * 									and in the  #1 spot the dice-direction(up,right,down,left) of the move(did he move up,right,etc)
		 */		
		public Integer[] decode(int dist) {
			Integer[] result = new Integer[2];
			if(dist%401 == 0) {
				dist = dist/401;
				dist = dist - 1;	//subtract the dice
				result[0] = dist;
				result[1] = 1;	//dice(up)
				return result;
			}
			else if(dist%421 == 0) {
				dist = dist/421;
				dist = dist - 3;	//subtract the dice
				result[0] = dist;
				result[1] = 3;	//dice (right)
				return result;
			}
			else if(dist%431 == 0) {
				dist = dist/431;
				dist = dist - 5;	//subtract the dice
				result[0] = dist;
				result[1] = 5;	//dice (down)
				return result;
			}
			else if(dist%461 == 0) {
				dist = dist/461;
				dist = dist - 7;	//subtract the dice
				result[0] = dist;
				result[1] = 7;	//dice (left)
				return result;
			}
			System.out.println("Cannot decode this number!");
			System.exit(1);
			return result;	//This would give an error. We just persuade the compiler that we return something
		}
		
		/**
		 * 
		 * does not belong in the explored set ideally 
		 * or
		 * has been visited the least times
		 * @param currentPos the current position of the player
		 * @param equalMoves an array with dices that have the same evaluation result
		 * @return the bestMove as a dice
		 */
		public int exploreMore(int currentPos, ArrayList<Integer> equalMoves) {
			//store the number of times each one of the equalMoves has been repeated
			ArrayList<Integer> movesRepetition = new ArrayList<Integer>(0);
			//loop over equalMoves,  find the number of times each move has been repeated
			for(int i = 0; i < equalMoves.size(); i++) {
				int closeId = closeTileId(currentPos, equalMoves.get(i));
				//if closeId = -1 it means we cannot move. That cannot happen because we have already evaluated each move
				movesRepetition.add(timesVisited(closeId));
				
			}
			//Now we want to find the least visited tile.
			//If 2 tiles are visited the same number of times we choose randomly
			int min = movesRepetition.get(0);
			ArrayList<Integer> minId = new ArrayList<Integer>(0);
			minId.add(0);
			for (int i = 1; i < movesRepetition.size(); i++) {
				if(min > movesRepetition.get(i)) {
					min = movesRepetition.get(i);
					minId.clear();	//clear the list of indexes that corresponded to the previously least visited tile 
					minId.add(i);
				}
				else if(min == movesRepetition.get(i)) {
					minId.add(i);	//save the index of tiles that have been visited the same number of times 
				}
			}
			int randomId = (int) (Math.random()*100);
			int size = minId.size();
			randomId = randomId % size;
			int bestMove = equalMoves.get(minId.get(randomId));	// belongs in {1,3,5,7} <-> {up,right,down,left}
			
			return bestMove;	
		}
		
		/**
		 * @param tileId the tileId of the tile we are checking
		 * @return the times that a tile has already been visited
		 */
		public int timesVisited(int tileId) {
			
			if(path.size() == 0)
				return 0;
			else {
				int count = 0;
				
				for(int i = 0; i < path.size(); i++) {
					//encoded form of the tileId of the newPos
					int tileId_dice = path.get(i).get(0);
					int newPos = decode(tileId_dice)[0];
					if(tileId == newPos) 
						count++;
				}
				//The initial Position is not stored in the path
				if(tileId == 0){
					count++;
				}
				return count;
			}
		}

		/**
		 * We want to get hold of the score in every round after the game has ended
		 * In order to achieve this we will use the info stored in path and get the element with index = 1
		 * from each ArrayList<Integer> that describes each move.
		 * @param n the n-th dice of Theseus
		 * @return the score at the round corresponding to the n-th dice of Theseus
		 */
		public int roundScore(int n){
			int score = 0;
			for(int i = 0; i < n; i++){
				if(path.get(i).get(2) != -1){
					score++;
				}
			}
			return score;
		}
		
		//until here is a copy-paste for hereusticPlayer with the appropriate changes
		/**
		 * 
		 * @param currentPos is the position of the player
		 * @return an ArrayList of the dices that are valid for the player
		 */
		
		
		public ArrayList<Integer> validDices(int currentPos)
		{
			ArrayList<Integer> array = new ArrayList<Integer>();
			for(int i = 0; i < 4; i++) {
				if(closeTileId(currentPos, (2 * i + 1)) != -1) array.add((2 * i) + 1);
			}
			return array;
		}
		
		//Our goal is in this function to create a tree with height 1.
		
		//******Note: The parametr root was created as Node() (Empty constructor), so i have to change all the variables*****
	
		public void createMySubtree(int currentPos, int opponentCurrentPos, Node root, int depth)
		{
			ArrayList<Node > nodes = new ArrayList<Node > (validDices(currentPos).size());
			ArrayList<Node > child = new ArrayList<Node >(); //We suppose that we are in leaves, they have not children
			int tempx, tempy, newDepth; //Temporary values of x, y. We don't want to change (x, y) coordination of the player ande the depth of the root
			for(int i = 0; i < validDices(currentPos).size(); i++) {
				tempx = x; //We initialize the values of this temporary variables in each repetition
				tempy = y;
				newDepth = depth + 1;
				switch(validDices(currentPos).get(i)) {
					case 1:
						tempx++;
						break;
					case 3:
						tempy++;
						break;
					case 5:
						tempx--;
						break;
					case 7:
						tempy--;
						break;
					default:
						System.out.println("There is error either in validDices, or createMySubtree. They both are in MinMaxPayer");
						System.exit(1);
				}
				int[] move = new int[3];
				move[0] = tempx;
				move[1] = tempy;
				move[2] = validDices(currentPos).get(i);
				Node newNode = new Node(root, child, newDepth, move, board, evaluate(currentPos, validDices(currentPos).get(i)));
				nodes.add(newNode);
			}
			root.setChildren(nodes);
			root.setNodeBoard(board);
		}
		
	//Our goal is in this function to create a tree with height 1.
		
		//******Note: The parametr root was created as Node() (Empty constructor), so i have to change all the variables*****
		//******Note: We have different root this time
		
		public void createOpponentSubtree(int currentPos, int opponentCurrentPos, Node root, int depth)
		{
			ArrayList<Node > nodes = new ArrayList<Node > (validDices(opponentCurrentPos).size());
			ArrayList<Node > child = new ArrayList<Node >(); //We suppose that we are in leaves, they have not children
			int tempx, tempy, newDepth; //Temporary values of x, y. We don't want to change (x, y) coordination of the player ande the depth of the root
			for(int i = 0; i <validDices(opponentCurrentPos).size(); i++) {
				tempx = (opponentCurrentPos / board.getN()); //We initialize the values of this temporary variables in each repetition of the loop
				tempy = (opponentCurrentPos % board.getN());
				newDepth = depth + 1;
				switch(validDices(opponentCurrentPos).get(i)) {
					case 1:
						tempx++;
						break;
					case 3:
						tempy++;
						break;
					case 5:
						tempx--;
						break;
					case 7:
						tempy--;
						break;
					default:
						System.out.println("There is error either in validDices, or createMySubtree. They both are in MinMaxPayer");
						System.exit(1);
				}
				int[] move = new int[3];
				move[0] = tempx;
				move[1] = tempy;
				move[2] = validDices(opponentCurrentPos).get(i);
				int opi = 0; //OpponentsPlayerId. Need cause the evaluate differs from player to player
				switch(playerId) {
					case 1:
						opi = 2;
						break;
					case 2:
						opi = 1;
					default:
						System.out.println("Error in createOpponentSubTree(Class: MinMaxPlayer");
				}
				MinMaxPlayer p = new MinMaxPlayer();
				p.setPlayerId(opi);
				p.setBoard(board);
				Node newNode = new Node(root, child, newDepth, move, board, p.evaluate(opponentCurrentPos, validDices(opponentCurrentPos).get(i)));
				nodes.add(newNode);
			}
			root.setChildren(nodes);
			root.setNodeBoard(board);
		}
		
		public int chooseMinMaxMove(int currentPos, int opponentCurrentPos)
		{
			int bestDice = -1; //not valid value for dice, in this variable we will store the best option for the player
			Node myRoot = new Node();
			Node opponentRoot = new Node();
			createMySubtree(currentPos, opponentCurrentPos, myRoot, 0);
			createOpponentSubtree(currentPos, opponentCurrentPos, opponentRoot, 0);
			switch(playerId) {
				case 1://Theseus is playing
					//To take the best move, we use this routine
					//For each valid move of Theseus we take its evaluation and reduce it by the evaluation of every valid move of Minotaur
					//we will take the return the dice of the dice with the max value of this subtraction
					double[] evals = new double[validDices(currentPos).size()]; // here i will store the min of the evaluation differentials so to choose the max one
					double minEval;
					double anOption = 0; //In each loop we will use this variable as the subtraction that we refer forwards
					double maxValue = -1000.0; //really small value to take the best option
					for(int i = 0; i < validDices(currentPos).size(); i++) { //represents the dice of valid moves of Theseus
						minEval = Double.POSITIVE_INFINITY; //Big enough value
						if(myRoot.getChildren().get(i).getNodeEvaluation() <= -1000) {
							//neighboor tile lies Minotaur. Run for your life
							evals[i] = Double.NEGATIVE_INFINITY;
						}
						else {
							for(int j = 0; j < validDices(opponentCurrentPos).size(); j++) { //represents the dice of valid moves of Minotaur
								anOption = myRoot.getChildren().get(i).getNodeEvaluation() - opponentRoot.getChildren().get(j).getNodeEvaluation();
								if(minEval > anOption)
									minEval = anOption;
							}
							evals[i] = minEval;
						}
					}
					ArrayList <Integer > equalmoves = new ArrayList<Integer >();
					maxValue = evals[0];
					bestDice = validDices(currentPos).get(0);
					equalmoves.add(validDices(currentPos).get(0));
					for(int i = 1; i < validDices(currentPos).size(); i++) { //represents the dice of valid moves of Theseus
						if(evals[i] > maxValue) {
							equalmoves.clear();
							maxValue = evals[i];
							bestDice = validDices(currentPos).get(i);
							equalmoves.add(bestDice);
						}
						else if(evals[i] == maxValue) {
							equalmoves.add(validDices(currentPos).get(i));
						}
					}
					if(equalmoves.size() == 1) {
						break;
					}
					else if(equalmoves.size() > 1){
						bestDice = exploreMore(currentPos, equalmoves);
						break;
				}
				else {
					System.out.println("Error in chooseMinMaxMove case 2");
					System.exit(1);
					break;
				}
				case 2:
					//To take the best move, we use this routine
					//For each valid move of Theseus we take its evaluation and reduce it by the evaluation of every valid move of Minotaur
					//we will take the return the dice of the dice with the max value of this subtraction
					double option = 0; //In each loop we will use this variable as the subtraction that we refer forwards
					double minValue = 1000.0; //really small value to take the best option
					ArrayList <Integer > equals = new ArrayList<Integer >();
					double[] eval = new double[validDices(currentPos).size()];
					double maxEval;
					for(int i = 0; i < validDices(currentPos).size(); i++) {
						maxEval = Double.NEGATIVE_INFINITY;//Small enough value
						/*if(myRoot.getChildren().get(i).getNodeEvaluation() <= -1000) { 
							//if there is wall, do not do the subtraction. Avoid this move
							eval[i] = Double.POSITIVE_INFINITY;							
						}*/
						if(myRoot.getChildren().get(i).getNodeEvaluation() >= 1000) {
							//neghboor tile lies Theseus. Go and get them
							eval[i] = Double.NEGATIVE_INFINITY;
						}
						else{
							for(int j = 0; j < validDices(opponentCurrentPos).size(); j++) {
								option = opponentRoot.getChildren().get(j).getNodeEvaluation() - myRoot.getChildren().get(i).getNodeEvaluation();
								if(maxEval < option)
									maxEval = option;
							}
							eval[i] = maxEval;
						}
					}
					minValue = eval[0];
					bestDice = validDices(currentPos).get(0);
					equals.add(validDices(currentPos).get(0));
					for(int i = 1; i < validDices(currentPos).size(); i++) { //represents the dice of valid moves of Minotaur
						if(minValue > eval[i]) {
							minValue = eval[i];
							equals.clear();
							bestDice = validDices(currentPos).get(i);
							equals.add(bestDice);
						}
						else if(minValue == eval[i]) {
							equals.add(validDices(currentPos).get(i));;
						}
					}
					if(equals.size() == 1) {
						break;
					}
					else if(equals.size() > 1){
							bestDice = exploreMore(currentPos, equals);
							break;
					}
					else {
						System.out.println("Error in chooseMinMaxMove case 2");
						System.exit(1);
						break;
					}
					default:
						System.out.println("There is error in chooseMinMaxMove. Check there(Class: MiMaxPlayer)");
						System.exit(1);
				}
			return bestDice;
		}
	
}
