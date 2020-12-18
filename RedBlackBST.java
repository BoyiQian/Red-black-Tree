import java.util.*;
import java.io.*;



/**
 * RedBlackBST class
 *
 */
public class RedBlackBST<Key extends Comparable<Key>, Value> {

	private static final boolean RED   = true;
	private static final boolean BLACK = false;
	Node root;     // root of the BST

	/*************************************************************************
	 *  Node Class and methods - DO NOT MODIFY
	 *************************************************************************/
	public class Node {
		Key key;           // key
		Value val;         // associated data
		Node left, right;  // links to left and right subtrees
		boolean color;     // color of parent link
		int N;             // subtree count

		public Node(Key key, Value val, boolean color, int N) {
			this.key = key;
			this.val = val;
			this.color = color;
			this.N = N;
		}
	}

	// is node x red; false if x is null ?
	private boolean isRed(Node x) {
		if (x == null) return false;
		return (x.color == RED);
	}

	// number of node in subtree rooted at x; 0 if x is null
	private int size(Node x) {
		if (x == null) return 0;
		return x.N;
	}

	// return number of key-value pairs in this symbol table
	public int size() { return size(root); }

	// is this symbol table empty?
	public boolean isEmpty() {
		return root == null;
	}

	public RedBlackBST() {
		this.root = null;
	}

	/*************************************************************************
	 *  Modification Functions
	 *************************************************************************/

	/*public Node parentOf(Node node) {
		if (node == root) {
			return null;
		} else {
			Node parent = root;
			Node result = null;
			while(parent != null) {
				int cmp = node.key.compareTo(parent.key);
				if (cmp <0) {
					if (parent.left != null && parent.left.equals(node)) {
						return parent.left;

					} else {
						parent = parent.left;
					}
				} else if (cmp >0) {
					if (parent.right != null && parent.right.equals(node)) {
						return parent.right;
					} else {
						parent = parent.right;
					}
				} else {
					parent.val = node.val;
					break;
				}
			}
			return result;
		}
	}*/
	// insert the key-value pair; overwrite the old value with the new value
	// if the key is already present
	public void insert(Key key, Value val) {
		//the root should be black if the RBST is empty

		/*else {
			Node parent = root;
			while(parent != null) {
				cmp = key.compareTo(parent.key);
				if (cmp <0) {
					if (parent.left == null) {
						parent.left = newNode;
						}
					} else {
						parent = parent.left;
					}
				} else if (cmp >0) {
					if (parent.right == null) {
						parent.right = newNode;
					} else {
						parent = parent.right;
					}
				} else {
					parent.val = val;
					break;
				}
			}

			while(parent!= null) {
				parent = parentOf(balance(parent));
			}

		}*/

		root = insert(root, key, val);
		root.color = BLACK;

	}

	public Node insert(Node parent, Key key, Value val) {

		if (parent == null) {
			Node newNode = new Node(key, val, RED, 1);
			return newNode;
		}
		int cmp = key.compareTo(parent.key);
		if (cmp <0) {
			parent.left = insert(parent.left, key, val);
		} else if (cmp >0) {
			parent.right = insert(parent.right, key, val);
		} else {
			parent.val = val;
		}
		parent = balance(parent);
		return parent;
	}

	// delete the key-value pair with the given key
	public void delete(Key key) {
		if (key!= null && contains(key)) {
			// assume root is red to make easier when we need red node
			if(!isRed(root.right) && !isRed(root.left)) {
				root.color = RED;
			}
			root = delete(root, key);
			//fixed up, because root always should be black
			root.color = BLACK;
		}
	}

