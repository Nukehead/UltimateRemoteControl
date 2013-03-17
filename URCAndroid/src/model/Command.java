package model;

public class Command implements Comparable<Command> {

	@Override
 	public int compareTo(Command another) {
		if (this == another) {
			return 0;
		}
		return 0;
	}
	
	public byte[] toByte()
	{
		return new byte[0];
	}

}
