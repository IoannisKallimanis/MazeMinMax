public class Tile {

	int TileId;
	int x;
	int y;
	boolean up;
	boolean down;
	boolean left;
	boolean right;

	public Tile() {
		TileId = -1;
		x = -1;
		y = -1;
		up = false;
		down = false;
		left = false;
		right = false;
	}

	public Tile(int TileId, int x, int y, boolean up, boolean down, boolean left, boolean right)
	{
		this.TileId = TileId;
		this.x = x;
		this.y = y;
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
	}

	public Tile(Tile c)
	{
		TileId = c.getTileId();
		x = c.getx();
		y = c.gety();
		up = c.getUp();
		down = c.getDown();
		left = c.getLeft();
		right = c.getRight();
	}

	public void setTileId(int TileId)
	{
		this.TileId = TileId;
	}

	public void setx(int x)
	{
		this.x = x;
	}

	public void sety(int y)
	{
		this.y = y;
	}

	public void setUp(boolean up)
	{
		this.up = up;
	}

	public void setDown(boolean down)
	{
		this.down = down;
	}

	public void setLeft(boolean left)
	{
		this.left = left;
	}

	public void setRight(boolean right)
	{
		this.right = right;
	}

	public int getTileId()
	{
		return TileId;
	}

	public int getx()
	{
		return x;
	}

	public int gety()
	{
		return y;
	}

	public boolean getUp()
	{
		return up;
	}

	public boolean getDown()
	{
		return down;
	}

	public boolean getLeft()
	{
		return left;
	}

	public boolean getRight()
	{
		return right;
	}

	/**
	@return the number of walls this tile has
	*/
	 public int getNumberOfWalls()
	 {
		 int counter = 0;
		 if(up) counter++;
		 if(down || (TileId == 0)) counter++;
		 if(left) counter++;
		 if(right) counter++;
		 return counter;
	 }
	 /** when looking info on neighboring tiles we must ensure that
	  we stay in BOUNDS */
	  public boolean closeIsValid(int x, int N){
	    if(x >= 0 && x < N)
	      return true;
	    return false;
	  }

	}
