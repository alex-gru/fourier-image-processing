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
//		images.addAll(createImages(1,magOrPhase,colorChannel));
//		images.addAll(createImages(2,magOrPhase,colorChannel));
		return images;
	}
	
	public static ArrayList<Image> createImages(int patientID,int magOrPhase, int colorChannel) throws Exception{
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
			Array test = rs.getArray(6);
			image.setImage((Double[][])test.getArray());
			images.add(image);
		}      
		return images;
	}
	
}
