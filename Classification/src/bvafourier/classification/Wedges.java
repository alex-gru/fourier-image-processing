import java.io.BufferedReader;
import java.util.ArrayList;

/*****************************************************************************************************************************
 * Calculate Feature Vector of Fourier transformed picture using 'wedges' (dt. 'Kuchenstuecke')
 * @author schusterpa@stud.sbg.ac.at
 *
 * explanation: square is separated into 4 quarters: top left square (tls), top right square (trs), ...
 * 
 * (# represents a pixel)
 * for instance the tls is then separated into two halfs. since I can't really separate a single pixel, I have to either round
 * up or down. my convention: round up: 	tls's lower part, trs's lower part, bls's lower part, brs's lower part
 * 							  round down:	tls's upper part, trs's upper part, bls's upper part, brs's upper part
 * example:
 * tls:		==> becomes ==>		(lower part rounded up)		and (upper part rounded down)
 * ####							#								-###
 * ####							##								--##
 * ####							###								---#
 * ####							####							----
 * 
 * then, the opposite halfs (either rounded up or down) form a coordinate of the feature vector (fv has length 4)
 * --
 * sample input: 8x8 matrix with matrix[0][0]=1, ... matrix[7][7]=64 (consecutively numbered)
 * results in fv = (32.5,32.5,32.5,32.5)
 * 
 * not only are the image means stored, the same process is also done for image variance and standard degression.
 * mean, variance and degression are stored in arraylists which are in turn stored in another arraylist (arraylist of arraylist)
 * called 'allFeatures'.
 ****************************************************************************************************************************/
public class Wedges {
	
	static Double[][] testpicture = new Double[8][8];
	
	/*** squares ***/
	static Double[][] topLeftSquare, topRightSquare, bottomLeftSquare, bottomRightSquare;
	
	/*** image means (Mittelwerte) ***/
	static Double tlsHalfSum_ru = 0.0;	// top left square half sum rounded up
	static Double trsHalfSum_ru = 0.0;
	static Double blsHalfSum_ru = 0.0;
	static Double brsHalfSum_ru = 0.0;
	
	static Double tlsHalfSum_rd = 0.0;	// top left square half sum rounded down
	static Double trsHalfSum_rd = 0.0;
	static Double blsHalfSum_rd = 0.0;
	static Double brsHalfSum_rd = 0.0;
	
	/*** image variance (Varianz = Standardabweichung zum Quadrat) ***/
	static Double tlsHalfSum_ru_v = 0.0;	// v = variance
	static Double trsHalfSum_ru_v = 0.0;
	static Double blsHalfSum_ru_v = 0.0;
	static Double brsHalfSum_ru_v = 0.0;
	
	static Double tlsHalfSum_rd_v = 0.0;
	static Double trsHalfSum_rd_v = 0.0;
	static Double blsHalfSum_rd_v = 0.0;
	static Double brsHalfSum_rd_v = 0.0;
	
	/*** FV coordinates ***/
	static Double mean_coord1 = 0.0, variance_coord1 = 0.0;
	static Double mean_coord2 = 0.0, variance_coord2 = 0.0;
	static Double mean_coord3 = 0.0, variance_coord3 = 0.0;
	static Double mean_coord4 = 0.0, variance_coord4 = 0.0;
	
	static int elemsInRoundedUpHalf = 0;	// nr of elements in a rounded up half (i.e. half of a square)
	static int elemsInRoundedDownHalf = 0;
	
	/*** FV ArrayList ***/
	static ArrayList<ArrayList<Double>> allFeatures = new ArrayList<ArrayList<Double>>();
	static ArrayList<Double> meanFeatures = new ArrayList<Double>();
	static ArrayList<Double> varianceFeatures = new ArrayList<Double>();
	static ArrayList<Double> standardDegressionFeatures = new ArrayList<Double>();
	
	// dummy input
	private static void fillPicture(){
//		int counter = 1;
//		for(int i = 0; i < testpicture[0].length; i++){
//			for(int j = 0; j < testpicture[0].length; j++){
//				testpicture[i][j] = counter;	// 8x8 matrix
//				counter++;
//			}
//		}
	}

