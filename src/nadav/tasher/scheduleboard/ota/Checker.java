package nadav.tasher.scheduleboard.ota;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import org.json.JSONObject;

public class Checker {
	public static final String versionFile = "latest.json";
	public static final String otaFile = "latestota.jar";
	public static final String otaInstallerFile = "ota.jar";

	public static void checkOTA(String maintainer, double currentVersion,OTAListener resultHanlder) {
		try {
		URL website;
		if (maintainer.endsWith("/")) {
			website = new URL(maintainer + versionFile);
		} else {
			website = new URL(maintainer + "/" + versionFile);
		}
		Scanner s = new Scanner(website.openStream());
		StringBuilder sb=new StringBuilder();
		while(s.hasNextLine()) {
			sb.append(s.nextLine());
		}
		JSONObject mObject=new JSONObject(sb.toString());
		if(mObject.has("latestVersion")) {
			if(mObject.getDouble("latestVersion")>currentVersion) {
				resultHanlder.onOTACheck(true);
			}else {
				resultHanlder.onOTACheck(false);

			}
		}else {
			resultHanlder.onOTACheck(false);
		}
		}catch(Exception e) {
			resultHanlder.onOTACheck(false);
		}
	}

	public interface OTAListener {
		void onOTACheck(boolean updateAvailable);
	}
}
