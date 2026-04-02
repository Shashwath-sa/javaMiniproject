package package1;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SeatAllocator {

    public AllocationResult allocateSeats(List<Student> students, List<ExamHall> halls) {
        Map<String, Deque<Student>> branchQueues = buildBranchQueues(students);
        Map<ExamHall, SeatAssignment[][]> hallLayouts = new LinkedHashMap<>();
        List<SeatAssignment> assignments = new ArrayList<>();

        for (ExamHall hall : halls) {
            SeatAssignment[][] layout = new SeatAssignment[hall.getRows()][hall.getColumns()];
            hallLayouts.put(hall, layout);

            int seatsToFill = hall.getUsableSeats();
            int filled = 0;

            for (int r = 0; r < hall.getRows() && filled < seatsToFill && remainingStudents(branchQueues) > 0; r++) {
                for (int c = 0; c < hall.getColumns() && filled < seatsToFill && remainingStudents(branchQueues) > 0; c++) {

                    String leftBranch = getBranch(layout, r, c - 1);
                    String topBranch = getBranch(layout, r - 1, c);

                    Student selected = selectBestStudent(branchQueues, leftBranch, topBranch);
                    if (selected == null) {
                        break;
                    }

                    String seatLabel = getSeatLabel(r, c);
                    SeatAssignment assignment = new SeatAssignment(selected, hall, r, c, seatLabel);

                    layout[r][c] = assignment;
                    assignments.add(assignment);
                    filled++;
                }
            }
        }

        List<Student> unassigned = collectRemainingStudents(branchQueues);
        return new AllocationResult(hallLayouts, assignments, unassigned);
    }

    private Map<String, Deque<Student>> buildBranchQueues(List<Student> students) {
        Map<String, List<Student>> grouped = new LinkedHashMap<>();

        for (Student student : students) {
            grouped.computeIfAbsent(student.getBranch(), k -> new ArrayList<>()).add(student);
        }

        Map<String, Deque<Student>> branchQueues = new LinkedHashMap<>();
        grouped.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().size(), a.getValue().size()))
                .forEach(entry -> branchQueues.put(entry.getKey(), new ArrayDeque<>(entry.getValue())));

        return branchQueues;
    }

    private Student selectBestStudent(Map<String, Deque<Student>> branchQueues, String leftBranch, String topBranch) {
        List<Map.Entry<String, Deque<Student>>> available = new ArrayList<>();

        for (Map.Entry<String, Deque<Student>> entry : branchQueues.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                available.add(entry);
            }
        }

        available.sort(Comparator
                .comparingInt((Map.Entry<String, Deque<Student>> e) -> e.getValue().size())
                .reversed()
                .thenComparing(Map.Entry::getKey));

        for (Map.Entry<String, Deque<Student>> entry : available) {
            String branch = entry.getKey();
            if (!branch.equals(leftBranch) && !branch.equals(topBranch)) {
                return entry.getValue().pollFirst();
            }
        }

        for (Map.Entry<String, Deque<Student>> entry : available) {
            String branch = entry.getKey();
            if (!branch.equals(leftBranch) || !branch.equals(topBranch)) {
                return entry.getValue().pollFirst();
            }
        }

        if (!available.isEmpty()) {
            return available.get(0).getValue().pollFirst();
        }

        return null;
    }

    private String getBranch(SeatAssignment[][] layout, int row, int col) {
        if (row < 0 || col < 0 || row >= layout.length || col >= layout[0].length) {
            return "";
        }

        SeatAssignment assignment = layout[row][col];
        if (assignment == null) {
            return "";
        }

        return assignment.getStudent().getBranch();
    }

    private int remainingStudents(Map<String, Deque<Student>> branchQueues) {
        int total = 0;
        for (Deque<Student> queue : branchQueues.values()) {
            total += queue.size();
        }
        return total;
    }

    private List<Student> collectRemainingStudents(Map<String, Deque<Student>> branchQueues) {
        List<Student> remaining = new ArrayList<>();
        for (Deque<Student> queue : branchQueues.values()) {
            remaining.addAll(queue);
        }
        return remaining;
    }

    private String getSeatLabel(int row, int col) {
        String rowLabel = row < 26 ? String.valueOf((char) ('A' + row)) : "R" + (row + 1);
        return rowLabel + (col + 1);
    }
}