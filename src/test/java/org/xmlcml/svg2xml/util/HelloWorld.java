package org.xmlcml.svg2xml.util;

public class HelloWorld {

	private String hello;

	public HelloWorld() {
		hello = "Hello World";
	}
	
	@Override
	public String toString() {
		return hello;
	}
	public static void main(String[] args) {
		HelloWorld hw = new HelloWorld();
		System.out.println(hw);
		System.out.println(hw.toString());
	}
	
	
}
