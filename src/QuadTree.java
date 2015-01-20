import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QuadTree {

	static class Node {
		int count;
		Node topLeft, topRight, bottomLeft, bottomRight;
		int x1,y1,x2,y2;
		public Node(int x1,int y1,int x2,int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	};

	Node root;

	static final int maxx = 1<<31;
	static final int maxy = 1<<31;

	public void insert(long x, long y) {
		root = insert(root, -maxx, -maxy, maxx - 1, maxy - 1, (int) x, (int)y);
	}

	Node insert(Node node, int ax, int ay, int bx, int by, int x, int y) {
		if (ax > x || x > bx || ay > y || y > by)
			return node;
		if (node == null)
			node = new Node(ax,ay,bx,by);
		++node.count;
		if (ax == bx && ay == by)
			return node;

		int mx = (ax + bx) >> 1;
		int my = (ay + by) >> 1;

		node.bottomLeft = insert(node.bottomLeft, ax, ay, mx, my, x, y);
		node.topLeft = insert(node.topLeft, ax, my + 1, mx, by, x, y);
		node.bottomRight = insert(node.bottomRight, mx + 1, ay, bx, my, x, y);
		node.topRight = insert(node.topRight, mx + 1, my + 1, bx, by, x, y);

		return node;
	}

	// number of points in [x1,x2] x [y1,y2]
	public int count(int x1, int y1, int x2, int y2) {
		return count(root, 0, 0, maxx - 1, maxy - 1, x1, y1, x2, y2);
	}

	int count(Node node, int ax, int ay, int bx, int by, int x1, int y1, int x2, int y2) {
		if (node == null || ax > x2 || x1 > bx || ay > y2 || y1 > by)
			return 0;
		if (x1 <= ax && bx <= x2 && y1 <= ay && by <= y2)
			return node.count;

		int mx = (ax + bx) >> 1;
		int my = (ay + by) >> 1;
		int res = 0;
		res += count(node.bottomLeft, ax, ay, mx, my, x1, y1, x2, y2);
		res += count(node.topLeft, ax, my + 1, mx, by, x1, y1, x2, y2);
		res += count(node.bottomRight, mx + 1, ay, bx, my, x1, y1, x2, y2);
		res += count(node.topRight, mx + 1, my + 1, bx, by, x1, y1, x2, y2);
		return res;
	}

	
	public static QuadTree gentree(List<long[]> randPoints) {
		QuadTree t = new QuadTree();

		for (long[] prn: randPoints) {
			t.insert(prn[1], prn[2]);
		}
		
		return t;
	}

	public LinkedList<Node> genleaves(int maxsize) {
		
		LinkedList<Node> ret = new LinkedList<Node>();
		
		Queue<Node> w = new LinkedList<QuadTree.Node>();
		w.add(root);
		while(!w.isEmpty()){
			Node current = w.poll();
			if (current==null) continue;
			if(current.count<=maxsize){
				ret.add(current);
				continue;
			}
			w.add(current.topLeft);
			w.add(current.topRight);
			w.add(current.bottomLeft);
			w.add(current.bottomRight);
		}
		
		return ret;
	}
}