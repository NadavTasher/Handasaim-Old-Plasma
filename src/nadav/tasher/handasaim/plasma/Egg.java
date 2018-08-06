package nadav.tasher.handasaim.plasma;

import java.util.Random;

public class Egg {
	private static String[] goodDayMessages= {
			"יום נעים","יום מהנה","יום טוב","יום מרנין"
	};
	
	public static final int TYPE_GOOD_DAY=0;
	public static final int TYPE_OTHER=1;
	
	public static String dispense(int type) {
		switch(type) {
		case TYPE_GOOD_DAY:
			return goodDayMessages[new Random().nextInt(goodDayMessages.length)];
		case TYPE_OTHER:
			return null;
		}
		return "";
	}
}
