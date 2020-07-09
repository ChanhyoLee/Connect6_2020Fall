package Game;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import ArtificailIntelligence.Decision;
import ArtificailIntelligence.Moves;

public class Tile extends JLabel implements MouseListener, MouseMotionListener{
	
	//private Shape tile_shape;
	private Point location;
	private Stone stone;
	private BufferedImage whiteStone;
	private BufferedImage blackStone;
	private BufferedImage redStone;
	private static HashMap<String, int[]> winner_condition;
	private static HashMap<String, int[]> winner_5_condition;
	private static HashMap<String, int[]> loser_condition;
	private static HashMap<String, int[]> loser_5_condition;
	public static Point first_move;
	public static Point second_move;
	public static Robot airobot;
	
	public Tile(int x, int y) throws IOException {
		whiteStone = ImageIO.read(new File("whiteStone.png"));
		blackStone = ImageIO.read(new File("blackStone.png"));
		redStone = ImageIO.read(new File("redStone.png"));
		location = new Point(x,y);
		stone = new Stone(Stone.EMPTY);
		winner_condition = makeConnect6_hashmap(Decision.player);
		winner_5_condition = makeConnect6_5_hashmap(Decision.player);
		loser_condition = makeConnect6_hashmap(3-Decision.player);
		loser_5_condition = makeConnect6_5_hashmap(3-Decision.player);
		this.setLocation(10+x*40,10+y*40);
		this.setSize(30,30);
		this.setHorizontalAlignment(CENTER);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		try {
			airobot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paintComponent(Graphics g) {
		if(stone!=null) {
			if(stone.color==Stone.RED) {
				g.drawImage(redStone, 0, 0, 30, 30, null);
			}
			else if(stone.color==Stone.WHITE) {
				g.drawImage(whiteStone, 0, 0, 30, 30, null);
			}
			else if(stone.color==Stone.BLACK) {
				g.drawImage(blackStone, 0, 0, 30, 30, null);
			}
		}
	}
	@Override 
	public void mouseClicked(MouseEvent e) {
		
		this.setEnabled(false);
		SoundPackage.play_stoneSound();
		if(GameController.pause) {
			this.stone = new Stone(Stone.RED);
		}
		else {
			this.stone = new Stone(GameController.turn%2+1);
			if(is_end()) {
				// SoundPackage.stop_bgm();
				SoundPackage.play_winSound();
				GameController.playerTimer_stop();
				new AutoExitFrame(AutoExitFrame.END);
				new AutoExitFrame(AutoExitFrame.TIMER);
				}
			else {
				GameController.possible_actionNumber--; //한 수 놓았음 
				if(GameController.possible_actionNumber==0) { //주어진 수를 모두 놓았음 
					if(GameController.turn%2==Math.abs(Decision.player-2)) find_best(); //흑돌일때 0, 백돌일때 1
					GameController.turn++; //다음 턴으로! 
					GameController.reset_playerTime();
					GameController.possible_actionNumber=2;
					//new AutoExitFrame(GameController.turn%2);
					PlayerBoard.update_color();
				}
			}
		}
		repaint();
		this.removeMouseListener(this);
		this.removeMouseMotionListener(this);
	}
	@Override
	public void mouseDragged(MouseEvent e) {}
	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {

	}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {this.setCursor(new Cursor(Cursor.HAND_CURSOR));}
	@Override
	public void mouseExited(MouseEvent e) {this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));}
	
	public void setStone(Stone stone) {this.stone = stone;}
	public Stone getStone() {return stone;}
	
