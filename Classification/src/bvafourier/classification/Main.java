import java.io.File;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws Exception {
		deleteOldFiles();
		Classifier c;
		long start;
		int [] k = {1,2,4,8,16,32};
		//magnitude and phase
		for (int i = 0; i < 2; i++) {
			//color channels
			for (int j = 0; j < 6; j++) {
				start = System.currentTimeMillis();
				ArrayList<Image> Images = ImageCreator.createAllImages(i, j);
				System.out.println(Images.get(0).getColorChannel());
				System.out.println("Duration ImageCreator("+i+"/"+j+"): "+ Math.round((System.currentTimeMillis() - start) / 1000)+ "sec");		
				start = System.currentTimeMillis();	
				c = new Classifier(Images);				
				c.classify(k, 0, 2);
				c.classify(k, 0, 4);
				c.classify(k, 0, 8);
				c.classify(k, 0, 16);
//				c.classify(k, 0, 32);
				c.classify(k, 1, 2);
				c.classify(k, 1, 4);
				c.classify(k, 1, 8);
				c.classify(k, 1, 16);
//				c.classify(k, 1, 32);			
				c.classify(k, 2, 0);
//				c.classify(k, 3, 0);
				System.out.println("Duration Classifier("+i+"/"+j+"): "+ Math.round((System.currentTimeMillis() - start) / 1000)+ "sec");
			}
		}
	}
	
	private static void deleteOldFiles(){
		File file = new File("square_2.csv");
		file.delete();
		file = new File("square_4.csv");
		file.delete();
		file = new File("square_8.csv");
		file.delete();
		file = new File("square_16.csv");
		file.delete();
		file = new File("fullSquare_2.csv");
		file.delete();
		file = new File("fullSquare_4.csv");
		file.delete();
		file = new File("fullSquare_8.csv");
		file.delete();
		file = new File("fullSquare_16.csv");
		file.delete();
		file = new File("wedges.csv");
		file.delete();
		file = new File("wedge.csv");
		file.delete();
	}
}
