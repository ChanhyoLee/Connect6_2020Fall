package Game;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPackage {
	
	public static Clip clip;
	public static void play_countdown5() {
		File f = new File("CountDown5.wav");
		AudioInputStream audioInput;
		try {
			audioInput = AudioSystem.getAudioInputStream(f);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}		
	}
	public static void play_countdown4() {
		File f = new File("CountDown4.wav");
		AudioInputStream audioInput;
		try {
			audioInput = AudioSystem.getAudioInputStream(f);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}		
	}
	public static void play_countdown3() {
		File f = new File("CountDown3.wav");
		AudioInputStream audioInput;
		try {
			audioInput = AudioSystem.getAudioInputStream(f);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}		
	}
	public static void play_countdown2() {
		File f = new File("CountDown2.wav");
		AudioInputStream audioInput;
		try {
			audioInput = AudioSystem.getAudioInputStream(f);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}		
	}
	public static void play_countdown1() {
		File f = new File("CountDown1.wav");
		AudioInputStream audioInput;
		try {
			audioInput = AudioSystem.getAudioInputStream(f);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}		
	}
	
	public static void play_stoneSound() {
		File f = new File("stoneEffect_1.wav");
		AudioInputStream audioInput;
		try {
			audioInput = AudioSystem.getAudioInputStream(f);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}	
	}
	
	public static void play_winSound() {
		File f = new File("winEffect.wav");
		AudioInputStream audioInput;
		try {
			audioInput = AudioSystem.getAudioInputStream(f);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}	
	}
	
	public static void play_bgm() {
		File f = new File("BackGroundMusic.wav");
		AudioInputStream audioInput;
		try {
			audioInput = AudioSystem.getAudioInputStream(f);
			clip = AudioSystem.getClip();
			clip.open(audioInput);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}	
	}
	
	public static void stop_bgm() {
		clip.stop();
	}
}
