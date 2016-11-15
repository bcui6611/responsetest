package edu.montavisa.mango;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
 

// TODO play the sound twice
// TODO cleanup code
// TODO verify that code is properly on EDT; use Swing Timer instead?
// TODO threading approach is very old (taken from an old applet); update or replace
// TODO close all our resources on application exit
// TODO just get our audio input stream one time at app startup
// TODO com.apple.mrj.application.apple.menu.about.name is obfuscated and needs to be replaced

public class Meditation implements LineListener
{
  ThreadedCountdownField tcf;
  
  // our main frame
  MeditationMainFrame mainFrame;
  
  // meditation panel references
  MeditationPanel meditationPanel;
  private JButton startStopButton;
  private JButton testSoundButton;
  private JButton dogSoundButton;
  private JTextField timeEntryTextField;
  private JLabel timeCountdownLabel;
  
  private static final String BUTTON_START_TEXT = "Start";
  private static final String BUTTON_STOP_TEXT  = "Stop";
  
  // for debugging
  private static final String DEBUG_FILENAME = "ddmeditation.debug";
  private String homeDir;
  private String canonDebugFilename;
  private File debugFile;
  private PrintWriter debugFileWriter;
  
  public static void main(String[] args) 
  throws Exception
  {
    new Meditation();
  }
  
