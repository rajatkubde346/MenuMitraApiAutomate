package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.BookViewRequest;
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
public class ViewBookingTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private BookViewRequest bookViewRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(ViewBookingTestScript.class);

    @BeforeClass
    private void bookingViewSetUp() throws customException {
        try {
            LogUtils.info("Booking View SetUp");
            ExtentReport.createTest("Booking View SetUp");
            ExtentReport.getTest().log(Status.INFO, "Booking View SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getBookingViewUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No booking view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No booking view URL found in test data");
                throw new customException("No booking view URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            bookViewRequest = new BookViewRequest();

        } catch (Exception e) {
            LogUtils.failure(logger, "Error in booking view setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in booking view setup: " + e.getMessage());
            throw new customException("Error in booking view setup: " + e.getMessage());
        }
    }

    @DataProvider(name = "getBookingViewUrl")
    private Object[][] getBookingViewUrl() throws customException {
        try {
            LogUtils.info("Reading Booking View API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Booking View API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Booking View API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "bookingview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No booking view URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting booking view URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting booking view URL: " + e.getMessage());
            throw new customException("Error in getting booking view URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getBookingViewData")
    public Object[][] getBookingViewData() throws customException {
        try {
            LogUtils.info("Reading booking view test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading booking view test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length >= 3 &&
                            "bookingview".equalsIgnoreCase(row[0].toString()) &&
                            "positive".equalsIgnoreCase(row[2].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No valid booking view test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting booking view test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting booking view test data: " + e.getMessage());
            throw new customException("Error in getting booking view test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getBookingViewData")
    public void bookingViewTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting booking view test case: " + testCaseid);
            ExtentReport.createTest("Booking View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("bookingview")) {
                requestBodyJson = new JSONObject(requestBody);

                bookViewRequest.setOutlet_id(requestBodyJson.getInt("outlet_id"));
                bookViewRequest.setBooking_id(requestBodyJson.getInt("booking_id"));
                bookViewRequest.setApp_source(requestBodyJson.getString("app_source"));

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, bookViewRequest, httpsmethod, accessToken);

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
                LogUtils.info("Booking view response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Booking view response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

                LogUtils.success(logger, "Booking view test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Booking view test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in booking view test: " + e.getMessage();
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
