import java.util.Random;


public class RandomDataGen {

	static int[][] genData(int size,int maxx,int maxy) {
		
		Random rx = new Random();
		Random ry = new Random();
		
		int [][] ret = new int[size][4];
		
		for (int i = 0; i < ret.length; i++) {
			ret[i][0] = i; // ID
			ret[i][1] = rx.nextInt(maxx); // X
			ret[i][2] = ry.nextInt(maxy); // Y
		}
		
		return ret;
	}
	
}