  public Meditation()
  {
    // TODO can this be moved to the ant build script?
    System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
    
    populateTestData();

    // determine the name of our debug file
    createDebugFileWriter();
    
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          instantiateMeditationPanel();
          mainFrame = new MeditationMainFrame(meditationPanel);
          mainFrame.setLocationRelativeTo(null);
          mainFrame.setVisible(true);
        }
        catch (Exception exception)
        {
          if (debugFileWriter!=null) exception.printStackTrace(debugFileWriter);
        }
      }
    });
  }

  private void populateTestData(){

  }

  private void createDebugFileWriter()
  {
    homeDir = System.getProperty("user.home");
    canonDebugFilename = homeDir + System.getProperty("file.separator") + DEBUG_FILENAME;
    debugFile = new File(canonDebugFilename);
    try
    {
      debugFileWriter = new PrintWriter(debugFile);
    }
    catch (FileNotFoundException e)
    {
      // just going to ignore this one; if we can't write a debug file,
      // we can't write.
    }
  }
  
  private void instantiateMeditationPanel()
  {
    meditationPanel = new MeditationPanel();
    startStopButton = meditationPanel.getStartStopButton();
    testSoundButton = meditationPanel.getTestSoundButton();
    dogSoundButton = meditationPanel.getDogSoundButton();
    timeEntryTextField = meditationPanel.getTimeField();
    timeCountdownLabel = meditationPanel.getTimeCountdownField();

    // make sure the button starts with our start text
    startStopButton.setText(BUTTON_START_TEXT);
    
    addListenersToWidgets();
  }
  
  private void addListenersToWidgets()
  {
    // [enter] on this field should be just like clicking the start button
    timeEntryTextField.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        doStartButtonClickedAction();
      } 
    });

    // handle start/stop actions
    startStopButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        String currentButtonText = startStopButton.getText();
        if (currentButtonText.toUpperCase().equals(BUTTON_START_TEXT.toUpperCase()))
        {
          doStartButtonClickedAction();
        }
        else
        {
          doStopButtonClickedAction();
        }
      } 
    });

    // this is the gong/sound test button
    testSoundButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        //playGongSound(CAT_SOUND);
      } 
    });

    // this is the gong/sound test button
    dogSoundButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        //playGongSound(DOG_SOUND);
      } 
    });
  }
  
  /**
   * Things to do when the Start button is clicked.
   */
  private void doStartButtonClickedAction()
  {
    // go from Start to Stop
    String desiredTimeString = timeEntryTextField.getText();
    long desiredSleepTime = 1;
    //String soundFile = CAT_SOUND;
    try
    {
      // get the time in seconds, might be 0.5, 10, 10.5, whatever
      float timeInMinutesFromUser = Float.parseFloat(desiredTimeString);
      
      // note: removed a 'blank string' test; blank strings don't survive parseFloat

      // don't allow '0'
      if (timeInMinutesFromUser == 0)
      {
        JOptionPane.showMessageDialog(mainFrame, "Dude, I can't deal with zero.");
        return;
      }
      
      // convert the float time to seconds
      desiredSleepTime = (long) timeInMinutesFromUser;
    }
    catch (NumberFormatException nfe)
    {
      JOptionPane.showMessageDialog(mainFrame, "Dude, I don't think that was a number.");
      return;
    }
    
    // lock the textfield so the user can't change it
    enableTimeEntryField(false);

    // change button text to "Stop"
    startStopButton.setText(BUTTON_STOP_TEXT);
    
    // disable the 'test sound' button
    testSoundButton.setEnabled(false);
    
    // desiredSleepTime needs to be in seconds
    tcf = new ThreadedCountdownField(this, timeCountdownLabel, desiredSleepTime);

    if (tcf.isAlive())
    {
      tcf.resume();
    }
    else
    {
      tcf.start();
    }
  }
  
  /**
   * Things to do when the Stop button is clicked.
   */
  void doStopButtonClickedAction()
  {
    // go from Stop to Start: stop everything, change text to Start
    startStopButton.setText(BUTTON_START_TEXT);
    enableTimeEntryField(true);
    
    // re-enable the 'test sound' button
    testSoundButton.setEnabled(true);

    clearCountdownField();

    if (tcf != null)
    {
      try
      {
        tcf.stop();
        tcf = null;
      }
      catch (Exception e)
      {
        if (debugFileWriter!=null) e.printStackTrace(debugFileWriter);
      }
    }
    
    timeEntryTextField.requestFocus();
  }
  
  private void enableTimeEntryField(boolean b)
  {
    timeEntryTextField.setEditable(b);
    timeEntryTextField.setEnabled(b);
  }
  
  private void clearCountdownField()
  {
    timeCountdownLabel.setText("");
  }
  
  public void doEndOfTimerAction(String soundFile)
  {
    playGongSound(soundFile);
    doStopButtonClickedAction();
  }
  
  public void playGongSound_old() 
  {
    try
    {
      // how to get an image file as a resource out of a jar file
      //imgBoldText = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("text_bold.png"));
      
      // (1) get resource as a file on the filesystem
      //InputStream inputStream = new FileInputStream("/Users/al/DevDaily/Projects/MeditationApp/resources/gong.au");

      // (2) get the sound file as a resource out of our jar file;
      //     the sound file must be in the same directory as this class file.
      //     this input stream recipe comes from a javaworld.com article.
      //InputStream inputStream = getClass().getResourceAsStream(SOUND_FILENAME);
      //AudioStream audioStream = new AudioStream(inputStream);
      //AudioPlayer.player.start(audioStream);

      // other methods (not needed here; just kept here for reference)
      //audioStream.close();
      //AudioPlayer.player.stop(as);
    }
    catch (Exception e)
    {
      if (debugFileWriter!=null) e.printStackTrace(debugFileWriter);
    }
  }

  /**
   * this flag indicates whether the playback completes or not.
   */
  private boolean playCompleted;
   
  /**
   * Play a given audio file.
   * @param audioFilePath Path of the audio file.
   */
  public void playGongSound(String audioFile) {

      try {
          //AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
          URL inputUrl = getClass().getResource(audioFile);
          AudioInputStream audioStream = AudioSystem.getAudioInputStream(inputUrl);
          AudioFormat format = audioStream.getFormat();

          DataLine.Info info = new DataLine.Info(Clip.class, format);

          Clip audioClip = (Clip) AudioSystem.getLine(info);

          //audioClip.addLineListener(this);

          audioClip.open(audioStream);
           
          audioClip.start();
           
          int count = 3;
          while (count-- > 0) {
              // wait for the playback completes
              try {
                  Thread.sleep(1000);
              } catch (InterruptedException ex) {
                  ex.printStackTrace();
              }
          }
           
          audioClip.close();
           
      } catch (UnsupportedAudioFileException ex) {
          System.out.println("The specified audio file is not supported.");
          ex.printStackTrace();
      } catch (LineUnavailableException ex) {
          System.out.println("Audio line for playing back is unavailable.");
          ex.printStackTrace();
      } catch (IOException ex) {
          System.out.println("Error playing the audio file.");
          ex.printStackTrace();
      }     
  }

  @Override
  public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
         
        if (type == LineEvent.Type.START) {
            System.out.println("Playback started.");
             
        } else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
            System.out.println("Playback completed.");
        }
 
  }

  public PrintWriter getDebugFileWriter()
  {
    return this.debugFileWriter;
  }

}





