package com.harystolho.misc.explorer;

public interface FileInterface {

	public String getName();

	default public int compareTo(FileInterface other) {
		return getName().toLowerCase().compareTo(other.getName().toLowerCase());
	}

}
