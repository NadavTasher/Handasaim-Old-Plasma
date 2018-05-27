package nadav.tasher.scheduleboard.ota;

public class Checker {
	public static void checkOTA(String maintainer, OTAListener resultHanlder) {
		
	}
	
	public interface OTAListener{
		void onOTACheck(boolean updateAvailable);
	}
}
