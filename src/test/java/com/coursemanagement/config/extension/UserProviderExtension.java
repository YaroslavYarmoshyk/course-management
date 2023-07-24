package com.coursemanagement.config.extension;

import com.coursemanagement.config.annotation.AdminTestUser;
import com.coursemanagement.config.annotation.InstructorTestUser;
import com.coursemanagement.config.annotation.NewTestUser;
import com.coursemanagement.config.annotation.StudentTestUser;
import com.coursemanagement.model.User;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserProviderExtension implements ParameterResolver {
    private static final String ADMIN_KEY = "admin";
    private static final String INSTRUCTOR_KEY = "instructor";
    private static final String STUDENT_KEY = "student";
    private static final String NEW_USER_KEY = "new-user";

    private final Map<String, User> users;

    public User getAdmin() {
        return users.get(ADMIN_KEY);
    }

    public User getInstructor() {
        return users.get(INSTRUCTOR_KEY);
    }

    public User getStudent() {
        return users.get(STUDENT_KEY);
    }

    public User getNewUser() {
        return users.get(NEW_USER_KEY);
    }

    public UserProviderExtension() {
        final String env = System.getenv("ENV");
        final String applicationConfigFile = Strings.isBlank(env) ? "application.yml" : "application-" + env + ".yml";
        final Yaml yaml = new Yaml();
        final InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(applicationConfigFile);
        final LinkedHashMap<String, LinkedHashMap<String, Object>> ymlProperties = yaml.load(inputStream);
        final LinkedHashMap<String, Object> usersLinkedMap = ymlProperties.get("users");
        final ObjectMapper objectMapper = new ObjectMapper();

        users = usersLinkedMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> objectMapper.convertValue(entry.getValue(), User.class))
                );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(AdminTestUser.class)
                || parameterContext.isAnnotated(InstructorTestUser.class)
                || parameterContext.isAnnotated(StudentTestUser.class)
                || parameterContext.isAnnotated(NewTestUser.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        if (parameterContext.isAnnotated(AdminTestUser.class)) {
            return getAdmin();
        } else if (parameterContext.isAnnotated(InstructorTestUser.class)) {
            return getInstructor();
        } else if (parameterContext.isAnnotated(StudentTestUser.class)) {
            return getStudent();
        } else if (parameterContext.isAnnotated(NewTestUser.class)) {
            return getNewUser();
        } else {
            return null;
        }
    }
}
