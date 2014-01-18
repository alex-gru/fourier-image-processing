import java.io.File;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws Exception {
		deleteOldFiles();
		Classifier c = null;
		long start;
		int [] k = {1,2,4,8,16,32};
		//magnitude and phase
		for (int i = 0; i < 2; i++) {
			//color channels
			for (int j = 0; j < 6; j++) {
				
				start = System.currentTimeMillis();
				ArrayList<Image> Images = ImageCreator.createAllImages(i, j);
				if (i==0 && j==0){
					c = new Classifier(Images);
				}else{
					c.setImages(Images);
				}
				System.out.println(Images.get(0).getColorChannel());
				System.out.println("Duration ImageCreator("+i+"/"+j+"): "+ Math.round((System.currentTimeMillis() - start) / 1000)+ "sec");		
				start = System.currentTimeMillis();				
				c.classify(k, 0, 2);
				c.classify(k, 0, 4);
				c.classify(k, 0, 8);
				c.classify(k, 0, 16);
				c.classify(k, 0, 32);
				c.classify(k, 1, 2);
				c.classify(k, 1, 4);
				c.classify(k, 1, 8);
				c.classify(k, 1, 16);
				c.classify(k, 1, 32);			
				c.classify(k, 2, 0);
				c.classify(k, 3, 0);
				System.out.println("Duration Classifier("+i+"/"+j+"): "+ Math.round((System.currentTimeMillis() - start) / 1000)+ "sec");
			}
		}
	}
	
	private static void deleteOldFiles(){
		File file = new File("square.csv");
		file.delete();
		file = new File("fullSquare.csv");
		file.delete();
		file = new File("wedges.csv");
		file.delete();
		file = new File("wedge.csv");
		file.delete();
	}
}
