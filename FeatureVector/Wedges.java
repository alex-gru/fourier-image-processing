import java.io.BufferedReader;
import java.util.ArrayList;

/*****************************************************************************************************************************
 * Calculate Feature Vector of Fourier transformed picture using 'wedges' (dt. 'Kuchenstuecke')
 * @author schusterpa@stud.sbg.ac.at
 *
 * explanation: square is separated into 4 quarters: top left square, top right square, ...
 * 
 * (# represents pixel)
 * e.g. tls is then separated into two halfs. since I can't really separate a single pixel, I have to either round
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
 ****************************************************************************************************************************/
public class Wedges {
	
	static double[][] testpicture = new double[8][8];
	
	static double tlsHalfSum_ru = 0;	// top left square half sum rounded up
	static double trsHalfSum_ru = 0;
	static double blsHalfSum_ru = 0;
	static double brsHalfSum_ru = 0;
	
	static double tlsHalfSum_rd = 0;	// top left square half sum rounded down
	static double trsHalfSum_rd = 0;
	static double blsHalfSum_rd = 0;
	static double brsHalfSum_rd = 0;
	
	static int elemsInRoundedUpHalf = 0;	// nr of elements in a rounded up half (i.e. half of a square)
	static int elemsInRoundedDownHalf = 0;
	
	// dummy input
	private static void fillPicture(){
		int counter = 1;
		for(int i = 0; i < testpicture[0].length; i++){
			if(i == 4) System.out.println("-----------------------------------------------------------------");
			for(int j = 0; j < testpicture[0].length; j++){
				testpicture[i][j] = counter;	// 8x8 matrix
				System.out.print(testpicture[i][j] + "	|");
				if(j == 7) System.out.println();
				counter++;
			}
		}
	}
	
	public static void main(String[] args){
		fillPicture();
		extractWedgesFV(testpicture);
		System.out.println("\n FEATURE VECTOR:");
		System.out.println("coord. 1 = " + (tlsHalfSum_ru + brsHalfSum_ru) / (elemsInRoundedUpHalf*2));
		System.out.println("coord. 2 = " + (blsHalfSum_ru + trsHalfSum_ru) / (elemsInRoundedUpHalf*2));
		System.out.println("coord. 3 = " + (tlsHalfSum_rd + brsHalfSum_rd) / (elemsInRoundedDownHalf*2));
		System.out.println("coord. 4 = " + (trsHalfSum_rd + blsHalfSum_rd) / (elemsInRoundedDownHalf*2));
	}
	
	public static void extractWedgesFV(double[][] pic){
		int height = pic[0].length;
		int width = height;
		double[][] topLeftSquare = fillTLS(pic); // TLS (= top left square)
		printArr(topLeftSquare, "topLeftSquare");
		double[][] topRightSquare = fillTRS(pic);
		printArr(topRightSquare, "topRightSquare");
		double[][] bottomLeftSquare = fillBLS(pic);
		printArr(bottomLeftSquare, "bottomLeftSquare");
		double[][] bottomRightSquare = fillBRS(pic);
		printArr(bottomRightSquare, "bottomRightSquare");
		
		calcTLShalfSum_ru(topLeftSquare);	// coordinate 1
		calcBRShalfSum_ru(bottomRightSquare);
		
		calcTRShalfSum_ru(topRightSquare);	// coordinate 2
		calcBLShalfSum_ru(bottomLeftSquare);
	}
	
	private static void calcTLShalfSum_ru(double[][] tls){
		boolean[][] map = new boolean[tls[0].length][tls[0].length];
		int zeilen = tls[0].length;
		int reihen = zeilen;
		int cnt = 0;
		for(int i = 0; i < zeilen; i++){
			int j = 0;
			while(j < reihen){
				//System.out.println("tls["+i+"]["+j+"] = " + tls[i][j]);
				tlsHalfSum_ru += tls[i][j];
				map[i][j] = true;
				if(j == cnt)
					break;
				j++;
			}
			cnt++;
		}
		System.out.println("###tlsHalfSum_ru = " + tlsHalfSum_ru);
		calcElemsInRoundedUpHalf(map);
		calcElemsInRoundedDownHalf(tls[0].length * tls[0].length);
		
		calcShalfSum_rd(tls, map, "tlsHalfSum_rd");	// coordinate 3
		System.out.println("---tlsHalfSum_rd = " + tlsHalfSum_rd);
	}
	
