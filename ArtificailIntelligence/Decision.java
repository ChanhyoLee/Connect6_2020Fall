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
	
	public double Get_ScoreOfSingleMove(int myBoard[][], Point myMove, double[] f, int player) {
	    double PlayerFactor[] = {0.0, 1.0, 3.96, 12.05, 0.0, 0.0}; // PlayerFactor[i] : 나의 착수로 window 안에 나의 돌 i개를 만들었을 때 score. 검은돌 4/5개는 어차피 Threat에서 알아서 카운트될테니 계산할 필요 없음
	    double OpponentFactor[] = {0.0, 1.33, 6.79, 19.52, 0.0, 0.0};// OpponentFacotr[i] : 나의 착수로 window 안에 상대 돌 i개를 저지했을 때 score. 하얀돌 4/5개를 막는건 어차피 Threat에서 걸러지므로 따로 score를 부여할 필요 없음
	    
	    int opponent = 3 - player;
	    double score = 0.0;
	    int board_copy[][] = new int[19][19];
	    CheckBoard.deepCopy_Board(CheckBoard.tile_board, board_copy);
	    board_copy[myMove.x][myMove.y] = player; // Threat의 수를 쉽게 구하기 위해 일단 착수시켜둠
	    int dx[] = { 1, 0, 1, 1 };
	    int dy[] = { 0, 1, 1, -1 };
	    for (int dir = 0; dir < 4; dir++) {
	        for (int i = 0; i < 6; i++) {
	            int x = myMove.x - i * dx[dir];
	            int y = myMove.y - i * dy[dir];
	            if (IsOutOfBounds(x, y) || IsOutOfBounds(x + 5 * dx[dir], y + 5 * dy[dir]))
	                continue; // 6칸이 다 보드 안에 있지 않는 threat window는 그냥 버림
	            boolean isThreat = true;
	            int PlayerStoneCnt = 0; // 6칸 내에 player 돌의 갯수(최소 1이어야 함, 0일 경우 6칸 내에 상대 돌이나 Block이 있는 경우
	            int OpponentStoneCnt = 0;
	            for (int k = 0; k < 6; k++) {
	                if (board_copy[x + k*dx[dir]][y + k*dy[dir]] == Stone.RED) { // Block이 있는 경우
	                    PlayerStoneCnt = 0;
	                    OpponentStoneCnt = 0; // Player, Opponent 둘 다 가능성이 없음
	                    isThreat = false;
	                    break;
	                }
	                else if (board_copy[x + i*dx[dir]][y + i*dy[dir]] == opponent) { // 상대 돌인 경우
	                    OpponentStoneCnt++;
	                    isThreat = false;
	                }
	                else if (board_copy[x + i*dx[dir]][y + i*dy[dir]] == player) // 내 돌인 경우
	                    PlayerStoneCnt++;
//	                else if (board_copy[x + i*dx[dir]][y + i*dy[dir]] == MARK) // MARK가 놓여있는 경우(계속 진행은 하지만 Threat은 아님)
//	                    isThreat = false;
	                // EMPTY가 놓여있는 경우 따로 처리해줄게 없음
	            }
	            if (isThreat && PlayerStoneCnt == 4 && (IsOutOfBounds(x - dx[dir], y - dy[dir]) || myBoard[x - dx[dir]][y - dy[dir]] != player) && (IsOutOfBounds(x + 6 * dx[dir], y + 6 * dy[dir]) || myBoard[x + 6 * dx[dir]][y + 6 * dy[dir]] != player)) { // 상대에게 Threat을 생성했으면서 window의 양 끝이 player의 돌이 아닌경우(7,8목 방지용). 갯수가 4일때만 취급하는 이유는, 5일때는 그 수를 두지 않아도 이미 Threat이라는 소리니 굳이 쳐줄 필요가 없음                    
	                score += 10000.0;
	                for (int h = 0; h < 6; h++) {
//	                    if (board_copy[x + h*dx[dir]][y + h*dy[dir]] == Stone.EMPTY)
//	                        board_copy[x + h*dx[dir]][y + h*dy[dir]] = MARK; // 비어있는 칸에 mark를 둠
	                }
	            }
	            if (OpponentStoneCnt == 0 && (IsOutOfBounds(x - dx[dir], y - dy[dir]) || board_copy[x - dx[dir]][y - dy[dir]] != player) && (IsOutOfBounds(x + 6 * dx[dir], y + 6 * dy[dir]) || board_copy[x + 6 * dx[dir]][y + 6 * dy[dir]] != player)) // 상대돌이 없을 때
	                score += PlayerFactor[PlayerStoneCnt];
	            if (PlayerStoneCnt == 1 && (IsOutOfBounds(x - dx[dir], y - dy[dir]) || board_copy[x - dx[dir]][y - dy[dir]] != opponent) && (IsOutOfBounds(x + 6 * dx[dir], y + 6 * dy[dir]) || board_copy[x + 6 * dx[dir]][y + 6 * dy[dir]] != opponent)) // 내 돌이 없는(자기자신때문에 값은 1) window일 경우.(즉 상대의 6목을 저지했음)
	                score += OpponentFactor[OpponentStoneCnt];
	        }
	    }
	    return score;
	}
}
