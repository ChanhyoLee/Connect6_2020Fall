package Game;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
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
				GameController.possible_actionNumber--;
				if(GameController.possible_actionNumber==0) {
					if(GameController.turn%2==Decision.player) find_best();
					GameController.turn++;
					GameController.reset_playerTime();
					GameController.possible_actionNumber=2;
					new AutoExitFrame(GameController.turn%2);
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
	public void mousePressed(MouseEvent e) {}
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

	public void find_best() {
		//System.out.println(Arrays.toString(board));
		int[][] board = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board);
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				if(find_pattern(i,j)!=null) { //승리할 수 있는 패턴이 존재하면 아래의 조건들은 고려하지 않아도 됨 
					return;
				}
				else {
					if(find_threat(board, i, j).size()>0) { //승리할 수 있는 패턴이 존재하지 않을 경우, 상대가 승리할 수 있는 패턴을 찾아 수비해야함
						 Moves.print_Moves(defend_threat()); //위협이 되는 패턴을 막을 수 있는 돌의 위치를 출력, 하나로 막을 수 있는 경우 나머지 하나는 -1,-1 로 출력, 둘다 -1,-1 일 경우 게임에서 패배한 상황 
					}
				}
			}
		}		
	}
	
	public static boolean IsOutOfBounds(int x, int y) {
		if (0 <= x && x <19 && 0 <= y && y < 19)
			return false;
		return true;
	}
	
	private Moves defend_threat() {
		int[][] board = new int[19][19];
		CheckBoard.deepCopy_Board(CheckBoard.tile_board, board);
		Vector<Point> threat_vector = new Vector<Point>();
		Vector<Point> after_threat_vector = new Vector<Point>();
		Moves forced_moves = new Moves(-1,-1,-1,-1);
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				threat_vector.addAll(find_threat(board, i,j));
			}
		}
		//System.out.println("original_count"+threat_vector.size());
		for(int i=0; i<threat_vector.size(); i++) {
			if(threat_vector.get(i).x==-1 || threat_vector.get(i).y==-1);
			else board[threat_vector.get(i).x][threat_vector.get(i).y]= Decision.player; 
			for(int j=i+1; j<threat_vector.size(); j++) {
				if(threat_vector.get(j).x==-1 || threat_vector.get(j).y==-1);
				else board[threat_vector.get(j).x][threat_vector.get(j).y]= Decision.player;
				for(int x=0; x<19; x++) {
					for(int y=0; y<19; y++) {
						after_threat_vector.addAll(find_threat(board, x, y));
					}
				}
				if(after_threat_vector.size()==0) {
					forced_moves = new Moves(threat_vector.get(i), threat_vector.get(j));
					return forced_moves;
				}
				else {
					forced_moves = new Moves(-1,-1,-1,-1);
					CheckBoard.deepCopy_Board(CheckBoard.tile_board, board);
					after_threat_vector.clear();
				}
			}
		}	
		return forced_moves;
	}
	private Vector<Point> find_threat(int[][] board, int x, int y) {
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
				System.out.println(String.format("(%d, %d), (%d, %d)",x+winner_condition.get(str_pattern)[0]*dx[dir], y+winner_condition.get(str_pattern)[0]*dy[dir],x+winner_condition.get(str_pattern)[1]*dx[dir], y+winner_condition.get(str_pattern)[1]*dy[dir]));
				temp_vector.add(new Point(x+winner_condition.get(str_pattern)[0]*dx[dir], y+winner_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(x+winner_condition.get(str_pattern)[1]*dx[dir], y+winner_condition.get(str_pattern)[1]*dy[dir]));
				return temp_vector; //결과값이 한개 이상 도출되었으므로 종료 
			}
			else if(winner_5_condition.containsKey(str_pattern) && (IsOutOfBounds(x+6*dx[dir],y+6*dy[dir])||board[x+6*dx[dir]][y+6*dy[dir]]!=Decision.player) && (IsOutOfBounds(x-1*dx[dir], y-1*dy[dir]) || board[x-1*dx[dir]][y-1*dy[dir]]!=Decision.player)) {
				//이미 5목이면서 기준점 x,y의 -1번째, 6번째 위치이 돌이 플레이어의 돌이 아닐때(장목방지)
				System.out.print("winning pattern check!");
				System.out.println(String.format("(%d, %d)",x+winner_5_condition.get(str_pattern)[0]*dx[dir], y+winner_5_condition.get(str_pattern)[0]*dy[dir]));
				temp_vector.add(new Point(x+winner_condition.get(str_pattern)[0]*dx[dir], y+winner_condition.get(str_pattern)[0]*dy[dir]));
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
