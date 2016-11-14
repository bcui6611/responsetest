package edu.montavisa.mango;

import javax.swing.JLabel;


public class ThreadedCountdownField extends Thread
{
  private Meditation meditation;
  private JLabel timeRemainingLabel;
  private long timeRemaining;
  private String soundFile;

  /**
   * @param meditation Our controller class, needed for a callback to doEndOfTimerAction().
   * @param timeLabel The JLabel we will update.
   * @param timeToSleep Time to sleep, in seconds.
   */
  ThreadedCountdownField(Meditation meditation, JLabel timeLabel, long timeToSleep, String soundFile)
  {
    this.meditation = meditation;
    this.timeRemainingLabel = timeLabel;
    this.timeRemaining = timeToSleep;
    this.soundFile = soundFile;
  }

  public void run()
  {
    // TODO test, is this running on the EDT? should be, it is triggered from the start button
    
    while (timeRemaining > 0)
    {
      timeRemainingLabel.setText("time to sound: " + Long.toString(timeRemaining) + " seconds");
      try
      {
        Thread.sleep(1000);
      }
      catch (InterruptedException e)
      {
        if (meditation.getDebugFileWriter()!=null) e.printStackTrace(meditation.getDebugFileWriter());
      }
      timeRemaining--;
    }

    timeRemainingLabel.setText("");
    meditation.doEndOfTimerAction(soundFile);
  }

}



