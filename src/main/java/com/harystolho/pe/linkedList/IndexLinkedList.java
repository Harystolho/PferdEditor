package com.harystolho.pe.linkedList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

	class Node {

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
						if (data.compareTo(leftNode.getData()) == 1) { // Insert after the node
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
				}
				size++;
				updateIndexes(true);
				break;
			case 0: // If the word is at the same position as the middle node, insert it after the
					// middle node
				node.setRight(middleNode.getRight());
				
				if(middleNode.getRight() != null) {
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
		if (size == 0 || root == null) {
			return null;
		}

		Node middleNode = nodeIndexes.getMiddleIndex().getKey();

		Word temp = new Word(TYPES.NORMAL);
		temp.setX(x);
		temp.setY(y);

		switch (temp.compareTo(middleNode.getData())) {
		case -1:
			Node leftNode = middleNode.getLeft();
			if (leftNode != null) {
				while (leftNode != null) {
					if (temp.compareTo(leftNode.getData()) == 0) {
						return leftNode.getData();
					} else {
						leftNode = leftNode.getLeft();
					}
				}
			}
			break;
		case 0:
			return middleNode.getData();
		case 1:
			Node rightNode = middleNode.getRight();
			if (rightNode != null) {
				while (rightNode != null) {
					if (temp.compareTo(rightNode.getData()) == 0) {
						return rightNode.getData();
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

	// TODO improve
	public Word findLastWordIn(float y) {
		if (size == 0 || root == null) {
			return null;
		}

		Word lastWord = new Word(TYPES.NORMAL);
		lastWord.setX(0f);

		for (Word w : this) {
			if (w.getY() == y) {
				if (w.getX() >= lastWord.getX()) {
					lastWord = w;
				}
			} else if (w.getY() > y) {
				break;
			}
		}

		return lastWord;

	}

	public E getFirst() {
		return root.getData();
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
	public E get(int arg0) {
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
