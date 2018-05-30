package nadav.tasher.scheduleboard.board.views;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nadav.tasher.scheduleboard.board.Utils;

public class BirthdayView extends JPanel {
	public static final File birthdayList = new File(System.getProperty("user.dir"), "birthdayList.xlsx");
	JPanel birthdays, nine, ten, eleven, tw;

	public BirthdayView(String maintainer) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));
		setBackground(Color.WHITE);
		JLabel introduction = Utils.getLabel("\u200Fמזל טוב לילדי השבוע!");
		Utils.enlargeFont(introduction);
		birthdays = new JPanel();
		birthdays.setLayout(new GridLayout(1, 4));
		birthdays.setBackground(Color.WHITE);
		birthdays.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
		add(introduction);
		add(birthdays);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					downloadFile(maintainer);
					updateView();
					try {
						Thread.sleep(60000 * 30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void downloadFile(String maintainer) {
		try {
			URL website;
			if (maintainer.endsWith("/")) {
				website = new URL(maintainer + "excel/birthdays.xlsx");
			} else {
				website = new URL(maintainer + "/excel/birthdays.xlsx");
			}
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(birthdayList);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
		} catch (Exception e) {

		}
	}

	public void updateView() {
		Calendar current = Calendar.getInstance();
		birthdays.removeAll();
		nine = new JPanel();
		ten = new JPanel();
		eleven = new JPanel();
		tw = new JPanel();
		nine.add(Utils.enlarge(Utils.getLabel("שכבת ט'")));
		ten.add(Utils.enlarge(Utils.getLabel("שכבת י'")));
		eleven.add(Utils.enlarge(Utils.getLabel("שכבת יא'")));
		tw.add(Utils.enlarge(Utils.getLabel("שכבת יב'")));
		birthdays.add(tw);
		birthdays.add(eleven);
		birthdays.add(ten);
		birthdays.add(nine);
		tw.setBackground(Color.WHITE);
		eleven.setBackground(Color.WHITE);
		ten.setBackground(Color.WHITE);
		nine.setBackground(Color.WHITE);
		nine.setLayout(new BoxLayout(nine, BoxLayout.Y_AXIS));
		ten.setLayout(new BoxLayout(ten, BoxLayout.Y_AXIS));
		eleven.setLayout(new BoxLayout(eleven, BoxLayout.Y_AXIS));

		ArrayList<Birthday> bds = getBirthdaysForWeek(current);
		for (int b = 0; b < bds.size(); b++) {
			JLabel birthday = Utils.getLabel(bds.get(b).name + " - " + bds.get(b).day + "." + bds.get(b).month);
			Utils.enlargeFont(birthday);
			if (bds.get(b).grade.equals("ט")) {
				nine.add(birthday);
			} else if (bds.get(b).grade.equals("י")) {
				ten.add(birthday);
			} else if (bds.get(b).grade.equals("יא")) {
				eleven.add(birthday);
			} else if (bds.get(b).grade.equals("יב")) {
				tw.add(birthday);
			}

		}
	}

	private ArrayList<Birthday> getBirthdaysForWeek(Calendar current) {
		ArrayList<Birthday> bds = new ArrayList<>();
		int startMonth = current.get(Calendar.MONTH)+1;
		int startDay = current.get(Calendar.DAY_OF_MONTH);
		int endMonth = startMonth, endDay = startDay + 7;
		switch (startMonth) {
		case 1:
			if (endDay > 31) {
				endMonth = startMonth + 1;
				endDay %= 31;
			}
			break;
		case 2:
			if (endDay > 28) {
				endMonth = startMonth + 1;
				endDay %= 28;
			}
			break;
		case 3:
			if (endDay > 31) {
				endMonth = startMonth + 1;
				endDay %= 31;
			}
			break;
		case 4:
			if (endDay > 30) {
				endMonth = startMonth + 1;
				endDay %= 30;
			}
			break;
		case 5:
			if (endDay > 31) {
				endMonth = startMonth + 1;
				endDay %= 31;
			}
			break;
		case 6:
			if (endDay > 30) {
				endMonth = startMonth + 1;
				endDay %= 30;
			}
			break;
		case 7:
			if (endDay > 31) {
				endMonth = startMonth + 1;
				endDay %= 31;
			}
			break;
		case 8:
			if (endDay > 31) {
				endMonth = startMonth + 1;
				endDay %= 31;
			}
			break;
		case 9:
			if (endDay > 30) {
				endMonth = startMonth + 1;
				endDay %= 30;
			}
			break;
		case 10:
			if (endDay > 31) {
				endMonth = startMonth + 1;
				endDay %= 31;
			}
			break;
		case 11:
			if (endDay > 30) {
				endMonth = startMonth + 1;
				endDay %= 30;
			}
			break;
		case 12:
			if (endDay > 31) {
				endMonth = 1;
				endDay %= 31;
			}
			break;
		}
		Sheet s = getSheet(birthdayList);
		for (int i = 1; i < s.getLastRowNum() + 1; i++) {
			String name = s.getRow(i).getCell(0).getStringCellValue();
			String grade = s.getRow(i).getCell(1).getStringCellValue();
			int month = (int) s.getRow(i).getCell(2).getNumericCellValue();
			int day = (int) s.getRow(i).getCell(3).getNumericCellValue();
			Birthday currentBd = new Birthday();
			currentBd.day = day;
			currentBd.month = month;
			currentBd.grade = grade;
			currentBd.name = name;
			if ((day >= startDay || day <= endDay) && (month == startMonth || month == endMonth)) {
				bds.add(currentBd);
			}
		}
		return bds;
	}

	private Sheet getSheet(File f) {
		try {
			if (f.toString().endsWith(".xls")) {
				POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(f));
				Workbook workBook = new HSSFWorkbook(fileSystem);
				return workBook.getSheetAt(0);
			} else {
				XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(f));
				return workBook.getSheetAt(0);
			}
		} catch (IOException ignored) {
			return null;
		}
	}

	private class Birthday {
		int month, day;
		String grade, name;
	}
}
