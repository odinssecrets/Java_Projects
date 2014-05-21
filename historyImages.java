
public class historyImages {

	int undoRedo = 0;
	int [] [] hist_img;
	
	public historyImages(int [][]tempImg, int i){
		hist_img = tempImg;
		undoRedo = i;
	}
		
}
