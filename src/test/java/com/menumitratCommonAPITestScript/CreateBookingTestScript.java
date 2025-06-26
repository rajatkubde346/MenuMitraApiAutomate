package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.BookRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.Listener;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class CreateBookingTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private BookRequest bookingCreateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(CreateBookingTestScript.class);

    @BeforeClass
    private void bookingCreateSetUp() throws customException {
        try {
            LogUtils.info("Booking Create SetUp");
            ExtentReport.createTest("Booking Create SetUp");
            ExtentReport.getTest().log(Status.INFO, "Booking Create SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getBookingCreateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No booking create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No booking create URL found in test data");
                throw new customException("No booking create URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            bookingCreateRequest = new BookRequest();

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in booking create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in booking create setup: " + e.getMessage());
            throw new customException("Error in booking create setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getBookingCreateUrl")
    private Object[][] getBookingCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Booking Create API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Booking Create API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Booking Create API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "bookingcreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No booking create URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting booking create URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting booking create URL: " + e.getMessage());
            throw new customException("Error in getting booking create URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getBookingCreateData")
    public Object[][] getBookingCreateData() throws customException {
        try {
            LogUtils.info("Reading booking create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading booking create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 3 &&
                        "bookingcreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row);
                }
            }

            if (filteredData.isEmpty()) {
                String errorMsg = "No valid booking create test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] result = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                result[i] = filteredData.get(i);
            }

            return result;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting booking create test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting booking create test data: " + e.getMessage());
            throw new customException("Error in getting booking create test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getBookingCreateData")
    public void bookingCreateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting booking create test case: " + testCaseid);
            ExtentReport.createTest("Booking Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("bookingcreate")) {
                requestBodyJson = new JSONObject(requestBody);

                bookingCreateRequest.setUser_id(user_id);
                bookingCreateRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
                bookingCreateRequest.setApp_source(requestBodyJson.getString("app_source"));

                // Set menu items
                List<BookRequest.MenuItem> menuItems = new ArrayList<>();
                org.json.JSONArray menuItemsArray = requestBodyJson.getJSONArray("menu_items");
                for (int i = 0; i < menuItemsArray.length(); i++) {
                    JSONObject menuItemJson = menuItemsArray.getJSONObject(i);
                    BookRequest.MenuItem menuItem = new BookRequest.MenuItem();
                    menuItem.setMenu_id(menuItemJson.getInt("menu_id"));
                    menuItem.setQuantity(menuItemJson.getInt("quantity"));
                    menuItem.setPrice(menuItemJson.getDouble("price"));
                    menuItem.setPortion_name(menuItemJson.getString("portion_name"));
                    if (menuItemJson.has("special_note")) {
                        menuItem.setSpecial_note(menuItemJson.getString("special_note"));
                    }
                    menuItems.add(menuItem);
                }
                bookingCreateRequest.setMenu_items(menuItems);

                // Set booking details
                JSONObject bookingDetailsJson = requestBodyJson.getJSONObject("booking_details");
                BookRequest.BookingDetails bookingDetails = new BookRequest.BookingDetails();
                bookingDetails.setDelivery_datetime(bookingDetailsJson.getString("delivery_datetime"));
                bookingDetails.setExpected_datetime(bookingDetailsJson.getString("expected_datetime"));
                bookingDetails.setCustomer_name(bookingDetailsJson.getString("customer_name"));
                bookingDetails.setCustomer_mobile(bookingDetailsJson.getString("customer_mobile"));
                if (bookingDetailsJson.has("special_message")) {
                    bookingDetails.setSpecial_message(bookingDetailsJson.getString("special_message"));
                }
                if (bookingDetailsJson.has("comment")) {
                    bookingDetails.setComment(bookingDetailsJson.getString("comment"));
                }
                bookingCreateRequest.setBooking_details(bookingDetails);

                // Set payment details
                JSONObject paymentDetailsJson = requestBodyJson.getJSONObject("payment_details");
                BookRequest.PaymentDetails paymentDetails = new BookRequest.PaymentDetails();
                paymentDetails.setAdvance_amount(paymentDetailsJson.getDouble("advance_amount"));
                paymentDetails.setPayment_method(paymentDetailsJson.getString("payment_method"));
                bookingCreateRequest.setPayment_details(paymentDetails);

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, bookingCreateRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate status code
                if (response.getStatusCode() != Integer.parseInt(statusCode)) {
                    String errorMsg = "Status code mismatch - Expected: " + statusCode + ", Actual: " + response.getStatusCode();
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Only show response without validation
                actualJsonBody = new JSONObject(response.asString());
                LogUtils.info("Booking create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Booking create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

                LogUtils.success(logger, "Booking create test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Booking create test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in booking create test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }
}
