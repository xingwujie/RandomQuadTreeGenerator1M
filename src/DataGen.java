import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class DataGen {

	static List<long[]> randomGenData(int size,int maxx,int maxy) {
		
		Random rx = new Random();
		Random ry = new Random();
		
		List<long []> ret = new LinkedList<long []>();
		
		for (int i = 0; i < size; i++) {
			long [] x = new long[4];
			x[0] = i; // ID
			x[1] = rx.nextInt(maxx); // X
			x[2] = ry.nextInt(maxy); // Y
			ret.add(x);
		}
		
		return ret;
	}
	
	static List<long[]> twitterdata() throws Exception{
//		HandleTwitterData.play();
		List<long []> ret = new LinkedList<long[]>();
		BufferedReader in = new BufferedReader(new FileReader("tweetout"));
		Random r = new Random();
		String line;
		while ((line = in.readLine()) != null) {
			boolean use = r.nextInt(100) == 0;
			if (!use) continue;
			long[] x = new long[4];
			String[] l = line.split(",");
			for (int i = 0; i < l.length; i++) {
				x[i] = Long.parseLong(l[i]);
			}
			ret.add(x);
		}
		return ret;
	}
	
}
