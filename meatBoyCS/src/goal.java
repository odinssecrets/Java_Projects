import javax.swing.ImageIcon;


public class goal extends platform {

	public goal(int xPos, int yPos) {
		super(xPos, yPos);
		type="goal";
		img=new ImageIcon(getClass().getResource("bandage_girl.png"));
		// TODO Auto-generated constructor stub
	}

}
