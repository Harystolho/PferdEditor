package com.harystolho.misc.explorer;

import java.io.File;

public interface FileInterface {

	public String getName();

	public File getDiskFile();

	default public boolean isParent(FileInterface supposedParent) {
		if (getDiskFile().getParentFile().equals(supposedParent.getDiskFile())) {
			return true;
		}
		return false;
	}

	default public int compareTo(FileInterface other) {
		return getName().toLowerCase().compareTo(other.getName().toLowerCase());
	}

}
