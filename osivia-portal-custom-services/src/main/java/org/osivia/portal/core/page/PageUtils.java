package org.osivia.portal.core.page;

import java.util.Comparator;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.portal.core.model.portal.Page;
import org.osivia.portal.core.dynamic.DynamicPageBean;


public class PageUtils {
	protected static final Log logger = LogFactory.getLog(PageUtils.class);
	
	public static final String TAB_ORDER = "order";

	public static final Comparator<Object> orderComparator = new Comparator<Object>() {
		
		public int compare(Object o1, Object o2) {
			Page page1 = (Page) o1;
			Page page2 = (Page) o2;
			
			
			String orderString1 = page1.getDeclaredProperty(TAB_ORDER);
			String orderString2 = page2.getDeclaredProperty(TAB_ORDER);
			
			int order1 = 0;
			int order2 = 0;

			//
			try {
				order1 = Integer.parseInt(orderString1);

				//
				try {
					order2 = Integer.parseInt(orderString2);
				} catch (NumberFormatException e) {
					// We have window2>window1
					order2 = DynamicPageBean.DYNAMIC_PRELOADEDPAGES_FIRST_ORDER - 1;
				}
			} catch (NumberFormatException e1) {
				try {
					

					// We have order2=0 and order1=1 and thus window1>window2
					order1 = DynamicPageBean.DYNAMIC_PRELOADEDPAGES_FIRST_ORDER - 1;
					
					order2 = Integer.parseInt(orderString2);
				} catch (NumberFormatException e2) {
					// Orders have the same value of zero that will lead to the
					// comparison of their ids
					order2 = DynamicPageBean.DYNAMIC_PRELOADEDPAGES_FIRST_ORDER - 1;
				}
			}

			// If order are the same we compare the id
			if (order1 == order2) {
				return page1.getId().compareTo(page2.getId());
			} else {
				return order1 - order2;
			}

		}
	};
	
	public static final Locale FR_LOCALE = Locale.FRENCH;
	
	public static final Comparator<Object> nameComparator = new Comparator<Object>() {

		public int compare(Object o1, Object o2) {
			
			Page page1 = ((Page) o1);
			Page page2 = ((Page) o2);

			String page1Name = page1.getDisplayName().getString(FR_LOCALE, true);
			if(page1Name == null)
				page1Name = page1.getName();
			
			String page2Name = page2.getDisplayName().getString(FR_LOCALE, true);
			if(page2Name == null)
				page2Name = page2.getName();
			
			int result = page1Name.compareToIgnoreCase(page2Name);
			if(result == 0)
				result = page1.getId().compareTo(page2.getId());
			
			return result;
		}
		
	};

}
