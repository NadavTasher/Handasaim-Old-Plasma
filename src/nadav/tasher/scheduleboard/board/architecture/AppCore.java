package nadav.tasher.scheduleboard.board.architecture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFTextbox;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AppCore {
	public static int startReadingRow(Sheet s) {
        Cell secondCell = s.getRow(0).getCell(1);
        if (secondCell != null) {
            return 0;
        } else {
            return 1;
        }
    }

    public static String getRealTimeForHourNumber(int hour) {
        switch (hour) {
            case 0:
                return "07:45";
            case 1:
                return "08:30";
            case 2:
                return "09:15";
            case 3:
                return "10:15";
            case 4:
                return "11:00";
            case 5:
                return "12:10";
            case 6:
                return "12:55";
            case 7:
                return "13:50";
            case 8:
                return "14:35";
            case 9:
                return "15:30";
            case 10:
                return "16:15";
            case 11:
                return "17:00";
            case 12:
                return "17:45";
        }
        return null;
    }

    public static String getRealEndTimeForHourNumber(int hour) {
        switch (hour) {
            case 0:
                return "08:30";
            case 1:
                return "09:15";
            case 2:
                return "10:00";
            case 3:
                return "11:00";
            case 4:
                return "11:45";
            case 5:
                return "12:55";
            case 6:
                return "13:40";
            case 7:
                return "14:35";
            case 8:
                return "15:20";
            case 9:
                return "16:15";
            case 10:
                return "17:00";
            case 11:
                return "17:45";
            case 12:
                return "18:30";
        }
        return null;
    }

    public static ArrayList<String> getMessages(Sheet sheet){
        ArrayList<String> messages=new ArrayList<>();
        HSSFPatriarch patriarch = (HSSFPatriarch)sheet.createDrawingPatriarch();
        List<HSSFShape> shapes=patriarch.getChildren();
        for(int s=0;s<shapes.size();s++){
            if(shapes.get(s) instanceof HSSFTextbox){
                try {
                    HSSFShape mShape = shapes.get(s);
                    if (mShape != null) {
                        HSSFTextbox mTextShape = (HSSFTextbox) mShape;
                        HSSFRichTextString mString = mTextShape.getString();
                        if (mString != null) {
                            messages.add(mString.getString());
                        }
                    }
                }catch (NullPointerException ignored){

                }
//                messages.add(((HSSFTextbox)shapes.get(s)).getString().getString());
            }
        }
        return messages;
    }

    public static Sheet getSheet(File f){
        try {
            if (f.toString().endsWith(".xls")) {
                POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(f));
                Workbook workBook = new HSSFWorkbook(fileSystem);
                workBook.close();
                return workBook.getSheetAt(0);
            } else {
                XSSFWorkbook workBook = new XSSFWorkbook(new FileInputStream(f));
                workBook.close();
                return workBook.getSheetAt(0);
            }
        }catch(IOException ignored){
            return null;
        }
    }

    public static ArrayList<StudentClass> getClasses(Sheet sheet) {
        try {
                ArrayList<StudentClass> classes = new ArrayList<>();
                int startReadingRow = startReadingRow(sheet);
                int rows = sheet.getLastRowNum();
                int cols = sheet.getRow(startReadingRow).getLastCellNum();
                for (int c = 1; c < cols; c++) {
                    ArrayList<StudentClass.Subject> subs = new ArrayList<>();
                    for (int r = startReadingRow + 1; r < rows; r++) {
                        Row row = sheet.getRow(r);
                        StudentClass.Subject sc=new StudentClass.Subject(r - (startReadingRow + 1), row.getCell(c).getStringCellValue().split("\\r?\\n")[0], row.getCell(c).getStringCellValue());
                        if(sc.fullName!=null&&!sc.fullName.equals(""))
                        subs.add(sc);
                    }
                    classes.add(new StudentClass(sheet.getRow(startReadingRow).getCell(c).getStringCellValue(), subs));
                }
                return classes;

        } catch (Exception e) {
            return null;
        }
    }
    public static String getDay(Sheet s) {
        try {
            return s.getRow(0).getCell(0).getStringCellValue();
        } catch (Exception e) {
            return null;
        }
    }
}
