package Game;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CheckBoard extends JPanel{

	public static Tile[][] tile_board = new Tile[19][19];
	JLabel background_label;
	BufferedImage background;
	
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int[][] startPoint_array = {{3,9,15},{3,9,15}};
        g.setColor(new Color(253, 245, 230));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.black);
        for(int i=0; i<19; i++) {
        	g.drawLine(25, 25+40*i, 25+40*18, 25+40*i);
        	g.drawLine(25+40*i, 25, 25+40*i, 25+40*18);
        }
        for(int i: startPoint_array[0]) {
        	for(int t: startPoint_array[1]) {
        		g.fillOval(21+40*i, 21+40*t, 8, 8);
        	}
        }
    }
    
	public CheckBoard() throws IOException {
		this.setLocation(0,0);
		this.setLayout(null);
		this.setSize(770,770);
		createBoard();
	}
	
	public void createBoard() throws IOException {
		for(int i=0; i<19; i++) {
			for(int j=0; j<19; j++) {
				Tile temp_tile = new Tile(i,j);
				tile_board[i][j] = temp_tile;
				this.add(temp_tile);
			}
		}
	}
	
	public static void deepCopy_Board(Tile[][] src, int[][] dst) {
		for(int x=0; x<19; x++) {
			for(int y=0; y<19; y++) {
				if(src[x][y].getStone().color==Stone.BLACK) dst[x][y] = Stone.BLACK;
				else if(src[x][y].getStone().color==Stone.WHITE) dst[x][y] = Stone.WHITE;
				else if(src[x][y].getStone().color==Stone.EMPTY) dst[x][y] = Stone.EMPTY;
				else if(src[x][y].getStone().color==Stone.RED) dst[x][y] = Stone.RED;
				else if(src[x][y].getStone().color==Stone.MARK) dst[x][y] = Stone.MARK;
			}
		}
	}
}
