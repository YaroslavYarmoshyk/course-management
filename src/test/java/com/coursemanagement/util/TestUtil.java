package com.coursemanagement.util;

import com.coursemanagement.model.User;

public class TestUtil {
    public static User ADMIN = new User().setId(1L).setEmail("john-smith@gmail.com");
    public static User INSTRUCTOR = new User().setId(2L).setEmail("poppins@yahoo.com");
    public static User STUDENT = new User().setId(3L).setEmail("goldlannister@gmail.com");
}
