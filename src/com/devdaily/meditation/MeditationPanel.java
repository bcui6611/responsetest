package edu.montavisa.mango;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;

public class MeditationPanel
extends JPanel
{
  private JLabel headerLabel = new JLabel();
  private JLabel helpTextLabel = new JLabel();
  private JLabel timeLabel = new JLabel();
  private JLabel timeCountdownLabel = new JLabel();
  
  private JTextField timeEntryField=new JTextField(20);
  private JButton startStopButton = new JButton("Start");
  private JButton testSoundButton = new JButton("Cat");
  private JButton dogSoundButton = new JButton("Dog");

  public JTextField getTimeField()
  {
    return timeEntryField;
  }
  
  public JButton getStartStopButton()
  {
    return startStopButton;
  }
  
  public JButton getTestSoundButton()
  {
    return testSoundButton;
  }
  
  public JButton getDogSoundButton()
  {
    return dogSoundButton;
  }

  public JLabel getTimeCountdownField()
  {
    return timeCountdownLabel;
  }
  
  public MeditationPanel()
  {
    try
    {
      headerLabel.setText("Response Measurement");
      headerLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
      helpTextLabel.setText("Set the desired response time, then click the Start button.");
      timeLabel.setText("Desired Time (seconds):");
      timeEntryField.setText("");
      timeCountdownLabel.setForeground(Color.GRAY);
      buildMeditationPanel();
    }
    catch(Exception exception)
    {
      // do nothing right now
    }
  }

  private void buildMeditationPanel()
  {
    FormLayout layout = new FormLayout("pref, 2dlu, pref:grow",                                  // columns
                        "p,3dlu, p,9dlu, p,3dlu, p,9dlu, p,3dlu, fill:pref:grow,3dlu, p,9dlu");  // rows
    PanelBuilder builder = new PanelBuilder(layout, this);
    builder.setDefaultDialogBorder();

    CellConstraints cc = new CellConstraints();

    // row 1
    builder.add(headerLabel, cc.xyw(1,1,3));

    // row 3
    builder.add(helpTextLabel, cc.xyw(1,3,3,"left,top"));

    // row 5
    builder.add(timeLabel, cc.xy(1,5));
    builder.add(timeEntryField, cc.xy(3,5));
    timeLabel.setLabelFor(timeEntryField);

    // row 7 (buttons)
    JButton[] buttons = {dogSoundButton, testSoundButton, startStopButton};
    JPanel buttonPanel = ButtonBarFactory.buildLeftAlignedBar(buttons);
    builder.add(buttonPanel, cc.xy(3,7));
    
    // row 9 (countdown label)
    builder.add(timeCountdownLabel, cc.xy(3,9));
  }

}








