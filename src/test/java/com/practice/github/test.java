package com.practice.github;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

public class test {


    @BeforeAll
    public static void setup(){
        baseURI="https://api.github.com";
    }

    /*
    1.Send a get request to /orgs/:org. Request includes :•Path param org with value cucumber
    2.Verify status code 200, content type application/json; charset=utf-8
    3.Verify value of the login field is cucumber
    4.Verify value of the name field is cucumber
    5.Verify value of the id field is 320565
     */
    @Test
    @DisplayName("Verify organization information")
    public void test1() {
         given().
                when().
        get("orgs/cucumber").then().assertThat().statusCode(200).body("login",is("cucumber")).
                body("name",is("Cucumber")).body("id",is(320565));

    }


    /*
    1.Send a get request to /orgs/:org. Request includes :
    •Header Accept with value application/xml
    •Path param org with value cucumber
    2.Verify status code 415, content type application/json; charset=utf-83.
    Verify response status line include message Unsupported Media Type
     */
    @Test
    @DisplayName("Verify error message ")
    public void test2(){
        Response response =given().accept(ContentType.XML).when().
                get("orgs/cucumber");

        response.then().assertThat().statusCode(415).contentType("application/json");
        assertTrue(response.statusLine().contains("Unsupported Media Type"));
    }


    /*
    1.Send a get request to /orgs/:org. Request includes :•Path param org with value cucumber
    2.Grab the value of the field public_repos
    3.Send a get request to /orgs/:org/repos. Request includes :•Path param org with value cucumber
    4.Verify that number of objects in the response  is equal to value from step 2
     */
    @Test
    @DisplayName("Number of repositories")
    public void test3() {
        String basePath = "https://api.github.com/";
        Response response =given().accept(ContentType.JSON).when().
                get("orgs/cucumber");

        //System.out.println(response.getBody().prettyPeek());
        //step3
        Integer public_repos = response.jsonPath().get("public_repos");
        System.out.println(public_repos);
        //step2
        response =given().baseUri(basePath).accept(ContentType.JSON).when().
                get("orgs/cucumber?repos");
        Map<String,Integer>allbody = response.jsonPath().get();
        System.out.println(allbody.size());

        assertEquals(public_repos,allbody.size());
    }



    /*
    1.Send a get request to /orgs/:org/repos.
    Request includes :•Path param org with value cucumber
    2.Verify that id field is unique in every in every object in the response
    3.Verify that node_id field is unique in every in every object in the response
    */
    @Test
    @DisplayName("Repository id information")
    public void test4(){
    Response response =given().accept(ContentType.JSON).when().
            get("orgs/cucumber/repos");
    ArrayList<Map<String,Object>> list = response.body().jsonPath().get();

    //checking id is unique
    ArrayList<Object> arr = new ArrayList<>();

    //List<Object> idList = Arrays.asList(given().when().get(basePath+"orgs/cucumber/repos").then().extract().path("id"));
    for (int i = 0; i < list.size(); i++) {
        arr.add(list.get(i).get("id"));
    }
    System.out.println(arr);

    TreeSet<Object> uniqueArr = new TreeSet<>();
    for (int i = 0; i < list.size(); i++) {
        uniqueArr.add(list.get(i).get("id"));
    }
    System.out.println(uniqueArr);
   assertEquals(arr.size(), uniqueArr.size());


   //checking node_id is unique
    ArrayList<Object> arrNode = new ArrayList<>();

    //List<Object> idList = Arrays.asList(given().when().get(basePath+"orgs/cucumber/repos").then().extract().path("id"));
    for (int i = 0; i < list.size(); i++) {
        arrNode.add(list.get(i).get("node_id"));
    }

    System.out.println(arrNode);

    TreeSet<Object> uniqueNodeArr = new TreeSet<>();
    for (int i = 0; i < list.size(); i++) {
        uniqueNodeArr.add(list.get(i).get("node_id"));
    }
    System.out.println(uniqueNodeArr);
    assertEquals(arrNode.size(), uniqueNodeArr.size());

}

    /*
    1.Send a get request to /orgs/:org. Request includes :•Path param org with value cucumber
    2.Grab the value of the field id
    3.Send a get request to /orgs/:org/repos. Request includes :•Path param org with value cucumber
    4.Verify that value of the id inside the owner object in every response is equal to value from step 2
    */
    @Test
    @DisplayName("Repository owner information")
    public void test5(){
    Response response1 =given().accept(ContentType.JSON).when().
            get("orgs/cucumber");

    Integer id1 = response1.jsonPath().get("id");
    System.out.println(id1);

   // container+="/repos";
    Response response2 = given().accept(ContentType.JSON).when().
            get("orgs/cucumber/repos");



    Object id2 = 0;
    List<Map<String,Object>> wholeList = response2.jsonPath().getList("");

        for (int i = 0; i < wholeList.size(); i++) {
           if(wholeList.get(i).containsKey("owner")){
               Object owner = wholeList.get(i).get("owner");
               String[] arr = owner.toString().split(",");

               for(String each : arr){
                   if(each.split("=").toString().equals("320565")){
                   }
               }

           }
        }

        System.out.println(wholeList);
        System.out.println(id2);
}

/*
Ascending order by full_name sort
1.Send a get request to /orgs/:org/repos. Request includes :•Path param org with value cucumber
•Query param sort with value full_name
2.Verify that all repositories are listed in alphabetical order based on the value of the field name
 */
@Test
@DisplayName("Ascending order by full_name sort")
public void test6() {

    Response response = given().accept(ContentType.JSON).queryParam("sort", "full_name").when().
            get("orgs/cucumber/repos");

    List<String> listOfNames = response.jsonPath().get("full_name");
    assertEquals(listOfNames.stream().sorted().collect(Collectors.toList()), listOfNames);

}

/*
1.Send a get request to /orgs/:org/repos. Request includes :
Path param org with value cucumber
•Query param sort with value full_name•Query param direction with value desc
2.Verify that all repositories are listed in reverse alphabetical order based on the value of the field name
 */
@Test
@DisplayName("Descending order by full_name sort")
public void test7(){

    Response response=given().queryParam("sort","full_name").
            queryParam("direction","desc").when().
            get("orgs/cucumber/repos");

    List<String>listOfNames = response.then().extract().jsonPath().getList("full_name");

    assertEquals(listOfNames.stream().sorted().collect(Collectors.toList()),listOfNames);
}


/*
1.Send a get request to /orgs/:org/repos. Request includes :•Path param org with value cucumber
2.Verify that by default all repositories are listed in descending order based on the value of the field created_at
 */
    @Test
    @DisplayName("Default sort")
    public void test8(){
    Response response=given().accept(ContentType.JSON).when().
            get("orgs/cucumber/repos");
    List<String>created_at = response.then().extract().jsonPath().getList("created_at");

    assertEquals(created_at.stream().sorted().collect(Collectors.toList()),created_at);
}
}
