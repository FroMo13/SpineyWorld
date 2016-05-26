package sw;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.MaskFormatter;

import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;

class WorldWindow extends JFrame implements ComponentListener {

  public static int  panelWidth=100;
  public static int  statusHeight=50;
  private JTextField news;	
  private JTextField txtOcu;
  private JCheckBox  ckImg;
  protected World    thisWorld;
  public int newsDisplayLength=120;
  
  public WorldWindow() {
  }
  
  public boolean writeImages() {
	  if (ckImg.isSelected()) {
		  return true;
	  }
	  return false;
  }
  
  public WorldWindow(World w) {
    super("World");
    thisWorld=w;
    setBounds(20,20,thisWorld.windowWidth(),thisWorld.windowHeight());
    Container con=this.getContentPane();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    this.addComponentListener(this);

    // Set up animation panel
    AniPanel aniPanel =new AniPanel();
    aniPanel.ww=this;
    con.add(aniPanel);
    
    // Set up input Box
    // Uses formatter to only allow 2 digits of input
    JLabel lOcu= new JLabel("Occupants");
    try {
        MaskFormatter mf = new MaskFormatter("##"); 
        txtOcu = new JFormattedTextField(mf);        
        } catch (Exception ex) {
        }
    txtOcu.setText(String.valueOf(thisWorld.initOccupants));
    txtOcu.setHorizontalAlignment(JTextField.RIGHT);
    
    // Images Check Box
    ckImg = new JCheckBox();
    ckImg.setText("Images");
    
    // Set up controlBox
    JButton btnRun = new JButton();
    btnRun.setText("Run");
    
    // Run Button
    btnRun.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		if (thisWorld.isAlive()) {
    			World.running=true;
    		} else {
    		thisWorld.start();
    		}
    	}
    });
    
    // Run One Button
    JButton btnOne = new JButton();
    btnOne.setText("Run One");
    
    btnOne.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		thisWorld.doTurnAndDraw();
    	}
    });
    
    // Stop Button
    JButton btnStop = new JButton();
    btnStop.setText("Stop");
  
    btnStop.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		if (thisWorld.isAlive()) {
    			//thisWorld.suspend();
    			World.running=false;
    		}
    	}
    });    
    
    // Reset Button;
    JButton btnReset = new JButton();
    btnReset.setText("Reset");
    
    btnReset.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		Al.lastID=0;
    		String s=txtOcu.getText();
    		int i = 10;
    		try {
    		    i = Integer.valueOf(s); 
    		} catch (NumberFormatException ex) {
    		}
    		thisWorld.worldWanted=false;
    		WorldWindow wWind = thisWorld.ww;
    		thisWorld = new World(thisWorld.x, thisWorld.y,i, thisWorld.cellSize);
    		thisWorld.interval=RunMe.interval;
    		thisWorld.ww = wWind;
                Terrain.setWorld(thisWorld);
     		repaint();
    	}
    });
    
    // Control Box
    JPanel controlBox = new JPanel();
    GridLayout g = new GridLayout();
    g.setRows(8);
    g.setColumns(2);
    g.setVgap(5);

    controlBox.setLayout(g);
    controlBox.setBackground(Color.lightGray);
    controlBox.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.lightGray,Color.gray));
    
    // Input Box sits at the top of the Control Box.
    controlBox.add(lOcu);
    controlBox.add(txtOcu);
    controlBox.add(ckImg);
    controlBox.add(btnRun);
    controlBox.add(btnOne);
    controlBox.add(btnStop);
    controlBox.add(btnReset);
    con.add(controlBox,"East");

    // Status Bar: News
    JPanel statusBar = new JPanel();
    statusBar.setBackground(Color.white);
    FlowLayout g2 = new FlowLayout();
    news = new JTextField((int)this.getWidth()/12);
    news.setBackground(Color.white);
    news.setForeground(Color.blue);
    news.setHorizontalAlignment(JTextField.LEFT);
    
    statusBar.add(news);
    
    con.add(statusBar,"South");
    setVisible(true);
  }
  
  public void setNews(String n) {
	  if (n.length() > newsDisplayLength) {
	      news.setText(n.substring(n.length()-newsDisplayLength,n.length()));
	  } else {
	      news.setText(n);
	  }
  }

  public void updateWindow() {
      this.getContentPane().getComponent(0).repaint();
  }
  
  // Listener Stuff
  public void componentHidden(ComponentEvent e) {
      //System.out.println(e.getComponent().getClass().getName() + " --- Hidden");
  }

  public void componentMoved(ComponentEvent e) {
	  //System.out.println(e.getComponent().getClass().getName() + " --- Moved");
  }

  public void componentResized(ComponentEvent e) {
	  //System.out.println(e.getComponent().getClass().getName() + " --- Resized ");
	  // Calc size by height and width, use the smallest.
	  int wc = (this.getWidth()-panelWidth) / (thisWorld.x+4);
	  int hc = (this.getHeight() -statusHeight) / (thisWorld.y+4);
	  
	  if (wc <= hc) {
	      thisWorld.setCellSize(wc);
	  } else {
		  thisWorld.setCellSize(hc);
	  }
	  news.setColumns(this.getWidth()/12);
	  updateWindow();
  }

  public void componentShown(ComponentEvent e) {
	  //System.out.println(e.getComponent().getClass().getName() + " --- Shown");
  }  
}
