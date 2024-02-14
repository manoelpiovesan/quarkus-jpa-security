package io.github.manoelpiovesan;

import io.github.manoelpiovesan.entities.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class UserResourceTest {

    private static Integer count = 3; // From startup
    private static final String username = "admin"; // From startup
    private static final String password = "admin"; // From startup
    private static final Map<String, Object> userMap = new HashMap<>();

    @Test
    @Order(1)
    public void testFirstCount() {
        given().auth()
               .basic(username, password)
               .when()
               .get("/users/count")
               .then()
               .statusCode(200)
               .contentType(ContentType.TEXT)
               .body(is(count.toString()));
    }

    @Test
    @Order(2)
    public void testCreateUserWithWrongPassword() {
        userMap.put("username", "test");
        userMap.put("password", "123");

        given()
                .contentType(ContentType.JSON)
                .body(userMap)
                .when()
                .post("/users/create")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(3)
    public void testCreateUserWithWrongUsername() {
        userMap.put("username", "");
        userMap.put("password", "test");

        given()
                .contentType(ContentType.JSON)
                .body(userMap)
                .when()
                .post("/users/create")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(4)
    public void testCreateUser() {
        userMap.put("username", "newuser");
        userMap.put("password", "newuser123");

        given()
                .contentType(ContentType.JSON)
                .body(userMap)
                .when()
                .post("/users/create")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200).body(
                        "username", is("newuser")
                );

        count++;
    }

    @Test
    @Order(5)
    public void testCreateUserWithExistingUsername() {
        userMap.put("username", "admin");
        userMap.put("password", "admin");

        given()
                .contentType(ContentType.JSON)
                .body(userMap)
                .when()
                .post("/users/create")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(6)
    public void testSecondCount() {
        given().auth()
               .basic(username, password)
               .when()
               .get("/users/count")
               .then()
               .statusCode(200)
               .contentType(ContentType.TEXT)
               .body(is(count.toString()));
    }

    @Test
    @Order(7)
    public void testDeleteUser() {
        userMap.put("username", "newuser");
        userMap.put("password", "newuser123");

        given()
                .contentType(ContentType.JSON)
                .auth()
                .basic(username, password)
                .body(userMap)
                .when()
                .delete("/users/delete")
                .then()
                .statusCode(200);

        count--;
    }

    @Test
    @Order(8)
    public void testThirdCount() {
        given().auth()
               .basic(username, password)
               .when()
               .get("/users/count")
               .then()
               .statusCode(200)
               .contentType(ContentType.TEXT)
               .body(is(count.toString()));
    }

}
