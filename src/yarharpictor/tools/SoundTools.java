/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yarharpictor.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import yarharpictor.YarHarPictor;

/**
 *
 * @author jaime
 */
public class SoundTools {
    
    public static synchronized void playPirate() {

      try {
          
        Clip clip = AudioSystem.getClip();
        
        InputStream audioSrc = YarHarPictor.class.getResourceAsStream("/resources/pirate.wav");
        InputStream bufferedIn = new BufferedInputStream(audioSrc);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
        clip.open(audioStream);
        clip.start(); 
      } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
        System.err.println(e.getMessage());
      }
    
    }
    
}
