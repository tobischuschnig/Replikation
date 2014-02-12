package model;

import java.io.File;
import java.io.Serializable;

public class FileVorlage implements Serializable{
	private byte[] f;
	private String name;
	private int method; //0=Create, 1=Delete, 2=Modify
	
	public FileVorlage(byte[] fi, String n, int m){
		f = fi;
		name = n;
		method = m;
	}

	public byte[] getF() {
		return f;
	}

	public void setF(byte[] f) {
		this.f = f;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMethod() {
		return method;
	}

	public void setMethod(int method) {
		this.method = method;
	}
}
