package Game;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class GameController{
	
	private static int whole_time=0;
	private static Timer whole_timer;
	private static int remain_time=0;
	private static Timer player_timer;
	
	public static boolean pause = true;
	public static int turn = 1;
	public static int possible_actionNumber=1;
	
	public GameController() {
		whole_timer = new Timer(1000,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setWhole_time(getWhole_time() + 1);
				String temp_time = String.format("%d : %d ", whole_time/60, whole_time%60);
				PlayerBoard.wholeTime_label.setText(temp_time);
			}
		});
		player_timer = new Timer(1000,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        PlayerBoard.remainTime_label.setText(String.valueOf(15 - remain_time));
		        remain_time++;
		        if(remain_time>=10) { 
		        	if(remain_time==10) {
			        	PlayerBoard.remainTime_label.setForeground(Color.RED);
			        	PlayerBoard.remainTime_head.setForeground(Color.RED);
		        	}
		        	else if(remain_time==11) SoundPackage.play_countdown5();
			        else if(remain_time==12) SoundPackage.play_countdown4();
			        else if(remain_time==13) SoundPackage.play_countdown3();
			        else if(remain_time==14) SoundPackage.play_countdown2();
			        else if(remain_time==15) SoundPackage.play_countdown1();
			        if(remain_time == 30) {
			        	GameController.turn++;
						GameController.possible_actionNumber=2;
						new AutoExitFrame(GameController.turn%2);
			        	PlayerBoard.remainTime_label.setForeground(Color.BLACK);
			        	PlayerBoard.remainTime_head.setForeground(Color.BLACK);
						PlayerBoard.update_color();
			        	remain_time = 0;
			        }
		        }
		        else {
		        	PlayerBoard.remainTime_label.setForeground(Color.BLACK);
		        	PlayerBoard.remainTime_head.setForeground(Color.BLACK);
		        }
			}
		});
	}
	
	public static void start() {
		whole_timer.start();
	}
	public static void stop() {
		whole_timer.stop();
	}
	public static void playerTimer_start() {
		player_timer.start();
	}
	public static void playerTimer_stop() {
		player_timer.stop();
	}
	public static void playerTimer_restart() {
		player_timer.restart();
	}
	public static void reset_playerTime() {
		remain_time = 0;
	}
	public static void reset_wholeTime() {
		whole_time = 0;
	}
	
	public int getWhole_time() {
		return whole_time;
	}

	public void setWhole_time(int whole_time) {
		GameController.whole_time = whole_time;
	}

	public static Timer getPlayer_timer() {
		return player_timer;
	}

	public static void setPlayer_timer(Timer player_timer) {
		GameController.player_timer = player_timer;
	}
	public static void reset_all() {
		whole_time = 0;
		remain_time = 0;
		pause = true;
		turn =1;
		possible_actionNumber = 1;
		whole_timer.stop();
	}

}
