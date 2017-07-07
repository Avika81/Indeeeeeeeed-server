package Network;

public final class Util {
	
	public static String addColumns(String... args) {
		String retVal = "";
		
		for(int i=0; i<args.length; i++) {
			retVal = retVal.concat(args[i]);
			if(i<args.length-1)
				retVal = retVal.concat(Constants.FIELD_SEPERATOR);
		}		
		retVal = retVal.concat(Constants.GAME_SEPERATOR);
		return retVal;
	}
	
	public static String numToString(float num) {
//		System.out.println("num: " + num);
		String retVal = Integer.toString((int)(Math.floor((Math.abs(num) + 100) * 100)));
//		System.out.println("retVal: " + retVal);
		retVal = retVal.substring(1, 3) + '.' + retVal.substring(3, 5);
		if (num < 0)
			retVal = '-' + retVal;
		else
			retVal = '+' + retVal;
		return retVal;
	}
	
	public static float addPercent(float bonus) {
		return (1+bonus/100);
	}
	
	public static float subtractPercent(float bonus) {
		return (1-bonus/100);
	}
	
	public static float applyPercent(float bonus) {
		return (bonus/100);
	}
}
