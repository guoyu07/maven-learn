package com.shimh;

import org.junit.Assert;
import org.junit.Test;

public class HelloWorldTest {
	
	@Test
	public void test1(){
		HelloWorld h = new HelloWorld();
		Assert.assertEquals("hello", h.hello());
	}
}
