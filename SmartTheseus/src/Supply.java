public class Supply {

	int supplyId;
	int x;
	int y;
	int supplyTileId;
	boolean founded; // true if theseus found the supply, false in the other case

	public Supply()
	{
		supplyId = -1;	//not set properly
		x = -1;	//out of bounds	
		y = -1;	//out of bounds
		supplyTileId = -1;	//not yet on board
		founded = false;
	}

	public Supply(int supplyId, int x, int y, int supplyTileId, boolean founded)
	{
		this.supplyId = supplyId;
		this.x = x;
		this.y = y;
		this.supplyTileId = supplyTileId;
		this.founded = founded;
	}

	public Supply(Supply c)
	{
		supplyId = c.getSupplyId();
		x = c.getx();
		y = c.gety();
		supplyTileId = c.getSupplyTileId();
		founded = c.getFounded();
	}

	public void setSupplyId(int supplyId)
	{
		this.supplyId = supplyId;
	}

	public void setx(int x)
	{
		this.x = x;
	}

	public void sety(int y)
	{
		this.y = y;
	}

	public void setSupplyTileId(int supplyTileId)
	{
		this.supplyTileId = supplyTileId;
	}
	//We also create the setter for the extra variable
	public void setFounded(boolean founded)
	{
		this.founded = founded;
	}

	public int getSupplyId()
	{
		return supplyId;
	}

	public int getx()
	{
		return x;
	}

	public int gety()
	{
		return y;
	}

	public int getSupplyTileId() {
		return supplyTileId;
	}
	//We also create the getter for the extra variable
	public boolean getFounded()
	{
		return founded;
	}

	public boolean HasId(int id) //checks if the supply has ID the parameter of this method 
	{
		return supplyTileId == id ? true : false;
	}

}
