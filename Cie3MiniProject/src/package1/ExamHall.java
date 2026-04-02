package package1;

public class ExamHall {
    private final String hallId;
    private final String hallName;
    private final int rows;
    private final int columns;
    private final int capacity;

    public ExamHall(String hallId, String hallName, int rows, int columns, int capacity) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.rows = rows;
        this.columns = columns;
        this.capacity = capacity;
    }

    public String getHallId() {
        return hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getUsableSeats() {
        return Math.min(rows * columns, capacity);
    }

    @Override
    public String toString() {
        return hallId + " | " + hallName + " | Rows=" + rows + " | Columns=" + columns + " | Capacity=" + capacity;
    }
}