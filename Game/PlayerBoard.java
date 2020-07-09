package Game;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import ArtificialIntelligence.Decision;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Point;
import javax.swing.JRadioButton;

public class PlayerBoard extends JPanel{
	
	public static JLabel wholeTime_label;
	public static JLabel remainTime_label;
	public static JLabel p1_label;
	public static JLabel p2_label;
	private JLabel p1_image_label;
	private JLabel p2_image_label;
	public static JLabel remainTime_head;
	public static JLabel wholeTime_head;
	private JLabel status_label;
	private Decision decision = new Decision();
	
	public PlayerBoard() {
		setLayout(null);
		setLocation(770,0);
		setSize(430,800);
		setBackground(new Color(240, 248, 255));
		
		JRadioButton aiblack_radio = new JRadioButton("AI");
		aiblack_radio.setHorizontalAlignment(SwingConstants.CENTER);
		aiblack_radio.setBounds(52, 151, 70, 23);
		add(aiblack_radio);
		
		JRadioButton aiwhite_radio = new JRadioButton("AI");
		aiwhite_radio.setHorizontalAlignment(SwingConstants.CENTER);
		aiwhite_radio.setBounds(294, 151, 70, 23);
		add(aiwhite_radio);
		aiwhite_radio.setSelected(true);
		
		ButtonGroup group = new ButtonGroup();
		group.add(aiblack_radio);
		group.add(aiwhite_radio);
		
		p1_label = new JLabel("Player1");
		p1_label.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		p1_label.setHorizontalAlignment(SwingConstants.CENTER);
		p1_label.setBounds(41, 186, 100, 40);
		add(p1_label);
				
		p2_label = new JLabel("Player2");
		p2_label.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		p2_label.setHorizontalAlignment(SwingConstants.CENTER);
		p2_label.setBounds(283, 186, 100, 40);
		add(p2_label);
		
		remainTime_label = new JLabel("15");
		remainTime_label.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		remainTime_label.setHorizontalAlignment(SwingConstants.CENTER);
		remainTime_label.setBounds(283, 415, 110, 50);
		add(remainTime_label);

		
		wholeTime_label = new JLabel("0 : 0");
		wholeTime_label.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		wholeTime_label.setHorizontalAlignment(SwingConstants.CENTER);
		wholeTime_label.setBounds(283, 473, 110, 50);
		add(wholeTime_label);
		
		JButton start_button = new JButton("Start");
		start_button.requestFocus();
		start_button.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		start_button.setBounds(165, 700, 100, 40);
		start_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(start_button.getText().equals("Start")) { //스타트 누르면, 
					GameController.pause = false; //중지상태 해제 
					//SoundPackage.play_bgm();
					if(aiwhite_radio.isSelected()) { //백돌이 AI로 선택되었다면, 
						Decision.player = 1; 
						aiblack_radio.setEnabled(false);
						aiblack_radio.setText("User");
						aiwhite_radio.setEnabled(false);
					}
					else { //백돌이 AI가 아니면 흑돌이 AI임 
						Decision.player = 2;
						aiblack_radio.setEnabled(false);
						aiwhite_radio.setText("User");
						aiwhite_radio.setEnabled(false);
					}
					Decision.update_HashMap(); //플레이어에 맞는 승리, 패배 해쉬맵 생성 
					update_color();
					new AutoExitFrame(AutoExitFrame.START);
					start_button.setText("Pause");
					new GameController();
					GameController.start();
					GameController.playerTimer_start();
					if(Decision.player==2) { //흑돌일때 첫 착수 위치 결정하는 부분 
						int i,j;
						if(calculate_center(CheckBoard.tile_board)!=null) { //적목이 있다면,  
							i=calculate_center(CheckBoard.tile_board).x;
							j=calculate_center(CheckBoard.tile_board).y; //적목들을 기준으로 가장 넓은 곳의 중심에 착수 
						}
						else {
							i=9; j=9; //적목이 없으면 중심,
						}
						boolean is_empty = (CheckBoard.tile_board[i][j].getStone().color==Stone.EMPTY); 
						//최종적으로 주어진 중심이 비어있는지 확인
						while(!is_empty) { //중심이 채워져 있다면, 
							for(int dx = 0; dx<2; dx++) { //주변 탐색 후 빈 곳에 착수 
								for(int dy=0; dy<2; dy++) {
									i+=dx; j+=dy;
									is_empty = (CheckBoard.tile_board[i][j].getStone().color==Stone.EMPTY);
									if(is_empty) break; //새로 선택된 곳이 빈 곳이면, 반복문 탈출 
								}
								if(is_empty) break; //새로 선택된 곳이 빈 곳이면, 반복문 탈출 
							}
						}
						Decision.ai_click(new Point(i,j)); //중심에 착수 						
					}
				}
				else if(start_button.getText().equals("Pause")) {
					GameController.pause = true;
					new AutoExitFrame(AutoExitFrame.PAUSE);
					start_button.setText("Resume");
					GameController.playerTimer_stop();
				}
				else if(start_button.getText().equals("Resume")) {
					GameController.pause = false;
					new AutoExitFrame(AutoExitFrame.RESUME);
					start_button.setText("Pause");
					GameController.playerTimer_restart();
				}
			}
		});
		add(start_button);
		
		p1_image_label = new JLabel("");
		p1_image_label.setIcon(new ImageIcon("/Users/ichanhyo/eclipse-workspace/Connect6/whiteStone_small.png"));
		p1_image_label.setHorizontalAlignment(SwingConstants.CENTER);
		p1_image_label.setBounds(303, 238, 61, 58);
		add(p1_image_label);
		
		p2_image_label = new JLabel("");
		p2_image_label.setHorizontalAlignment(SwingConstants.CENTER);
		p2_image_label.setIcon(new ImageIcon("/Users/ichanhyo/eclipse-workspace/Connect6/blackStone_small.png"));
		p2_image_label.setBounds(61, 238, 61, 61);
		add(p2_image_label);
		
		remainTime_head = new JLabel("Remain Time");
		remainTime_head.setBackground(Color.WHITE);
		remainTime_head.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		remainTime_head.setHorizontalAlignment(SwingConstants.CENTER);
		remainTime_head.setBounds(129, 415, 148, 50);
		add(remainTime_head);
		
		wholeTime_head = new JLabel("Whole Time");
		wholeTime_head.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		wholeTime_head.setHorizontalAlignment(SwingConstants.CENTER);
		wholeTime_head.setBounds(129, 473, 148, 50);
		add(wholeTime_head);
		
		status_label = new JLabel("Status");
		status_label.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
		status_label.setHorizontalAlignment(SwingConstants.CENTER);
		status_label.setBounds(165, 40, 100, 25);
		add(status_label);
	
		
	}
	
	public static void update_color() {
		if(GameController.turn%2==1) {
			p1_label.setForeground(Color.RED);
			p2_label.setForeground(Color.BLACK);
		}
		else {
			p1_label.setForeground(Color.BLACK);
			p2_label.setForeground(Color.RED);
		}
	}
	public Point calculate_center(Tile[][] tile_board) {
		int total_x=0, total_y=0, count =0;
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				if(tile_board[i][j].getStone().color == Stone.RED) {
					total_x += i-9;
					total_y += j-9;
					count++;
				}
			}
		}
		if(count>0) return new Point(9-(total_x/count),9-(total_y/count));
		else return null;
	}
}
