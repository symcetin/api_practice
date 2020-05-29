package com.practice.harry_potter;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

public class HaryyPotter {

    @BeforeAll
    public static void setup(){
        baseURI="https://www.potterapi.com/v1";
    }

    /*
    1.Send a get request to /sortingHat. Request includes :
    2.Verify status code 200, content type application/json; charset=utf-8
    3.Verify that response body contains one of the following houses: "Gryffindor", "Ravenclaw", "Slytherin", "Hufflepuff"
     */
    @Test
    @DisplayName("Verify sorting hat")
    public void sortHat() {
        List<String> hats = new ArrayList(Arrays.asList("\"Gryffindor\"", "\"Ravenclaw\"", "\"Slytherin\"", "\"Hufflepuff\""));
        Response response = given().when().get("/sortingHat");
        response.then().assertThat().
                statusCode(200).contentType("application/json;charset=utf-8");

        String body = response.body().prettyPrint();

        assertTrue(hats.contains(body));
    }

    /*
    1.Send a get request to /characters. Request includes :•Header Accept with value application/json•Query param key with value invalid
    2.Verify status code 401, content type application/json; charset=utf-8
    3.Verify response status line include message Unauthorized
    4.Verify that response body says"error":"APIKeyNotFound"
     */
    @Test
    @DisplayName("Verify bad key")
    public void badKey() {
        given().accept(ContentType.JSON).queryParam("key", "invalid").
                when().get("/characters").then().assertThat().
                statusCode(401).contentType(ContentType.JSON).statusLine("HTTP/1.1 401 Unauthorized").body("error", is("API Key Not Found"));

    }

    /*
    1.Send a get request to /characters. Request includes :•Header Accept with value application/json
    2.Verify status code 409, content type application/json; charset=utf-8
    3.Verify response status line include message Conflict
    4.Verify that response body says"error":"MustpassAPIkeyforrequest"
     */
    @Test
    @DisplayName("Verify no key")
    public void noKey() {
        given().accept(ContentType.JSON).
                when().get("/characters").then().assertThat().
                statusCode(409).contentType(ContentType.JSON).statusLine("HTTP/1.1 409 Conflict").body("error", is("Must pass API key for request"));
    }

    /*
    1.Send a get request to /characters. Request includes :•Header Accept with value application/json•Query param key with value {{apiKey}}
    2.Verify status code 200, content type application/json; charset=utf-8
    3.Verify response contains 195 characters
     */
    @Test
    @DisplayName("Verify number of characters ")
    public void characters() {
        given().accept(ContentType.JSON).queryParam("key", "$2a$10$RwapZMshqeMSubQ4vguv.eC2xR/ptN5bkRZZ.tDXkLWa6lG5hZLee").
                when().get("/characters").then().assertThat().
                statusCode(200).contentType(ContentType.JSON);
        Response response = given().accept(ContentType.JSON).queryParam("key", "$2a$10$RwapZMshqeMSubQ4vguv.eC2xR/ptN5bkRZZ.tDXkLWa6lG5hZLee").get("/characters");
        List<Map<Object, Object>> list = response.body().jsonPath().get();
        System.out.println(list.size());
        assertEquals(195, list.size());

    }

    /*

    1.Send a get request to /characters. Request includes :•Header Accept with value application/json•Query param key with value {{apiKey}}
    2.Verify status code 200, content type application/json; charset=utf-8
    3.Verify all characters in the response have id field which is not empty
    4.Verify that value type of the field dumbledoresArmy is a boolean in all characters in the response
    5.Verify value of the house in all characters in the response is one of the following: "Gryffindor", "Ravenclaw", "Slytherin", "Hufflepuff"
     */
    @Test
    @DisplayName("Verify number of character id and house")
    public void dubledoresArmy() {
        Response response = given().accept(ContentType.JSON).queryParam("key", "$2a$10$RwapZMshqeMSubQ4vguv.eC2xR/ptN5bkRZZ.tDXkLWa6lG5hZLee").
                when().get("/characters");
        response.then().assertThat().
                statusCode(200).contentType(ContentType.JSON);
        List<String> idList = response.body().jsonPath().get("id");
        assertFalse(idList.isEmpty());

        List<Boolean> dumbleDoresList = response.body().jsonPath().get("dumbledoresArmy");
        assertTrue((dumbleDoresList.contains(true) || dumbleDoresList.contains(false)));

        List<Map<Object, Object>> housesList = response.body().jsonPath().get();
        List<Map<Object, Object>> housesList2 = response.jsonPath().getList("");
        System.out.println(housesList);
        System.out.println(housesList2);

        String houses = "\"Gryffindor\", \"Ravenclaw\", \"Slytherin\", \"Hufflepuff\"";
        for (int i = 0; i < housesList.size(); i++) {
            if (housesList.get(i).equals("house")) {
                List<String> houseList = response.body().jsonPath().get("house");
                assertTrue(houses.contains(houseList.get(i)));
            }
        }
    }

