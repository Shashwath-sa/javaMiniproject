package package1;

import java.util.List;
import java.util.Map;

public class AllocationResult {
    private final Map<ExamHall, SeatAssignment[][]> hallLayouts;
    private final List<SeatAssignment> assignments;
    private final List<Student> unassignedStudents;

    public AllocationResult(Map<ExamHall, SeatAssignment[][]> hallLayouts,
                            List<SeatAssignment> assignments,
                            List<Student> unassignedStudents) {
        this.hallLayouts = hallLayouts;
        this.assignments = assignments;
        this.unassignedStudents = unassignedStudents;
    }

    public Map<ExamHall, SeatAssignment[][]> getHallLayouts() {
        return hallLayouts;
    }

    public List<SeatAssignment> getAssignments() {
        return assignments;
    }

    public List<Student> getUnassignedStudents() {
        return unassignedStudents;
    }
}