	public static void main(String[] args){
		fillPicture();
		extract(testpicture);
	}
	
	public static ArrayList<ArrayList<Double>> extract(Double[][] input){
		fillSquares(input);
		long start = System.currentTimeMillis();
		calcMeanCoordinates(topLeftSquare, bottomRightSquare, topRightSquare, bottomLeftSquare);	// FV consisting of image means
		mean_coord1 = (tlsHalfSum_ru + brsHalfSum_ru) / (elemsInRoundedUpHalf*2);
		mean_coord2 = (trsHalfSum_ru + blsHalfSum_ru) / (elemsInRoundedUpHalf*2);
		mean_coord3 = (tlsHalfSum_rd + brsHalfSum_rd) / (elemsInRoundedDownHalf*2);
		mean_coord4 = (trsHalfSum_rd + blsHalfSum_rd) / (elemsInRoundedDownHalf*2);
		//printMeanFV();
		calcVarianceCoordinates(topLeftSquare, bottomRightSquare, topRightSquare, bottomLeftSquare);
		variance_coord1 = (tlsHalfSum_ru_v + brsHalfSum_ru_v) / (elemsInRoundedUpHalf*2-1);
		variance_coord2 = (trsHalfSum_ru_v + blsHalfSum_ru_v) / (elemsInRoundedUpHalf*2-1);
		variance_coord3 = (tlsHalfSum_rd_v + brsHalfSum_rd_v) / (elemsInRoundedDownHalf*2-1);
		variance_coord4 = (trsHalfSum_rd_v + blsHalfSum_rd_v) / (elemsInRoundedDownHalf*2-1);
		//printVarianceFV();
		//printStandardDegressionFV();
		addToArrayList(meanFeatures, mean_coord1, mean_coord2, mean_coord3, mean_coord4);
		addToArrayList(varianceFeatures, variance_coord1, variance_coord2, variance_coord3, variance_coord4);
		addToArrayList(standardDegressionFeatures, Math.sqrt(variance_coord1), Math.sqrt(variance_coord2), Math.sqrt(variance_coord3), Math.sqrt(variance_coord4));
		allFeatures.add(meanFeatures);
		allFeatures.add(varianceFeatures);
		allFeatures.add(standardDegressionFeatures);
		long stop = System.currentTimeMillis();
		System.out.println(" \n ** FV extraction took approx. " + (stop - start) + " ms. **");
		return allFeatures;
	}
	
	private static void addToArrayList(ArrayList<Double> al, Double coord1, Double coord2, Double coord3, Double coord4){
		al.add(coord1);
		al.add(coord2);
		al.add(coord3);
		al.add(coord4);
	}
	
	private static void printMeanFV(){
		System.out.println("\n IMAGE MEAN FEATURE VECTOR:");
		System.out.println("coord. 1 = " + mean_coord1);
		System.out.println("coord. 2 = " + mean_coord2);
		System.out.println("coord. 3 = " + mean_coord3);
		System.out.println("coord. 4 = " + mean_coord4);
	}
	
	private static void printVarianceFV(){
		System.out.println("\n IMAGE VARIANCE FEATURE VECTOR:");
		System.out.println("coord. 1 = " + variance_coord1);
		System.out.println("coord. 2 = " + variance_coord2);
		System.out.println("coord. 3 = " + variance_coord3);
		System.out.println("coord. 4 = " + variance_coord4);
	}
	
	private static void printStandardDegressionFV(){
		System.out.println("\n IMAGE STANDARD DEGRESSION FEATURE VECTOR:");
		System.out.println("coord. 1 = " + Math.sqrt(variance_coord1));
		System.out.println("coord. 2 = " + Math.sqrt(variance_coord2));
		System.out.println("coord. 3 = " + Math.sqrt(variance_coord3));
		System.out.println("coord. 4 = " + Math.sqrt(variance_coord4));
	}
	
