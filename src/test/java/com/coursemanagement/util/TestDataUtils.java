package com.coursemanagement.util;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.When;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.instancio.Assign.given;
import static org.instancio.Select.field;

public final class TestDataUtils {
    private static final AtomicReference<Long> START_USER_ID = new AtomicReference<>(10L);
    private static final AtomicReference<Long> START_COURSE_CODE = new AtomicReference<>(22332L);
    private static final int USER_ID_INCREMENT_STEP = 1;
    private static final int COURSE_CDE_INCREMENT_STEP = 10000;
    public static final Model<User> USER_TEST_MODEL = Instancio.of(User.class)
            .supply(field(User::getId), () -> START_USER_ID.getAndSet(START_USER_ID.get() + USER_ID_INCREMENT_STEP))
            .generate(field(User::getFirstName), gen -> gen.oneOf("John", "Marry", "Tyrion"))
            .generate(field(User::getLastName), gen -> gen.oneOf("Smith", "Poppins", "Lannister"))
            .assign(given(field(User::getLastName), field(User::getEmail))
                    .set(When.is("Smith"), "smith@gmail.com")
                    .set(When.is("Poppins"), "poppins@yahoo.com")
                    .set(When.is("Lannister"), "goldlannister@gmail.com"))
            .generate(field(User::getPhone), gen -> gen.text().pattern("+38(097)-#d#d#d-#d#d-#d#d"))
            .set(field(User::getStatus), UserStatus.ACTIVE)
            .generate(field(User::getRoles), gen -> gen.collection().maxSize(2))
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

    public static final User ADMIN = getAdmin();
    public static final User INSTRUCTOR = getInstructor();
    public static final User FIRST_STUDENT = getFistStudent();
    public static final User SECOND_STUDENT = getSecondStudent();
    public static final User NEW_USER = getNewUser();
    public static final Course RANDOM_COURSE = getRandomCourse();

    public static Set<User> createDefaultUsers() {
        return Instancio.ofSet(USER_TEST_MODEL).size(4).create();
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
                .set(field(User::getRoles), Set.of())
                .create();
    }

    public static Course getRandomCourse() {
        return Instancio.of(COURSE_TEST_MODEL).create();
    }
}