	public Node delete(Node node, Key key) {
		int cmp = key.compareTo(node.key);
		if (cmp <0) {
			//go left
			if (isRed(node) && !isRed(node.left) && !isRed(node.left.left)) {
				//move red link to left when children and grand children are black
				node = moveRedLeft(node);
			} else if (isRed(node.right)) {
				node = rotateLeft(node);
			}
			node.left = delete(node.left, key);
		} else if (cmp >0) {
			//go right
			// move red link to right when children and grand children are black
			if (isRed(node.left)) {
				node = rotateRight(node);
			}
			if (isRed(node) && !isRed(node.right) && !isRed(node.right.left)) {
				node = moveRedRight(node);
			}
			node.right = delete(node.right, key);
		} else {
			if (node.right == null || node.left == null) {
				return null;
			} else {
				//find the smallest larger number
				Node replace;
				if (node.right == null) {
					replace = node.left;
					node.key = replace.key;
					node.val = replace.val;
					node.left = delete(node.left, replace.key);
				} else {
					replace = node.right;
					while (replace.left != null) {
						replace = replace.left;
					}
					node.key = replace.key;
					node.val = replace.val;
					node.right = delete(node.right, replace.key);
				}

			}
		}
		node = balance(node);
		return node;
	}

	/*************************************************************************
	 *  Search FUnctions
	 *************************************************************************/

	// value associated with the given key; null if no such key
	public Value search(Key key) {
		Node node = root;
		if (key != null) {
			while (node != null) {
				int cmp = key.compareTo(node.key);
				if (cmp < 0) {
					node = node.left;
				} else if (cmp > 0) {
					node = node.right;

				} else {
					return node.val;

				}
			}
		}
		return null;
	}

	// is there a key-value pair with the given key?
	public boolean contains(Key key) {
		return (search(key) != null);
	}



	/*************************************************************************
	 *  Utility functions
	 *************************************************************************/

	// height of tree (1-node tree has height 0)
	public int height() { return height(root); }
	private int height(Node x) {
		if (x == null) return -1;
		return 1 + Math.max(height(x.left), height(x.right));
	}

	/*************************************************************************
	 *  Rank Methods
	 *************************************************************************/



	// the key of rank k
	public Key getValByRank(int k) {
		if (k < 0 || k >= root.N) {
			return null;
		} else {
			Node node = root;
			int rank = root.N - root.right.N - 1;
			while(node != null) {
				if (k < rank) {
					if (node.left.right == null) {
						rank = rank - 1;
					} else {
						rank = rank - node.left.right.N -1;
					}
					node = node.left;
				} else if (k == rank) {
					return node.key;
				} else {
					if (node.right.left == null) {
						rank = rank +1;
					}else {
						rank = rank + node.right.left.N + 1;
					}
					node = node.right;
				}
			}
			return node.key;
		}

	}


	// number of keys less than key
	public int rank(Key key) {
		if (isEmpty()) {
			return 0;
		}
		Node node = root;
		int rank;
		if (root.right == null) {
			rank = root.N - 1;
		} else {
			rank = root.N - root.right.N - 1;
		}

		//System.out.println(rank);
		int cmp;
		while(node != null) {
			cmp = key.compareTo(node.key);
			if (cmp < 0) {
				if (node.left == null) {
					return rank;
				}
				if (node.left.right == null) {
					rank = rank - 1;
				} else {
					rank = rank - node.left.right.N -1;
				}

				node = node.left;
			} else if (cmp == 0) {
				return rank;
			} else {
				if (node.right == null) {
				return rank+1;
				}
				if (node.right.left == null) {
					rank = rank +1;
				}else {
					rank = rank + node.right.left.N + 1;
				}
				node = node.right;
			}
		}
		return rank;
	}


	/***********************************************************************
	 *  Range count and range search.
	 ***********************************************************************/

	public List<Key> getElements(int a, int b){
		List<Key> list = new ArrayList<Key>();
		if (root==null) {

			return list;
		}
		if (a < 0 || b <0 || a >= root.N || b >= root.N) {
			return list;
		}
		for (int i = a; i <= b; i++) {
			Key key = getValByRank(i);
			if (key != null) {
				list.add(key);
			}
		}
		return list;
	}

	/*************************************************************************
	 *  red-black tree helper functions
	 *************************************************************************/

