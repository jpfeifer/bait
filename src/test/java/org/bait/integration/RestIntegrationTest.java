package org.bait.integration;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertNotNull;

public class RestIntegrationTest {

    public static final String BAI_ID_JSON_FIELD = "baiId";

    public static final String ACCOUNT_NUMBER_JSON_FIELD = "accountNumber";

    public static final String BANK_NUMBER_JSON_FIELD = "bankNumber";

    public static final String BANK_NAME_JSON_FIELD = "bankName";

    public static final String AMOUNT_JSON_FIELD = "amount";

    public static final String SUBJECT_JSON_FIELD = "subject";

    public static final String TRANSFER_ID_JSON_FIELD = "transferId";

    public static JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

    @Before
    public void setUp() {
        RestAssured.basePath = "/api/bai";
        RestAssured.port = 7070;
    }

    @Test
    public void postBankAccountInformation() throws Exception {
        given().
            contentType(MediaType.APPLICATION_JSON).
        when().
             body(buildBaiJson("1234", "56789", "My eco bank")).
             post().
        then().
            statusCode(Response.Status.CREATED.getStatusCode()).
            body(BAI_ID_JSON_FIELD, not(isEmptyOrNullString()));
    }

    @Test
    public void getNoBankAccountInformation() throws Exception {
        given().
            accept(MediaType.APPLICATION_JSON).
        when().
            get("/" + UUID.randomUUID().toString()).
        then().
            statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }

    @Test
    public void postAndGetBankAccountInformation() throws Exception {
        String accountNumber = "1234";
        String bankNumber = "56789";
        String bankName = "My eco bank";
        String returnJson =
            given().
                contentType(MediaType.APPLICATION_JSON).
            when().
                 body(buildBaiJson(accountNumber, bankNumber, bankName)).
            then().
                statusCode(Response.Status.CREATED.getStatusCode()).
            post().
                body().asString();

        final String baiId = from(returnJson).get(BAI_ID_JSON_FIELD);
        assertNotNull(baiId);

        given().
            accept(MediaType.APPLICATION_JSON).
        when().
            get("/" + baiId).
        then().
            statusCode(Response.Status.OK.getStatusCode()).
            body(BAI_ID_JSON_FIELD, equalTo(baiId)).
            body(ACCOUNT_NUMBER_JSON_FIELD, equalTo(accountNumber)).
            body(BANK_NUMBER_JSON_FIELD, equalTo(bankNumber)).
            body(BANK_NAME_JSON_FIELD, equalTo(bankName));

    }

    @Test
    public void postAndDeleteBankAccountInformation() throws Exception {
        String accountNumber = "1234";
        String bankNumber = "56789";
        String bankName = "My eco bank";
        String returnJson =
                given().
                        contentType(MediaType.APPLICATION_JSON).
                when().
                        body(buildBaiJson(accountNumber, bankNumber, bankName)).
                then().
                        statusCode(Response.Status.CREATED.getStatusCode()).
                post()
                        .asString();

        final String baiId = from(returnJson).get(BAI_ID_JSON_FIELD);
        assertNotNull(baiId);

        given().
                accept(MediaType.APPLICATION_JSON).
        when().
                get("/" + baiId).
        then().
                statusCode(Response.Status.OK.getStatusCode()).
                body(BAI_ID_JSON_FIELD, equalTo(baiId));

        given().
                accept(MediaType.APPLICATION_JSON).
        when().
                delete("/" + baiId).
        then().
                statusCode(Response.Status.NO_CONTENT.getStatusCode());

        given().
                accept(MediaType.APPLICATION_JSON).
        when().
                get("/" + baiId).
        then().
                statusCode(Response.Status.NOT_FOUND.getStatusCode());

    }

    @Test
    public void postAndGetBankTransfer() throws Exception {
        String accountNumber = "1234";
        String bankNumber = "56789";
        String bankName = "My eco bank";
        String baiReturnJson =
                given().
                        contentType(MediaType.APPLICATION_JSON).
                when().
                        body(buildBaiJson(accountNumber, bankNumber, bankName)).
                then().
                        statusCode(Response.Status.CREATED.getStatusCode()).
                post().
                        body().asString();

        final String baiId = from(baiReturnJson).get(BAI_ID_JSON_FIELD);
        assertNotNull(baiId);

        String amount = "142.23";
        String subject = "Bill Number 01257091725";

        String transferReturnJson =
                given().
                        contentType(MediaType.APPLICATION_JSON).
                when().
                        body(buildTransferJson(subject, amount)).
                then().
                        statusCode(Response.Status.CREATED.getStatusCode()).
                post("/" + baiId + "/transfer").
                        body().asString();

        final String transferId = from(baiReturnJson).get(TRANSFER_ID_JSON_FIELD);
        assertNotNull(transferId);

        given().
                contentType(MediaType.APPLICATION_JSON).
        when().
                get("/"+ baiId + "/transfer/" + transferId).
        then().
                statusCode(Response.Status.OK.getStatusCode()).
                body(SUBJECT_JSON_FIELD, equalTo(subject)).
                body(AMOUNT_JSON_FIELD, equalTo(amount)).
                body(TRANSFER_ID_JSON_FIELD, equalTo(transferId)).
                body(BAI_ID_JSON_FIELD, equalTo(baiId));

    }

    private String buildTransferJson(String subject, String amount) {
        return jsonNodeFactory.objectNode().
                put(SUBJECT_JSON_FIELD, subject).
                put(AMOUNT_JSON_FIELD, amount).
                toString();
    }

    private String buildBaiJson(String accountNumber, String bankNumber, String bankName) {
        return jsonNodeFactory.objectNode().
                put(ACCOUNT_NUMBER_JSON_FIELD, accountNumber).
                put(BANK_NUMBER_JSON_FIELD, bankNumber).
                put(BANK_NAME_JSON_FIELD, bankName).
                toString();
    }
}
