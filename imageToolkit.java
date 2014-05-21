import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class imageToolkit {

	ArrayList<historyImages> history = new ArrayList<historyImages>();
	ArrayList<historyImages> future = new ArrayList<historyImages>();

	/**
	 * Debugging only
	 * @param pixelsArray
	 */
	//Print out the pixels in the array of RGB values
	public void printPixels(int[][] pixelsArray) {
		// TODO Auto-generated method stub
		for(int i = 0; i < pixelsArray.length; i++)
		{
			for(int j = 0; j < pixelsArray[i].length; j+=3)
			{
				System.out.print("(" + pixelsArray[i][j] + ","+ pixelsArray[i][j+1] +","+pixelsArray[i][j+2] + ")");
			}
			System.out.println("\n|-----------------------------------------------------------------------------------------------------------------------|");
		}
	}

	/**
	 * Debugging only
	 * @param pixelsArray
	 */
	//Print out the bytes in the mainImage
	public void printBytes(byte [] b, BufferedImage mainImage) {
		// TODO Auto-generated method stub
		for(int i = 0; i < (mainImage.getHeight()); i++)
		{
			for(int j = 0; j < mainImage.getWidth()*3; j+=3){
				System.out.print("(" + b[i*(mainImage.getHeight())+j]+","+ b[i*(mainImage.getHeight())+j]+","+ b[i*(mainImage.getHeight())+j+2] + ")");
			}
			System.out.println("");
			System.out.println("\n|-----------------------------------------------------------------------------------------------------------------------|");
			break;
		}
	}

	//Duplicate an array without the same pointer
	public int[][] replicateArray(int [] [] x){
		int [] [] temp = new int[x.length][x[0].length];
		for(int i = 0; i < x.length; i ++)
		{
			for(int j = 0; j < x[0].length; j++){
				temp[i][j] = x[i][j];
			}
		}
		return temp;
	}

	public int[][] grayscale(int[][] pixelsArray){
		addToHistory(replicateArray(pixelsArray));
		for(int i = 0; i < pixelsArray.length; i++)
		{
			for(int j = 0; j < pixelsArray[i].length; j+=3)
			{
				int avg = (pixelsArray[i][j]+pixelsArray[i][j+1]+pixelsArray[i][j+2])/3;
				pixelsArray[i][j] = avg;
				pixelsArray[i][j+1] = avg;
				pixelsArray[i][j+2] = avg;
			}
		}
		return pixelsArray;
	}

	//

	public void noResetaddToHistory(int[][] pixelsArray) {
		if(history.size()==0 || !(checkEquals(pixelsArray,history.get(history.size()-1).hist_img)))
		{
			history.add(new historyImages(pixelsArray,0));
		}
	}

	public void addToHistory(int[][] pixelsArray) {
		if(history.size() == 0 || !(checkEquals(pixelsArray,history.get(history.size()-1).hist_img)))
		{
			history.add(new historyImages(pixelsArray,0));
			resetFuture();
		}
	}

	public void addToFuture(int[][] pixelsArray) {
		if(future.size() == 0 || !(checkEquals(pixelsArray,future.get(future.size()-1).hist_img)))
		{
			future.add(new historyImages(pixelsArray,1));
		}
	}

	//
	
	public int[][] takeFromFuture(int [] [] img) {
		try{
			noResetaddToHistory(img);
			int [] [] temp = future.get(future.size()-1).hist_img;
			future.remove(future.size()-1);
			return temp;
		}catch(Exception e){
		}
		return img;
	}

	public int[][] takeFromHistory(int [] [] img) {
		try{
			addToFuture(img);
			int [] [] temp = history.get(history.size()-1).hist_img;
			history.remove(history.size()-1);
			return temp;
		}catch(Exception e){
		}
		return img;
	}

	private void resetFuture() {
		future = new ArrayList<historyImages>();
	}

	private boolean checkEquals(int[][] pixelsArray, int[][] hist_img) {
		// TODO Auto-generated method stub
		int x = (pixelsArray.length>hist_img.length)? pixelsArray.length:hist_img.length;
		int y = (pixelsArray[0].length>hist_img[0].length)? pixelsArray[0].length:hist_img[0].length;
		for(int i = 0; i < x; i++){
			for(int j = 0; j < y; j++){
				if(pixelsArray[i][j] != hist_img[i][j]){
					return false;
				}
			}
		}
		return true;
	}

	//Return a pixel at point (xM, yM)
	/*

	public String getPixel(int SIZE, int X, int Y, JFrame jf, int xM, int yM, int[][] pixelsArray){
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
		return "";
	}

	 */

	//Make a byte array from a 2d array of RGB values
	public byte[] toBytes(int[][] resizeImage) {
		byte [] bytes = new byte[resizeImage.length*resizeImage[0].length];
		int counter = 0;
		for(int i = 0; i < resizeImage.length;i ++)
			for(int j = 0; j < resizeImage[0].length; j++)
			{
				bytes[counter] = (byte) resizeImage[i][j];
				counter++;
			}
		return bytes;
	}

	//Make an image from an array of bytes
	public BufferedImage createImageFromBytes(byte[] imageData) {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		try {
			return ImageIO.read(bais);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] createByteArray(int[][] x){
		byte[] b = new byte[x.length*x[0].length];
		int l = 0;
		for(int i = 0; i < x.length;i++)
		{
			for(int j = 0; j < x[0].length;j+=3)
			{
				b[l]=(byte)x[i][j];
				l++;
			}
		}
		return b;
	}

	//Take a 2d array of RGB values and convert to a buffered image
	public BufferedImage makeNewImage(BufferedImage b, int[][] resizeImage) {
		for(int i = 0; i < resizeImage.length;i++)
		{
			for(int j = 0; j < resizeImage[0].length;j+=3)
			{
				int red = resizeImage[i][j];
				int green = resizeImage[i][j+1];
				int blue = resizeImage[i][j+2];
				int rgbInt = blue;
				rgbInt = (rgbInt << 8) + green;
				rgbInt = (rgbInt << 8) + red;
				b.setRGB(j/3,i,rgbInt);
			}
		}
		return b;
	}

	public int imageHeight(int [] [] pixelsArray, int SIZE) {
		// TODO Auto-generated method stub
		return (int) (pixelsArray.length*getSize(SIZE));
	}

	public int imageWidth(int [] [] pixelsArray, int SIZE) {
		// TODO Auto-generated method stub
		return (int) (pixelsArray[0].length*getSize(SIZE));
	}

	public double getSize(int size) {
		// TODO Auto-generated method stub
		if(size > 0)return size;
		else
		{
			size = Math.abs(size);
			return Math.pow(size, -1);
		}
	}

	public int getRGBVal(int i) {
		// TODO Auto-generated method stub
		if(i<0)
			i = i+255;
		return i;
	}

	public int[][] resizeImage(int pixelsArray[][], int newX, int newY){
		try{
			if(newX==pixelsArray[0].length/3&&newY==pixelsArray.length)
				return pixelsArray;
			int [] [] newImage = new int[newY][newX*3];
			int xleftovers,yleftovers;
			int yextra=0,xextra=0;
			int ymult = (int)(newY/pixelsArray.length);
			int xmult = ((newX*3)/pixelsArray[0].length);
			if(newY%pixelsArray.length!=0){
				try{yleftovers = ((pixelsArray.length/((newY)%pixelsArray.length))%1.0!=0)? ((pixelsArray.length/((newY)%pixelsArray.length))):((pixelsArray.length/((newY)%pixelsArray.length))+1);}
				catch(ArithmeticException e){yleftovers = 1;};
				yextra=0;
			}
			else{
				yleftovers=0;
			}
			if(newX%(pixelsArray[0].length/3)!=0){
				xextra=0;
				try{xleftovers = ((pixelsArray[0].length/((newX*3)%pixelsArray[0].length))%1.0!=0)? (pixelsArray[0].length/((newX*3)%pixelsArray[0].length)):(pixelsArray[0].length/((newX*3)%pixelsArray[0].length))+1;}
				catch(ArithmeticException e){xleftovers = 1;};
			}else{
				xleftovers=0;
			}
			int ypos =0, xpos=0;
			for(int yaxis= 0; yaxis <pixelsArray.length; yaxis++)
			{
				if(yleftovers!=0&&yaxis%yleftovers==0)
					ymult+=1;
				if(yaxis==pixelsArray.length-1)
				{
					yextra=newY-(ypos+ymult);
				}
				if(newY==pixelsArray.length)
					ymult = 1;
				for(int yaxismult = 0; yaxismult < ymult; yaxismult++)
				{
					for(int xaxis= 0; xaxis < pixelsArray[0].length; xaxis+=3)
					{
						if(xleftovers!=0&&(xaxis/3)%xleftovers==0)
							xmult+=1;			
						if(newX*3==pixelsArray[0].length)
							xmult = 1;
						if(xaxis/3==pixelsArray[0].length/3-1)
						{
							xextra=(newX)-((xpos/3)+xmult);
						}
						for(int xaxismult = 0; xaxismult < xmult; xaxismult++)
						{
							xpos+=3;
						}
						if(xleftovers!=0&&(xaxis/3)%xleftovers==0)
							xmult-=1;
					}
					xpos = 0;
					ypos++;
				}
				if(yleftovers!=0&&yaxis%yleftovers==0)
					ymult-=1;	
			}

			if(pixelsArray.length==newY){
				ymult=1;
				yextra=0;
			}
			else if(pixelsArray[0].length/3==newX)
			{
				xmult = 1;
				xextra=0;
			}
			ypos=0;
			if(yextra<0)yextra=0;
			if(xextra<0)xextra=0;
			int constXExtra = xextra;
			ymult = (int)(newY/pixelsArray.length);
			xmult = ((newX*3)/pixelsArray[0].length);

			//System.out.println(xextra + " = xextra ::  " + yextra + " = yextra");
			int xtrXpos = (xextra!=0)?(pixelsArray[0].length/(xextra*3)):0;
			int xtrYpos = (yextra!=0)?(pixelsArray.length/yextra):0;

			for(int yaxis= 0; yaxis <pixelsArray.length; yaxis++)
			{
				if(yleftovers!=0&&yaxis%yleftovers==0)
					ymult+=1;
				if(yextra>0&&xtrYpos!=0&&(yaxis)%xtrYpos==0)
				{
					ymult+=1;
					yextra-=1;
				}
				if(yextra>0&&yaxis==pixelsArray.length-1){
					//System.out.println("final extra y = " + yextra);
					ymult+=yextra;
				}
				if(newY==pixelsArray.length)
					ymult = 1;
				for(int yaxismult = 0; yaxismult < ymult; yaxismult++)
				{
					for(int xaxis= 0; xaxis < pixelsArray[0].length; xaxis+=3)
					{
						if(xleftovers!=0&&(xaxis/3)%xleftovers==0)
							xmult+=1;	
						if(xextra>0&&xaxis==pixelsArray[0].length-3)
						{
							xmult+=xextra;
						}
						else if(xextra>0&&xtrXpos!=0&&(xaxis/3)%xtrXpos==0)
						{
							xmult+=1;
							xextra-=1;
						}
						if(newX*3==pixelsArray[0].length)
							xmult = 1;

						for(int xaxismult = 0; xaxismult < xmult; xaxismult++)
						{
							if(xpos+2<newImage[0].length&&xaxis+2<pixelsArray[0].length
									&& ypos<newImage.length&&yaxis<pixelsArray.length)
							{
								newImage[ypos][xpos] = pixelsArray[yaxis][xaxis];
								newImage[ypos][xpos+1] = pixelsArray[yaxis][xaxis+1];
								newImage[ypos][xpos+2] = pixelsArray[yaxis][xaxis+2];
							}
							xpos+=3;
						}
						if(xextra>0&&xaxis==pixelsArray[0].length-3)
							xmult-=xextra;
						if(xextra>=0&&xtrXpos!=0&&(xaxis/3)%xtrXpos==0){
							xmult-=1;
							if(xextra==0)xextra = -1;
						}
						if(xleftovers!=0&&(xaxis/3)%xleftovers==0)
							xmult-=1;
					}
					xpos = 0;
					xextra = constXExtra;
					ypos++;
				}
				if(yleftovers!=0&&yaxis%yleftovers==0)
					ymult-=1;					
				if(yextra>=0&&xtrYpos!=0&&(yaxis)%xtrYpos==0){
					ymult-=1;
					if(yextra==0)yextra = -1;
				}

			}
			return newImage;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			//resizeImage(pixelsArray,newX, newY);
			JOptionPane.showMessageDialog(null, "Error resizing image!");
			e.printStackTrace();
		}
		return pixelsArray;
	}

	public void saveImage(int [][] img, int x, int y, String url){
		BufferedImage b = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
		b = makeNewImage(b, resizeImage(img, b.getWidth(),b.getHeight()));
		try {
			File outputfile = new File(url);
			outputfile.createNewFile();
			ImageIO.write(b, url.substring(url.indexOf('.')+1,url.length()), outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int[][] loadImage(String url){
		BufferedImage mainImage = null;
		try {
			mainImage = ImageIO.read(new File(url));
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte [] pixels = ((DataBufferByte) mainImage.getRaster().getDataBuffer()).getData();

		int [][] temp = new int[(mainImage.getHeight())][mainImage.getWidth()*3];
		int count = 0;
		for(int i = 0; i < (mainImage.getHeight()); i++)
		{
			for(int j = 0; j < mainImage.getWidth()*3; j+=3){
				for(int k =j; k < j+3; k++){
					temp[i][k] = getRGBVal(pixels[count]);
					count++;
				}
			}
		}
		history = new ArrayList<historyImages>();
		future = new ArrayList<historyImages>();
		return temp;
	}

	public int[][] zoom(int i, int[][] tempzoom) {
		//System.out.println((int)((tempzoom[0].length/3))+","+(int)(tempzoom.length));
		//System.out.println((int)((tempzoom[0].length/3)*(i/100.0))+","+(int)(tempzoom.length*(i/100.0)));
		return resizeImage(tempzoom,(int)((tempzoom[0].length/3)*(i/100.0)),(int)(tempzoom.length*(i/100.0)));
	}

}
