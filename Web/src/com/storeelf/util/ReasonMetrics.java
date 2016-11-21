package com.storeelf.util;

public class ReasonMetrics {

	String id;
	String metric;
	String value;
	String reason;
	String color;
	String weight;
	String desc;
	String time;
	String colorUpd;
	public ReasonMetrics(){
		id=null;
		metric =null;
		value = null;
		reason = null;
		color=null;
		weight=null;
		desc=null;
		time=null;
	}
	public ReasonMetrics(String i,String m, String v, String r,String c,String w, String d, String t, String cu){
		id=i;
		metric = m;
		value = v;
		reason =r;
		color=c;
		weight=w;
		desc=d;
		time=t;
		colorUpd = cu;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMetric() {
		return metric;
	}
	public void setMetric(String metric) {
		this.metric = metric;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
