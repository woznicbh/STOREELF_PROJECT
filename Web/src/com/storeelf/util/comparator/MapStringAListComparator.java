package com.storeelf.util.comparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * 
 * <B>Purpose:</B> This Class is used to create a generic Comparator for the
 * StoreElfReportBean. Custom Comparators will need to be developed if the key is
 * not of type String<BR/>
 * <B>Creation Date:</B> Oct 4, 2011 10:07:47 AM<BR/>
 */
public class MapStringAListComparator implements
		Comparator<Map.Entry<String, ArrayList<String>>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Map.Entry<String, ArrayList<String>> o1,
			Map.Entry<String, ArrayList<String>> o2) {
		return ((o1).getKey()).compareTo((o2).getKey());
	}
}
