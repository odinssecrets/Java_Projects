import javax.swing.ImageIcon;


public class platform extends block 
{
	public platform(int xPos, int yPos)
	{
		img = new ImageIcon(getClass().getResource("platformUp.png"));
		type = "platform";
		x = xPos; y = yPos;
	}
}
