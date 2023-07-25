package com.coursemanagement.util;

import com.coursemanagement.enumeration.Role;
import com.coursemanagement.enumeration.UserStatus;
import com.coursemanagement.model.Course;
import com.coursemanagement.model.User;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.When;
import org.instancio.generators.Generators;

import java.util.Set;

import static org.instancio.Assign.given;
import static org.instancio.Select.field;

public final class TestDataUtils {
    public static final Model<User> USER_TEST_MODEL = Instancio.of(User.class)
            .generate(field(User::getId), Generators::longSeq)
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
            .generate(field(Course::getCode), gen -> gen.oneOf(22324L, 34432L, 99831L, 56548L, 76552L))
            .generate(field(Course::getSubject), gen -> gen.oneOf("Mathematics", "History", "Literature", "Physics", "Computer Science"))
            .assign(given(field(Course::getSubject), field(Course::getDescription))
                    .set(When.is("Mathematics"), "Introductory course on mathematics")
                    .set(When.is("History"), "Overview of world history")
                    .set(When.is("Literature"), "Study of classical literature")
                    .set(When.is("Physics"), "Fundamentals of physics")
                    .set(When.is("Computer Science"), "Introduction to computer programming"))
            .assign(given(field(Course::getSubject), field(Course::getCode))
                    .set(When.is("Mathematics"), 22324L)
                    .set(When.is("History"), 34432L)
                    .set(When.is("Literature"), 99831L)
                    .set(When.is("Physics"), 56548L)
                    .set(When.is("Introduction to computer programming"), 76552L))
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
