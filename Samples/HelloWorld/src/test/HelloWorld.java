package test;

import java.util.Date;

/**
 * @author pradeep
 *
 */
public class HelloWorld {
	private String instantiatedDate;

	/**
	 * 
	 */
	public HelloWorld() {
		// TODO Auto-generated constructor stub
		instantiatedDate = new Date().toString();
	}

	public String helloOperation(String name) {
		return instantiatedDate + ": Hello - " + name;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HelloWorld hw = new HelloWorld();
		System.out.println(hw.helloOperation("John"));
	}

}
