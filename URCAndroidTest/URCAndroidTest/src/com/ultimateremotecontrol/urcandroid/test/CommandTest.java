package com.ultimateremotecontrol.urcandroid.test;

import java.io.UnsupportedEncodingException;

import com.ultimateremotecontrol.urcandroid.model.Command;

import junit.framework.Assert;
import junit.framework.TestCase;

public class CommandTest extends TestCase {
	
	public void testCommandToByteArray() throws UnsupportedEncodingException {
		Command command = new Command(12, 3, 4, 123, 54, 23, 67, 23, 2, true, false, true, false, true, false, true, false);
		byte[] commandBytes = command.toByte();
		Assert.assertNotNull(commandBytes);
		String expectedResult = "X12;Y3;Z4;A123;B54;C23;D67;E23;F2;G1;H0;I1;J0;K1;L0;M1;N0;\n";
		Assert.assertEquals(expectedResult, new String(commandBytes, "US-ASCII"));
	}
	
	public void testCommandLimitsUpperValues() throws Throwable {
		Command command = new Command(512, 512, 512, 512, 512, 512, 512, 512, 512, true, true, true, true, true, true, true, true);
		byte[] commandBytes = command.toByte();
		Assert.assertNotNull(commandBytes);
		String expectedResult = "X255;Y255;Z255;A255;B255;C255;D255;E255;F255;G1;H1;I1;J1;K1;L1;M1;N1;\n";
		Assert.assertEquals(expectedResult, new String(commandBytes, "US-ASCII"));		
	}

	public void testCommandLimitsLowerValues() throws Throwable {
		Command command = new Command(-512, -512, -512, -512, -512, -512, -512, -512, -512, false, false, false, false, false, false, false, false);
		byte[] commandBytes = command.toByte();
		Assert.assertNotNull(commandBytes);
		String expectedResult = "X0;Y0;Z0;A0;B0;C0;D0;E0;F0;G0;H0;I0;J0;K0;L0;M0;N0;\n";
		Assert.assertEquals(expectedResult, new String(commandBytes, "US-ASCII"));		
	}
}
