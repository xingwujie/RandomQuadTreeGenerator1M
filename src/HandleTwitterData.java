import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;

public class HandleTwitterData {

	public static void play() throws Exception {
		out = new PrintWriter("tweetout");
		all = countFilesForFolder(new File("/Users/fayyaz/Documents/DataSets/Twitter"));
		now = 0;
		listFilesForFolder(new File("/Users/fayyaz/Documents/DataSets/Twitter"));
		out.flush();
	}
	
	public static PrintWriter out;
	
	public static int countFilesForFolder(final File folder) throws Exception {
		int ret = 0;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            ret += countFilesForFolder(fileEntry);
	        } else if (fileEntry.getName().endsWith(".gzip")) {
	        	ret ++;
	        }
	    }
	    return ret;
	}
	
	static int all;
	static int now;
	
	public static void listFilesForFolder(final File folder) throws Exception {
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else if (fileEntry.getName().endsWith(".gzip")) {
	        	System.out.printf("precentage done: %.2f%%\n", 100.0*now/all);
	        	readtweets(fileEntry.getAbsolutePath());
	        	now++;
	        }
	    }
	}
	
	public static void readtweets(String filein)
			throws Exception {
		InputStream fileStream = new FileInputStream(filein);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
		BufferedReader buffered = new BufferedReader(decoder);

		String line;

		while ((line = buffered.readLine()) != null) {
			Status status = TwitterObjectFactory.createStatus(line);
			if (status.getGeoLocation() != null)
				out.println(status.getId() + ","
						+ ((long) (1000000 * status.getGeoLocation().getLatitude())) + ","
						+ ((long) (1000000 * status.getGeoLocation().getLongitude())));
			
		}
	}


}