	public boolean is_end() {
		if(horizontal_find()||vertical_find()||right_diagonal_find()||left_diagonal_find()) return true;
		else return false;
	}
	private boolean right_diagonal_find() {
		boolean previous_find = true;
		boolean next_find = true;
		Tile[][] board = CheckBoard.tile_board;
		int count = 1;
		int x = location.x;
		int y = location.y;
		int i=1, j =1;
		while(previous_find) {
			if(x+i<19 && y-i>=0 && board[x+i][y-i].stone.color!=Stone.EMPTY && board[x+i][y-i].stone.color==this.stone.color) {
				i++;
				count++;
			}
			else previous_find = false;
		}
		while(next_find) {
			if(x-j>=0 && y+j<19 && board[x-j][y+j].stone.color!=Stone.EMPTY && board[x-j][y+j].stone.color==this.stone.color) {
				j++;
				count++;
			}
			else next_find = false;
		}
		if(count==6) return true;
		else return false;
	}
	private boolean left_diagonal_find() {
		boolean previous_find = true;
		boolean next_find = true;
		Tile[][] board = CheckBoard.tile_board;
		int count = 1;
		int x = location.x;
		int y = location.y;
		int i=1, j =1;
		while(previous_find) {
			if(x-i>=0 && y-i>=0 && board[x-i][y-i].stone.color!=Stone.EMPTY && board[x-i][y-i].stone.color==this.stone.color) {
				i++;
				count++;
			}
			else previous_find = false;
		}
		while(next_find) {
			if(x+j<19 && y+j<19 && board[x+j][y+j].stone.color!=Stone.EMPTY && board[x+j][y+j].stone.color==this.stone.color) {
				j++;
				count++;
			}
			else next_find = false;
		}
		if(count==6) return true;
		else return false;
	}
	private boolean vertical_find() {
		boolean previous_find = true;
		boolean next_find = true;
		Tile[][] board = CheckBoard.tile_board;
		int count = 1;
		int x = location.x;
		int y = location.y;
		int i=1, j =1;
		while(previous_find) {
			if(y-i>=0 && board[x][y-i].stone.color!=Stone.EMPTY && board[x][y-i].stone.color==this.stone.color) {
				i++;
				count++;
			}
			else previous_find = false;
		}
		while(next_find) {
			if(y+j<19 && board[x][y+j].stone.color!=Stone.EMPTY && board[x][y+j].stone.color==this.stone.color) {
				j++;
				count++;
			}
			else next_find = false;
		}
		if(count==6) return true;
		else return false;
	}
	public boolean horizontal_find() {
		boolean previous_find = true;
		boolean next_find = true;
		Tile[][] board = CheckBoard.tile_board;
		int count = 1;
		int x = location.x;
		int y = location.y;
		int i=1, j =1;
		while(previous_find) {
			if(x-i>=0 && board[x-i][y].stone.color!=Stone.EMPTY && board[x-i][y].stone.color==this.stone.color) {
				i++;
				count++;
			}
			else previous_find = false;
		}
		while(next_find) {
			if(x+j<19 && board[x+j][y].stone.color!=Stone.EMPTY && board[x+j][y].stone.color==this.stone.color) {
				j++;
				count++;
			}
			else next_find = false;
		}
		if(count==6) return true;
		else return false;
	}	