    /*
   1.Send a get request to /characters. Request includes :•Header Accept with value application/json•Query param key with value {{apiKey}}
   2.Verify status code 200, content type application/json; charset=utf-8
   3.Select name of any random character
   4.Send a get request to /characters. Request includes :•Header Accept with value application/json•Query param key with value {{apiKey}}•Query param name with value from step 3
   5.Verify that response contains the same character information from step 3 Compare all fields.
     */
    @Test
    @DisplayName("Verify all character information")
    public void allCharacter() {
        Response response = given().accept(ContentType.JSON).queryParam("key", "$2a$10$RwapZMshqeMSubQ4vguv.eC2xR/ptN5bkRZZ.tDXkLWa6lG5hZLee").
                when().get("/characters");
        response.then().assertThat().
                statusCode(200).contentType(ContentType.JSON);
        List<Map<Object, Object>> charactersList = response.body().jsonPath().get();
        List<String > names = response.body().jsonPath().get("name");
        Random random = new Random();
        int x = random.nextInt(names.size());
        Response response1 = given().accept(ContentType.JSON).queryParam("key", "$2a$10$RwapZMshqeMSubQ4vguv.eC2xR/ptN5bkRZZ.tDXkLWa6lG5hZLee").
                queryParam("name",names.get(x)).
                when().get("/characters");

        for (int i = 0; i < charactersList.size(); i++) {
            if(charactersList.get(i).containsValue(names.get(x))){
                System.out.println(charactersList.get(i));
                Map<Object, Object> s =charactersList.get(i);
                System.out.println(s);
                assertTrue(charactersList.contains(s));
            }
        }
    }

    /*
    1.Send a get request to /characters. Request includes :•Header Accept with value application/json•Query param key with value {{apiKey}}•Query param name with value Harry Potter
    2.Verify status code 200, content type application/json; charset=utf-8
    3.Verify name Harry Potter
    4.Send a get request to /characters. Request includes :•Header Accept with value application/json•Query param key with value {{apiKey}}•Query param name with value Marry Potter
    5.Verify status code 200, content type application/json; charset=utf-8
    6.Verify response body is empty
     */
    @Test
    @DisplayName("Verify name search")
    public void name() {
       List<Object > arr =Arrays.asList("Harry Potter", "Marry Potter",
               "");
        Response response = given().accept(ContentType.JSON).queryParam("key", "$2a$10$RwapZMshqeMSubQ4vguv.eC2xR/ptN5bkRZZ.tDXkLWa6lG5hZLee").
                queryParam("name","Harry Potter").when().get("/characters");

        response.then().assertThat().
                statusCode(200).contentType(ContentType.JSON).assertThat().body("name",contains(arr.get(0)));



        given().accept(ContentType.JSON).queryParam("key", "$2a$10$RwapZMshqeMSubQ4vguv.eC2xR/ptN5bkRZZ.tDXkLWa6lG5hZLee").
                queryParam("name","Marry Potter").when().get("/characters").then().assertThat().
                statusCode(200).contentType(ContentType.JSON).body("",is(empty()));

    }

    /*
    1.Send a get request to /houses. Request includes :•Header Accept with value application/json•Query param key with value {{apiKey}}
    2.Verify status code 200, content type application/json; charset=utf-8
    3.Capture the id of the Gryffindor house
    4.Capture the ids of the all members of the Gryffindor house
    5.Send a get request to /houses/:id. Request includes :•Header Accept with value application/json•Query param key with value {{apiKey}} •Path param id with value from step 3
    6.Verify that response contains the  same memberids as the step 4
     */
    @Test
    @DisplayName("Verify house members")
    public void members() {
        Response response = given().accept(ContentType.JSON).
                                queryParam("key", "$2a$10$RwapZMshqeMSubQ4vguv.eC2xR/ptN5bkRZZ.tDXkLWa6lG5hZLee").
                            when().get("/houses");

        response.then().assertThat().
                statusCode(200).contentType(ContentType.JSON);

        List<Map<String,Object>> members = response.jsonPath().get("");
        int id = 0;
        for (int i = 0; i < members.size(); i++) {
            if(members.get(i).get("name").toString().equals("Gryffindor")){
               id= Integer.parseInt(members.get(i).get("id").toString());
                System.out.println(id);
            }
        }

        Response response2 = given().accept(ContentType.JSON).
                queryParam("key", "$2a$10$RwapZMshqeMSubQ4vguv.eC2xR/ptN5bkRZZ.tDXkLWa6lG5hZLee").
                when().get("/houses/{id}",id);

        System.out.println(response2.body().prettyPrint());


    }


}
