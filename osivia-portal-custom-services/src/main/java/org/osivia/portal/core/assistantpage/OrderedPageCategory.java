package org.osivia.portal.core.assistantpage;



public class OrderedPageCategory implements Comparable<OrderedPageCategory> {

	private int order;
	private String code;
	private String label;
	
	public String getLabel() {
		return label;
	}

	public String getCode() {
		return code;
	}

	public int getOrder() {
		return order;
	}

	public OrderedPageCategory(int order, String code, String label) {
		super();
		this.order = order;
		this.code = code;
		this.label = label;
	}

	public int compareTo(OrderedPageCategory page) {
		return this.getOrder() - page.getOrder();
	}

}
