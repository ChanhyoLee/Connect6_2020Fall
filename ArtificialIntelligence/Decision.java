package ArtificialIntelligence;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;

import Game.CheckBoard;
import Game.Stone;

public class Decision {

	public static int player = 1;
	public static HashMap<String, int[]> winner_condition;
	public static HashMap<String, int[]> winner_5_condition;
	public static HashMap<String, int[]> loser_condition;
	public static HashMap<String, int[]> loser_5_condition;
	public static Robot airobot;

	public Decision(){
		try {
			airobot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public static void find_best() { //최고의 수를 실행, 각 단계의 로직을 병합하는 부분 
		Vector<Point> final_moves = new Vector<Point>();
		int[][] board = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board); //보드 카피 
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) { 
				Vector<Point> temp_vector = find_pattern(i,j);
				if(temp_vector!=null) { //승리할 수 있는 패턴이 존재한다면, 
					ai_click(temp_vector.get(0));
					ai_click(temp_vector.get(1));
					return; //승리할 수 있는 패턴이 존재하면 아래의 조건들은 고려하지 않아도 됨 
				}
				else {
					if(find_threat(board, i, j).size()>0) { //승리할 수 있는 패턴이 존재하지 않을 경우, 상대가 승리할 수 있는 패턴을 찾아 수비해야함
						Moves temp = defend_threat();
						if(!final_moves.contains(temp.first_move)) { //중복 원소가 삽입되는 경우 걸러내기 
							final_moves.add(temp.first_move);
						}
						if(!final_moves.contains(temp.second_move)) { //중복 원소가 삽입되는 경우 걸러내기 
							final_moves.add(temp.second_move);
						}
					}
				}
			}
		}
		if(final_moves.size()==1) {
			try {//꼭 막아야하는 수가 하나일 경우 
				System.out.printf("should 1.(%d, %d)", final_moves.get(0).x, final_moves.get(0).y);
				ai_click(final_moves.get(0));
			    board[final_moves.get(0).x][final_moves.get(0).y] = player; //필수로 방어해야하는 곳을 막음 
		    }catch(Exception e) {
			    System.out.println("Lose....But Second Best");
			    return;
		    }
			Point second_move = Find_BestSingleMove(board);	//영향력이 가장 높은 포인트에 착수
		    System.out.printf(" Free 2. (%d, %d)\n", second_move.x, second_move.y); 
		    ai_click(second_move);
		}
		else if(final_moves.size()>=2) { //꼭 막아야하는 수가 두개 이상일 경우 
			if(final_moves.get(1).x==-1 && final_moves.get(1).y==-1) { //final moves 원소의 좌표가 -1,-1 인 경우 한 수로 방어하고 자유롭게 한 수가 남을 경우임 
				System.out.printf("should 1.(%d, %d)", final_moves.get(0).x, final_moves.get(0).y);
			    ai_click(final_moves.get(0));
			    board[final_moves.get(0).x][final_moves.get(0).y] = player;
				Point second_move = Find_BestSingleMove(board);	//영향력이 가장 높은 포인트에 착수
			    System.out.printf(" 2. Free (%d, %d)\n", second_move.x, second_move.y); 
			    ai_click(second_move);
			}
			else { //두어야하는 두 수가 모두 유효할 때 
				System.out.printf("should 1.(%d, %d) should 2.(%d, %d)\n", final_moves.get(0).x, final_moves.get(0).y, final_moves.get(1).x, final_moves.get(1).y);
				ai_click(final_moves.get(0));
				ai_click(final_moves.get(1));
			}
		}
		else { //강제되는 수가 하나도 없을 때 
			//영향력이 가장 높은 포인트에 착수 
			double max_score = -100000; //초기 점수는 낮게 설정 
			Moves best_combination = new Moves(-1,-1,-1,-1);
		    Vector<Point> all_moves = get_allPossibleMoves(); //가능한 모든 빈칸을 반환 
		    for(int i=0; i<all_moves.size(); i++) {
		    	for(int j=i+1; j<all_moves.size(); j++) { //모든 빈칸에 착수하는 경우 중 두가지를 골라 점수를 반환 
		    		Moves temp_moves = new Moves(all_moves.get(i), all_moves.get(j));
		    		double now_score = calculateDoubleMove_score(temp_moves);
		    		if(max_score<now_score) { //새로 얻은 점수가 이전 최대 값보다 크다면, 
			    		max_score = now_score; //점수 갱신
			    		best_combination = new Moves(all_moves.get(i), all_moves.get(j)); //좌표 갱신 
		    		}
		    	}
		    }
		    System.out.printf("Free 1. (%d, %d)", best_combination.first_move.x, best_combination.first_move.y);
		    ai_click(best_combination.first_move);
		    System.out.printf(" Free 2. (%d, %d)\n", best_combination.second_move.x, best_combination.second_move.y);
		    ai_click(best_combination.second_move); 
		}
	}
	
	public static void ai_click(Point first_move) { //자동으로 화면 클릭하는 robot 
		if(first_move.x!=-1&&first_move.y!=-1) CheckBoard.tile_board[first_move.x][first_move.y].mouseClicked();
	}
	public static boolean IsOutOfBounds(int x, int y) { //주어진 인덱스 값이 배열의 최대, 최소 범위를 벗어나는지 판단 (바둑판 영역 밖인지 판단)
		if (0 <= x && x <19 && 0 <= y && y < 19)
			return false;
		return true;
	}

	public static Vector<Point> get_allPossibleMoves(){ //모든 빈칸을 반환 
		Vector<Point> all_moves = new Vector<Point>();
		
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				if(CheckBoard.tile_board[i][j].getStone().color==Stone.EMPTY) {
					Point temp = new Point(i,j);
					all_moves.add(temp);
					
				}
			}
		}		
		return all_moves;	
	}
	
	public static double calculateDoubleMove_score(Moves myMoves) { //두 수의 점수 최대값을 계산 
		int board[][] = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board);
	    double score = 0.0;
	    score += calculateSingleMove_score(myMoves.first_move);
	    board[myMoves.first_move.x][myMoves.first_move.y] = player; // 첫번째 착점하는 곳을 player로 채워넣고 두번째 착수에 대한 score를 합산
	    score += calculateSingleMove_score(myMoves.second_move);
	    board[myMoves.first_move.x][myMoves.first_move.y] = Stone.EMPTY; // 다시 되돌림
	    return score;
	}

	public static double calculateSingleMove_score(Point myMove) { // 한 수의 점수 최대값을 계산 
		double PlayerFactor[] = {0.0, 1.0, 3.96, 12.05, 0.0, 0.0}; // PlayerFactor[i] : 나의 착수로 window 안에 나의 돌 i개를 만들었을 때 score. 검은돌 4/5개는 어차피 Threat에서 알아서 카운트될테니 계산할 필요 없음
		double OpponentFactor[] = {0.0, 1.33, 6.79, 19.52, 0.0, 0.0};// OpponentFacotr[i] : 나의 착수로 window 안에 상대 돌 i개를 저지했을 때 score. 하얀돌 4/5개를 막는건 어차피 Threat에서 걸러지므로 따로 score를 부여할 필요 없음
		if(myMove.x==-1 && myMove.y==-1) return 0.0;
		int opponent = 3 - player;
		double score = 0.0;
		int board[][] = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board);
		board[myMove.x][myMove.y] = player; // Threat의 수를 쉽게 구하기 위해 일단 착수시켜둠
		
		
	    int dx[] = { 1, 0, 1, 1 };
	    int dy[] = { 0, 1, 1, -1 };
		for(int dir=0; dir<4; dir++) { //4개 방향에 대하여, 
			for(int i=0; i<6; i++) { //인자로 전달받은 기준점을 포함하는 모든 6개의 칸에 대하여 수행 
	            int x = myMove.x - i * dx[dir];
	            int y = myMove.y - i * dy[dir];
				if(IsOutOfBounds(x,y)||IsOutOfBounds(x+5*dx[dir], y+5*dy[dir])) { //탐색할 6칸이 바둑판 안에 존재하지 않으면 스킵 
					//System.out.println("out of bounds!");
					continue;
				}
				int playercount =0, opponentcount=0; //6칸안에 존재하는 내 돌과 상대의 돌의 개수 
				boolean isThreat = true;
				for(int k=0; k<6; k++) {
					if(board[x+k*dx[dir]][y+k*dy[dir]]==Stone.RED) { //적돌이 6칸 안에 있을 경우 죽은 공간임 
						playercount=0;
						opponentcount=0;
						isThreat = false;
						break;
					}
					else if(board[x+k*dx[dir]][y+k*dy[dir]]==opponent) { // 상대의 돌 갯수 카운트 
						opponentcount++;
						isThreat = false;
					}
					else if(board[x+k*dx[dir]][y+k*dy[dir]]==player) { // 내 돌 갯수 카운트 
						playercount++;
					}
					else if(board[x+k*dx[dir]][y+k*dy[dir]]==Stone.MARK) { // 마크일 경우 아무 영향 없음 
						isThreat = false;
					}
				}
				
				if(isThreat&&playercount==4 && (IsOutOfBounds(x - dx[dir], y - dy[dir]) || board[x - dx[dir]][y - dy[dir]] != player) && (IsOutOfBounds(x + 6 * dx[dir], y + 6 * dy[dir]) || board[x + 6 * dx[dir]][y + 6 * dy[dir]] != player)){
					score += 10000.0;
	                for (int h = 0; h < 6; h++) {
	                    if (board[x + i*dx[dir]][y + h*dy[dir]] == Stone.EMPTY)
	                        board[x + i*dx[dir]][y + h*dy[dir]] = Stone.MARK; // 비어있는 칸에 mark를 둠
	                }
		        }
	            if (opponentcount == 0 && (IsOutOfBounds(x - dx[dir], y - dy[dir]) || board[x - dx[dir]][y - dy[dir]] != player) && (IsOutOfBounds(x + 6 * dx[dir], y + 6 * dy[dir]) || board[x + 6 * dx[dir]][y + 6 * dy[dir]] != player)) // 상대돌이 없을 때
	                score += PlayerFactor[playercount]; //가중치 합산 
	            if (playercount== 1 && (IsOutOfBounds(x - dx[dir], y - dy[dir]) || board[x - dx[dir]][y - dy[dir]] != opponent) && (IsOutOfBounds(x + 6 * dx[dir], y + 6 * dy[dir]) || board[x + 6 * dx[dir]][y + 6 * dy[dir]] != opponent)) // 내 돌이 없는(자기자신때문에 값은 1)6칸 (즉 상대의 6목을 저지했음)
	                score += OpponentFactor[opponentcount]; //가중치 합산 
			}			
		}
		return score;
	}
	
	public static Point Find_BestSingleMove(int[][] board) { //모든 빈 칸에 대하여 최고점인 점을 찾는 메소드 
	    double maxscore = -1.0;
	    Point bestmove = new Point(-1, -1);
	    for (int x = 0; x < 19; x++) {
	        for (int y = 0; y < 19; y++) {
	            if (board[x][y] != Stone.EMPTY) //빈칸이 아닐 경우 스킵 
	                continue;
	            double tmpScore = calculateSingleMove_score(new Point(x,y));
	            if (tmpScore >= maxscore && Math.abs(19 / 2 - x) + Math.abs(19 / 2 - y) < Math.abs(19/ 2 - bestmove.x) + Math.abs(19 / 2 - bestmove.y)) { //바둑판 중앙과 더 가까워야함! 
	                bestmove = new Point(x,y);
	                maxscore = tmpScore;
	            }
	        }
	    }
	    //System.out.printf("(%d, %d)", bestmove.x, bestmove.y);
	    return bestmove;
	}
	 
	
	private static Moves defend_threat() { //위협이 되는 자리를 효율적으로 막는 메소드 
		int[][] board = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board);
		Vector<Point> threat_vector = new Vector<Point>(); //기존 위협 포인트들이 담긴 벡터 
		Vector<Point> after_threat_vector = new Vector<Point>(); //시험 착수 이후의 위협포인트들이 담긴 벡터 
		Moves forced_moves = new Moves(-1,-1,-1,-1);
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				for(Point p: find_threat(board, i ,j)) {
					if(!threat_vector.contains(p)) threat_vector.add(p);//꼭 막아야하는 포인트 벡터 생성
				} 
			}
		}
		Vector<Moves> candidate_moves = new Vector<Moves>();
		Vector<Moves> second_candidate_moves = new Vector<Moves>();
		for(int i=0; i<threat_vector.size(); i++) { //위협(막아야하는) 포인트벡터 전체 원소 중 두개의 원소를 뽑아 놓았다고 가정 
			for(int j=i; j<threat_vector.size(); j++) {
				if(threat_vector.get(i).x==-1 || threat_vector.get(i).y==-1);
				else board[threat_vector.get(i).x][threat_vector.get(i).y]= player; 
				if(threat_vector.get(j).x==-1 || threat_vector.get(j).y==-1);
				else board[threat_vector.get(j).x][threat_vector.get(j).y]= player;
				//print_BOARD(board);
				for(int x=0; x<19; x++) { 
					for(int y=0; y<19; y++) {
						after_threat_vector.addAll(find_threat(board, x, y)); //놓았다고 가정 했을 때 위협 포인트 벡터 생성 
					}
				}
				if(after_threat_vector.size()==0) { //위협 포인트 벡터의 크기가 0이라는 것은 꼭 막아야하는 수가 없다는 의미, 즉 가정한 두 가지 원소의 위치에 강제적으로 놓아야함 
					candidate_moves.add(new Moves(threat_vector.get(i), threat_vector.get(j)));
				}
				else if(after_threat_vector.size()>0){ //위협 포인트 벡터의 크기가 0보다 크다는 것은 막아야하는 곳이 두 군데 이상임을 말함 - 패배, 하지만 상대가 이기는 수를 발견하지 못할 경우를 대비해 차선책을 
					second_candidate_moves.add(new Moves(threat_vector.get(i), threat_vector.get(j)));
				}
				forced_moves = new Moves(-1,-1,-1,-1); //초기화 
				CheckBoard.deepCopy_Board(CheckBoard.tile_board, board); //초기화 
				after_threat_vector.clear(); //초기화 	
			}
		}
		double topScore = -100000.0;
		if(candidate_moves.size()>0) {
			for(Moves temp_moves: candidate_moves) {
				if(topScore<calculateDoubleMove_score(temp_moves)) { //꼭 막아야하는 생성된 모든 경우의 수 중에서 가장 높은 점수로 막을 수 있는 수를 찾음  
					forced_moves = temp_moves;
					topScore = calculateDoubleMove_score(temp_moves); //Version2
				}
			}
		}
		else if(second_candidate_moves.size()>0){ //차선책들 중에 최선!
			forced_moves = second_candidate_moves.lastElement();
		}

		System.out.printf("(%d, %d), (%d, %d)\n", forced_moves.first_move.x, forced_moves.first_move.y, forced_moves.second_move.x, forced_moves.second_move.y);
		return forced_moves; 
	}
	private static Vector<Point> find_threat(int[][] board, int x, int y) { //상대의 위협(승리조건)을 만족하는 위치를 반환 
		Vector<Point> temp_vector = new Vector<Point>();
		String str_pattern = "";
		
		int[] dx = { -1, 0, 1, 1 };
		int[] dy = { 1, 1, 1, 0 };
		
		for(int dir=0; dir<4; dir++) { //4개 방향에 대하여, 
			for(int i=0; i<6; i++) { //인자로 전달받은 기준점 x,y에 대해 6개 칸을 탐색 
				if(!IsOutOfBounds(x+i*dx[dir],y+i*dy[dir])) { //6개의 칸이 보드 안에 들어온다면, 
					str_pattern+=String.valueOf(board[x+i*dx[dir]][y+i*dy[dir]]); //한 칸을 문자열에 이어붙이기 
				}
			}
			if(str_pattern.length()==6) {}
			else {
				str_pattern = "";
				continue;
			}
			if(loser_condition.containsKey(str_pattern) && (IsOutOfBounds(x+6*dx[dir],y+6*dy[dir])||board[x+6*dx[dir]][y+6*dy[dir]]!=3-player) && (IsOutOfBounds(x-1*dx[dir], y-1*dy[dir]) || board[x-1*dx[dir]][y-1*dy[dir]]!=3-player)) {
				//패배 패턴 해시맵 키에 최종 문자열이 존재하고 기준점 x,y의 -1번째, 6번째 위치의 돌이 상대 플레이어의 돌이 아닐때(장목방지)
				temp_vector.add(new Point(x+loser_condition.get(str_pattern)[0]*dx[dir], y+loser_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(x+loser_condition.get(str_pattern)[1]*dx[dir], y+loser_condition.get(str_pattern)[1]*dy[dir]));
			}
			else if(loser_5_condition.containsKey(str_pattern) && (IsOutOfBounds(x+6*dx[dir],y+6*dy[dir])||board[x+6*dx[dir]][y+6*dy[dir]]!=3-player) && (IsOutOfBounds(x-1*dx[dir], y-1*dy[dir]) || board[x-1*dx[dir]][y-1*dy[dir]]!=3-player)) {
				//이미 5목이면서 기준점 x,y의 -1번째, 6번째 위치이 돌이 플레이어의 돌이 아닐때(장목방지)
				temp_vector.add(new Point(x+loser_5_condition.get(str_pattern)[0]*dx[dir], y+loser_5_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(-1, -1));
			}
			str_pattern = ""; //다음 방향을 탐색하기 위해 문자열 초기화 
		}
		return temp_vector;
		
	}
	public void print_BOARD(int myBoard[][]) {
	    for (int x = 0; x < 19; x++) {
	        for (int y = 0; y < 19; y++)
	            System.out.printf("%d ", myBoard[x][y]);
	        System.out.printf("\n");
	    }
	}
	public static void deepCopy_board(int[][] src, int[][] dst) {
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				dst[i][j]=src[i][j];
			}
		}
	}
	public static Vector<Point> find_pattern(int x, int y) { //기준점 x,y에 대해 4방향 탐색 
		int[][] board = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board); 
		Vector<Point> temp_vector = new Vector<Point>();
		String str_pattern = "";
		
		int[] dx = { -1, 0, 1, 1 }; //4개 방향의 변화량, 인덱스 별로 구분 
		int[] dy = { 1, 1, 1, 0 };
		
		for(int dir=0; dir<4; dir++) { //4개 방향에 대하여, 
			for(int i=0; i<6; i++) { //인자로 전달받은 기준점 x,y에 대해 6개 칸을 탐색 
				if(!IsOutOfBounds(x+i*dx[dir],y+i*dy[dir])) { //6개의 칸이 보드 안에 들어온다면, 
					str_pattern+=String.valueOf(board[x+i*dx[dir]][y+i*dy[dir]]); //한 칸을 문자열에 이어붙이기 
				}
			}
			if(winner_condition.containsKey(str_pattern) && (IsOutOfBounds(x+6*dx[dir],y+6*dy[dir])||board[x+6*dx[dir]][y+6*dy[dir]]!=player) && (IsOutOfBounds(x-1*dx[dir], y-1*dy[dir]) || board[x-1*dx[dir]][y-1*dy[dir]]!=player)) {
				//승리패턴 해시맵 키에 최종 문자열이 존재하고 기준점 x,y의 -1번째, 6번째 위치의 돌이 플레이어의 돌이 아닐때(장목방지)
				System.out.print("winning pattern check!");
				System.out.println(String.format("1.(%d, %d), 2.(%d, %d)",x+winner_condition.get(str_pattern)[0]*dx[dir], y+winner_condition.get(str_pattern)[0]*dy[dir],x+winner_condition.get(str_pattern)[1]*dx[dir], y+winner_condition.get(str_pattern)[1]*dy[dir]));
				temp_vector.add(new Point(x+winner_condition.get(str_pattern)[0]*dx[dir], y+winner_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(x+winner_condition.get(str_pattern)[1]*dx[dir], y+winner_condition.get(str_pattern)[1]*dy[dir]));
				return temp_vector; //결과값이 한개 이상 도출되었으므로 종료 
			}
			else if(winner_5_condition.containsKey(str_pattern) && (IsOutOfBounds(x+6*dx[dir],y+6*dy[dir])||board[x+6*dx[dir]][y+6*dy[dir]]!=player) && (IsOutOfBounds(x-1*dx[dir], y-1*dy[dir]) || board[x-1*dx[dir]][y-1*dy[dir]]!=player)) {
				//이미 5목이면서 기준점 x,y의 -1번째, 6번째 위치이 돌이 플레이어의 돌이 아닐때(장목방지)
				System.out.print("winning pattern check!");
				System.out.println(String.format("1.(%d, %d)",x+winner_5_condition.get(str_pattern)[0]*dx[dir], y+winner_5_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(x+winner_5_condition.get(str_pattern)[0]*dx[dir], y+winner_5_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(-1, -1)); //나머지 한 개는 영향이 없는 다른 위치에 착수  
				return temp_vector; //결과값이 한개 이상 도출되었으므로 종료 
			}
			str_pattern = ""; //다음 방향을 탐색하기 위해 문자열 초기화 
		}
		return null;
	}
	
	public static HashMap<String, int[]> makeConnect6_hashmap(int player){ //4목이 완성되었을 때 승리패턴을 키값으로 하고 빈칸을 value값으로 가지는 해쉬맵 생성 
		HashMap<String, int[]> temp = new HashMap<String, int[]>();
		if(player==1) { //6C2가지의 경우의 수가 있음 
			temp.put("001111", new int[]{0,1});
			temp.put("011110", new int[]{0,5});
			temp.put("111100", new int[]{4,5});
			temp.put("101110", new int[]{1,5});
			temp.put("010111", new int[]{0,2});
			temp.put("110110", new int[]{2,5});
			temp.put("011011", new int[]{0,3});
			temp.put("111010", new int[]{3,5});
			temp.put("011101", new int[]{0,4});
			temp.put("100111", new int[]{1,2});
			temp.put("110011", new int[]{2,3});
			temp.put("111001", new int[]{3,4});
			temp.put("101011", new int[]{1,3});
			temp.put("101101", new int[]{1,4});
			temp.put("110101", new int[]{2,4});
		}
		else {
			temp.put("002222", new int[]{0,1});
			temp.put("022220", new int[]{0,5});
			temp.put("222200", new int[]{4,5});
			temp.put("202220", new int[]{1,5});
			temp.put("020222", new int[]{0,2});
			temp.put("220220", new int[]{2,5});
			temp.put("022022", new int[]{0,3});
			temp.put("222020", new int[]{3,5});
			temp.put("022202", new int[]{0,4});
			temp.put("200222", new int[]{1,2});
			temp.put("220022", new int[]{2,3});
			temp.put("222002", new int[]{3,4});
			temp.put("202022", new int[]{1,3});
			temp.put("202202", new int[]{1,4});
			temp.put("220202", new int[]{2,4});
		}
		
		return temp;
	}
	
	public static HashMap<String, int[]> makeConnect6_5_hashmap(int player){ //5목이 완성되었을 때 승리패턴을 키값으로 하고 빈칸을 value값으로 가지는 해쉬맵 생성 
		HashMap<String, int[]> temp = new HashMap<String, int[]>();
		if(player==1) { //6C1가지의 경우가 있음 
			temp.put("011111", new int[] {0});
			temp.put("101111", new int[] {1});
			temp.put("110111", new int[] {2});
			temp.put("111011", new int[] {3});
			temp.put("111101", new int[] {4});
			temp.put("111110", new int[] {5});
		}
		else {
			temp.put("022222", new int[] {0});
			temp.put("202222", new int[] {1});
			temp.put("220222", new int[] {2});
			temp.put("222022", new int[] {3});
			temp.put("222202", new int[] {4});
			temp.put("222220", new int[] {5});
		}
		return temp;
		
	}
	
	public static void update_HashMap() { //Player에 맞는 해쉬맵 생성 
		winner_condition = makeConnect6_hashmap(player);
		winner_5_condition = makeConnect6_5_hashmap(player);
		loser_condition = makeConnect6_hashmap(3-player);
		loser_5_condition = makeConnect6_5_hashmap(3-player);
	}

}