	private static void fillSquares(Double[][] pic){
		topLeftSquare = fillTLS(pic); // TLS (= top left square)
		topRightSquare = fillTRS(pic);
		bottomLeftSquare = fillBLS(pic);
		bottomRightSquare = fillBRS(pic);
	}
	
	private static void calcMeanCoordinates(Double[][] tls, Double[][] brs, Double[][] trs, Double[][] bls){
		calcTLShalfSum_ru(tls);	// coordinate 1
		calcBRShalfSum_ru(brs);
		calcTRShalfSum_ru(trs);	// coordinate 2
		calcBLShalfSum_ru(bls);
	}
	
	private static void calcTLShalfSum_ru(Double[][] tls){
		boolean[][] map = new boolean[tls[0].length][tls[0].length];
		int zeilen = tls[0].length;
		int reihen = zeilen;
		int cnt = 0;
		for(int i = 0; i < zeilen; i++){
			int j = 0;
			while(j < reihen){
				tlsHalfSum_ru += tls[i][j];
				map[i][j] = true;
				if(j == cnt)
					break;
				j++;
			}
			cnt++;
		}
		//System.out.println("###tlsHalfSum_ru = " + tlsHalfSum_ru);
		calcElemsInRoundedUpHalf(map);
		calcElemsInRoundedDownHalf(tls[0].length * tls[0].length);
		
		calcShalfSum_rd(tls, map, "tlsHalfSum_rd");	// coordinate 3
		//System.out.println("---tlsHalfSum_rd = " + tlsHalfSum_rd);
	}
	
	private static void calcBRShalfSum_ru(Double[][] brs){
		boolean[][] map = new boolean[brs[0].length][brs[0].length];
		int zeilen = brs[0].length;
		int reihen = zeilen;
		int cnt = 0;
		int j = 0;
		for(int i = 0; i < zeilen; i++){
			while(j < reihen){
				brsHalfSum_ru += brs[i][j];
				map[i][j] = true;
				j++;
			}
			cnt++;
			j = cnt;
		}
		//System.out.println("###brsHalfSum_ru = " + brsHalfSum_ru);
		calcShalfSum_rd(brs, map, "brsHalfSum_rd");	// coordinate 3
		//System.out.println("---brsHalfSum_rd = " + brsHalfSum_rd);
	}
	
	private static void calcTRShalfSum_ru(Double[][] trs){
		boolean[][] map = new boolean[trs[0].length][trs[0].length];
		int zeilen = trs[0].length;
		int reihen = zeilen;
		int cnt = trs[0].length-2;
		for(int i = 0; i < zeilen; i++){
			int j = trs[0].length-1;
			while(j > cnt){
				trsHalfSum_ru += trs[i][j];
				map[i][j] = true;
				j--;
			}
			cnt--;
		}
		//System.out.println("###trsHalfSum_ru = " + trsHalfSum_ru);
		calcShalfSum_rd(trs, map, "trsHalfSum_rd");	// coordinate 4
		//System.out.println("---trsHalfSum_rd = " + trsHalfSum_rd);
	}
	
	private static void calcBLShalfSum_ru(Double[][] bls){
		boolean[][] map = new boolean[bls[0].length][bls[0].length];
		int zeilen = bls[0].length;
		int reihen = zeilen;
		int cnt = bls[0].length-2;
		for(int i = 0; i < zeilen; i++){
			int j = 0;
			while(j < reihen){
				blsHalfSum_ru += bls[i][j];
				map[i][j] = true;
				j++;
			}
			reihen--;
		}
		//System.out.println("###blsHalfSum_ru = " + blsHalfSum_ru);
		calcShalfSum_rd(bls, map, "blsHalfSum_rd");	// coordinate 4
		//System.out.println("---blsHalfSum_rd = " + blsHalfSum_rd);
	}
	
	private static void calcElemsInRoundedUpHalf(boolean[][] map){
		for(int i = 0; i< map[0].length; i++){
			for(int j = 0; j < map[0].length; j++){
				if(map[i][j] == true)
					elemsInRoundedUpHalf++;
			}
		}
	}
	
