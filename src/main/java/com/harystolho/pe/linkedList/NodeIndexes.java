package com.harystolho.pe.linkedList;

import java.util.ArrayList;
import java.util.List;

import com.harystolho.pe.Word;
import com.harystolho.pe.linkedList.IndexLinkedList.Node;

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
		return nodeIndexes.get(1);
	}

	// TODO document this
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
						middleIndex.setKey(middleIndex.getKey().getLeft());
					}
				} else {
					if (middleIndex.getKey().getRight() != null) {
						middleIndex.setValue(middleIndex.getValue() + 1);
						middleIndex.setKey(middleIndex.getKey().getRight());
					}
				}
			}
		} else if (diff < 0) { // Decrease Index
			for (int x = 0; x > diff; x--) {
				Pair<Node, Integer> middleIndex = getMiddleIndex();

				if (beforeMiddleNode) {
					if (middleIndex.getKey().getLeft() != null) {
						middleIndex.setValue(middleIndex.getValue() - 1);
						middleIndex.setKey(middleIndex.getKey().getLeft());
					}
				} else {
					if (middleIndex.getKey().getRight() != null) {
						middleIndex.setValue(middleIndex.getValue() - 1);
						middleIndex.setKey(middleIndex.getKey().getRight());
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
