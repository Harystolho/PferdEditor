package com.harystolho.pe.linkedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.harystolho.canvas.CanvasManager;
import com.harystolho.pe.Word;
import com.harystolho.pe.Word.TYPES;

/**
 * A Doubly LinkedList implementation used to store words in a file. It has
 * O(n/2) in all operations. It keeps track of the middle node using
 * {@link NodeIndexes} and uses that node as the pivot for operations
 * 
 * @author Harystolho
 *
 */

public class IndexLinkedList<E extends Word> implements List<E>, Iterable<E> {

	private Node root;
	private Node last;

	private NodeIndexes<E> nodeIndexes;

	private int size;

	public class Node {

		public Node() {
			data = null;
			left = null;
			right = null;
		}

		public Node(E data) {
			this();
			this.data = data;
		}

		private E data;
		private Node left;
		private Node right;

		public void setData(E data) {
			this.data = data;
		}

		public E getData() {
			return data;
		}

		public void setLeft(Node node) {
			left = node;
		}

		public void setRight(Node node) {
			right = node;
		}

		public Node getLeft() {
			return left;
		}

		public Node getRight() {
			return right;
		}

	}

	public IndexLinkedList() {
		root = null;
		last = root;
		size = 0;
		initNodeIndexes();
	}

	private void initNodeIndexes() {
		nodeIndexes = new NodeIndexes<>(this);
		nodeIndexes.initNodes();
	}

	/**
	 * Updates the index of the pivot nodes. This method must be called after a new
	 * <code>Node</code> is inserted<br>
	 * NOTE :Update the size before calling this method
	 * 
	 * @param beforeMiddleNode
	 */
	private void updateIndexes(boolean beforeMiddleNode) {
		nodeIndexes.update(beforeMiddleNode);
	}

