import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				ArrayList<Image> allImages = ImageCreator.createAllImages(i, j);
				System.out.println(allImages.get(0).getColorChannel());
				System.out.println("Images: " + allImages.size());
				System.out.println("Duration ImageCreator("+i+"/"+j+"): "+ Math.round((System.currentTimeMillis() - start) / 1000)+ "sec");
				start = System.currentTimeMillis();
				Classifier c;
				c = new Classifier(allImages);
				c.classify(0, 5);
				c.classify(1, 5);
				c.classify(0, 10);
				c.classify(1, 10);
				c.classify(0, 15);
				c.classify(1, 15);
				c.classify(0, 20);
				c.classify(1, 20);
				c.classify(0, 25);
				c.classify(1, 25);
				System.out.println("Duration Classifier("+i+"/"+j+"): "+ Math.round((System.currentTimeMillis() - start) / 1000)+ "sec");
			}
		}
	}
}