	public void find_best() { //최고의 수를 실행, 각 단계의 로직을 병합하는 부분 
		//System.out.println(Arrays.toString(board));
		Vector<Point> final_moves = new Vector<Point>();
		int[][] board = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board);
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				if(find_pattern(i,j)!=null) {//승리할 수 있는 패턴이 존재하면 아래의 조건들은 고려하지 않아도 됨 
					ai_click(find_pattern(i,j).get(0));
					ai_click(find_pattern(i,j).get(1));
					return;
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
			System.out.printf("should_prevent 1.(%d, %d)", final_moves.get(0).x, final_moves.get(0).y);
		    ai_click(final_moves.get(0));
		    board[final_moves.get(0).x][final_moves.get(0).y] = Decision.player; //필수로 방어해야하는 곳을 막음 
			Point second_move = Find_BestSingleMove(board);	//영향력이 가장 높은 포인트에 착수
		    System.out.printf(" 2. (%d, %d)\n", second_move.x, second_move.y); 
		    ai_click(second_move);
		}
		else if(final_moves.size()>=2) {
			if(final_moves.get(1).x==-1 && final_moves.get(1).y==-1) { //final moves 원소의 좌표가 -1,-1 인 경우 한 수로 방어하고 자유롭게 한 수가 남을 경우임 
				System.out.printf("should_prevent 1.(%d, %d)", final_moves.get(0).x, final_moves.get(0).y);
			    ai_click(final_moves.get(0));
			    board[final_moves.get(0).x][final_moves.get(0).y] = Decision.player;
				Point second_move = Find_BestSingleMove(board);	//영향력이 가장 높은 포인트에 착수
			    System.out.printf(" 2. (%d, %d)\n", second_move.x, second_move.y); 
			    ai_click(second_move);

			}
			else {
				System.out.printf("should prevent 1.(%d, %d) 2.(%d, %d)\n", final_moves.get(0).x, final_moves.get(0).y, final_moves.get(1).x, final_moves.get(1).y);
				ai_click(final_moves.get(0));
				ai_click(final_moves.get(1));
			}
		}
		else {
			//영향력이 가장 높은 포인트에 착수 
			double max_score = -100000; //초기 점수는 낮게 설정 
			Moves best_combination = new Moves(-1,-1,-1,-1);
		    Vector<Point> all_moves = get_allPossibleMoves(); //가능한 모든 빈칸을 반환 
		    for(int i=0; i<all_moves.size(); i++) {
		    	for(int j=i+1; j<all_moves.size(); j++) { //모든 빈칸에 착수하는 경우 중 두가지를 골라 점수를 반환 
		    		Moves temp_moves = new Moves(all_moves.get(i), all_moves.get(j));
		    		double now_score = calculateDoubleMove_score(temp_moves, Decision.player);
		    		if(max_score<now_score) { //새로 얻은 점수가 이전 최대 값보다 크다면, 
			    		max_score = now_score; //점수 갱신
			    		best_combination = new Moves(all_moves.get(i), all_moves.get(j)); //좌표 갱신 
		    		}
		    	}
		    }
		    System.out.printf("1. (%d, %d)", best_combination.first_move.x, best_combination.first_move.y);
		    ai_click(best_combination.first_move);
		    System.out.printf(" 2. (%d, %d)\n", best_combination.second_move.x, best_combination.second_move.y);
		    ai_click(best_combination.second_move); 
		}
		
//		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);//좌클릭 다운
//		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);//좌클릭 업
//		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);//좌클릭 다운
//		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);//좌클릭 업
	}
	
	public static void ai_click(Point first_move) { //자동으로 화면 클릭하는 robot 
		airobot.mouseMove(24 + first_move.x*40, 67 + first_move.y*40);
		airobot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		airobot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		airobot.delay(500);		
	}
	public static boolean IsOutOfBounds(int x, int y) { //주어진 인덱스 값이 배열의 최대, 최소 범위를 벗어나는지 판단 (바둑판 영역 밖인지 판단)
		if (0 <= x && x <19 && 0 <= y && y < 19)
			return false;
		return true;
	}

	public Vector<Point> get_allPossibleMoves(){ //모든 빈칸을 반환 
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
	
	public double calculateDoubleMove_score(Moves myMoves, int player) { //두 수의 점수 최대값을 계산 
		int board[][] = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board);
	    double score = 0.0;
	    score += calculateSingleMove_score(myMoves.first_move, player);
	    board[myMoves.first_move.x][myMoves.first_move.y] = player; // 첫번째 착점하는 곳을 player로 채워넣고 두번째 착수에 대한 score를 합산
	    score += calculateSingleMove_score(myMoves.second_move, player);
	    board[myMoves.first_move.x][myMoves.first_move.y] = Stone.EMPTY; // 다시 되돌림
	    return score;
	}

	public double calculateSingleMove_score(Point myMove, int player) { // 한 수의 점수 최대값을 계산 
		double PlayerFactor[] = {0.0, 1.0, 3.96, 12.05, 0.0, 0.0}; // PlayerFactor[i] : 나의 착수로 window 안에 나의 돌 i개를 만들었을 때 score. 검은돌 4/5개는 어차피 Threat에서 알아서 카운트될테니 계산할 필요 없음
		double OpponentFactor[] = {0.0, 1.33, 6.79, 19.52, 0.0, 0.0};// OpponentFacotr[i] : 나의 착수로 window 안에 상대 돌 i개를 저지했을 때 score. 하얀돌 4/5개를 막는건 어차피 Threat에서 걸러지므로 따로 score를 부여할 필요 없음
		
		int opponent = 3 - Decision.player;
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
					//System.out.printf("myMove(%d, %d), xy(%d, %d), (%d, %d)\n",myMove.x, myMove.y, myMove.x - i * dx[dir], myMove.y - i * dy[dir],x+k*dx[dir], y+k*dy[dir]);
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
					else if(board[x+k*dx[dir]][y+k*dy[dir]]==Decision.player) { // 내 돌 갯수 카운트 
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
	                score += PlayerFactor[playercount];
	            if (playercount== 1 && (IsOutOfBounds(x - dx[dir], y - dy[dir]) || board[x - dx[dir]][y - dy[dir]] != opponent) && (IsOutOfBounds(x + 6 * dx[dir], y + 6 * dy[dir]) || board[x + 6 * dx[dir]][y + 6 * dy[dir]] != opponent)) // 내 돌이 없는(자기자신때문에 값은 1)6칸 (즉 상대의 6목을 저지했음)
	                score += OpponentFactor[opponentcount];
			}			
		}
		return score;
	}
	
	public Point Find_BestSingleMove(int[][] board) { //모든 빈 칸에 대하여 최고점인 점을 찾는 메소드 
	    double maxscore = -1.0;
	    Point bestmove = new Point(-1, -1);
	    for (int x = 0; x < 19; x++) {
	        for (int y = 0; y < 19; y++) {
	            if (board[x][y] != Stone.EMPTY) //빈칸이 아닐 경우 스킵 
	                continue;
	            double tmpScore = calculateSingleMove_score(new Point(x,y), Decision.player);
	            if (tmpScore >= maxscore && Math.abs(19 / 2 - x) + Math.abs(19 / 2 - y) < Math.abs(19/ 2 - bestmove.x) + Math.abs(19 / 2 - bestmove.y)) { //바둑판 중앙과 더 가까워야함! 
	                bestmove = new Point(x,y);
	                maxscore = tmpScore;
	            }
	        }
	    }
	    //System.out.printf("(%d, %d)", bestmove.x, bestmove.y);
	    return bestmove;
	}
	 
	
	private Moves defend_threat() { //위협이 되는 자리를 효율적으로 막는 메소드 
		int[][] board = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board);
		Vector<Point> threat_vector = new Vector<Point>();
		Vector<Point> after_threat_vector = new Vector<Point>();
		Moves forced_moves = new Moves(-1,-1,-1,-1);
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				for(Point p: find_threat(board, i ,j)) {
					if(!threat_vector.contains(p)) threat_vector.add(p);//꼭 막아야하는 포인트 벡터 생성
				} 
			}
		}
		//System.out.println("original vector size: "+threat_vector.size());
		for(int i=0; i<threat_vector.size(); i++) { //위협(막아야하는) 포인트벡터 전체 원소 중 두개의 원소를 뽑아 놓았다고 가정 
			for(int j=i; j<threat_vector.size(); j++) {
				if(threat_vector.get(i).x==-1 || threat_vector.get(i).y==-1);
				else board[threat_vector.get(i).x][threat_vector.get(i).y]= Decision.player; 
				if(threat_vector.get(j).x==-1 || threat_vector.get(j).y==-1);
				else board[threat_vector.get(j).x][threat_vector.get(j).y]= Decision.player;
				//print_BOARD(board);
				for(int x=0; x<19; x++) {
					for(int y=0; y<19; y++) {
						after_threat_vector.addAll(find_threat(board, x, y)); //놓았다고 가정 했을 때 위협 포인트 벡터 생성 
					}
				}
				//System.out.printf("(%s, %s) after vector size: %d\n", threat_vector.get(i), threat_vector.get(j), after_threat_vector.size());
				if(after_threat_vector.size()==0) { //위협 포인트 벡터의 크기가 0이라는 것은 꼭 막아야하는 수가 없다는 의미, 즉 가정한 두 가지 원소의 위치에 강제적으로 놓아야함 
					forced_moves = new Moves(threat_vector.get(i), threat_vector.get(j)); 
					return forced_moves;
				}
				else {
					forced_moves = new Moves(-1,-1,-1,-1); //초기화 
					CheckBoard.deepCopy_Board(CheckBoard.tile_board, board); //초기화 
					after_threat_vector.clear(); //초기화 
				}
			}
		}	
		return forced_moves; //여기에 도달했다는 것은 모든 경우의 수를 생각하여도 상대의 승리를 막을 수 없음 
	}
	private Vector<Point> find_threat(int[][] board, int x, int y) { //상대의 위협(승리조건)을 만족하는 위치를 반환 
		Vector<Point> temp_vector = new Vector<Point>();
		
		//int[] pattern = new int[6];
		String str_pattern = "";
		
		int[] dx = { -1, 0, 1, 1 };
		int[] dy = { 1, 1, 1, 0 };
		
		for(int dir=0; dir<4; dir++) { //4개 방향에 대하여, 
			for(int i=0; i<6; i++) { //인자로 전달받은 기준점 x,y에 대해 6개 칸을 탐색 
				if(!IsOutOfBounds(x+i*dx[dir],y+i*dy[dir])) { //6개의 칸이 보드 안에 들어온다면, 
					str_pattern+=String.valueOf(board[x+i*dx[dir]][y+i*dy[dir]]); //한 칸을 문자열에 이어붙이기 
				}
			}
			if(str_pattern.length()==6) {
				//System.out.println("string: "+str_pattern);
			}
			else {
				str_pattern = "";
				continue;
			}
			if(loser_condition.containsKey(str_pattern) && (IsOutOfBounds(x+6*dx[dir],y+6*dy[dir])||board[x+6*dx[dir]][y+6*dy[dir]]!=3-Decision.player) && (IsOutOfBounds(x-1*dx[dir], y-1*dy[dir]) || board[x-1*dx[dir]][y-1*dy[dir]]!=3-Decision.player)) {
				//패배 패턴 해시맵 키에 최종 문자열이 존재하고 기준점 x,y의 -1번째, 6번째 위치의 돌이 플레이어의 돌이 아닐때(장목방지)
				//System.out.print("losing pattern check!");
				//System.out.println(String.format("(%d, %d), (%d, %d)",x+loser_condition.get(str_pattern)[0]*dx[dir], y+loser_condition.get(str_pattern)[0]*dy[dir],x+loser_condition.get(str_pattern)[1]*dx[dir], y+loser_condition.get(str_pattern)[1]*dy[dir]));
				temp_vector.add(new Point(x+loser_condition.get(str_pattern)[0]*dx[dir], y+loser_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(x+loser_condition.get(str_pattern)[1]*dx[dir], y+loser_condition.get(str_pattern)[1]*dy[dir]));
			}
			else if(loser_5_condition.containsKey(str_pattern) && (IsOutOfBounds(x+6*dx[dir],y+6*dy[dir])||board[x+6*dx[dir]][y+6*dy[dir]]!=3-Decision.player) && (IsOutOfBounds(x-1*dx[dir], y-1*dy[dir]) || board[x-1*dx[dir]][y-1*dy[dir]]!=3-Decision.player)) {
				//이미 5목이면서 기준점 x,y의 -1번째, 6번째 위치이 돌이 플레이어의 돌이 아닐때(장목방지)
				//System.out.print("losing pattern check!");
				//System.out.println(String.format("(%d, %d)",x+loser_5_condition.get(str_pattern)[0]*dx[dir], y+loser_5_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(x+loser_5_condition.get(str_pattern)[0]*dx[dir], y+loser_5_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(-1, -1));
			}
			str_pattern = ""; //다음 방향을 탐색하기 위해 문자열 초기화 
		}
		//System.out.println("size: "+temp_vector.size());
		return temp_vector;
		
	}
	public void print_BOARD(int myBoard[][]) {
	    for (int x = 0; x < 19; x++) {
	        for (int y = 0; y < 19; y++)
	            System.out.printf("%d ", myBoard[x][y]);
	        System.out.printf("\n");
	    }
	}
	public Vector<Point> find_pattern(int x, int y) { //기준점 x,y에 대해 4방향 탐색 
		int[][] board = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board); 
		Vector<Point> temp_vector = new Vector<Point>();
		
		//int[] pattern = new int[6];
		String str_pattern = "";
		
		int[] dx = { -1, 0, 1, 1 };
		int[] dy = { 1, 1, 1, 0 };
		
		for(int dir=0; dir<4; dir++) { //4개 방향에 대하여, 
			for(int i=0; i<6; i++) { //인자로 전달받은 기준점 x,y에 대해 6개 칸을 탐색 
				if(!IsOutOfBounds(x+i*dx[dir],y+i*dy[dir])) { //6개의 칸이 보드 안에 들어온다면, 
					str_pattern+=String.valueOf(board[x+i*dx[dir]][y+i*dy[dir]]); //한 칸을 문자열에 이어붙이기 
				}
			}
			//System.out.println(dir+": "+str_pattern);
			if(winner_condition.containsKey(str_pattern) && (IsOutOfBounds(x+6*dx[dir],y+6*dy[dir])||board[x+6*dx[dir]][y+6*dy[dir]]!=Decision.player) && (IsOutOfBounds(x-1*dx[dir], y-1*dy[dir]) || board[x-1*dx[dir]][y-1*dy[dir]]!=Decision.player)) {
				//승리패턴 해시맵 키에 최종 문자열이 존재하고 기준점 x,y의 -1번째, 6번째 위치의 돌이 플레이어의 돌이 아닐때(장목방지)
				System.out.print("winning pattern check!");
				System.out.println(String.format("1.(%d, %d), 2.(%d, %d)",x+winner_condition.get(str_pattern)[0]*dx[dir], y+winner_condition.get(str_pattern)[0]*dy[dir],x+winner_condition.get(str_pattern)[1]*dx[dir], y+winner_condition.get(str_pattern)[1]*dy[dir]));
				temp_vector.add(new Point(x+winner_condition.get(str_pattern)[0]*dx[dir], y+winner_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(x+winner_condition.get(str_pattern)[1]*dx[dir], y+winner_condition.get(str_pattern)[1]*dy[dir]));
				return temp_vector; //결과값이 한개 이상 도출되었으므로 종료 
			}
			else if(winner_5_condition.containsKey(str_pattern) && (IsOutOfBounds(x+6*dx[dir],y+6*dy[dir])||board[x+6*dx[dir]][y+6*dy[dir]]!=Decision.player) && (IsOutOfBounds(x-1*dx[dir], y-1*dy[dir]) || board[x-1*dx[dir]][y-1*dy[dir]]!=Decision.player)) {
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
	
	public HashMap<String, int[]> makeConnect6_hashmap(int player){ //4목이 완성되었을 때 승리패턴을 키값으로 하고 빈칸을 value값으로 가지는 해쉬맵 생성 
		HashMap<String, int[]> temp = new HashMap<String, int[]>();
		if(player==1) {
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
	
	public HashMap<String, int[]> makeConnect6_5_hashmap(int player){ //5목이 완성되었을 때 승리패턴을 키값으로 하고 빈칸을 value값으로 가지는 해쉬맵 생성 
		HashMap<String, int[]> temp = new HashMap<String, int[]>();
		if(player==1) {
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
}
