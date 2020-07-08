package Game;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JButton;

public class AutoExitFrame extends JFrame{
	JLabel exist_message;
	public static final int BLACK = 1;
	public static final int WHITE = 0;
	public static final int START = 2;
	public static final int PAUSE = 3;
	public static final int RESUME = 4;
	public static final int END = 5;
	public static final int SYSTEMOUT = 6;
	public static final int TIMER = 7;
	
	public static int countdown=15;
	int type;
	
	public AutoExitFrame(int type){
		this.type = type;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("System Message");
		setAlwaysOnTop(true);
		this.setSize(350,150);
		setLocation(630,500);
		getContentPane().setLayout(null);
		exist_message = new JLabel("");
		exist_message.setBounds(0, 0, 350, 78);
		exist_message.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(exist_message);

		this.setVisible(true);
		if(type==TIMER) {
			exist_message.setText("Another new Game??  <<"+String.valueOf(countdown)+">>");
			auto_selection_exit();
			return;
		}
		else if(type==BLACK) {
			exist_message.setText("Player BLACK turn!");
		}
		else if(type==WHITE){
			exist_message.setText("Player WHITE turn!");
		}
		else if(type==START) {
			exist_message.setText("GAME Start with BLACK!");
		}
		else if(type==PAUSE) {
			exist_message.setText("PAUSE!");
		}
		else if(type==RESUME) {
			exist_message.setText("RESUME!");
		}
		else if(type==END) {
			exist_message.setText("Player"+GameController.turn%2+1+"WIN!!");
		}
		else if(type==SYSTEMOUT) {
			exist_message.setText("Thank you for Playing... Good BYE!");
		}
		setVisible(true);
		auto_exit();
		
	}
	
	public void auto_selection_exit() {
		Timer timer = new Timer(1000,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				countdown--;
				exist_message.setText("Another new Game??  <<"+String.valueOf(countdown)+">>");
				if(countdown<0) {
					new AutoExitFrame(AutoExitFrame.SYSTEMOUT);
					quit();
				}
			}
		});
		timer.start();
		
		JButton confirm_button= new JButton("Confirm");
		confirm_button.setBounds(50, 80, 100, 30);
		confirm_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				try {
					countdown=15;
					GameFrame.restart_game();
					timer.stop();
					quit();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		JButton cancel_button = new JButton("Cancel");
		cancel_button.setBounds(200, 80, 100, 30);
		cancel_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AutoExitFrame(AutoExitFrame.SYSTEMOUT);
				quit();
			}
		});
		add(confirm_button);
		add(cancel_button);

	}
	
	public void auto_exit() {
		Timer timer = new Timer(1000,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(type==SYSTEMOUT) System.exit(0);
				quit();
			}
		});
		timer.start();
	}
	
	public void quit() {
		this.dispose();
	}

}