	private static void calcBRShalfSum_ru(double[][] brs){
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
		System.out.println("###brsHalfSum_ru = " + brsHalfSum_ru);
		calcShalfSum_rd(brs, map, "brsHalfSum_rd");	// coordinate 3
		System.out.println("---brsHalfSum_rd = " + brsHalfSum_rd);
	}
	
	private static void calcTRShalfSum_ru(double[][] trs){
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
		System.out.println("###trsHalfSum_ru = " + trsHalfSum_ru);
		calcShalfSum_rd(trs, map, "trsHalfSum_rd");	// coordinate 4
		System.out.println("---trsHalfSum_rd = " + trsHalfSum_rd);
	}
	
	private static void calcBLShalfSum_ru(double[][] bls){
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
		System.out.println("###blsHalfSum_ru = " + blsHalfSum_ru);
		calcShalfSum_rd(bls, map, "blsHalfSum_rd");	// coordinate 4
		System.out.println("---blsHalfSum_rd = " + blsHalfSum_rd);
	}
	
	private static void calcElemsInRoundedUpHalf(boolean[][] map){
		for(int i = 0; i< map[0].length; i++){
			for(int j = 0; j < map[0].length; j++){
				if(map[i][j] == true)
					elemsInRoundedUpHalf++;
			}
		}
	}
	
	private static void calcShalfSum_rd(double[][] square, boolean[][] tls_map, String name){	// TODO get rid of code duplication!
		if(name.equals("tlsHalfSum_rd")){
			for(int i = 0; i< square[0].length; i++){
				for(int j = 0; j < square[0].length; j++){
					if(tls_map[i][j] != true)
						tlsHalfSum_rd += square[i][j];
				}
			}
		}
		else if(name.equals("trsHalfSum_rd")){
			for(int i = 0; i< square[0].length; i++){
				for(int j = 0; j < square[0].length; j++){
					if(tls_map[i][j] != true)
						trsHalfSum_rd += square[i][j];
				}
			}
		}
		else if(name.equals("blsHalfSum_rd")){
			for(int i = 0; i< square[0].length; i++){
				for(int j = 0; j < square[0].length; j++){
					if(tls_map[i][j] != true)
						blsHalfSum_rd += square[i][j];
				}
			}
		}
		else if(name.equals("brsHalfSum_rd")){
			for(int i = 0; i< square[0].length; i++){
				for(int j = 0; j < square[0].length; j++){
					if(tls_map[i][j] != true)
						brsHalfSum_rd += square[i][j];
				}
			}
		}
	}
	
	private static void calcElemsInRoundedDownHalf(int total){
		elemsInRoundedDownHalf = total - elemsInRoundedUpHalf;
	}
	
	private static void printMap(boolean[][] map){
		for(int i = 0; i< map[0].length; i++){
			for(int j = 0; j < map[0].length; j++){
				System.out.print(map[i][j] + " ");
				if(j == map[0].length-1)
					System.out.println();
			}
		}
	}
	
	private static double[][] fillTLS(double[][] square){
		double[][] res = new double[square[0].length/2][square[0].length/2];	// viertel so gross wie original
		for(int zeile = 0; zeile < res.length; zeile++){
			for(int spalte = 0; spalte < res.length; spalte++){
				res[zeile][spalte] = testpicture[zeile][spalte];
			}
		}
		return res;
	}
	
	private static double[][] fillTRS(double[][] square){
		double[][] res = new double[square[0].length/2][square[0].length/2];
		for(int zeile = 0; zeile < res[0].length; zeile++){
			int k = 0;
			for(int spalte = square[0].length/2; spalte < square[0].length; spalte++){
				res[zeile][k] = testpicture[zeile][spalte];
				k++;
			}
		}
		return res;
	}
	
	private static double[][] fillBLS(double[][] square){
		double[][] res = new double[square[0].length/2][square[0].length/2];
		int k = 0;
		for(int zeile = square[0].length/2; zeile < square[0].length; zeile++){
			for(int spalte = 0; spalte < res.length; spalte++){
				res[k][spalte] = testpicture[zeile][spalte];
			}
			k++;
		}
		return res;
	}
	
	private static double[][] fillBRS(double[][] square){
		double[][] res = new double[square[0].length/2][square[0].length/2];
		int k = 0;
		for(int zeile = square[0].length/2; zeile < square[0].length; zeile++){
			int l = 0;
			for(int spalte = square[0].length/2; spalte < square[0].length; spalte++){
				res[k][l] = testpicture[zeile][spalte];
				l++;
			}
			k++;
		}
		return res;
	}
	
	private static void printArr(double[][] square, String name){
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
