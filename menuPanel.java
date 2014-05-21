import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class menuPanel extends JPanel implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	imageToolkit imgToolkit = new imageToolkit();
	imageMain GUIpointer;
	final JFileChooser fc = new JFileChooser();
	JTextField newX,newY;
	JLabel imageFields[] = new JLabel[4];
	DecimalFormat nf = new DecimalFormat("#.##");
	final char undo = 8630, redo = 8631;
	final int UNDO = 1, REDO = 2; 

	public menuPanel(int height, imageMain temp) {
		// TODO Auto-generated constructor stub		
		GUIpointer = temp;
		temp = null;
		setBackground(Color.GRAY);
		Font defaultFont = new Font("Arial", Font.BOLD, 13);
		Font defaultFont2 = new Font("Arial", Font.BOLD, 30);

		JButton changeSize = new JButton("Set new image size");
		changeSize.setLocation(20,340);
		changeSize.setSize(160,20);
		changeSize.setFont(defaultFont);
		add(changeSize);
		changeSize.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resizeImage(testNewWidth(newX.getText()),testNewHeight(newY.getText()));
			}
		});

//		JLabel logo = new JLabel();
//		ImageIcon icon = new ImageIcon(getClass().getResource("/Images/logo.png")); 
//		logo.setIcon(icon);
//		logo.setSize(150,150);
//		logo.setLocation(65,-10);
//		add(logo);

		JLabel title = new JLabel("Image Tool");
		title.setSize(200,200);
		title.setLocation(21,-20);
		title.setFont(defaultFont2);
		add(title);
		
		newX = new JTextField("");
		newX.setLocation(40,370);
		newX.setSize(45,20);
		add(newX);

		newY = new JTextField("");
		newY.setLocation(115,370);
		newY.setSize(45,20);
		add(newY);

		JLabel x = new JLabel("x");
		x.setLocation(95,365);
		x.setSize(30,30);
		x.setFont(defaultFont);
		add(x);

		//Undo & Redo buttons
		
		JPanel urBack = new JPanel();
		urBack.setLayout(null);
		urBack.setSize(180,60);
		urBack.setLocation(10,260);
		urBack.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		JLabel uR = new JLabel("Undo       -       Redo");
		uR.setLocation(25,30);
		uR.setSize(150,30);
		uR.setFont(defaultFont);
		urBack.add(uR);
		
		JButton undo = new JButton(""+(char)8630);
		undo.setSize(50,20);
		undo.setLocation(15,10);
		undo.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GUIpointer.pixelsArray = imgToolkit.takeFromHistory(GUIpointer.pixelsArray);
				updateImgInfo();
				GUIpointer.repaint();
				GUIpointer.jf.requestFocusInWindow();
			}
		});
		urBack.add(undo);

		JButton redo = new JButton(""+(char)8631);
		redo.setSize(50,20);
		redo.setLocation(115,10);
		redo.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GUIpointer.pixelsArray = imgToolkit.takeFromFuture(GUIpointer.pixelsArray);
				updateImgInfo();
				GUIpointer.repaint();
				GUIpointer.jf.requestFocusInWindow();
			}
		});
		urBack.add(redo);
	
		add(urBack);
		//
		
		JButton gray = new JButton("Grayscale");
		gray.setLocation(25,580);
		gray.setSize(150,30);
		add(gray);
		gray.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				grayscale();
			}
		});

		JButton load = new JButton("Open Image");
		load.setLocation(25,620);
		load.setSize(150,30);
		add(load);
		load.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openImage();
			}
		});

		JButton close = new JButton("Close Image");
		close.setLocation(25,660);
		close.setSize(150,30);
		add(close);
		close.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				closeImage();
			}
		});

		JButton save = new JButton("Save Image");
		save.setLocation(25,700);
		save.setSize(150,30);
		add(save);
		save.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});

		JButton saveAs = new JButton("Save Image As...");
		saveAs.setLocation(25,740);
		saveAs.setSize(150,30);
		add(saveAs);
		saveAs.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveAs();
			}
		});

		JPanel imgInfo = new JPanel();
		imgInfo.setLayout(null);
		imgInfo.setSize(180,140);
		imgInfo.setLocation(10,420);
		imgInfo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		if(new File(GUIpointer.imgUrl)!=null)
			imageFields[0]=new JLabel("Image Size :: " + nf.format((new File(GUIpointer.imgUrl)).length()/1024.0)+"kB");
		else
			imageFields[0]=new JLabel("Image Size :: " + 0.0+"kB");
		if(!GUIpointer.imgUrl.equals(""))
			imageFields[1]=new JLabel("Image Name :: " + GUIpointer.imgUrl.substring(GUIpointer.imgUrl.lastIndexOf("\\")+1,GUIpointer.imgUrl.length())+"");
		else
			imageFields[1]=new JLabel("Image Name :: " +"n/a");
		if(!GUIpointer.imgUrl.equals(""))
			imageFields[2]=new JLabel("Image Size :: " + GUIpointer.imgX+" x " + GUIpointer.imgY);
		else
			imageFields[2]=new JLabel("Image Size :: " +"n/a");
		if(!GUIpointer.imgUrl.equals(""))
			imageFields[3]=new JLabel("File Location :: " + GUIpointer.imgUrl.substring(0,GUIpointer.imgUrl.lastIndexOf("\\")));
		else
			imageFields[3]=new JLabel("File Location :: " +"n/a");

		JLabel mag = new JLabel("Zoom");