	private static void calcShalfSum_rd(Double[][] square, boolean[][] map, String name){	// TODO get rid of code duplication!
		if(name.equals("tlsHalfSum_rd")){
			for(int i = 0; i< square[0].length; i++){
				for(int j = 0; j < square[0].length; j++){
					if(map[i][j] != true)
						tlsHalfSum_rd += square[i][j];
				}
			}
		}
		else if(name.equals("trsHalfSum_rd")){
			for(int i = 0; i< square[0].length; i++){
				for(int j = 0; j < square[0].length; j++){
					if(map[i][j] != true)
						trsHalfSum_rd += square[i][j];
				}
			}
		}
		else if(name.equals("blsHalfSum_rd")){
			for(int i = 0; i< square[0].length; i++){
				for(int j = 0; j < square[0].length; j++){
					if(map[i][j] != true)
						blsHalfSum_rd += square[i][j];
				}
			}
		}
		else if(name.equals("brsHalfSum_rd")){
			for(int i = 0; i< square[0].length; i++){
				for(int j = 0; j < square[0].length; j++){
					if(map[i][j] != true)
						brsHalfSum_rd += square[i][j];
				}
			}
		}
	}
	
	private static void calcElemsInRoundedDownHalf(int total){
		elemsInRoundedDownHalf = total - elemsInRoundedUpHalf;
	}
	
	private static void calcVarianceCoordinates(Double[][] tls, Double[][] brs, Double[][] trs, Double[][] bls){
		calcTLShalfSum_ru_v(tls);
		calcBRShalfSum_ru_v(brs);
		calcTRShalfSum_ru_v(trs);
		calcBLShalfSum_ru_v(bls);
	}
	
	private static void calcTLShalfSum_ru_v(Double[][] tls){
		boolean[][] map = new boolean[tls[0].length][tls[0].length];
		Double mean = tlsHalfSum_ru / elemsInRoundedUpHalf;
		int zeilen = tls[0].length;
		int reihen = zeilen;
		int cnt = 0;
		for(int i = 0; i < zeilen; i++){
			int j = 0;
			while(j < reihen){
				tlsHalfSum_ru_v += Math.pow((tls[i][j] - mean), 2);
				map[i][j] = true;
				if(j == cnt)
					break;
				j++;
			}
			cnt++;
		}
		calcShalfSum_rd_v(tls, map, "tlsHalfSum_rd_v");	// coordinate 3
	}
	
	private static void calcBRShalfSum_ru_v(Double[][] brs){
		boolean[][] map = new boolean[brs[0].length][brs[0].length];
		Double mean = brsHalfSum_ru / elemsInRoundedUpHalf;
		int zeilen = brs[0].length;
		int reihen = zeilen;
		int cnt = 0;
		int j = 0;
		for(int i = 0; i < zeilen; i++){
			while(j < reihen){
				brsHalfSum_ru_v += Math.pow((brs[i][j] - mean), 2);
				map[i][j] = true;
				j++;
			}
			cnt++;
			j = cnt;
		}
		calcShalfSum_rd_v(brs, map, "brsHalfSum_rd_v");	// coordinate 3
	}
	
	private static void calcTRShalfSum_ru_v(Double[][] trs){
		boolean[][] map = new boolean[trs[0].length][trs[0].length];
		Double mean = trsHalfSum_ru / elemsInRoundedUpHalf;
		int zeilen = trs[0].length;
		int reihen = zeilen;
		int cnt = trs[0].length-2;
		for(int i = 0; i < zeilen; i++){
			int j = trs[0].length-1;
			while(j > cnt){
				trsHalfSum_ru_v += Math.pow(trs[i][j] - mean, 2);
				map[i][j] = true;
				j--;
			}
			cnt--;
		}
		calcShalfSum_rd_v(trs, map, "trsHalfSum_rd_v");	// coordinate 4
	}
	
