package com.coursemanagement.util;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserCourseStatus;
import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.Lesson;
import com.coursemanagement.model.User;
import com.coursemanagement.model.UserCourse;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.When;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.instancio.Assign.given;
import static org.instancio.Select.field;

public final class TestDataUtils {
    private static final AtomicReference<Long> START_USER_ID = new AtomicReference<>(10L);
    private static final AtomicReference<Long> START_COURSE_CODE = new AtomicReference<>(22332L);
    private static final AtomicReference<Long> START_LESSON_ID = new AtomicReference<>(100L);
    private static final int DEFAULT_INCREMENT_STEP = 1;
    private static final int COURSE_CDE_INCREMENT_STEP = 10000;
    private static final int DEFAULT_ROLES_COUNT = 2;
    private static final int DEFAULT_USERS_COUNT = 4;
    public static final Model<User> USER_TEST_MODEL = Instancio.of(User.class)
            .supply(field(User::getId), () -> START_USER_ID.getAndSet(START_USER_ID.get() + DEFAULT_INCREMENT_STEP))
            .generate(field(User::getFirstName), gen -> gen.oneOf("John", "Marry", "Tyrion"))
            .generate(field(User::getLastName), gen -> gen.oneOf("Smith", "Poppins", "Lannister"))
            .assign(given(field(User::getLastName), field(User::getEmail))
                    .set(When.is("Smith"), "smith@gmail.com")
                    .set(When.is("Poppins"), "poppins@yahoo.com")
                    .set(When.is("Lannister"), "goldlannister@gmail.com"))
            .generate(field(User::getPhone), gen -> gen.text().pattern("+38(097)-#d#d#d-#d#d-#d#d"))
            .set(field(User::getStatus), UserStatus.ACTIVE)
            .generate(field(User::getRoles), gen -> gen.collection().maxSize(DEFAULT_ROLES_COUNT))
            .toModel();

    public static final Model<Course> COURSE_TEST_MODEL = Instancio.of(Course.class)
            .supply(field(Course::getCode), () -> START_COURSE_CODE.getAndSet(START_COURSE_CODE.get() + COURSE_CDE_INCREMENT_STEP))
            .generate(field(Course::getSubject), gen -> gen.oneOf("Mathematics", "History", "Literature", "Physics", "Computer Science"))
            .assign(given(field(Course::getSubject), field(Course::getDescription))
                    .set(When.is("Mathematics"), "Introductory course on mathematics")
                    .set(When.is("History"), "Overview of world history")
                    .set(When.is("Literature"), "Study of classical literature")
                    .set(When.is("Physics"), "Fundamentals of physics")
                    .set(When.is("Computer Science"), "Introduction to computer programming"))
            .supply(field(Course::getUsers), gen -> createDefaultUsers())
            .toModel();

    public static final Model<Lesson> LESSON_TEST_MODEL = Instancio.of(Lesson.class)
            .supply(field(Lesson::getId), () -> START_LESSON_ID.getAndSet(START_LESSON_ID.get() + DEFAULT_INCREMENT_STEP))
            .supply(field(Course::getCode), () -> START_COURSE_CODE.getAndSet(START_COURSE_CODE.get() + COURSE_CDE_INCREMENT_STEP))
            .generate(field(Course::getSubject), gen -> gen.oneOf("Mathematics", "History", "Literature", "Physics", "Computer Science"))
            .assign(given(field(Course::getSubject), field(Lesson::getTitle))
                    .set(When.is("Mathematics"), "Math Lesson")
                    .set(When.is("History"), "History Lesson")
                    .set(When.is("Literature"), "Literature Lesson")
                    .set(When.is("Physics"), "Physics Lesson")
                    .set(When.is("Computer Science"), "Computer Science Lesson"))
            .assign(given(field(Course::getSubject), field(Course::getDescription))
                    .set(When.is("Mathematics"), "Introductory course on mathematics")
                    .set(When.is("History"), "Overview of world history")
                    .set(When.is("Literature"), "Study of classical literature")
                    .set(When.is("Physics"), "Fundamentals of physics")
                    .set(When.is("Computer Science"), "Introduction to computer programming"))
            .supply(field(Course::getUsers), gen -> createDefaultUsers())
            .toModel();

    public static final User ADMIN = getAdmin();
    public static final User INSTRUCTOR = getInstructor();
    public static final User FIRST_STUDENT = getFistStudent();
    public static final User SECOND_STUDENT = getSecondStudent();
    public static final User NEW_USER = getNewUser();
    public static final Course RANDOM_COURSE = getRandomCourse();

