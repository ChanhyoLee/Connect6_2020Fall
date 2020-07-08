package Game;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.Font;

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
	
	public PlayerBoard() {
		setLayout(null);
		setLocation(770,0);
		setSize(430,800);
		setBackground(new Color(240, 248, 255));
		
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
		start_button.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		start_button.setBounds(165, 700, 100, 40);
		start_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(start_button.getText().equals("Start")) {
					GameController.pause = false;
					//SoundPackage.play_bgm();
					update_color();
					new AutoExitFrame(AutoExitFrame.START);
					start_button.setText("Pause");
					new GameController();
					GameController.start();
					GameController.playerTimer_start();
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
	
}