//		ImageIcon magGlass = new ImageIcon(getClass().getResource("/Images/magglass.png")); 
//		mag.setIcon(magGlass);
		mag.setSize(60,20);
		imgInfo.add(mag);
		mag.setLocation(70,113);

		JButton plus = new JButton("+");
		plus.setSize(45,20);
		plus.setLocation(10,112);
		plus.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				zoom(GUIpointer.ZOOM+20);
			}
		});
		imgInfo.add(plus);

		JButton minus = new JButton("-");
		minus.setSize(45,20);
		minus.setLocation(125,112);
		minus.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				zoom(GUIpointer.ZOOM-20);
			}
		});
		imgInfo.add(minus);

		for(int i =0; i < imageFields.length; i++)
		{
			imageFields[i].setSize(160,20);
			imageFields[i].setLocation(10,5+25*i);
			imageFields[i].setToolTipText(imageFields[i].getText());
			imgInfo.add(imageFields[i]);
		}
		add(imgInfo);

		setSize(200,height);
		setLayout(null);
		setVisible(true);
		addMouseListener(this);
	}

	public void zoom(int i) {
		if(GUIpointer.imageLoaded)
		{
			i = (i%10==0)? i:i-5;
			i = (i<5)? 5:i;
			i = (i>500)? 500:i;
			GUIpointer.ZOOM = i;
			GUIpointer.tempzoomPixelsArray = imgToolkit.zoom(GUIpointer.ZOOM, GUIpointer.pixelsArray);
			GUIpointer.repaint();
		}
		GUIpointer.jf.requestFocusInWindow();
	}

	public void update(int height){
		setSize(200,height);
	}

	public void updateImgInfo(){
		if(new File(GUIpointer.imgUrl)!=null)
			imageFields[0].setText("Image Size :: " + nf.format((new File(GUIpointer.imgUrl)).length()/1024.0)+"kB");
		else
			imageFields[0].setText("Image Size :: " + 0.0+"kB");
		if(!GUIpointer.imgUrl.equals(""))
			imageFields[1].setText("Image Name :: " + GUIpointer.imgUrl.substring(GUIpointer.imgUrl.lastIndexOf("\\")+1,GUIpointer.imgUrl.length())+"");
		else
			imageFields[1].setText("Image Name :: " +"n/a");
		if(!GUIpointer.imgUrl.equals(""))
			imageFields[2].setText("Image Size :: " + GUIpointer.imgX+" x " + GUIpointer.imgY);
		else
			imageFields[2].setText("Image Size :: " +"n/a");
		if(!GUIpointer.imgUrl.equals(""))
			imageFields[3].setText("File Location :: " + GUIpointer.imgUrl.substring(0,GUIpointer.imgUrl.lastIndexOf("\\")));
		else
			imageFields[3].setText("File Location :: " +"n/a");

		for(int i =0; i < imageFields.length; i++)
		{
			imageFields[i].setToolTipText(imageFields[i].getText());
		}
		zoom(GUIpointer.ZOOM);
		GUIpointer.jf.requestFocusInWindow();
	}

	public void openImage(){
		if(fc.showOpenDialog(GUIpointer.jf)==fc.APPROVE_OPTION)
		{
			GUIpointer.imageLoaded=true;
			GUIpointer.imgUrl=fc.getSelectedFile().toString();
			GUIpointer.imgType=fc.getSelectedFile().toString().substring(fc.getSelectedFile().toString().indexOf('.')+1,fc.getSelectedFile().toString().length());
			GUIpointer.pixelsArray=imgToolkit.loadImage(GUIpointer.imgUrl);
			GUIpointer.imgX=GUIpointer.pixelsArray[0].length/3;
			GUIpointer.imgY=GUIpointer.pixelsArray.length;
			zoom(GUIpointer.ZOOM);
			GUIpointer.repaint();
		}
		fc.cancelSelection();
		updateImgInfo();
		GUIpointer.jf.requestFocusInWindow();
	}

	public void openImage(String s){
		GUIpointer.imageLoaded=true;
		GUIpointer.imgUrl=s;
		GUIpointer.imgType=s.substring(s.indexOf('.')+1,s.length());
		GUIpointer.pixelsArray=imgToolkit.loadImage(GUIpointer.imgUrl);
		GUIpointer.imgX=GUIpointer.pixelsArray[0].length/3;
		GUIpointer.imgY=GUIpointer.pixelsArray.length;
		GUIpointer.repaint();
		zoom(GUIpointer.ZOOM);
		updateImgInfo();
		GUIpointer.jf.requestFocusInWindow();
	}

	public void save(){
		if(GUIpointer.imageLoaded)imgToolkit.saveImage(GUIpointer.pixelsArray, GUIpointer.imgX, GUIpointer.imgY, GUIpointer.imgUrl);
		updateImgInfo();
		GUIpointer.jf.requestFocusInWindow();
	}

	public void saveAs(){
		if(GUIpointer.imageLoaded&&fc.showSaveDialog(GUIpointer.jf)==fc.APPROVE_OPTION)
		{
			String tempUrl = (!fc.getSelectedFile().toString().endsWith(GUIpointer.imgType))?fc.getSelectedFile().toString()+"."+GUIpointer.imgType:fc.getSelectedFile().toString();
			imgToolkit.saveImage(GUIpointer.pixelsArray, GUIpointer.imgX, GUIpointer.imgY, tempUrl);
		}
		updateImgInfo();
		GUIpointer.jf.requestFocusInWindow();
	}

	public void closeImage(){
		GUIpointer.imageLoaded=false;
		GUIpointer.imgUrl="";
		GUIpointer.imgType="";
		GUIpointer.pixelsArray=null;
		GUIpointer.imgX=0;
		GUIpointer.imgY=0;
		GUIpointer.repaint();
		updateImgInfo();
		GUIpointer.jf.requestFocusInWindow();
		GUIpointer.ZOOM=100;
	}

	public void grayscale(){
		GUIpointer.pixelsArray=imgToolkit.grayscale(GUIpointer.pixelsArray);
		updateImgInfo();
		GUIpointer.repaint();
		GUIpointer.jf.requestFocusInWindow();
		imgToolkit.addToFuture(imgToolkit.replicateArray(GUIpointer.pixelsArray));
	}
	
	public void resizeImage(int x, int y){
		imgToolkit.addToHistory(imgToolkit.replicateArray(GUIpointer.pixelsArray));
		if(GUIpointer.pixelsArray!=null&&GUIpointer.imageLoaded)
		{
			GUIpointer.pixelsArray=imgToolkit.resizeImage(GUIpointer.pixelsArray,x,y);
			GUIpointer.imgX = x;
			GUIpointer.imgY = y;
		}
		updateImgInfo();
		GUIpointer.jf.requestFocusInWindow();
		GUIpointer.repaint();
		imgToolkit.addToFuture(imgToolkit.replicateArray(GUIpointer.pixelsArray));
	}

	private int testNewWidth(String s){
		int i = 0;
		try{
			i=Integer.parseInt(s);
		}catch(Exception e){return GUIpointer.imgX;}
		if(i > 10000 && i > 0)return GUIpointer.imgX;
		return i;
	}

	private int testNewHeight(String s){
		int i = 0;
		try{
			i=Integer.parseInt(s);
		}catch(Exception e){return GUIpointer.imgY;}
		if(i > 10000 && i > 0)return GUIpointer.imgY;
		return i;
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		GUIpointer.jf.requestFocusInWindow();
		GUIpointer.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		GUIpointer.jf.requestFocusInWindow();
		GUIpointer.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		GUIpointer.jf.requestFocusInWindow();
		GUIpointer.repaint();
	}

}
