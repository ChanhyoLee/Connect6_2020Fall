package ArtificailIntelligence;

import java.awt.Point;

public class Moves {

	public Point first_move;
	public Point second_move;
	
	public Moves(int x1, int y1, int x2, int y2) {
		first_move = new Point(x1, y1);
		second_move = new Point(x2, y2);
	}
	
	public Moves(Point fm, Point sm) {
		first_move = fm;
		second_move = sm;
	}
	
	public static void print_Moves(Moves tmp2) {
	    System.out.printf("(%d, %d), (%d, %d)\n", tmp2.first_move.x, tmp2.first_move.y, tmp2.second_move.x, tmp2.second_move.y);
	}
}