	/**
	 * Uses the {@link Word#compareTo(Word)} to insert the node at the correct
	 * position
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean add(E data) {
		Node node = new Node(data);

		if (root == null) {
			root = node;
			last = node;
			nodeIndexes.setNodesAs(node);
			size++;

			// Because this is the first node, it has to update the pivot node in
			// CanvasManager
			CanvasManager.getInstance().setPivotNode(node);
		} else {
			Node middleNode = nodeIndexes.getMiddleIndex().getKey();

			switch (data.compareTo(middleNode.getData())) {
			case -1: // If word comes before the middle node
				Node leftNode = middleNode.getLeft();
				if (leftNode != null) {
					boolean inserted = false;
					// While the word's position is smaller then the leftNode, go left
					// If the leftNode becomes null the inserted boolean will remain false
					while (leftNode != null) {
						// Insert after the leftNode
						if (data.compareTo(leftNode.getData()) == 1 || data.compareTo(leftNode.getData()) == 0) {
							node.setRight(leftNode.getRight());
							node.setLeft(leftNode);
							leftNode.getRight().setLeft(node);
							leftNode.setRight(node);
							inserted = true;
							break;
						} else {
							leftNode = leftNode.getLeft();
						}
					}
					// If it didn't add the word in the loop above, it's because the word comes
					// before the root node
					// Insert the word before or after the root node
					if (!inserted) {
						switch (data.compareTo(root.getData())) {
						case -1:
							root.setLeft(node);
							node.setRight(root);
							root = node;
							CanvasManager.getInstance().updatePivotNode();
							break;
						case 0:
						case 1:
							node.setRight(root.getRight());
							node.setLeft(root);
							root.setRight(node);
							break;
						default:
							break;
						}
					}
				} else { // If there's no node before the middle node
					middleNode.setLeft(node);
					node.setRight(middleNode);
					root = node;
					// Because this is the first node, it has to update the pivot node in
					// CanvasManager
					CanvasManager.getInstance().setPivotNode(node);
				}
				size++;
				updateIndexes(true);
				break;
			case 0: // If the word is at the same position as the middle node, insert it after the
					// middle node
				node.setRight(middleNode.getRight());

				if (middleNode.getRight() != null) {
					middleNode.getRight().setLeft(node);
				} else {
					last = node;
				}

				middleNode.setRight(node);
				node.setLeft(middleNode);
				size++;
				updateIndexes(false);
				break;
			case 1: // If word comes after the middle node
				Node rightNode = middleNode.getRight();
				if (rightNode != null) {
					boolean inserted = false;
					// While the word's position is bigger then the rightNode, go right
					while (rightNode != null) {
						if (data.compareTo(rightNode.getData()) == -1) {
							node.setLeft(rightNode.getLeft());
							node.setRight(rightNode);
							rightNode.getLeft().setRight(node);
							rightNode.setLeft(node);
							inserted = true;
							break;
						} else {
							rightNode = rightNode.getRight();
						}
					}
					// If it didn't add the word in the loop above, it's because the word comes
					// after the last word
					// Insert the word before or after the last node
					if (!inserted) {
						switch (data.compareTo(last.getData())) {
						case -1:
							last.getLeft().setRight(node);
							node.setLeft(last.getLeft());
							last.setLeft(node);
							node.setRight(last);
							break;
						case 0:
						case 1:
							last.setRight(node);
							node.setLeft(last);
							last = node;
							break;
						default:
							break;
						}
					}
				} else { // If there is no node after the right node
					middleNode.setRight(node);
					node.setLeft(middleNode);
					last = node;
				}
				size++;
				updateIndexes(false);
				break;
			default:
				return false;
			}
		}

		return true;
	}

	public boolean addFirst(E data) {
		Node node = new Node(data);

		if (root == null) {
			nodeIndexes.setNodesAs(node);
			last = node;
		} else {
			node.setRight(root);
			root.setLeft(node);
		}

		root = node;
		size++;
		updateIndexes(true);
		return true;
	}

	public boolean addLast(E data) {
		Node node = new Node(data);

		if (root == null) {
			root = node;
			nodeIndexes.setNodesAs(node);
		} else {
			last.setRight(node);
			node.setLeft(last);
		}

		last = node;
		size++;
		updateIndexes(false);
		return true;
	}

	@Override
	public void clear() {
		root = null;
		last = null;
		size = 0;

		nodeIndexes.setNodesAs(null);
	}

	@Override
	public boolean contains(Object obj) {
		if (obj instanceof Word) {
			return contains((Word) obj);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean contains(E data) {

		if (size == 0 || root == null) {
			return false;
		}

		Node middleNode = nodeIndexes.getMiddleIndex().getKey();

		switch (data.compareTo(middleNode.getData())) {
		case -1:
			Node leftNode = middleNode.getLeft();
			if (leftNode != null) {
				while (leftNode != null) {
					if (data.compareTo(leftNode.getData()) == 0) {
						return true;
					} else {
						leftNode = leftNode.getLeft();
					}
				}
			}
			break;
		case 0:
			return true;
		case 1:
			Node rightNode = middleNode.getRight();
			if (rightNode != null) {
				while (rightNode != null) {
					if (data.compareTo(rightNode.getData()) == 0) {
						return true;
					} else {
						rightNode = rightNode.getRight();
					}
				}
			}
			break;
		default:
			break;
		}

		return false;
	}

	public E get(float x, float y) {
		Word temp = new Word(TYPES.NORMAL);
		temp.setX(x);
		temp.setY(y);

		return get(temp);
	}

	public E get(Word temp) {
		Node node = getNode(temp);
		if (node != null) {
			return node.getData();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Node getNode(Word temp) {
		if (size == 0 || root == null) {
			return null;
		}

		Node middleNode = nodeIndexes.getMiddleIndex().getKey();

		switch (temp.compareTo(middleNode.getData())) {
		case -1:
			Node leftNode = middleNode.getLeft();
			if (leftNode != null) {
				while (leftNode != null) {
					if (temp.compareTo(leftNode.getData()) == 0) {
						return leftNode;
					} else {
						leftNode = leftNode.getLeft();
					}
				}
			}
			break;
		case 0:
			return middleNode;
		case 1:
			Node rightNode = middleNode.getRight();
			if (rightNode != null) {
				while (rightNode != null) {
					if (temp.compareTo(rightNode.getData()) == 0) {
						return rightNode;
					} else {
						rightNode = rightNode.getRight();
					}
				}
			}
			break;
		default:
			break;
		}

		return null;
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<E> iterator() {
		Node nodePointingToRoot = new Node();
		nodePointingToRoot.setRight(root);
		return (Iterator<E>) new IndexLinkedListIterator(this, nodePointingToRoot);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListIterator<E> listIterator() {
		Node nodePointingToRoot = new Node();
		nodePointingToRoot.setRight(root);
		return (ListIterator<E>) new IndexLinkedListIterator(this, nodePointingToRoot);
	}

	@Override
	public boolean remove(Object obj) {
		if (obj instanceof Word) {
			remove((Word) obj);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void remove(E word) {
		Node middleNode = nodeIndexes.getMiddleIndex().getKey();

		word.setX(word.getX() + 1f);

		switch (word.compareTo(middleNode.getData())) {
		case -1: // If the word's position is smaller than the middle node's position
			Node leftNode = middleNode.getLeft();
			while (leftNode != null) {
				if (word.compareTo(leftNode.getData()) == 0) {
					if (leftNode.getLeft() != null) {
						leftNode.getLeft().setRight(leftNode.getRight());
					} else {
						root = leftNode.getRight();
						root.getData().setX(0);
						CanvasManager.getInstance().movePivotNodeRight();
					}
					if (leftNode.getRight() != null) {
						leftNode.getRight().setLeft(leftNode.getLeft());
					}
					leftNode = null;
				} else {
					leftNode = leftNode.getLeft();
				}
			}
			size--;
			updateIndexes(false);
			break;
		case 0: // If the word is at the middle node's position
			if (middleNode.getLeft() != null) {
				middleNode.getLeft().setRight(middleNode.getRight());
			} else {
				root = middleNode.getRight();
			}
			if (middleNode.getRight() != null) {
				middleNode.getRight().setLeft(middleNode.getLeft());
			} else {
				last = middleNode.getLeft();
			}

			// middleNode = null;

			size--;
			updateIndexes(true);
			break;
		case 1: // If the word's position is greater than the middle node's position
			Node rightNode = middleNode.getRight();
			while (rightNode != null) {
				if (word.compareTo(rightNode.getData()) == 0) {
					if (rightNode.getLeft() != null) {
						rightNode.getLeft().setRight(rightNode.getRight());
					}
					if (rightNode.getRight() != null) {
						rightNode.getRight().setLeft(rightNode.getLeft());
					} else { // last node
						last = rightNode.getLeft();
					}

					rightNode = null;
				} else {
					rightNode = rightNode.getRight();
				}
			}
			size--;
			updateIndexes(true);
			break;
		default:
			return;
		}

		if (size == 0) {
			clear();
		}

	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[size];

		Node node = root;

		for (int x = 0; x < size; x++) {
			array[x] = node;
			node = node.getRight();
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	public Word findLastWordIn(float y) {
		if (size == 0 || root == null) {
			return null;
		}

		if (size == 1) {
			return nodeIndexes.getMiddleIndex().getKey().getData();
		}

		Node middleNode = nodeIndexes.getMiddleIndex().getKey();
		float middleNodeY = middleNode.getData().getY();

		if (y < middleNodeY) {
			Node node = middleNode.getLeft();
			while (node != null) {
				if (node.getData().getY() == y) {
					return node.getData();
				}
				node = node.getLeft();
			}
		} else if (y >= middleNodeY) {
			Node node = middleNode.getRight();
			while (node != null) {
				if (node.getData().getY() > y) {
					return node.getLeft().getData();
				}

				if (node.getRight() != null) {
					node = node.getRight();
				} else {
					if (node.getData().getType() == TYPES.NEW_LINE) {
						if (node.getData().getY() == y) {
							return node.getData();
						}
						return null;
					}
					return node.getData();
				}
			}

		}

		return null;
	}

	@Override
	public E get(int idx) {

		Node node = getRoot();
		int currentIndex = 0;
		while (currentIndex < idx && node != null) {
			node = node.getRight();
			currentIndex++;
		}

		return node.getData();
	}

	/**
	 * Returns a list containing all the words from (initX, initY) to (lastX,
	 * lastY). InitX and initY have to be greater than 0.
	 * 
	 * @param initX
	 * @param initY
	 * @param lastX
	 * @param lastY
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Word> getWordsFrom(double initX, double initY, double lastX, double lastY) {
		List<Word> words = new ArrayList<>();

		// Exit case
		if (initX < 0 || initY < 0) {
			return words;
		}
		if (initY == lastY && initX > lastX) {
			return words;
		}

		Node startNode = nodeIndexes.getMiddleIndex().getKey(); // Start at the middle node
		if (startNode.getData().getY() > initY) { // If startNode comes after the initial Y,
			while (startNode.getData().getY() > initY) { // Move left
				startNode = startNode.getLeft();
			}
		} else {
			while (startNode.getData().getY() < initY) { // Move right
				startNode = startNode.getRight();
			}
		}

		if (startNode.getData().getX() > initX) {// If startNode comes after the initial X,
			while (startNode.getData().getX() > initX) { // Move left
				startNode = startNode.getLeft();
			}
		} else {
			while (startNode.getData().getX() + startNode.getData().getDrawingSize() <= initX) { // Move right
				startNode = startNode.getRight();
			}
		}

		Node endNode = startNode; // endNode can't come before startNode, so start from here and more right
		if (endNode.getData().getY() > lastY) { // If endNode comes after the last Y, move it left
			while (endNode.getData().getY() > lastY) { // Move up
				endNode = endNode.getLeft();
			}
			while (endNode.getData().getX() > lastX) { // Move left
				endNode = endNode.getLeft();
			}
		} else { // If endNode comes before the last Y, move it right
			while (endNode.getData().getY() < lastY) { // Move down
				endNode = endNode.getRight();
			}
			while (endNode.getData().getX() + endNode.getData().getDrawingSize() < lastX) { // Move right
				endNode = endNode.getRight();
			}
		}

		while (startNode != endNode) { // Add all words between start and end
			words.add(startNode.getData());
			startNode = startNode.getRight();
		}
		words.add(endNode.getData());

		return words;
	}

	public Node getFirstNode() {
		return root;
	}

	public E getFirst() {
		return getFirstNode().getData();
	}

	public E getLast() {
		return last.getData();
	}

	public Node getRoot() {
		return root;
	}

	public void printDebug() {
		printNodes();
		printIndexes();
	}

	private void printNodes() {
		System.out.println("---- NODES ------");
		for (E e : this) {
			System.out.print(e + ", ");
		}
		System.out.print("\n");
	}

	private void printIndexes() {
		if (nodeIndexes.getMiddleIndex().getKey() != null) {
			System.out.println("---- INDEXES ------");
			System.out.println("Middle: " + nodeIndexes.getMiddleIndex().getKey().getData() + " - "
					+ nodeIndexes.getMiddleIndex().getValue());
		}
	}

	public void printDebug2() {
		System.out.println(String.format("Size: %d\n", size));
		System.out.println("Root: " + root);
		System.out.println("Last: " + last);
	}

	// ---------- ALL METHODS BELOW ARE NOT IMPLEMENTED ------------------ //

	@Deprecated
	@Override
	public void add(int index, E data) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean addAll(Collection<? extends E> data) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean addAll(int index, Collection<? extends E> data) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean containsAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public int indexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public int lastIndexOf(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public ListIterator<E> listIterator(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public E remove(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public E set(int arg0, E arg1) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public List<E> subList(int arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	public <T> T[] toArray(T[] arg0) {
		throw new UnsupportedOperationException();
	}

}
