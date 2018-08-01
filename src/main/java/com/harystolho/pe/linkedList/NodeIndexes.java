package com.harystolho.pe.linkedList;

import java.util.ArrayList;
import java.util.List;

import com.harystolho.pe.Word;
import com.harystolho.pe.linkedList.IndexLinkedList.Node;

/**
 * This class keeps a reference to the middle node of the linked list (If the
 * list has 20 elements, it will keep a reference to the 10th node). This is
 * used to improve efficiency on basic operations. When a node is either removed
 * or added to the list, the method {@link #update(boolean)} must be called.
 * 
 * @author Harystolho
 *
 * @param <G>
 */
@SuppressWarnings("rawtypes")
public class NodeIndexes<G extends Word> {

	private List<Pair<Node, Integer>> nodeIndexes;
	private IndexLinkedList<G> list;

	public NodeIndexes(IndexLinkedList<G> indexLinkedList) {
		nodeIndexes = new ArrayList<>(3);
		this.list = indexLinkedList;
	}

	public void initNodes() {
		nodeIndexes.add(new Pair<>(null, 0));
		nodeIndexes.add(new Pair<>(null, 1));
		nodeIndexes.add(new Pair<>(null, 0));
	}

	public void setNodesAs(Node node) {
		for (Pair<Node, Integer> p : nodeIndexes) {
			p.setKey(node);
		}
	}

	public Pair<Node, Integer> getMiddleIndex() {
		return nodeIndexes.get(nodeIndexes.size() / 2);
	}

	/**
	 * Updates the middle node's reference.
	 * 
	 * @param beforeMiddleNode if the element was added or removed before the middle
	 *                         node, then <code>true</code>
	 */
	public void update(boolean beforeMiddleNode) {
		// FIRST

		// MIDDLE
		int diff = (list.size() / 2) - getMiddleIndex().getValue();

		if (diff > 0) { // Increase index
			for (int x = 0; x < diff; x++) {
				Pair<Node, Integer> middleIndex = getMiddleIndex();

				if (beforeMiddleNode) {
					if (middleIndex.getKey().getLeft() != null) {
						middleIndex.setValue(middleIndex.getValue() + 1);
						middleIndex.setKey(middleIndex.getKey().getLeft()); // Move reference left
					}
				} else {
					if (middleIndex.getKey().getRight() != null) {
						middleIndex.setValue(middleIndex.getValue() + 1);
						middleIndex.setKey(middleIndex.getKey().getRight()); // Move reference right
					}
				}
			}
		} else if (diff < 0) { // Decrease Index
			for (int x = 0; x > diff; x--) {
				Pair<Node, Integer> middleIndex = getMiddleIndex();

				if (beforeMiddleNode) {
					if (middleIndex.getKey().getLeft() != null) {
						middleIndex.setValue(middleIndex.getValue() - 1);
						middleIndex.setKey(middleIndex.getKey().getLeft()); // Move reference left
					}
				} else {
					if (middleIndex.getKey().getRight() != null) {
						middleIndex.setValue(middleIndex.getValue() - 1);
						middleIndex.setKey(middleIndex.getKey().getRight()); // Move reference right
					}
				}
			}
		}

		// LAST
	}

	public void clear() {
		nodeIndexes.clear();
	}

}
