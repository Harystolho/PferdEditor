package com.harystolho.pe.linkedList;

import java.util.ListIterator;

import com.harystolho.pe.Word;
import com.harystolho.pe.linkedList.IndexLinkedList.Node;

@SuppressWarnings("rawtypes")
public class IndexLinkedListIterator implements ListIterator<Word> {

	private Node next;

	public IndexLinkedListIterator(IndexLinkedList<? extends Word> e, Node nodePointingToRoot) {
		next = nodePointingToRoot;
	}

	@Override
	public boolean hasNext() {
		if (next.getRight() != null)
			return true;
		return false;
	}

	@Override
	public Word next() {
		next = next.getRight();
		Word word = next.getData();
		return word;
	}

	@Override
	public boolean hasPrevious() {
		if (next.getLeft() != null)
			return true;
		return false;
	}

	@Override
	public Word previous() {
		next = next.getLeft();
		Word word = next.getData();
		return word;
	}

	// ---------- ALL METHODS BELOW ARE NOT IMPLEMENTED ------------------ //

	@Override
	public int previousIndex() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(Word e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(Word e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int nextIndex() {
		throw new UnsupportedOperationException();
	}
}
