package ArtificailIntelligence;

import java.awt.Point;

import Game.CheckBoard;
import Game.Stone;
import Game.Tile;

public class Decision {

	public static Moves CurrentOpponentMoves = new Moves(-1, -1, -1, -1); // opmove가 실행된 후 상대방의 착수를 저장하는 변수
	public static Moves CurrentMyMoves = new Moves(-1, -1, -1, -1); // opmove가 실행된 후 상대방의 착수를 저장하는 변수
	public static int player = 1;
	public static void RenewalOpponentMoves(int x0, int x1, int y0, int y1) {
		CurrentOpponentMoves = new Moves(x0, y0, x1, y1);
	}
	
	public static boolean IsOutOfBounds(int x, int y) {
		if (0 <= x && x <19 && 0 <= y && y < 19)
			return false;
		return true;
	}
	
	public void print_POSITION(Point tmp) {
	    System.out.printf("(%d, %d)\n", tmp.x, tmp.y);
	}


	public static Moves find_connect6(Tile[][] board_src, Moves myMoves, int player) {
		if(myMoves.first_move.x==-1) return new Moves(-1,-1,-1,-1);
		
		int[][] board_dst= new int[19][19];
		CheckBoard.deepCopy_Board(board_src, board_dst);
		
		int[] dx = { -1, 0, 1, 1 };
		int[] dy = { 1, 1, 1, 0 };
		Point[] tmp1 = {myMoves.first_move, myMoves.second_move };
		
		for (int iter = 0; iter < 2; iter++) {
			int st_x = tmp1[iter].x;
			int st_y = tmp1[iter].y;
			for (int dir = 0; dir < 4; dir++) {
				for (int i = 0; i < 6; i++) {
					int x = st_x - dx[dir] * i;
					int y = st_y - dy[dir] * i;
					if (IsOutOfBounds(x, y) || IsOutOfBounds(x + 5 * dx[dir], y + 5 * dy[dir]))
						continue;
					int playerStone = 0;
					for (int j = 0; j < 6; j++) { // threat window 안의 6개 돌을 살펴보는 중
						if (board_dst[x + j*dx[dir]][y + j*dy[dir]] == Stone.EMPTY) // 빈 칸일 경우
							continue;
						else if (board_dst[x + j*dx[dir]][y + j*dy[dir]] == player) // player 돌이 놓여있을 경우
							playerStone++;
						else { // 상대방의 돌이 놓여있을 경우
							playerStone = 0;
							break;
						}
					}
					if (playerStone >= 4 && (IsOutOfBounds(x - dx[dir], y - dy[dir]) || board_dst[x - dx[dir]][y - dy[dir]] != player) && (IsOutOfBounds(x + 6 * dx[dir], y + 6 * dy[dir]) || board_dst[x + 6 * dx[dir]][y + 6 * dy[dir]] != player)) { // threat window 안에 4개 이상의 player 돌이 존재하고, 상대방 돌이 없으며 threat window와 인접한 2개의 돌이 player의 돌이 아닐때(7목 8목 방지용)
						Point[] tmp = { new Point(0,0),new Point(0,0)};
						int idx = 0;
						for (int k = 0; k < 6; k++) {
							if (board_dst[x + k*dx[dir]][y + k*dy[dir]] == Stone.EMPTY) {
								board_dst[x + k*dx[dir]][y + k*dy[dir]] = player;
								tmp[idx++] = new Point(x + i*dx[dir], y + i*dy[dir]);
							}
						}
						if (idx == 1) { // 5개의 player 돌로 채워진 곳이라 한 개의 돌은 다른 곳에 착수해야하는 상황일 때
							for (int h = 0; h < 19; h++) {
								for (int j = 0; j < 19; j++) {
									if (board_dst[h][j] != Stone.EMPTY) // 빈칸이 아닐 경우
										continue;
									if (h == x - dx[dir] && j == y - dy[dir]) // 6목을 깨버릴 경우
										continue;
									if (h == x + 6 * dx[dir] && j == y + 6 * dx[dir]) // 6목을 꺠버릴 경우
										continue;
									tmp[1] = new Point(h, j);
									System.out.println(tmp[0].toString()+tmp[1].toString());
									return new Moves(tmp[0], tmp[1]);
								}
							}
							// 여기에 도달했다는 것은 한 개의 돌을 어디에 두더라도 승리조건이 위배된다는 의미.
							return new Moves(-1, -1, -1, -1);
						}
						return new Moves(tmp[0], tmp[1]); // 빈칸을 반환. 여기에 착수하면 됨
					}
				}
			}
		}
		return new Moves(-1, -1, -1, -1);
	}
}
