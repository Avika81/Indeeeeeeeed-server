import java.util.HashMap;

public class Main {
	
//	private static int count = 0; 

	private static int count = 0;
	
	public static void main(String[] args) {		
		
		Node node1 = new Node(1);
		Node node2 = new Node(2);
		Node node3 = new Node(3);
		Node node4 = new Node(4);
		Node node5 = new Node(5);
		
		node1.next = node2;
		node2.next = node3;
		node3.next = node4;
		node4.next = node5;
		node5.next = null;
		
		SetOfStacks stack = new SetOfStacks();
		stack.push(3);
		
//		Node foundNode = findNode(node1);
//		System.out.println(foundNode.data);
	}
	
	public static Node findNode(Node node) {		
		Node newNode = node;
		
		count++;
		newNode = findNode(node.next);
				
		return newNode;
	}
	
//	public static Node findNode(Node node, int n) {		
//		Node newNode = node;
//		
//		if(node.next != null)
//			newNode = findNode(node.next, n);
//		
//		count++;
//		if(count <= n+1)
//			newNode = node;
//		
//		return newNode;
//	}
	
	public static void rotate(int[][] matrix, int n) {
		printMatrix(matrix);
		
		float center = (n-1) / 2;
		for (int layer = 0; layer < n / 2; ++layer) {
			for(int i = 0; i < n - 1 - layer; ++i) {
				int nowX = i;
				int nowY = layer; 
				int now = matrix[nowX][nowY]; 
				int otherX = 0;
				int otherY = 0;
				for(int j=0; j<3; j++){
					otherX = (int)(-((float)nowY - center) + center);
					otherY = (int)(((float)nowX - center) + center);
					matrix[nowX][nowY]=matrix[otherX][otherY];
					nowX = otherX;
					nowY = otherY;
				}

				matrix[otherX][otherY]=now;
			}
//			System.out.println("-");
//			printMatrix(matrix);
		}
		System.out.println("");
		printMatrix(matrix);
	}
	
	static void printMatrix(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
		    for (int j = 0; j < matrix[i].length; j++) {
		        System.out.print(matrix[i][j] + " ");
		    }
		    System.out.println();
		}
	}
	
	public static void replaceFun(char[] str, int length) {
		int spaceCount = 0, newLength, i = 0;
		for (i = 0; i < length; i++) {
			if (str[i] == ' ')
				spaceCount++;
		}
		if(spaceCount==0)
			return;
		
		newLength = length + spaceCount * 2;
		str[newLength] = '\0';
		for (i = length - 1; i >= 0; i--) {
			System.out.println(str);
			if (str[i] == ' ') {
				str[newLength - 1] = '0';
				str[newLength - 2] = '2';
				str[newLength - 3] = '%';
				newLength -= 3;
			} else {
				str[newLength - 1] = str[i];
				newLength -= 1;
			}			
		}		
		
	}
	
	public static boolean isAnagram(String str1, String str2) {
		HashMap<Character, Integer> map = new HashMap<Character, Integer>(); 
		for(int i = 0; i < str1.length(); ++i) {
			char key = str1.charAt(i);
			int value = map.containsKey(key) ? map.put(key,map.get(key)+1) : 1;
			map.put(key,value);
		}		
		for(int i = 0; i < str2.length(); ++i) {
			char key = str2.charAt(i);
			if(map.containsKey(key) && map.get(key)>0)
				map.put(key,map.get(key)-1);
			else
				return false;
		}
		return true;
	}	
	
	public static boolean isUniqueChars(String str) {
		int checker = 0;
		for(int i = 0; i < str.length(); ++i) {
			int val = str.charAt(i) - 'a';
			System.out.println("val: " + val);
			System.out.printf("0x%02X", checker & (1 << val));
			System.out.println("");
//			System.out.println(Integer.toHexString(checker & (1 << val)));		
			if((checker & (1 << val)) > 0)
				return false;
			
			checker |= (1 << val);
			System.out.printf("0x%02X", checker);
			System.out.println("");
			System.out.println("");
//			System.out.println("checker: " + Integer.toHexString(checker));
		}
		return true;
	}
	
	public static boolean isUniqueChars2(String str) {
		boolean[] char_set = new boolean[256];
		for (int i = 0; i < str.length(); i++) {
		int val = str.charAt(i);
		System.out.println(val);
		if (char_set[val]) return false;
		char_set[val] = true;
		}
		return true;
	}
}
