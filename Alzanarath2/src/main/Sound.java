package main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {
	Clip clip;
	URL soundURL[] = new URL[30];
	public Sound() {
		soundURL[0] = getClass().getResource("/Music/knights-of-camelot-8038.wav");
		soundURL[1] = getClass().getResource("/Music/TalkingSE.wav");
		soundURL[2] = getClass().getResource("/Music/DamageSE.wav");
		soundURL[3] = getClass().getResource("/Music/sword_clash.8.wav");
		soundURL[4] = getClass().getResource("/Music/ChangingCursorSE.wav");
		soundURL[5] = getClass().getResource("/Music/inventory_open.wav");
	}
	
	public void setFile(int i) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
			clip = AudioSystem.getClip();
			clip.open(ais);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		clip.start();
	}
	
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop() {
		clip.stop();
	}
}
