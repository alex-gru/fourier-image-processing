import java.util.ArrayList;


public class Main {

	public static void main(String[] args) {
		ArrayList<Image> allImages = ImageCreater.createAllImages();
		Classifier c;
		c = new Classifier(allImages,Integer.parseInt(args[0]));
		c.classify();
	}
}
