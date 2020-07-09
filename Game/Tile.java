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

import ArtificialIntelligence.Decision;
import ArtificialIntelligence.Moves;

public class Tile extends JLabel implements MouseListener, MouseMotionListener{
	
	//private Shape tile_shape;
	private Point location; //각 타일의 인덱스(좌표아님) 저장하는 포인트 
	private Stone stone; //타일에 저장된 Stone 
	private BufferedImage whiteStone;
	private BufferedImage blackStone;
	private BufferedImage redStone;

	public static Point first_move;
	public static Point second_move;
	
	
	public Tile(int x, int y) throws IOException {
		whiteStone = ImageIO.read(new File("whiteStone.png"));
		blackStone = ImageIO.read(new File("blackStone.png"));
		redStone = ImageIO.read(new File("redStone.png"));
		location = new Point(x,y);
		stone = new Stone(Stone.EMPTY);
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
	public void mouseClicked() {
		this.mouseClicked(new MouseEvent(this, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), MouseEvent.BUTTON1_DOWN_MASK, this.getLocation().x+15, this.getLocation().y, 1, false));
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
				//new AutoExitFrame(AutoExitFrame.TIMER);
				}
			else {
				GameController.possible_actionNumber--; //한 수 놓았음 
				if(GameController.possible_actionNumber==0) { //주어진 수를 모두 놓았음
					GameController.turn++; //다음 턴으로! 
					GameController.reset_playerTime();
					GameController.possible_actionNumber=2;
					//new AutoExitFrame(GameController.turn%2);
					PlayerBoard.update_color();
					if(GameController.turn%2==Decision.player-1) Decision.find_best(); //상대의 턴이 종료되었을때 최선의 수를 자동으로 착수 
				}
			}
		}
		this.removeMouseListener(this);
		this.removeMouseMotionListener(this);
		repaint();
	}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {this.setCursor(new Cursor(Cursor.HAND_CURSOR));}
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
	private boolean horizontal_find() {
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
}