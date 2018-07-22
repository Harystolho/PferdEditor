package com.harystolho.pe;

import com.harystolho.pe.Word.TYPES;

class BTNode {

	BTNode left, right;

	Word data;

	/* Constructor */

	public BTNode() {

		left = null;

		right = null;

		data = null;

	}

	/* Constructor */

	public BTNode(Word n) {
		left = null;

		right = null;

		data = n;

	}

	/* Function to set left node */

	public void setLeft(BTNode n) {
		left = n;
	}

	/* Function to set right node */

	public void setRight(BTNode n) {
		right = n;
	}

	/* Function to get left node */

	public BTNode getLeft() {
		return left;
	}

	/* Function to get right node */

	public BTNode getRight() {

		return right;

	}

	/* Function to set data to node */

	public void setData(Word d) {
		data = d;
	}

	/* Function to get data from node */

	public Word getData() {
		return data;
	}

}

/* Class BT */

public class BT {

	private BTNode root;

	public BT() {
		root = null;
	}

	/* Function to check if tree is empty */

	public boolean isEmpty() {

		return root == null;

	}

	/* Functions to insert data */

	public void insert(Word data) {
		root = insert(root, data);
	}

	/* Function to insert data recursively */

	private BTNode insert(BTNode node, Word data) {
		if (node == null)
			node = new BTNode(data);
		else {
			if (node.data.compareTo(data) == -1)
				node.right = insert(node.right, data);
			else
				node.left = insert(node.left, data);
		}

		return node;

	}

	/* Functions to delete data */

	public void delete(Word k) {

		if (isEmpty())
			System.out.println("Tree Empty");
		else if (search(k) == null)
			System.out.println("Sorry " + k + " is not present");
		else {
			root = delete(root, k);
			System.out.println(k + " deleted from the tree");
		}

	}

	private BTNode delete(BTNode root, Word k) {
		BTNode p, p2, n;

		if (root.data.compareTo(k) == 0) {
			BTNode lt, rt;

			lt = root.getLeft();

			rt = root.getRight();

			if (lt == null && rt == null)
				return null;
			else if (lt == null) {
				p = rt;
				return p;
			} else if (rt == null) {
				p = lt;
				return p;
			} else {
				p2 = rt;
				p = rt;
				while (p.getLeft() != null)
					p = p.getLeft();
				p.setLeft(lt);
				return p2;

			}

		}

		if (k.compareTo(root.data) == -1) {
			n = delete(root.getLeft(), k);

			root.setLeft(n);
		} else {
			n = delete(root.getRight(), k);
			root.setRight(n);
		}
		return root;
	}

	/* Function to count number of nodes */
	public int countNodes() {
		return countNodes(root);
	}

	/* Function to count number of nodes recursively */
	private int countNodes(BTNode r) {

		if (r == null)
			return 0;
		else {
			int l = 1;

			l += countNodes(r.getLeft());

			l += countNodes(r.getRight());

			return l;
		}
	}

	/* Function to search for an element */
	public BTNode search(Word val) {
		return search(root, val);
	}

	/* Function to search for an element recursively */
	private BTNode search(BTNode r, Word val) {
		if (r.getData() == val) {
			return r;
		} else if (r.data.compareTo(val) >= 0) {
			return search(r.getLeft(), val);
		} else if (r.data.compareTo(val) == -1) {
			return search(r.getRight(), val);
		}

		return null;
	}

	public Word get(double cursorX, double cursorY) {
		Word word = new Word(TYPES.NORMAL);
		word.setX((float) cursorX);
		word.setY((float) cursorY);
		return search(word).data;
	}

	public Word max() {
		return max(root).data;
	}

	public BTNode max(BTNode node) {
		BTNode currentNode = node;
		BTNode lastNode = null;
		while (currentNode != null) {
			lastNode = currentNode;
			currentNode = currentNode.right;
		}
		return lastNode;
	}

	/* Function for inorder traversal */
	public void inorder() {
		inorder(root);
	}

	private void inorder(BTNode r) {

		if (r != null)

		{

			inorder(r.getLeft());

			System.out.print(r.getData() + " ");

			inorder(r.getRight());

		}

	}

	/* Function for preorder traversal */

	public void preorder() {

		preorder(root);

	}

	private void preorder(BTNode r) {

		if (r != null)

		{

			System.out.print(r.getData() + " ");

			preorder(r.getLeft());

			preorder(r.getRight());

		}

	}

	/* Function for postorder traversal */

	public void postorder() {

		postorder(root);

	}

	private void postorder(BTNode r) {

		if (r != null)

		{

			postorder(r.getLeft());

			postorder(r.getRight());

			System.out.print(r.getData() + " ");

		}

	}

}