	// make a left-leaning link lean to the right
	private Node rotateRight(Node h) {
		Node x = h.left;
		h.left = x.right;
		x.right = h;
		x.color = x.right.color;
		x.right.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;
		return x;
	}

	// make a right-leaning link lean to the left
	private Node rotateLeft(Node h) {
		Node x = h.right;
		h.right = x.left;
		x.left = h;
		x.color = x.left.color;
		x.left.color = RED;
		x.N = h.N;
		h.N = size(h.left) + size(h.right) + 1;
		return x;
	}

	// flip the colors of a node and its two children
	private void flipColors(Node h) {
		h.color = !h.color;
		h.left.color = !h.left.color;
		h.right.color = !h.right.color;
	}

	// Assuming that h is red and both h.left and h.left.left
	// are black, make h.left or one of its children red.
	private Node moveRedLeft(Node h) {
		flipColors(h);
		if (isRed(h.right.left)) {
			h.right = rotateRight(h.right);
			h = rotateLeft(h);
		}
		return h;
	}

	// Assuming that h is red and both h.right and h.right.left
	// are black, make h.right or one of its children red.
	private Node moveRedRight(Node h) {		
		flipColors(h);
		if (isRed(h.left.left)) {
			h = rotateRight(h);
		}
		return h;
	}

	// restore red-black tree invariant
	private Node balance(Node h) {
		// assert (h != null);

		if (isRed(h.right))                      h = rotateLeft(h);
		if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
		if (isRed(h.left) && isRed(h.right))     flipColors(h);

		h.N = size(h.left) + size(h.right) + 1;
		return h;
	}

    
    
    
    
    /*************************************************************************
     *  The Main Function
        Use this for testing
     *************************************************************************/
    public static void main(String[] args) {
        
        Scanner readerTest = null;

        try {
            //Change File name to test other test files.
            readerTest = new Scanner(new File("delete.txt"));
        } catch (IOException e) {
            System.out.println("Reading Oops");
        }
        
        RedBlackBST<Integer, Integer> test = new RedBlackBST<>();
        
        while(readerTest.hasNextLine()){
           String[] input  =readerTest.nextLine().split(" ");
           
           for(String x: input){
               System.out.print(x+" ");
           }
            
           System.out.println();
           switch (input[0]){
               case "insert":
                   Integer key = Integer.parseInt(input[1]);
                   Integer val = Integer.parseInt(input[2]);                 
                   test.insert(key,val);
                   printTree(test.root);
                   System.out.println();
                   break;
                   
               case "delete":
                    Integer key1 = Integer.parseInt(input[1]);
                    test.delete(key1);
                    printTree(test.root);
                    System.out.println();
                    break;
                   
               case "search":
                    Integer key2 = Integer.parseInt(input[1]);
                    Integer ans2 = test.search(key2);                    
                    System.out.println(ans2);
                    System.out.println();
                    break;   
                   
               case "getval":
                    Integer key3 = Integer.parseInt(input[1]);
                    Integer ans21 = test.getValByRank(key3);
                    System.out.println(ans21);
                    System.out.println();
                    break;
                   
               case "rank":
                    Integer key4 = Integer.parseInt(input[1]);
                    Object ans22 = test.rank(key4);
                    System.out.println(ans22);
                    System.out.println();
                    break;   
                   
               case "getelement":
                    Integer low = Integer.parseInt(input[1]);
                    Integer high = Integer.parseInt(input[2]);
                    List<Integer> testList = test.getElements(low,high);

                    for(Integer list : testList){
                        System.out.println(list);    
                    }
                   
                    break;
               
               default:
                    System.out.println("Error, Invalid test instruction! "+input[0]);    
           }
        }
        
    }    
    
    
    /*************************************************************************
     *  Prints the tree
     *************************************************************************/
    public static void printTree(RedBlackBST.Node node){
        
	    if (node == null){
		    return;
	    }
	   
	    printTree(node.left);
	    System.out.print(((node.color == true)? "Color: Red; ":"Color: Black; ") + "Key: " + node.key + "\n");
        printTree(node.right);
    }
}