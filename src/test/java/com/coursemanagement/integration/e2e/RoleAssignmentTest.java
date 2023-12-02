package com.coursemanagement.integration.e2e;

import com.coursemanagement.config.annotation.IntegrationTest;
import com.coursemanagement.enumeration.Role;
import com.coursemanagement.model.User;
import com.coursemanagement.rest.dto.RoleAssignmentDto;
import com.coursemanagement.service.UserService;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.coursemanagement.util.BaseEndpoints.ROLE_ASSIGNMENT_ENDPOINT;
import static com.coursemanagement.util.JwtTokenUtils.getAuthTokenRequestSpec;
import static com.coursemanagement.util.TestDataUtils.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@IntegrationTest
public class RoleAssignmentTest {
    @Autowired
    private RequestSpecification requestSpecification;
    @Autowired
    private ResponseSpecification validResponseSpecification;
    @Autowired
    private UserService userService;

    @Order(1)
    @ParameterizedTest(name = "[{index}] role assignment = {0}")
    @MethodSource(value = "roleAssignmentProvider")
    @DisplayName("Test successful role assignment flow")
    void testSuccessfulRoleAssignmentFlow(final RoleAssignmentDto roleAssignmentDto) {
        final User userBeforeAssignment = userService.getUserById(roleAssignmentDto.userId());

        final User userFromResponse = getAuthTokenRequestSpec(ADMIN, requestSpecification)
                .body(roleAssignmentDto)
                .post(ROLE_ASSIGNMENT_ENDPOINT)
                .then()
                .spec(validResponseSpecification)
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/userResponseSchema.json"))
                .extract()
                .as(User.class);

        final Set<Role> expectedRoles = new HashSet<>(userBeforeAssignment.getRoles());
        expectedRoles.addAll(roleAssignmentDto.roles());

        final Set<Role> userRolesAfterAssignment = userService.getUserById(roleAssignmentDto.userId()).getRoles();
        assertTrue(CollectionUtils.isEqualCollection(userRolesAfterAssignment, expectedRoles));
        assertTrue(CollectionUtils.isEqualCollection(userRolesAfterAssignment, userFromResponse.getRoles()));
    }

    static Stream<RoleAssignmentDto> roleAssignmentProvider() {
        final Long newUserId = NEW_USER.getId();
        final Long instructorId = INSTRUCTOR.getId();
        return Stream.of(
                getRoleAssignment(newUserId, Set.of()),
                getRoleAssignment(newUserId, Set.of(Role.STUDENT)),
                getRoleAssignment(instructorId, Set.of(Role.STUDENT)),
                getRoleAssignment(newUserId, Set.of(Role.STUDENT, Role.INSTRUCTOR)),
                getRoleAssignment(newUserId, Set.of(Role.INSTRUCTOR, Role.ADMIN)),
                getRoleAssignment(newUserId, Set.of(Role.ADMIN))
        );
    }

    @Order(2)
    @TestFactory
    @DisplayName("Test failure role assignment flow")
    Stream<DynamicTest> testFailureRoleAssignmentFlow() {
        return Stream.of(
                dynamicTest("Test user id and roles are null", () -> testBadRequestRoleAssignment(getRoleAssignment(null, null))),
                dynamicTest("Test user id is null", () -> testBadRequestRoleAssignment(getRoleAssignment(null, Set.of()))),
                dynamicTest("Test roles are null", () -> testBadRequestRoleAssignment(getRoleAssignment(NEW_USER.getId(), null))),
                dynamicTest("Test role assignment is not provided", () -> testBadRequestRoleAssignment(null))
        );
    }

    private void testBadRequestRoleAssignment(final RoleAssignmentDto roleAssignmentDto) {
        if (Objects.nonNull(roleAssignmentDto)) {
            requestSpecification.body(roleAssignmentDto);
        }

        getAuthTokenRequestSpec(ADMIN, requestSpecification)
                .post(ROLE_ASSIGNMENT_ENDPOINT)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private static RoleAssignmentDto getRoleAssignment(final Long userId, final Set<Role> roles) {
        return new RoleAssignmentDto(userId, roles);
    }
}
