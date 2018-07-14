package com.harystolho.pe;

import java.util.LinkedList;
import java.util.ListIterator;

public class WordLinkedList extends LinkedList<Word> {

	private static final long serialVersionUID = 4234622452530895473L;

	public WordLinkedList() {
		super();
	}

	@Override
	public boolean add(Word wordToAdd) {

		ListIterator<Word> it = listIterator();

		while (it.hasNext()) {
			Word word = it.next();

			switch (wordToAdd.compareTo(word)) {
			case -1:
				if (it.hasPrevious()) {
					it.previous();
					it.add(wordToAdd);
				} else {
					addFirst(wordToAdd);
				}
				return true;
			case 0:
				it.add(wordToAdd);
				return true;
			default:
				break;
			}

		}

		it.add(wordToAdd);

		return true;
	}

	/**
	 * @param x
	 * @param y
	 * @return the Word at this position or <code>null</code> if there is no word at
	 *         this position.
	 */
	public Word get(double x, double y) {

		ListIterator<Word> it = listIterator();

		while (it.hasNext()) {
			Word word = it.next();

			if (word.getY() == y) {
				if (word.getX() < x && word.getX() + word.getDrawingSize() > x) {
					return word;
				}
			}

		}

		return null;

	}

}
