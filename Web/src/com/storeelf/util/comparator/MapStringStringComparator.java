package com.storeelf.util.comparator;

import java.util.Comparator;
import java.util.Map;

/**
 * 
 * <B>Purpose:</B> This Class is used to create a generic Comparator for the
 * StoreElfReportBean. Custom Comparators will need to be developed if the key is
 * not of type String<BR/>
 * <B>Creation Date:</B> Oct 4, 2011 10:07:47 AM<BR/>
 */
public class MapStringStringComparator implements
		Comparator<Map.Entry<String, String>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Map.Entry<String, String> o1,
			Map.Entry<String, String> o2) {
		if((o1==null||o1.getValue()==null) && (o2==null||o2.getValue()==null)){
			return 0;
		}
		if(o1==null||o1.getValue()==null){
			return -1;
		}
		if(o2==null||o2.getValue()==null){
			return 1;
		}
		return ((o1).getValue()).compareTo((o2).getValue());
	}
}
