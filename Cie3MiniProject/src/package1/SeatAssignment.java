package package1;

public class SeatAssignment {
    private final Student student;
    private final ExamHall hall;
    private final int rowIndex;
    private final int columnIndex;
    private final String seatLabel;

    public SeatAssignment(Student student, ExamHall hall, int rowIndex, int columnIndex, String seatLabel) {
        this.student = student;
        this.hall = hall;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.seatLabel = seatLabel;
    }

    public Student getStudent() {
        return student;
    }

    public ExamHall getHall() {
        return hall;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getSeatLabel() {
        return seatLabel;
    }

    @Override
    public String toString() {
        return student.getStudentId() + " | " + student.getName() + " | " +
               student.getBranch() + " | " + hall.getHallName() + " | " + seatLabel;
    }
}