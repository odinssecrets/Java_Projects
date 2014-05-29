import javax.swing.ImageIcon;


public class buzzsaw extends block {
	static int count = 0;
	ImageIcon b1 = new ImageIcon(getClass().getResource("SMB_Buzz_saw.png"));
	ImageIcon b2 = new ImageIcon(getClass().getResource("SMB_Buzz_saw2.png"));
	public buzzsaw(int xPos, int yPos)
	{
		img = b1;
		if(count%2==0)
			swapImage();
		type = "platform";
		x = xPos; y = yPos;
		count++;
	}
	public void swapImage() {
		// TODO Auto-generated method stub
		if(img == b1)
			img= b2;
		else
			img = b1;
	}

}
