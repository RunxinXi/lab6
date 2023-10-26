package edu.toronto.dbservice.types;

import java.io.Serializable;

public class Document implements Serializable {
	public String docname;
	public int docnumber;
	public String description;
	
	public Document(String n, int u, String d) {
		this.docname = n;
		this.docnumber = u;
		this.description = d;
	}
	
	
	public String getdocname() {
		return docname;
	}
}