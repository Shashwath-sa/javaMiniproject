package package1;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String inputFile = "exam_data.xlsx";

        File file = new File(inputFile);
        if (!file.exists()) {
            System.out.println("Input file not found: " + file.getAbsolutePath());
            return;
        }

        ExcelReader reader = new ExcelReader();
        List<Student> students = reader.readStudents(inputFile);
        List<ExamHall> halls = reader.readHalls(inputFile);

        if (students.isEmpty()) {
            System.out.println("No student data found.");
            return;
        }

        if (halls.isEmpty()) {
            System.out.println("No hall data found.");
            return;
        }

        SeatAllocator allocator = new SeatAllocator();
        AllocationResult result = allocator.allocateSeats(students, halls);

        ExcelWriter writer = new ExcelWriter();
        writer.generateCoordinatorWorkbook("Coordinator_Output.xlsx", result, halls);
        writer.generateInvigilatorWorkbook("Invigilator_Output.xlsx", result);
        writer.generateStudentWorkbook("Student_Output.xlsx", result);

        System.out.println("Seat allotment completed successfully.");
        System.out.println("Generated files:");
        System.out.println("1. Coordinator_Output.xlsx");
        System.out.println("2. Invigilator_Output.xlsx");
        System.out.println("3. Student_Output.xlsx");

        printSummary(students, halls, result);

        SearchService searchService = new SearchService();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n========== MENU ==========");
            System.out.println("1. Search student allotment by name");
            System.out.println("2. Search hall view for invigilator");
            System.out.println("3. Open coordinator Excel file");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                System.out.print("Enter student name: ");
                String name = scanner.nextLine();
                searchService.searchStudentByName(result.getAssignments(), name);

            } else if ("2".equals(choice)) {
                System.out.print("Enter hall name or hall ID: ");
                String hallInput = scanner.nextLine();

                searchService.searchHallForInvigilator(result, hallInput);

                System.out.print("Do you want to open Invigilator Excel file? (yes/no): ");
                String openChoice = scanner.nextLine().trim().toLowerCase();

                if (openChoice.equals("yes") || openChoice.equals("y")) {
                    openExcelFile("Invigilator_Output.xlsx");
                }

            } else if ("3".equals(choice)) {
                openExcelFile("Coordinator_Output.xlsx");

            } else if ("4".equals(choice)) {
                System.out.println("Exiting program.");
                break;

            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }

    private static void printSummary(List<Student> students, List<ExamHall> halls, AllocationResult result) {
        int totalSeats = 0;
        for (ExamHall hall : halls) {
            totalSeats += hall.getUsableSeats();
        }

        System.out.println("\n========== SUMMARY ==========");
        System.out.println("Total students      : " + students.size());
        System.out.println("Total halls         : " + halls.size());
        System.out.println("Total usable seats  : " + totalSeats);
        System.out.println("Total allotted      : " + result.getAssignments().size());
        System.out.println("Unassigned students : " + result.getUnassignedStudents().size());
    }

    private static void openExcelFile(String fileName) {
        try {
            File excelFile = new File(fileName);

            if (!excelFile.exists()) {
                System.out.println("File not found: " + excelFile.getAbsolutePath());
                return;
            }

            if (!Desktop.isDesktopSupported()) {
                System.out.println("Desktop is not supported on this system.");
                System.out.println("Open this file manually: " + excelFile.getAbsolutePath());
                return;
            }

            Desktop desktop = Desktop.getDesktop();
            desktop.open(excelFile);

            System.out.println("Opened: " + excelFile.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("Could not open Excel file.");
            System.out.println("Please open it manually from the project folder.");
            e.printStackTrace();
        }
    }
}