	private static void calcBLShalfSum_ru_v(Double[][] bls){
		boolean[][] map = new boolean[bls[0].length][bls[0].length];
		Double mean = blsHalfSum_ru / elemsInRoundedUpHalf;
		int zeilen = bls[0].length;
		int reihen = zeilen;
		int cnt = bls[0].length-2;
		for(int i = 0; i < zeilen; i++){
			int j = 0;
			while(j < reihen){
				blsHalfSum_ru_v += Math.pow((bls[i][j] - mean), 2);
				map[i][j] = true;
				j++;
			}
			reihen--;
		}
		calcShalfSum_rd_v(bls, map, "blsHalfSum_rd_v");	// coordinate 3
	}
	
	private static void calcShalfSum_rd_v(Double[][] square, boolean[][] map, String name){
		Double mean = 0.0;
		if(name.equals("tlsHalfSum_rd_v"))
			mean = tlsHalfSum_rd / elemsInRoundedDownHalf;
		else if(name.equals("trsHalfSum_rd_v"))
			mean = trsHalfSum_rd / elemsInRoundedDownHalf;
		else if(name.equals("blsHalfSum_rd_v"))
			mean = blsHalfSum_rd / elemsInRoundedDownHalf;
		else
			mean = brsHalfSum_rd / elemsInRoundedDownHalf;
		Double value = 0.0;
		for (int i = 0; i < square[0].length; i++) {
			for (int j = 0; j < square[0].length; j++) {
				if (map[i][j] != true){
					value += Math.pow((square[i][j] - mean), 2);
				}
			}
		}
		if(name.equals("tlsHalfSum_rd_v"))
			tlsHalfSum_rd_v = value;
		else if(name.equals("trsHalfSum_rd_v"))
			trsHalfSum_rd_v = value;
		else if(name.equals("blsHalfSum_rd_v"))
			blsHalfSum_rd_v = value;
		else
			brsHalfSum_rd_v = value;
	}
	
	@SuppressWarnings("unused")
	private static void printMap(boolean[][] map){
		for(int i = 0; i< map[0].length; i++){
			for(int j = 0; j < map[0].length; j++){
				System.out.print(map[i][j] + " ");
				if(j == map[0].length-1)
					System.out.println();
			}
		}
	}
	
	private static Double[][] fillTLS(Double[][] square){
		Double[][] res = new Double[square[0].length/2][square[0].length/2];	// viertel so gross wie original
		for(int zeile = 0; zeile < res.length; zeile++){
			for(int spalte = 0; spalte < res.length; spalte++){
				res[zeile][spalte] = square[zeile][spalte];
			}
		}
		return res;
	}
	
	private static Double[][] fillTRS(Double[][] square){
		Double[][] res = new Double[square[0].length/2][square[0].length/2];
		for(int zeile = 0; zeile < res[0].length; zeile++){
			int k = 0;
			for(int spalte = square[0].length/2; spalte < square[0].length; spalte++){
				res[zeile][k] = square[zeile][spalte];
				k++;
			}
		}
		return res;
	}
	
	private static Double[][] fillBLS(Double[][] square){
		Double[][] res = new Double[square[0].length/2][square[0].length/2];
		int k = 0;
		for(int zeile = square[0].length/2; zeile < square[0].length; zeile++){
			for(int spalte = 0; spalte < res.length; spalte++){
				res[k][spalte] = square[zeile][spalte];
			}
			k++;
		}
		return res;
	}
	
	private static Double[][] fillBRS(Double[][] square){
		Double[][] res = new Double[square[0].length/2][square[0].length/2];
		int k = 0;
		for(int zeile = square[0].length/2; zeile < square[0].length; zeile++){
			int l = 0;
			for(int spalte = square[0].length/2; spalte < square[0].length; spalte++){
				res[k][l] = square[zeile][spalte];
				l++;
			}
			k++;
		}
		return res;
	}
	
	@SuppressWarnings("unused")
	public static void printArr(Double[][] square, String name){
		System.out.println(name + ": ");
		for(int i = 0; i < square[0].length; i++){
			for(int j = 0; j < square[0].length; j++){
				System.out.print(square[i][j] + "	|");
				if(j == square[0].length-1)
					System.out.println();
			}
		}
	}

}
