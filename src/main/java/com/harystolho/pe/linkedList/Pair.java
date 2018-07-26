package com.harystolho.pe.linkedList;

public class Pair<E, F> {

	private E e;
	private F f;

	public Pair(E e, F f) {
		this.e = e;
		this.f = f;
	}

	public E getKey() {
		return e;
	}

	public void setKey(E e) {
		this.e = e;
	}

	public F getValue() {
		return f;
	}

	public void setValue(F f) {
		this.f = f;
	}

}
