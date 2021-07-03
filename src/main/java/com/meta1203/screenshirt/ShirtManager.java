package com.meta1203.screenshirt;

import java.io.File;
import java.io.FilenameFilter;

public class ShirtManager {
	public static String[] getShirts() {
		return (new File("shirts")).list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});
	}
}
