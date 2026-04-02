package package1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SearchService {

    public void searchStudentByName(List<SeatAssignment> assignments, String inputName) {
        String search = inputName.trim().toLowerCase();
        List<SeatAssignment> matches = new ArrayList<>();

        for (SeatAssignment assignment : assignments) {
            if (assignment.getStudent().getName().trim().toLowerCase().equals(search)) {
                matches.add(assignment);
            }
        }

        if (matches.isEmpty()) {
            for (SeatAssignment assignment : assignments) {
                if (assignment.getStudent().getName().trim().toLowerCase().contains(search)) {
                    matches.add(assignment);
                }
            }
        }

        if (matches.isEmpty()) {
            System.out.println("No student allotment found for name: " + inputName);
            return;
        }

        matches.sort(Comparator.comparing(a -> a.getStudent().getStudentId()));

        System.out.println("\n===== STUDENT ALLOTMENT =====");
        for (SeatAssignment assignment : matches) {
            System.out.println("Student ID : " + assignment.getStudent().getStudentId());
            System.out.println("Name       : " + assignment.getStudent().getName());
            System.out.println("Branch     : " + assignment.getStudent().getBranch());
            System.out.println("Semester   : " + assignment.getStudent().getSemester());
            System.out.println("Subject    : " + assignment.getStudent().getSubject());
            System.out.println("Hall ID    : " + assignment.getHall().getHallId());
            System.out.println("Hall Name  : " + assignment.getHall().getHallName());
            System.out.println("Seat       : " + assignment.getSeatLabel());
            System.out.println("-----------------------------------");
        }
    }

    public void searchHallForInvigilator(AllocationResult result, String hallInput) {
        String search = hallInput.trim().toLowerCase();
        ExamHall matchedHall = null;

        for (ExamHall hall : result.getHallLayouts().keySet()) {
            if (hall.getHallId().trim().toLowerCase().equals(search) ||
                hall.getHallName().trim().toLowerCase().equals(search)) {
                matchedHall = hall;
                break;
            }
        }

        if (matchedHall == null) {
            for (ExamHall hall : result.getHallLayouts().keySet()) {
                if (hall.getHallId().trim().toLowerCase().contains(search) ||
                    hall.getHallName().trim().toLowerCase().contains(search)) {
                    matchedHall = hall;
                    break;
                }
            }
        }

        if (matchedHall == null) {
            System.out.println("No hall found for input: " + hallInput);
            return;
        }

        SeatAssignment[][] layout = result.getHallLayouts().get(matchedHall);

        System.out.println("\n===== INVIGILATOR HALL VIEW =====");
        System.out.println("Hall ID   : " + matchedHall.getHallId());
        System.out.println("Hall Name : " + matchedHall.getHallName());

        System.out.println("\nStudent List:");
        List<SeatAssignment> hallAssignments = new ArrayList<>();

        for (SeatAssignment[] row : layout) {
            for (SeatAssignment seat : row) {
                if (seat != null) {
                    hallAssignments.add(seat);
                }
            }
        }

        hallAssignments.sort(Comparator
                .comparingInt(SeatAssignment::getRowIndex)
                .thenComparingInt(SeatAssignment::getColumnIndex));

        for (SeatAssignment assignment : hallAssignments) {
            System.out.println(assignment.getSeatLabel() + " | " +
                               assignment.getStudent().getStudentId() + " | " +
                               assignment.getStudent().getName() + " | " +
                               assignment.getStudent().getBranch());
        }

        System.out.println("\nSeating Arrangement:");
        for (int r = 0; r < layout.length; r++) {
            String rowLabel = r < 26 ? String.valueOf((char) ('A' + r)) : "R" + (r + 1);
            System.out.print("Row " + rowLabel + " : ");

            for (int c = 0; c < layout[r].length; c++) {
                if (layout[r][c] == null) {
                    System.out.print("[Empty] ");
                } else {
                    SeatAssignment assignment = layout[r][c];
                    System.out.print("[" + assignment.getSeatLabel() + "-" +
                                     assignment.getStudent().getStudentId() + "-" +
                                     assignment.getStudent().getBranch() + "] ");
                }
            }
            System.out.println();
        }
    }
}