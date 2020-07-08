package Game;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameFrame extends JFrame{
	
	private static GameFrame gf;
	private CheckBoard check_board;
	private PlayerBoard player_board;
	
	public GameFrame() throws IOException{
		gf = this;
		this.setSize(1200,790);
		this.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		check_board = new CheckBoard();
		this.getContentPane().add(check_board);
		player_board = new PlayerBoard();
		this.getContentPane().add(player_board);
		setVisible(true);

	}
	
	public static void restart_game() throws IOException {
		gf.dispose();
		new GameFrame();
		GameController.reset_all();
	}
}
