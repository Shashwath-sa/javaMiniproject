package package1;

public class Student {
    private final String studentId;
    private final String name;
    private final String branch;
    private final int semester;
    private final String subject;

    public Student(String studentId, String name, String branch, int semester, String subject) {
        this.studentId = studentId;
        this.name = name;
        this.branch = branch;
        this.semester = semester;
        this.subject = subject;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }

    public int getSemester() {
        return semester;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return studentId + " | " + name + " | " + branch + " | Sem " + semester + " | " + subject;
    }
}