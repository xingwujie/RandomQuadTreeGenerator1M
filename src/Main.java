import java.io.PrintWriter;
import java.util.LinkedList;

public class Main {

	public static void main(String[] args) throws Exception {
		
		// Random Generate 1M data points (id,x,y,tag)
		int[][] randPoints = RandomDataGen.genData(1000000, QuadTree.maxx, QuadTree.maxy);
		
		// Creating Quad tree
		QuadTree tree = QuadTree.gentree(randPoints);
		
		// Extracting Leaf nodes
		LinkedList<QuadTree.Node> indexNodes = tree.genleaves(1000); 
		
		
		// Printing index node into file named points_global_index 
		printindex(indexNodes); // (id,x1,y2,x2,y2,tag)
		
		// Annotate data
		annotateData(randPoints,indexNodes);
		
		// printing partitioned data into different files under dir data (id,x,y,tag)
		printPartationedData(randPoints,indexNodes.size());
	
		// Generate SQL for creating and loading data and index on table points
		GenSQl(indexNodes.size());
		
		// Generate Bash
		GenBash(indexNodes.size());
		
		// Run Bash file
//		Runtime.getRuntime().exec("./runbash.sh");
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
		out.println("create table points_tb (id int,x int,y int) partitioned by (tag STRING);");
		
		// add partitions
		for (int i = 0; i < size; i++) {
			out.println("alter table points_tb add partition (tag="+i+");");
		}
		
		// Load data
		for(int i=0; i < size; i++){
			out.println("load data inpath '/data/points_"+i+".csv' into table points_tb partition (tag='"+i+"');");
		}
		
		// Load Index
		out.println("ALTER TABLE points_tb SET TBLPROPERTIES ('globalIndex'='/index/points_tb_global_index');");
		
		out.flush();
		out.close();
	}

	private static void printPartationedData(int[][] randPoints, int size) throws Exception {
		PrintWriter[] out = new PrintWriter[size];
		for (int i = 0; i < out.length; i++) {
			out[i] = new PrintWriter("data/points_"+i+".csv");
		}
		for (int i = 0; i < randPoints.length; i++) {
			out[randPoints[i][3]].printf("%d,%d,%d,%d\n", randPoints[i][0],randPoints[i][1],randPoints[i][2],randPoints[i][3]);
		}
		for (int i = 0; i < out.length; i++) {
			out[i].flush();
			out[i].close();
		}
	}

	private static void annotateData(int[][] randPoints,
			LinkedList<QuadTree.Node> indexNodes) {
		
		for (int[] is : randPoints) {
			int id = 0;
			for (QuadTree.Node node : indexNodes) {
				if (is[1]>=node.x1 && is[1] <= node.x2 && is[2] >= node.y1 && is[2] <= node.y2)
					break;
				id++;
			}
			is[3] = id;
		}
		
	}

	private static void printindex(LinkedList<QuadTree.Node> indexNodes) throws Exception {
		PrintWriter out = new PrintWriter("points_tb_global_index");
		PrintWriter out2 = new PrintWriter("index_count.csv");
		int id = 0;
		for (QuadTree.Node node : indexNodes) {
			out.printf("%d,%d,%d,%d,%d,%d\n",id,node.x1,node.y1,node.x2,node.y2,id);
			out2.printf("%d,%d\n",id,node.count);
			id++;
		}
		out.flush();
		out.close();
		out2.flush();
		out2.close();
	}

}