    public static Set<User> createDefaultUsers() {
        return Instancio.ofSet(USER_TEST_MODEL).size(DEFAULT_USERS_COUNT).create();
    }

    public static User getAdmin() {
        return Instancio.of(USER_TEST_MODEL)
                .set(field(User::getId), 1L)
                .set(field(User::getRoles), Set.of(Role.ADMIN))
                .create();
    }

    public static User getInstructor() {
        return Instancio.of(USER_TEST_MODEL)
                .set(field(User::getId), 2L)
                .set(field(User::getRoles), Set.of(Role.INSTRUCTOR))
                .create();
    }

    public static User getFistStudent() {
        return Instancio.of(USER_TEST_MODEL)
                .set(field(User::getId), 3L)
                .set(field(User::getRoles), Set.of(Role.STUDENT))
                .create();
    }

    public static User getSecondStudent() {
        return Instancio.of(USER_TEST_MODEL)
                .set(field(User::getId), 4L)
                .set(field(User::getRoles), Set.of(Role.STUDENT))
                .create();
    }

    public static User getNewUser() {
        return Instancio.of(USER_TEST_MODEL)
                .set(field(User::getId), 5L)
                .set(field(User::getRoles), new HashSet<>())
                .create();
    }

    public static Set<Course> getRandomCourses() {
        return Instancio.ofSet(COURSE_TEST_MODEL).create();
    }

    public static Course getRandomCourse() {
        return Instancio.of(COURSE_TEST_MODEL).create();
    }

    public static Course getRandomCourseContainingUser(final User user) {
        return Instancio.of(COURSE_TEST_MODEL)
                .set(field(Course::getUsers), Set.of(user))
                .create();
    }

    public static UserCourse getRandomUserCourseByUser(final User user) {
        return Instancio.of(UserCourse.class)
                .supply(field(UserCourse::getUser), () -> user)
                .supply(field(UserCourse::getCourse), () -> Instancio.of(COURSE_TEST_MODEL)
                        .onComplete(field(Course::getUsers), (Set<User> users) -> users.add(FIRST_STUDENT))
                        .create())
                .set(field(UserCourse::getStatus), UserCourseStatus.STARTED)
                .create();
    }

    public static Set<Lesson> getRandomLessonsByCourse(final Course course) {
        return Instancio.ofSet(Lesson.class)
                .supply(field(Lesson::getId), () -> START_LESSON_ID.getAndSet(START_LESSON_ID.get() + DEFAULT_INCREMENT_STEP))
                .set(field(Lesson::getCourse), course)
                .assign(given(Lesson::getCourse).satisfies((Course courseVar) -> courseVar.getSubject().equals("Mathematics"))
                        .supply(field(Lesson::getTitle), () -> "Math Lesson №" + START_LESSON_ID.get())
                        .supply(field(Lesson::getDescription), () -> "Lesson on mathematics concepts №" + START_LESSON_ID.get()))
                .assign(given(Lesson::getCourse).satisfies((Course courseVar) -> courseVar.getSubject().equals("History"))
                        .supply(field(Lesson::getTitle), () -> "History Lesson №" + START_LESSON_ID.get())
                        .supply(field(Lesson::getDescription), () -> "Lesson on History concepts №" + START_LESSON_ID.get()))
                .assign(given(Lesson::getCourse).satisfies((Course courseVar) -> courseVar.getSubject().equals("Literature"))
                        .supply(field(Lesson::getTitle), () -> "Literature Lesson №" + START_LESSON_ID.get())
                        .supply(field(Lesson::getDescription), () -> "Lesson on Literature concepts №" + START_LESSON_ID.get()))
                .assign(given(Lesson::getCourse).satisfies((Course courseVar) -> courseVar.getSubject().equals("Physics"))
                        .supply(field(Lesson::getTitle), () -> "Physics Lesson №" + START_LESSON_ID.get())
                        .supply(field(Lesson::getDescription), () -> "Lesson on Physics concepts №" + START_LESSON_ID.get()))
                .assign(given(Lesson::getCourse).satisfies((Course courseVar) -> courseVar.getSubject().equals("Computer Science"))
                        .supply(field(Lesson::getTitle), () -> "Computer Science Lesson №" + START_LESSON_ID.get())
                        .supply(field(Lesson::getDescription), () -> "Lesson on Computer Science concepts №" + START_LESSON_ID.get()))
                .create();
    }
}
