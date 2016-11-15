package edu.montavisa.mango;

import javax.swing.JLabel;


public class ThreadedCountdownField extends Thread
{
  private Meditation meditation;
  private JLabel timeRemainingLabel;
  private long timeRemaining;
  private String soundFile;

  private static final String CYMBAL_CRASH = "gong.au";
  private static final String CAT_SOUND = "cat_meow_x.wav";
  private static final String DOG_SOUND = "dog_bark_x.wav";

  private static final long[] elapseArray1 = new long[] {7, 4, 5, 2, 2, 7, 8, 6, 2, 5};
  private static final String[] fileArray1 = new String[] {
    CAT_SOUND, CAT_SOUND, CAT_SOUND, CAT_SOUND, CAT_SOUND,
    CAT_SOUND, CAT_SOUND, CAT_SOUND, CAT_SOUND, CAT_SOUND
  };

  private static final long[] elapseArray2 = new long[] {5, 3, 8, 7, 2, 6, 8, 2, 8, 7};
  private static final String[] fileArray2 = new String[] {
    CYMBAL_CRASH, CAT_SOUND, CYMBAL_CRASH, CAT_SOUND, CAT_SOUND,
    CYMBAL_CRASH, DOG_SOUND, CAT_SOUND, CYMBAL_CRASH, CAT_SOUND
  };

  /**
   * @param meditation Our controller class, needed for a callback to doEndOfTimerAction().
   * @param timeLabel The JLabel we will update.
   * @param timeToSleep Time to sleep, in seconds.
   */
  ThreadedCountdownField(Meditation meditation, JLabel timeLabel, long timeToSleep)
  {
    this.meditation = meditation;
    this.timeRemainingLabel = timeLabel;
    this.timeRemaining = timeToSleep;
  }

  public void run()
  {
    // TODO test, is this running on the EDT? should be, it is triggered from the start button
    for(int i=0; i < elapseArray1.length; i++)  {
      timeRemaining = elapseArray1[i];
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
      meditation.playGongSound(fileArray1[i]);   
    }
    meditation.doEndOfTimerAction(fileArray1[0]);   
  }

}



