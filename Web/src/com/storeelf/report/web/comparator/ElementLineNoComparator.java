package com.storeelf.report.web.comparator;

import java.util.Comparator;

import org.w3c.dom.Element;
/**
 * 
 * ElementLineNoComparator is added as part of Warehouse Transfer StoreElf Utility Screen
 *
 */

public class ElementLineNoComparator implements Comparator<Element>{

	@Override
	public int compare(Element ele1, Element ele2) {
		int lineNo1 = Integer.parseInt(((Element)ele1).getElementsByTagName("td").item(1).getTextContent());
		int lineNo2 = Integer.parseInt(((Element)ele2).getElementsByTagName("td").item(1).getTextContent());
		return lineNo1-lineNo2;
	}

}
