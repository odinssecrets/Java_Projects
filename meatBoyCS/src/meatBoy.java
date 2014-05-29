import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;


public class meatBoy extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final double GRAVITY = 0.35;
	final double FRICTION = 0.5;
	
	boolean paused = false;

	Font f = new Font("Fixedsys", Font.PLAIN, 36);

	int cubeSize = 25;

	String screenPosition = "menu";

	int counter = 0;

	MouseInfo m;

	Timer ticker;

	String [] winOptions = {"Retry", "Exit", "Next Level"};
	String [] dieOptions = {"Retry", "Exit"};
	String [] pauseOptions = {"Unpause", "Retry", "Exit"};

	int jumpCount;
	double meatX = 6*cubeSize, meatY = 18*cubeSize, oldMeatX = meatX, oldMeatY = meatY;
	ImageIcon MeatBoy = new ImageIcon(getClass().getResource("meatBoy.png")); 
	ImageIcon backgroundBlock = new ImageIcon(getClass().getResource("background.png")); 
	ImageIcon MeatBoy2 = new ImageIcon(getClass().getResource("MB_2.png")); 
	ImageIcon MeatBoyTitle = new ImageIcon(getClass().getResource("MB_Title.png")); 
	ImageIcon LevelsList = new ImageIcon(getClass().getResource("levelsList.png")); 

	int menuSelect;

	int X_SIZE;
	int Y_SIZE;

	levelMap lvl;
	final int LEVEL_COUNT = 1;
	int levelPos;
	levelMap level [] = {new levelOne()};
	int uCount = 0, bCount = 0;
	double yVel;
	double xVel;

	boolean tick = false, jump = false, right = false, left = false, onLeftWall, onRightWall, onPlatform, jumpStart = false;

	ArrayList <platform> platforms = new ArrayList<platform>();
	ArrayList <buzzsaw> buzzsaws = new ArrayList<buzzsaw>();

	boolean [] lvlSel = new boolean[4];

	public meatBoy()
	{
		JFrame jF = new JFrame("Meat Boy!");

		lvl = new levelOne();
		X_SIZE = cubeSize * lvl.xLength;
		Y_SIZE = cubeSize * lvl.yLength;

		jF.setSize(X_SIZE,Y_SIZE);
		jF.pack();
		jF.setLocation(250,50);
		jF.setVisible(true);
		jF.setSize(X_SIZE+7,Y_SIZE+29);
		jF.setResizable(false);
		jF.add(this);
		jF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		keyListener kL = new keyListener();
		jF.addKeyListener(kL);
		ml mL = new ml();
		jF.addMouseListener(mL);
		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				counter++;
				tick = true;
			}
		};

		for (int i = 0; i<lvl.xLength; i++)
		{
			for(int j = 0; j < lvl.yLength; j++ )
			{
				switch(lvl.map[j][i])
				{
				case('p'):
					platforms.add(uCount,new platform(i*cubeSize,j*cubeSize));
				uCount++;
				break;
				case('|'):
					platforms.add(uCount,new platform(i*cubeSize,j*cubeSize));
				uCount++;
				break;	
				case('g'):
					platforms.add(uCount,new goal(i*cubeSize,j*cubeSize));
				uCount++;
				break;	
				case('*'):
					buzzsaws.add(bCount,new buzzsaw(i*cubeSize,j*cubeSize));
				bCount++;
				break;					
				}
			}
		}		
		ticker = new Timer(20, al); 
		ticker.start();

		while(true)
		{		
			int mx = m.getPointerInfo().getLocation().x - jF.getLocation().x - 6;
			int my = m.getPointerInfo().getLocation().y - jF.getLocation().y - 15;

			for(int i = 0; i < 3; i++)
			{
				if(mx > (int)(cubeSize*3.33) && mx < (int)(cubeSize*3.33) + cubeSize*6 && my > (int)(i*cubeSize/1.3 + i*cubeSize/1.3 + cubeSize*12.3)&& my < (int)(i*cubeSize/1.3 + i*cubeSize/1.3 + cubeSize*12.3 + cubeSize*1.4))
				{
					menuSelect = i+1;
					break;
				}
				else
					menuSelect = 0;
			}
			int x = (LEVEL_COUNT % 4) == 0 ? 4 : LEVEL_COUNT % 4;
			for(int i = 0; i < x; i ++)
			{
				if(mx > (int)(cubeSize*2) && mx < (int)(cubeSize*10))
				{
					if(my > (int)((cubeSize*3.5) + i * (cubeSize*3.75)) && my < (int)((cubeSize*3.5) + (i+1) * (cubeSize*3.75)))
					{
						lvlSel[i] = true;
					}
					else
					{
						lvlSel[i] = false;		
					}
				}

			}
			for(int j = 0; j < bCount; j++)
			{
				buzzsaws.get(j).swapImage();	
			}
			boolean tempLeftWall = false;
			boolean tempRightWall = false;
			boolean tempPlatform = false;
			for(int i = 0; i < platforms.size(); i ++)
			{
				if(oldMeatX > platforms.get(i).x+cubeSize || oldMeatX + cubeSize < platforms.get(i).x)
				{
					if(meatY + cubeSize != platforms.get(i).y)
					{
						if(meatX<=platforms.get(i).x+cubeSize && meatX + cubeSize > platforms.get(i).x + cubeSize/2.0)
						{
							if((meatY>=platforms.get(i).y && meatY<= platforms.get(i).y+cubeSize) ||
									(meatY + cubeSize >= platforms.get(i).y && meatY + cubeSize <= platforms.get(i).y + cubeSize))
							{
								if(platforms.get(i).type.equals("goal"))
									winning();
								else
								{
									tempLeftWall = true;
									meatX = platforms.get(i).x + cubeSize;
									if(!onLeftWall)yVel -= 1;
									jumpCount = 0;
								}
							}
							else if(meatY < platforms.get(i).y+cubeSize && meatY >= platforms.get(i).y+10)
							{
								if(platforms.get(i).type.equals("goal"))
									winning();
								{
									meatY = platforms.get(i).y+cubeSize;
									yVel = 0;
								}
							}
						}
						else if(meatX+cubeSize>=platforms.get(i).x && meatX <platforms.get(i).x - cubeSize/2.0)
						{
							if((meatY>=platforms.get(i).y && meatY<= platforms.get(i).y+cubeSize) ||
									(meatY + cubeSize >= platforms.get(i).y && meatY + cubeSize <= platforms.get(i).y + cubeSize))
							{
								if(platforms.get(i).type.equals("goal"))
									winning();
								else
								{
									tempRightWall = true;
									meatX = platforms.get(i).x - cubeSize;
									if(!onRightWall)yVel -= 1;
									jumpCount = 0;
								}
							}
							else if(meatY < platforms.get(i).y+cubeSize && meatY > platforms.get(i).y+10)
							{
								if(platforms.get(i).type.equals("goal"))
									winning();
								else
								{
									meatY = platforms.get(i).y+cubeSize;
									yVel = 0;
								}
							}
						}
					}
				} 
				else if((meatX >= platforms.get(i).x && meatX <= platforms.get(i).x + cubeSize) ||
						(meatX + cubeSize >= platforms.get(i).x && meatX + cubeSize <= platforms.get(i).x + cubeSize))
				{
					if(meatY + cubeSize >= platforms.get(i).y && meatY < platforms.get(i).y-cubeSize/2.0)
					{ 
						if(meatX+cubeSize == platforms.get(i).x || meatX==platforms.get(i).x+cubeSize)
						{
							//Nothing happens here,the edge of a platform will not support meat boy
						}
						else
						{
							if(platforms.get(i).type.equals("goal"))
								winning();			
							else
							{
								tempPlatform = true;
								meatY = platforms.get(i).y - cubeSize;
								jumpCount = 0;
							}
						}
					}
					else if(meatY < platforms.get(i).y+cubeSize && meatY > platforms.get(i).y+10)
					{
						if(platforms.get(i).type.equals("goal"))
							winning();
						else
						{
							meatY = platforms.get(i).y+cubeSize;
							yVel = 0;
						}
					}
				}
			}

			//Buzz saws
			for(int i = 0; i < buzzsaws.size(); i ++)
			{

				if(meatY + cubeSize != buzzsaws.get(i).y)
				{
					if(meatX<=buzzsaws.get(i).x+cubeSize && meatX + cubeSize > buzzsaws.get(i).x + cubeSize/2.0)
					{
						if((meatY>=buzzsaws.get(i).y && meatY<= buzzsaws.get(i).y+cubeSize) ||
								(meatY + cubeSize >= buzzsaws.get(i).y && meatY + cubeSize <= buzzsaws.get(i).y + cubeSize))
						{
							death("buzzsaw");
							break;
						}
						else if(meatY < buzzsaws.get(i).y+cubeSize && meatY >= buzzsaws.get(i).y+10)
						{
							death("buzzsaw");
							break;
						}
					}
					else if(meatX+cubeSize>=buzzsaws.get(i).x && meatX <buzzsaws.get(i).x)
					{
						if((meatY>=buzzsaws.get(i).y && meatY<= buzzsaws.get(i).y+cubeSize) ||
								(meatY + cubeSize >= buzzsaws.get(i).y && meatY + cubeSize <= buzzsaws.get(i).y + cubeSize))
						{
							death("buzzsaw");
							break;
						}
						else if(meatY < buzzsaws.get(i).y+cubeSize && meatY > buzzsaws.get(i).y+10)
						{
							death("buzzsaw");
							break;
						}
					}
				}
				else if((meatX >= buzzsaws.get(i).x + cubeSize/6.0 && meatX <= buzzsaws.get(i).x + cubeSize - 2*(cubeSize/6.0)) ||
						(meatX + cubeSize >= buzzsaws.get(i).x + cubeSize/6.0 && meatX + cubeSize<= buzzsaws.get(i).x + cubeSize - 2*(cubeSize/6.0)))
				{
					if(meatY + cubeSize >= buzzsaws.get(i).y && meatY < buzzsaws.get(i).y-cubeSize/2.0)
					{ 
						if(meatX+cubeSize == buzzsaws.get(i).x || meatX==buzzsaws.get(i).x+cubeSize)
						{
							//Nothing happens here,the edge of a platform will not support meat boy
						}
						else
						{
							death("buzzsaw");
							break;
						}
					}
					else if(meatY < buzzsaws.get(i).y+cubeSize && meatY > buzzsaws.get(i).y+10)
					{
						death("buzzsaw");
					}
				}
			}

			onRightWall = tempRightWall;
			onLeftWall = tempLeftWall;
			onPlatform = tempPlatform;
			if(onRightWall && onLeftWall)
			{
				if(xVel > 0)
					onLeftWall = false;
				else if(xVel < 0)
					onRightWall = false;
			}
			if(tick && !paused)
			{
				if(!onRightWall && !onLeftWall)
				{
					oldMeatX = meatX;
					oldMeatY = meatY;
				}
				if(xVel>0)
					xVel-=FRICTION;
				else if (xVel < 0)
					xVel+=FRICTION;
				meatX += (int)(xVel*(cubeSize/30.0));
				if(!onPlatform || jump)
					meatY -= (int)(yVel*(cubeSize/30.0));
				yVel-=GRAVITY;
				if(yVel <= -4)
					yVel = -4;
				if(xVel >= 13)
				{
					xVel = 13;
				}
				if(xVel<=-13)
				{
					xVel = -13;
				}
				tick = false;
				if(jump)
				{
					if(jumpCount < 120)
					{
						jumpCount += 8;
					}
					else 
						jump = false;
				}
				if(right)
				{
					if(!onRightWall)
						xVel+=1;
				}
				if(left)
				{
					if(!onLeftWall)
						xVel-=1;
				}
				if(meatX< 0)
					meatX = 0;
				else if(meatX + cubeSize> X_SIZE)
					meatX = X_SIZE - cubeSize;
				if(meatY <=0)
				{
					meatY = 0;
					yVel -= 1;
				}
			}
			else if(paused)
			{
				switch(JOptionPane.showOptionDialog(this,
						"Paused!",
						"What would you like to do?",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						pauseOptions,
						pauseOptions[0]))
						{
						case 0:
							screenPosition = "game";
							paused = false;
							break;
						case 1:
							screenPosition = "game";
							reset();
							paused = false;
							break;
						case 2:
							screenPosition = "menu";
							reset();
							paused = false;
							break;
						}	
			}
			if(jumpStart && jumpCount == 0)
			{
				if(onRightWall || onLeftWall || onPlatform)
				{
					yVel = 7;
					if(!onPlatform)
					{
						if(onRightWall)
						{
							xVel = -10;
						}
						else if(onLeftWall)
						{
							xVel = 10;
						}	
					}
					jumpStart = false;
				}
				else
					jumpStart = false;
			}
			else
			{
				jumpStart = false;
			}
			repaint();
		}
	}

	//Code stolen from the internet :)
	protected Image mImage;
	public void update(Graphics g)
	{
		paint(g);
	}
	public void paint(Graphics g)
	{
		Dimension d = getSize();
		checkOffscreenImage();
		Graphics offG = mImage.getGraphics();
		offG.setColor(getBackground());
		offG.fillRect(0, 0, d.width, d.height);
		/**
		 * Draw into the offscreen image.
		 */
		if(screenPosition.equals("game"))
			paintOffscreen(mImage.getGraphics());
		else if(screenPosition.equals("menu"))
			paintMenu(mImage.getGraphics());
		else if(screenPosition.equals("levels"))
			paintLevels(mImage.getGraphics());
		/**
		 * Put the offscreen image on the screen.
		 */
		g.drawImage(mImage, 0, 0, null);
	}
	private void checkOffscreenImage() {
		Dimension d = getSize();
		if (mImage == null || mImage.getWidth(null) != d.width
				|| mImage.getHeight(null) != d.height) {
			mImage = createImage(d.width, d.height);
		}
	}
	//End of code stolen from the internet!

	public void paintMenu(Graphics g)
	{
		g.clearRect(0,0,X_SIZE, Y_SIZE);
		g.drawImage(MeatBoyTitle.getImage(), 0, 0, X_SIZE, Y_SIZE, null);

		for(int i = 0; i < 3; i++)
			if(i+1 == menuSelect)
				g.drawImage(MeatBoy2.getImage(), (int)(cubeSize*3.33),(int)(i*cubeSize/1.3 + i*cubeSize/1.3 + cubeSize*12.1),cubeSize, cubeSize,null);				
			else
				g.drawImage(MeatBoy.getImage(), (int)(cubeSize*3.33),(int)(i*cubeSize/1.3 + i*cubeSize/1.3 + cubeSize*12.1),cubeSize, cubeSize,null);
	}

	//Levels List
	public void paintLevels(Graphics g)
	{
		g.clearRect(0,0,X_SIZE, Y_SIZE);
		g.setFont(f);
		g.drawImage(LevelsList.getImage(), 0,0,X_SIZE,Y_SIZE,null);
		int x = (LEVEL_COUNT % 4) == 0 ? 4 : LEVEL_COUNT % 4;
		for(int i = 0; i < x; i++)
		{
			g.drawString("Level " + (i+1), (int)(cubeSize*3.75), (int)((cubeSize*5.75) + i * (cubeSize*3.75)));
		}
	}

	public void paintOffscreen (Graphics g)
	{
		g.clearRect(0,0,X_SIZE, Y_SIZE);

		for (int i = 0; i<lvl.xLength; i++)
		{
			for(int j = 0; j < lvl.yLength; j++ )
			{
				g.drawImage(backgroundBlock.getImage(),i*cubeSize,j*cubeSize, cubeSize, cubeSize, null);
			}
		}

		for(int i = 0; i < platforms.size(); i ++)
		{	
			g.drawImage(platforms.get(i).img.getImage(),platforms.get(i).x,platforms.get(i).y,cubeSize,cubeSize,null);
		}

		for(int i = 0; i < buzzsaws.size(); i ++)
		{	
			g.drawImage(buzzsaws.get(i).img.getImage(),buzzsaws.get(i).x,buzzsaws.get(i).y,cubeSize,cubeSize,null);
		}
		g.drawImage(MeatBoy.getImage(),(int)meatX,(int)meatY,cubeSize,cubeSize,null);

	}

	public void winning()
	{
		tick = false;
		switch(JOptionPane.showOptionDialog(this,
				"Level Complete!",
				"What would you like to do?",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				winOptions,
				winOptions[2]))
				{
				case 0:
					screenPosition = "game";
					break;
				case 1:
					screenPosition = "menu";
					break;
				case 2:
					try{
						screenPosition = "game";
						lvl = level[levelPos+1];
						levelPos++;
					}catch(Exception e)
					{
						screenPosition = "menu";
						JOptionPane.showMessageDialog(this, "Sorry, that was the last level!");
					}
					break;
				}
		meatX = (lvl.startX-1)*cubeSize; meatY = (lvl.startY-1)*cubeSize; oldMeatX = meatX; oldMeatY = meatY;
		xVel = 0; yVel = 0; jump = false;
		left = false; right = false;
		return;
	}

	public void death(String reason)
	{
		tick = false;
		switch(JOptionPane.showOptionDialog(this,
				"You Died!!",
				"You died to a " + reason+"!",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				dieOptions,
				dieOptions[0]))
				{
				case 0:
					screenPosition = "game";
					break;
				case 1:
					screenPosition = "menu";
					break;
				}
		meatX = (lvl.startX-1)*cubeSize; meatY = (lvl.startY-1)*cubeSize; oldMeatX = meatX; oldMeatY = meatY;
		xVel = 0; yVel = 0; jump = false;
		left = false; right = false;
		return;
	}
	
	public void reset()
	{
		tick = false;
		meatX = (lvl.startX-1)*cubeSize; meatY = (lvl.startY-1)*cubeSize; oldMeatX = meatX; oldMeatY = meatY;
		xVel = 0; yVel = 0; jump = false;
		left = false; right = false;
		return;
	}

	public static void main (String [] args)
	{
		meatBoy m = new meatBoy();
	}

	public class ml implements MouseListener
	{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			if(screenPosition.equals("menu"))
			{
				if(arg0.getButton()==1)
				{
					switch(menuSelect)
					{
					case(0):
						break;
					case(1):
						screenPosition = "levels";
					break;
					case(2):
						break;
					case(3):
						break;
					}
				}
			}
			else if(screenPosition.equals("levels"))
			{
				if(arg0.getButton()==1)
				{
					for(int i = 0; i < lvlSel.length; i++)
					{
						if(lvlSel[i])
						{
							screenPosition = ("game");
							levelPos = i;
							lvl = level[i];
						}
					}
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

	}

	public class keyListener implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			if(arg0.getKeyCode() == 37)
			{
				left = true;
			}
			else if(arg0.getKeyCode() == 39)
			{
				right = true;
			}
			else if(arg0.getKeyCode() == arg0.VK_SPACE)
			{
				jumpStart = true;
				jump = true;
			}
			else if(arg0.getKeyCode() == arg0.VK_ESCAPE)
			{
				if(screenPosition.equals("game"))
				{
					paused = true;
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			if(arg0.getKeyCode() == 37)
			{
				left = false;
			}
			else if(arg0.getKeyCode() == 39)
			{
				right = false;
			}
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}

	}
}
