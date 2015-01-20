import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws Exception {

		boolean tweet = true;

		// Random Generate 1M data points (id,x,y,tag)
		// List<int[]> points = DataGen.randomGenData(1000000, QuadTree.maxx,
		// QuadTree.maxy);
		List<long[]> points;
		if (tweet)
			points = DataGen.twitterdata();
		else
			points = DataGen.randomGenData(1000000, QuadTree.maxx,
					QuadTree.maxy);

		System.out.println("Genterate random sample done");
		// Creating Quad tree
		QuadTree tree = QuadTree.gentree(points);

		System.out.println("Build quad tree");

		// Extracting Leaf nodes
		LinkedList<QuadTree.Node> indexNodes = tree.genleaves(1000);

		// Printing index node into file named points_global_index
		printindex(indexNodes); // (id,x1,y2,x2,y2,tag)

		System.out.println("Print Global index");

		// printing partitioned data into different files under dir data
		// (id,x,y,tag)
		if (tweet)
			printPartationedTweetData(indexNodes);
		else
			printPartationedData(points, indexNodes);

		System.out.println("Print partationed data");

		// Generate SQL for creating and loading data and index on table points
		GenSQl(indexNodes.size());

		// Generate Bash
		GenBash(indexNodes.size());

		// Run Bash file
		// Runtime.getRuntime().exec("./runbash.sh");
	}

	private static void GenBash(int size) throws Exception {
		PrintWriter out = new PrintWriter("runbash.sh");

		// put the files into hadoop
		// index
		out.println("hadoop fs -mkdir /index");
		out.println("hadoop fs -put ./points_tb_global_index /index/");

		// data
		out.println("hadoop fs -mkdir /data");
		out.println("hadoop fs -put ./data/points_*.csv /data/");

		// run SQL
		out.println("~/SpatialImpala/bin/impala_shell.sh -f ./startSQL.sql");

		out.flush();
		out.close();
	}

	private static void GenSQl(int size) throws Exception {
		PrintWriter out = new PrintWriter("startSQl.sql");
		// create table points
		out.println("create table IF NOT EXISTS points_tb (id int,x int,y int) partitioned by (tag STRING);");

		// add partitions
		for (int i = 0; i <= size; i++) {
			out.println("alter table points_tb add partition (tag='" + i
					+ "');");
		}

		// Load data
		for (int i = 0; i <= size; i++) {
			out.println("load data inpath '/data/points_" + i
					+ ".csv' into table points_tb partition (tag='" + i + "');");
		}

		// Load Index
		out.println("ALTER TABLE points_tb SET TBLPROPERTIES ('globalIndex'='/index/points_tb_global_index');");

		out.flush();
		out.close();
	}

	private static void printPartationedTweetData(
			LinkedList<QuadTree.Node> indexNodes) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader("tweetout"));
		PrintWriter[] out = new PrintWriter[indexNodes.size()+1];
		for (int i = 0; i < out.length; i++) {
			out[i] = new PrintWriter("data/points_" + i + ".csv");
		}
		int misscount = 0,count = 0;
		String line;
		while ((line = in.readLine()) != null) {
			long[] prn = new long[4];
			String[] l = line.split(",");
			for (int i = 0; i < l.length; i++) {
				prn[i] = Long.parseLong(l[i]);
			}
			annotateData(prn, indexNodes);
			if(prn[3] == 0) misscount++;
			count++;
			out[(int) prn[3]].printf("%d,%d,%d,%d\n", prn[0], prn[1], prn[2],
					prn[3]);
		}
		for (int i = 0; i < out.length; i++) {
			out[i].flush();
			out[i].close();
		}
		System.out.println("Misscount over count: "+ misscount +"/"+count);
	}

	private static void printPartationedData(List<long[]> points,
			LinkedList<QuadTree.Node> indexNodes) throws Exception {
		PrintWriter[] out = new PrintWriter[indexNodes.size()+1];
		for (int i = 0; i < out.length; i++) {
			out[i] = new PrintWriter("data/points_" + i + ".csv");
		}
		for (long[] prn : points) {
			annotateData(prn, indexNodes);
			out[(int) prn[3]].printf("%d,%d,%d,%d\n", prn[0], prn[1], prn[2],
					prn[3]);
		}
		for (int i = 0; i < out.length; i++) {
			out[i].flush();
			out[i].close();
		}
	}

	private static void annotateData(long[] is,
			LinkedList<QuadTree.Node> indexNodes) {
		int id = 0;
		for (QuadTree.Node node : indexNodes) {
			if (is[1] >= node.x1 && is[1] <= node.x2 && is[2] >= node.y1
					&& is[2] <= node.y2)
				break;
			id++;
		}
		if (id == indexNodes.size())
			is[3] = 0;
		else
			is[3] = id+1;
	}

	private static void printindex(LinkedList<QuadTree.Node> indexNodes)
			throws Exception {
		PrintWriter out = new PrintWriter("points_tb_global_index");
		PrintWriter out2 = new PrintWriter("index_count.csv");
		int id = 1;
		for (QuadTree.Node node : indexNodes) {
			out.printf("%d,%d,%d,%d,%d,%d\n", id, node.x1, node.y1, node.x2,
					node.y2, id);
			out2.printf("%d,%d\n", id, node.count);
			id++;
		}
		out.flush();
		out.close();
		out2.flush();
		out2.close();
	}

}
