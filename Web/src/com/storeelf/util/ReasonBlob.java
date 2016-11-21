package com.storeelf.util;

import java.util.ArrayList;

public class ReasonBlob {


	String color;
	public int redcount=0;
	public int yellowcount=0;
	public ArrayList<ReasonMetrics> kpis;
	public ArrayList<ReasonMetrics> reasonsWhy;
	public int redmax=0;
	public int yellowmax=0;
	public String timestamp="";
	public String important="N";
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public ReasonBlob(){
		color=null;
		reasonsWhy=new ArrayList<ReasonMetrics>();
		kpis=new ArrayList<ReasonMetrics>();
		
	}
	public ReasonBlob(String c){
		color=c;
		reasonsWhy=new ArrayList<ReasonMetrics>();
		kpis=new ArrayList<ReasonMetrics>();
	}
	
	
}
