package package1;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

    public List<Student> readStudents(String filePath) {
        List<Student> students = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Students");

            if (sheet == null) {
                System.out.println("Sheet 'Students' not found.");
                return students;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String studentId = getCellValue(row.getCell(0), formatter);
                String name = getCellValue(row.getCell(1), formatter);
                String branch = getCellValue(row.getCell(2), formatter).toUpperCase();
                String semesterText = getCellValue(row.getCell(3), formatter);
                String subject = getCellValue(row.getCell(4), formatter);

                if (studentId.isEmpty() || name.isEmpty() || branch.isEmpty()) {
                    continue;
                }

                try {
                    int semester = parseNumber(semesterText);
                    students.add(new Student(studentId, name, branch, semester, subject));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid semester for student: " + studentId);
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading students sheet: " + e.getMessage());
        }

        return students;
    }

    public List<ExamHall> readHalls(String filePath) {
        List<ExamHall> halls = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Halls");

            if (sheet == null) {
                System.out.println("Sheet 'Halls' not found.");
                return halls;
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String hallId = getCellValue(row.getCell(0), formatter);
                String hallName = getCellValue(row.getCell(1), formatter);
                String rowsText = getCellValue(row.getCell(2), formatter);
                String columnsText = getCellValue(row.getCell(3), formatter);
                String capacityText = getCellValue(row.getCell(4), formatter);

                if (hallId.isEmpty() || hallName.isEmpty()) {
                    continue;
                }

                try {
                    int rows = parseNumber(rowsText);
                    int columns = parseNumber(columnsText);
                    int capacity = parseNumber(capacityText);
                    halls.add(new ExamHall(hallId, hallName, rows, columns, capacity));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid hall data for hall: " + hallId);
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading halls sheet: " + e.getMessage());
        }

        return halls;
    }

    private String getCellValue(Cell cell, DataFormatter formatter) {
        if (cell == null) {
            return "";
        }
        return formatter.formatCellValue(cell).trim();
    }

    private int parseNumber(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new NumberFormatException("Empty value");
        }

        text = text.trim().replace(",", "");
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }

        return (int) Double.parseDouble(text);
    }
}