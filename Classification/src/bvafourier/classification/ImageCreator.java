import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

public class ImageCreator {
	
	
	
	public static ArrayList<Image> createAllImages(int magOrPhase, int colorChannel) throws Exception{
		ArrayList<Image> images;
		HashSet<Integer> patientIDs;
		String csvFile = "patientmapping.csv";
		BufferedReader br = null;
		String line = "";
		br = new BufferedReader(new FileReader(csvFile));
		patientIDs = new HashSet<Integer>();
		while ((line = br.readLine()) != null) {
				String[] img = line.split(";");	
				if(!patientIDs.contains(Integer.parseInt(img[1])));
					patientIDs.add(Integer.parseInt(img[1]));
		}
		br.close();
		images = new ArrayList<Image>();
		for(Integer i : patientIDs){
			images.addAll(createImages(i,magOrPhase,colorChannel));
			System.out.println("added patientID: "+i);
		}
		return images;
	}
	
	public static ArrayList<Image> createImages(int patientID,int magOrPhase, int colorChannel) throws Exception{
		ArrayList<Image> images = new ArrayList<Image>();
		String select;
		select = "SELECT DISTINCT ON (imagename,magorphase,colorchannel) * from patientdata where patientid="+patientID+
				 "AND magorphase="+magOrPhase+" AND colorchannel="+colorChannel+";";
		Connection conn = DBConnector.getConnection();
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(select);
		while (rs.next()){
			Image image = new Image();
			image.setPatientID(rs.getInt(1));
			image.setImgName(rs.getString(2));
			image.setColorChannel(rs.getInt(3));
			image.setMagOrPhase(rs.getInt(4));
			image.setPatternClass(rs.getInt(5));
			if (image.getPatternClass() < 2)
				image.setPatternClass(0);
			else if (image.getPatternClass() < 5)
				image.setPatternClass(1);
			if (image.getPatternClass() == 5)
				image.setPatternClass(2);
			Array test = rs.getArray(6);
			image.setImage((Double[][])test.getArray());
			images.add(image);
		}      
		return images;
	}
	
	public static ArrayList<Image> createClassImage(int patientID,int magOrPhase, int colorChannel , int pclass) throws Exception{   
		ArrayList<Image> images = new ArrayList<Image>();
		String select;
		select = "SELECT * from patientdata where patientid="+patientID+
				 "AND magorphase="+magOrPhase+" AND colorchannel="+colorChannel+";";
		Connection conn = DBConnector.getConnection();
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(select);
		while (rs.next()){
			Image image = new Image();
			image.setPatientID(rs.getInt(1));
			image.setImgName(rs.getString(2));
			image.setColorChannel(rs.getInt(3));
			image.setMagOrPhase(rs.getInt(4));
			image.setPatternClass(rs.getInt(5));
			if (image.getPatternClass() < 2)
				image.setPatternClass(0);
			else if (image.getPatternClass() < 5)
				image.setPatternClass(1);
			if (image.getPatternClass() == 5)
				image.setPatternClass(2);
			Array test = rs.getArray(6);
			image.setImage((Double[][])test.getArray());
			if (image.getPatternClass() == pclass)
				images.add(image);
		}      
		return images;
	}
	
}
