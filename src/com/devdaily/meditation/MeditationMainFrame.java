package edu.montavisa.mango;

import java.awt.BorderLayout;
import javax.swing.JFrame;


public class MeditationMainFrame extends JFrame
{

  private MeditationPanel meditationPanel;
  
  public MeditationMainFrame(MeditationPanel meditationPanel)
  {
    this.meditationPanel = meditationPanel;

    // i don't really like having a title on the frame so i took this off
    //setTitle("DevDaily.com Meditation App");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(meditationPanel,BorderLayout.CENTER);
    pack();
    setResizable(false);
  }
}
