package package1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {

    public void generateCoordinatorWorkbook(String fileName, AllocationResult result, List<ExamHall> halls) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createFullAllotmentSheet(workbook, result.getAssignments());
            createHallSummarySheet(workbook, result, halls);
            createUnassignedSheet(workbook, result.getUnassignedStudents());

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            System.out.println("Error writing coordinator workbook: " + e.getMessage());
        }
    }

    public void generateInvigilatorWorkbook(String fileName, AllocationResult result) {
        try (Workbook workbook = new XSSFWorkbook()) {
            for (Map.Entry<ExamHall, SeatAssignment[][]> entry : result.getHallLayouts().entrySet()) {
                createInvigilatorHallSheet(workbook, entry.getKey(), entry.getValue());
            }

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            System.out.println("Error writing invigilator workbook: " + e.getMessage());
        }
    }

    public void generateStudentWorkbook(String fileName, AllocationResult result) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createStudentIndexSheet(workbook, result.getAssignments());

            Set<String> usedNames = new HashSet<>();

            List<SeatAssignment> sortedAssignments = new ArrayList<>(result.getAssignments());
            sortedAssignments.sort(Comparator.comparing(a -> a.getStudent().getStudentId()));

            for (SeatAssignment assignment : sortedAssignments) {
                String baseName = assignment.getStudent().getStudentId() + "_" + assignment.getStudent().getName();
                String sheetName = createUniqueSheetName(workbook, baseName, usedNames);
                Sheet sheet = workbook.createSheet(sheetName);

                int rowNum = 0;
                rowNum = writeKeyValue(sheet, rowNum, "Student ID", assignment.getStudent().getStudentId());
                rowNum = writeKeyValue(sheet, rowNum, "Name", assignment.getStudent().getName());
                rowNum = writeKeyValue(sheet, rowNum, "Branch", assignment.getStudent().getBranch());
                rowNum = writeKeyValue(sheet, rowNum, "Semester", String.valueOf(assignment.getStudent().getSemester()));
                rowNum = writeKeyValue(sheet, rowNum, "Subject", assignment.getStudent().getSubject());
                rowNum = writeKeyValue(sheet, rowNum, "Hall ID", assignment.getHall().getHallId());
                rowNum = writeKeyValue(sheet, rowNum, "Hall Name", assignment.getHall().getHallName());
                rowNum = writeKeyValue(sheet, rowNum, "Seat", assignment.getSeatLabel());

                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
            }

            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            System.out.println("Error writing student workbook: " + e.getMessage());
        }
    }

    private void createFullAllotmentSheet(Workbook workbook, List<SeatAssignment> assignments) {
        Sheet sheet = workbook.createSheet("Full_Allotment");

        String[] headers = {
                "Student ID", "Name", "Branch", "Semester", "Subject",
                "Hall ID", "Hall Name", "Seat"
        };

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        List<SeatAssignment> sorted = new ArrayList<>(assignments);
        sorted.sort(Comparator
                .comparing((SeatAssignment a) -> a.getHall().getHallName())
                .thenComparingInt(SeatAssignment::getRowIndex)
                .thenComparingInt(SeatAssignment::getColumnIndex));

        int rowNum = 1;
        for (SeatAssignment assignment : sorted) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(assignment.getStudent().getStudentId());
            row.createCell(1).setCellValue(assignment.getStudent().getName());
            row.createCell(2).setCellValue(assignment.getStudent().getBranch());
            row.createCell(3).setCellValue(assignment.getStudent().getSemester());
            row.createCell(4).setCellValue(assignment.getStudent().getSubject());
            row.createCell(5).setCellValue(assignment.getHall().getHallId());
            row.createCell(6).setCellValue(assignment.getHall().getHallName());
            row.createCell(7).setCellValue(assignment.getSeatLabel());
        }

        autoSize(sheet, headers.length);
    }

    private void createHallSummarySheet(Workbook workbook, AllocationResult result, List<ExamHall> halls) {
        Sheet sheet = workbook.createSheet("Hall_Summary");

        String[] headers = {
                "Hall ID", "Hall Name", "Rows", "Columns", "Capacity", "Usable Seats", "Allotted Seats"
        };

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (ExamHall hall : halls) {
            int allotted = 0;
            SeatAssignment[][] layout = result.getHallLayouts().get(hall);
            if (layout != null) {
                for (SeatAssignment[] row : layout) {
                    for (SeatAssignment seat : row) {
                        if (seat != null) {
                            allotted++;
                        }
                    }
                }
            }

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(hall.getHallId());
            row.createCell(1).setCellValue(hall.getHallName());
            row.createCell(2).setCellValue(hall.getRows());
            row.createCell(3).setCellValue(hall.getColumns());
            row.createCell(4).setCellValue(hall.getCapacity());
            row.createCell(5).setCellValue(hall.getUsableSeats());
            row.createCell(6).setCellValue(allotted);
        }

        autoSize(sheet, headers.length);
    }

    private void createUnassignedSheet(Workbook workbook, List<Student> unassignedStudents) {
        Sheet sheet = workbook.createSheet("Unassigned_Students");

        String[] headers = { "Student ID", "Name", "Branch", "Semester", "Subject" };
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        int rowNum = 1;
        for (Student student : unassignedStudents) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getStudentId());
            row.createCell(1).setCellValue(student.getName());
            row.createCell(2).setCellValue(student.getBranch());
            row.createCell(3).setCellValue(student.getSemester());
            row.createCell(4).setCellValue(student.getSubject());
        }

        autoSize(sheet, headers.length);
    }

    private void createInvigilatorHallSheet(Workbook workbook, ExamHall hall, SeatAssignment[][] layout) {
        String sheetName = WorkbookUtil.createSafeSheetName(hall.getHallId() + "_" + hall.getHallName());
        Sheet sheet = workbook.createSheet(sheetName);

        int rowNum = 0;

        Row title = sheet.createRow(rowNum++);
        title.createCell(0).setCellValue("Hall");
        title.createCell(1).setCellValue(hall.getHallName());

        Row info = sheet.createRow(rowNum++);
        info.createCell(0).setCellValue("Hall ID");
        info.createCell(1).setCellValue(hall.getHallId());

        rowNum++;

        Row listHeader = sheet.createRow(rowNum++);
        listHeader.createCell(0).setCellValue("Seat");
        listHeader.createCell(1).setCellValue("Student ID");
        listHeader.createCell(2).setCellValue("Name");
        listHeader.createCell(3).setCellValue("Branch");
        listHeader.createCell(4).setCellValue("Semester");
        listHeader.createCell(5).setCellValue("Subject");

        for (int r = 0; r < layout.length; r++) {
            for (int c = 0; c < layout[r].length; c++) {
                SeatAssignment assignment = layout[r][c];
                if (assignment != null) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(assignment.getSeatLabel());
                    row.createCell(1).setCellValue(assignment.getStudent().getStudentId());
                    row.createCell(2).setCellValue(assignment.getStudent().getName());
                    row.createCell(3).setCellValue(assignment.getStudent().getBranch());
                    row.createCell(4).setCellValue(assignment.getStudent().getSemester());
                    row.createCell(5).setCellValue(assignment.getStudent().getSubject());
                }
            }
        }

        rowNum += 2;

        Row layoutTitle = sheet.createRow(rowNum++);
        layoutTitle.createCell(0).setCellValue("Seating Diagram");

        for (int r = 0; r < layout.length; r++) {
            Row row = sheet.createRow(rowNum++);
            for (int c = 0; c < layout[r].length; c++) {
                SeatAssignment assignment = layout[r][c];
                if (assignment == null) {
                    row.createCell(c).setCellValue("Empty");
                } else {
                    row.createCell(c).setCellValue(
                            assignment.getSeatLabel() + "\n" +
                            assignment.getStudent().getStudentId() + "\n" +
                            assignment.getStudent().getBranch()
                    );
                }
            }
        }

        autoSize(sheet, 6);
    }

    private void createStudentIndexSheet(Workbook workbook, List<SeatAssignment> assignments) {
        Sheet sheet = workbook.createSheet("Index");

        String[] headers = { "Student ID", "Name", "Branch", "Hall", "Seat" };
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        List<SeatAssignment> sorted = new ArrayList<>(assignments);
        sorted.sort(Comparator.comparing(a -> a.getStudent().getStudentId()));

        int rowNum = 1;
        for (SeatAssignment assignment : sorted) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(assignment.getStudent().getStudentId());
            row.createCell(1).setCellValue(assignment.getStudent().getName());
            row.createCell(2).setCellValue(assignment.getStudent().getBranch());
            row.createCell(3).setCellValue(assignment.getHall().getHallName());
            row.createCell(4).setCellValue(assignment.getSeatLabel());
        }

        autoSize(sheet, headers.length);
    }

    private int writeKeyValue(Sheet sheet, int rowNum, String key, String value) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(value);
        return rowNum;
    }

    private String createUniqueSheetName(Workbook workbook, String baseName, Set<String> usedNames) {
        String safeBase = WorkbookUtil.createSafeSheetName(baseName);
        if (safeBase.length() > 25) {
            safeBase = safeBase.substring(0, 25);
        }

        String name = safeBase;
        int counter = 1;

        while (usedNames.contains(name) || workbook.getSheet(name) != null) {
            String suffix = "_" + counter;
            int maxBaseLength = 31 - suffix.length();
            String shortened = safeBase.length() > maxBaseLength ? safeBase.substring(0, maxBaseLength) : safeBase;
            name = shortened + suffix;
            counter++;
        }

        usedNames.add(name);
        return name;
    }

    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}