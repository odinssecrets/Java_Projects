import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class imageMain extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int SIZE = 1, X=0, Y=0;
	static int imgX=0, imgY=0;
	static imageMain iM=null;
	static String imgUrl="";
	static String imgType="";
	static boolean imageLoaded = false;
	static int ZOOM = 100;
	boolean mouseDrag;
	Point dragPoint = new Point(), dragPointEnd = new Point();
	JFrame jf=null;
	int [] [] pixelsArray=null;
	int [] [] tempzoomPixelsArray=null;
	imageToolkit imgToolkit = new imageToolkit();
	menuPanel m = new menuPanel(800, this);
	Font defaultFont = new Font("Arial", Font.PLAIN, 12);
	ArrayList<historyImages> history = new ArrayList<historyImages>();
	ArrayList<historyImages> future = new ArrayList<historyImages>();

	public imageMain(){
		this.setSize(800,800);
		iM=this;
		jf = new JFrame("Image Tool");
		jf.setLocation(100,20);
		jf.setLayout(null);
		jf.setSize(1000,826);
		jf.setResizable(false);
		this.repaint();
		JScrollPane ScrollPane = new JScrollPane(this,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ScrollPane.setLocation(0,0);
		ScrollPane.setSize(800,800);
		jf.add(ScrollPane);
		jf.setAutoRequestFocus(true);
		jf.setFocusable(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ScrollPane.addMouseListener(new MouseListener(){

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				jf.requestFocusInWindow();
				//	if(imageLoaded)imgToolkit.getPixel( SIZE,  X,  Y,  jf,arg0.getX(),arg0.getY(), pixelsArray);
				mouseDrag = true;
				dragPoint = new Point(arg0.getX(),arg0.getY());
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
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				dragPointEnd = new Point(arg0.getX(),arg0.getY());
				mouseDrag = false;
			}

		});
		jf.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if(imageLoaded)
				{
					if(arg0.getKeyCode()==KeyEvent.VK_UP)
					{
						m.zoom(ZOOM+20);
					}
					else if (arg0.getKeyCode()==KeyEvent.VK_DOWN)
					{
						m.zoom(ZOOM-20);
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		jf.add(m);
		jf.addWindowListener(new WindowListener(){

			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				saveSettings();
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				saveSettings();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

		});
		m.setLocation(800,0);
		m.setSize(200,800);
		jf.setVisible(true);
		jf.requestFocusInWindow();
		loadsettings();
		while(true){
			if(mouseDrag)repaint();
		}
	}

	protected Image mImage;
	public void update(Graphics g)
	{
		m.update(jf.getHeight());
		history = imgToolkit.history;
		future = imgToolkit.future;
		paint(g);
	}

	public void paint(Graphics g)
	{
		Dimension d = this.getSize();
		checkOffscreenImage();
		Graphics offG = mImage.getGraphics();
		offG.setColor(this.getBackground());
		offG.fillRect(0, 0, d.width, d.height);
		/**
		 * Draw into the offscreen image.
		 */
		paintOffscreen(mImage.getGraphics());
		/**
		 * Put the offscreen image on the screen.
		 */
		g.drawImage(mImage, 0, 0, null);
	}
	private void checkOffscreenImage() {
		Dimension d = this.getSize();
		if (mImage == null || mImage.getWidth(null) != d.width
				|| mImage.getHeight(null) != d.height) {
			mImage = this.createImage(d.width, d.height);
		}
	}

	public void paintOffscreen(Graphics g){

		if(imageLoaded&&pixelsArray!=null&&tempzoomPixelsArray!=null)
		{
			int startx = (this.getWidth()-(tempzoomPixelsArray[0].length/3))/2;
			int starty = (this.getHeight()-tempzoomPixelsArray.length)/2;
			int frameW =this.getWidth(), frameH =this.getHeight();
			for(int i = 0; i < tempzoomPixelsArray.length; i++)
			{
				if(i==0&&starty < 0){
					i+=Math.abs(starty);
					frameH+=Math.abs(starty);
				}
				for(int j = 0; j < tempzoomPixelsArray[i].length; j+=3)
				{
					if(j==0&&startx < 0){
						j+=Math.abs(startx*3);
						frameW+=Math.abs(startx*3);
					}
					g.setColor(new Color(imgToolkit.getRGBVal(tempzoomPixelsArray[i][j+2]),imgToolkit.getRGBVal(tempzoomPixelsArray[i][j+1]),imgToolkit.getRGBVal(tempzoomPixelsArray[i][j])));
					g.fillRect(startx+j/3,starty+i,1,1);
					if(j/3>frameW)
					{
						break;
					}
				}
				if(i>frameH)
				{
					break;
				}
			}
			Graphics2D g2d = (Graphics2D) g;
			Stroke oldStroke = g2d.getStroke();
			g2d.setStroke(new BasicStroke(3));

			//if(!mouseDrag)g2d.drawRect(dragPoint.x,dragPoint.y,dragPointEnd.x,dragPointEnd.y);
			//else if(mouseDrag)g2d.drawRect(dragPoint.x,dragPoint.y,MouseInfo.getPointerInfo().getLocation().x-150,
			//		MouseInfo.getPointerInfo().getLocation().y-50);
			g2d.setStroke(oldStroke);

		}
	}

	public void loadsettings(){
		File f = new File("C:/ProgramFiles/ImageTool/settings.txt");
		try {
			if(!f.exists()){
				(new File("C:/ProgramFiles/ImageTool/")).mkdirs();
				f.createNewFile();
				return;
			}
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			//Line 1 - Image was loaded on last run? (1 true : 0 false)
			imageLoaded = (Integer.parseInt(br.readLine())==1)? true:false;
			//Line 2 - Image Url
			imgUrl = br.readLine();
			m.openImage(imgUrl);
			br.close();
			fr.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			m.closeImage();
			e.printStackTrace();
		}
	}

	public void saveSettings(){
		File f = new File("C:/ProgramFiles/ImageTool/");
		try {
			f.mkdirs();
			f = new File("C:/ProgramFiles/ImageTool/settings.txt");
			f.createNewFile();
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			//Line 1 - Image was loaded on last run? (1 true : 0 false)
			bw.write((imageLoaded)? "1\n":"0\n");
			//Line 2 - Image Url
			bw.write(imgUrl+"\n");
			bw.close();
			fw.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		iM = new imageMain();
	}

	/**
	 * Debugging only
	 * @param i
	 */
	public void bugTest(int i){
		System.out.println("performing bugtest...");
		System.out.println("performing first round of tests...");
		for(int y = 1; y < i; y++)
		{
			if(imgToolkit.resizeImage(pixelsArray, 1000, y)==pixelsArray && !(1000==pixelsArray.length/3 && y==pixelsArray.length))
			{
				System.out.println("error resizing image");
				System.out.println(1 + ", " + y);
				System.out.println("continuing testing...");
			}
		}

		System.out.println("performing second round of tests...");
		for(int x = 1; x < i; x++)
		{
			if(imgToolkit.resizeImage(pixelsArray, x, 1000)==pixelsArray && !(x==pixelsArray.length/3 && 1000==pixelsArray.length))
			{
				System.out.println("error resizing image");
				System.out.println(x + ", " + 1000);
				System.out.println("continuing testing...");
			}
		}

	}

}
