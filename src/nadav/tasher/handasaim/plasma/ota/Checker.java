package nadav.tasher.handasaim.plasma.ota;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

public class Checker {
	public static final String maintainer = "http://nockio.com/h/p/ota/rollout/";
	public static final String versionFile = "info.json";
	public static final String otaFile = "latest.jar";
	public static final String otaInstallerFile = "ota.jar";
	public static final File otaInstallerLocalFile=new File(System.getProperty("user.dir"),otaInstallerFile);
	public static final File runnableFile=new File(System.getProperty("user.dir"),otaFile);

	public static void checkOTA(double currentVersion, OTAListener resultHanlder) {
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
		s.close();
		JSONObject mObject=new JSONObject(sb.toString());
			if (mObject.has("version")) {
				if (mObject.getDouble("version") > currentVersion) {
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

	public static void downloadAndStartInstaller() {
		try {
			URL website;
			if (maintainer.endsWith("/")) {
				website = new URL(maintainer + otaInstallerFile);
			} else {
				website = new URL(maintainer + "/" + otaInstallerFile);
			}
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos= new FileOutputStream(otaInstallerLocalFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			Runtime.getRuntime().exec("java -jar "+otaInstallerLocalFile.toString());
			System.exit(0);
			}catch(Exception e) {
			}
	}

	public interface OTAListener {
		void onOTACheck(boolean updateAvailable);
	}
}
