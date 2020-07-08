package ArtificailIntelligence;
import Game.Tile;

public class PotentialLine {
	//public Tile[][] tile_board = new Tile[19][19];
	
	public Tile[] potential_line = new Tile[11];
	
	public PotentialLine(Tile[] potential_line) {
		this.potential_line = potential_line; 
	}
	
	public boolean equals(Object obj) {
		if(this.hashCode()==((Tile[])obj).hashCode()) return true;
		else return false;
	}
	
	@Override
	public int hashCode() {
		int result = 0;
		for(int i=0; i<11; i++) {
			result += potential_line[i].getStone().color * Math.pow(3, i);
		}
		return result;
	}
}
