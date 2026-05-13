package qna.storage;
import qna.domain.Comment;
import qna.domain.Course;
import qna.domain.Enrollment;
import qna.domain.Invitation;
import qna.domain.Post;
import qna.domain.Professor;
import qna.domain.Section;
import qna.domain.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppData implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Student> students;
    private List<Professor> professors;
    private List<Course> courses;
    private List<Section> sections;
    private List<Enrollment> enrollments;
    private List<Post> posts;
    private List<Comment> comments;
    private List<Invitation> invitations;
    public AppData() {
        //모든 List를 ArrayList로 초기화하여 NullPointerException을 원천 차단한다.
                this.students = new ArrayList<>();
        this.professors = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.sections = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.invitations = new ArrayList<>();
    }
    public List<Student> getStudents() { return students; }
    public List<Professor> getProfessors() { return professors; }
    public List<Course> getCourses() { return courses; }
    public List<Section> getSections() { return sections; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public List<Post> getPosts() { return posts; }
    public List<Comment> getComments() { return comments; }
    public List<Invitation> getInvitations() { return invitations; }
}