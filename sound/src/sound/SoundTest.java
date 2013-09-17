package sound;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;
 
/** Test playing sound file */
@SuppressWarnings("serial")
public class SoundTest extends JFrame {
   private String fileGameOver = "gameover.wav"; // audio filename ("wav", "au", "aiff" only)
   private Clip soundClipGameOver;               // Java Internal Sound Clip
 
   /** Constructor to setup the GUI components and sound clips */
   public SoundTest() {
      // Prepare the Sound Clip
      try {
         // Generate an URL from filename
         URL url = this.getClass().getClassLoader().getResource(fileGameOver);
         if (url == null) {
            System.err.println("Couldn't find file: " + fileGameOver);
         } else {
            // Get an audio input stream to read the audio data
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Allocate a sound clip, used by Java internal
            soundClipGameOver = AudioSystem.getClip();
            // Read the audio data into sound clip
            soundClipGameOver.open(audioIn);
         }
      } catch (UnsupportedAudioFileException e) {
         System.err.println("Audio Format not supported: " + fileGameOver);
      } catch (Exception e) {
         e.printStackTrace();
      }
 
      Container cp = getContentPane();
      cp.setLayout(new FlowLayout());
      JButton btn = new JButton("Play Sound");
      cp.add(btn);
      btn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            // Play sound clip
            if (soundClipGameOver.isRunning()) soundClipGameOver.stop();
            soundClipGameOver.setFramePosition(0); // rewind to the beginning
            soundClipGameOver.start();             // start playing
         }
      });
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setTitle("Test Sound");
      setSize(200, 100);
      setVisible(true);
   }
 
   /** The entry "main" method */
   public static void main(String[] args) {
      // Run GUI codes in Event-Dispatching thread for thread safety
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new SoundTest(); // Let the constructor do the job
         }
      });
   }
}