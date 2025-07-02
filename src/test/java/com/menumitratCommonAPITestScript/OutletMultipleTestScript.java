package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.Arrays;
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
import com.menumitra.apiRequest.OuletMultipleRequest;
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
public class OutletMultipleTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private OuletMultipleRequest outletMultipleRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(OutletMultipleTestScript.class);

    @BeforeClass
    private void outletMultipleSetUp() throws customException {
        try {
            LogUtils.info("Outlet Multiple SetUp");
            ExtentReport.createTest("Outlet Multiple SetUp");
            ExtentReport.getTest().log(Status.INFO, "Outlet Multiple SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getOutletMultipleUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No outlet multiple URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No outlet multiple URL found in test data");
                throw new customException("No outlet multiple URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            outletMultipleRequest = new OuletMultipleRequest();
            LogUtils.info("Outlet Multiple SetUp completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Outlet Multiple SetUp completed successfully");
        } catch (Exception e) {
            String errorMsg = "Error in outlet multiple setup: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    private Object[][] getOutletMultipleUrl() throws customException {
        try {
            LogUtils.info("Reading Outlet Multiple API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Outlet Multiple API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Outlet Multiple API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "outletmultiple".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No outlet multiple URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting outlet multiple URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting outlet multiple URL: " + e.getMessage());
            throw new customException("Error in getting outlet multiple URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getOutletMultipleData")
    public Object[][] getOutletMultipleData() throws customException {
        try {
            LogUtils.info("Reading outlet multiple test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading outlet multiple test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length >= 3 &&
                            "outletmultiple".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                            "positive".equalsIgnoreCase(Objects.toString(row[2], "")))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No valid outlet multiple test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting outlet multiple test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting outlet multiple test data: " + e.getMessage());
            throw new customException("Error in getting outlet multiple test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getOutletMultipleData")
    public void outletMultipleTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting outlet multiple test case: " + testCaseid);
            ExtentReport.createTest("Outlet Multiple Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("outletmultiple")) {
                requestBodyJson = new JSONObject(requestBody);

                // Set owner_ids
                int[] ownerIds = new int[requestBodyJson.getJSONArray("owner_ids").length()];
                for (int i = 0; i < ownerIds.length; i++) {
                    ownerIds[i] = requestBodyJson.getJSONArray("owner_ids").getInt(i);
                }
                outletMultipleRequest.setOwner_ids(ownerIds);

                // Set other fields
                outletMultipleRequest.setUser_id(String.valueOf(user_id));
                outletMultipleRequest.setName(requestBodyJson.getString("name"));
                outletMultipleRequest.setMobile(requestBodyJson.getString("mobile"));
                outletMultipleRequest.setAddress(requestBodyJson.getString("address"));
                outletMultipleRequest.setOutlet_type(requestBodyJson.getString("outlet_type"));
                outletMultipleRequest.setOutlet_mode(requestBodyJson.getString("outlet_mode"));
                outletMultipleRequest.setVeg_nonveg(requestBodyJson.getString("veg_nonveg"));
                outletMultipleRequest.setApp_type(requestBodyJson.getString("app_type"));
                outletMultipleRequest.setUpi_id(requestBodyJson.getString("upi_id"));

                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, outletMultipleRequest, httpsmethod, accessToken);

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

                // Validate status code - accept both 200 and 201
                int actualStatusCode = response.getStatusCode();
                if (actualStatusCode != 200 && actualStatusCode != 201) {
                    String errorMsg = "Status code mismatch - Expected: 200 or 201, Actual: " + actualStatusCode;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                // Only show response without validation
                actualJsonBody = new JSONObject(response.asString());
                LogUtils.info("Outlet multiple response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Outlet multiple response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

                LogUtils.success(logger, "Outlet multiple test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Outlet multiple test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in outlet multiple test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

    @Test
    public void createMultipleOutletsTest() throws customException {
        try {
            LogUtils.info("Starting creation of 20 outlets");
            ExtentReport.createTest("Create Multiple Outlets Test");
            ExtentReport.getTest().log(Status.INFO, "Starting creation of 20 outlets");

            int successCount = 0;
            for (int i = 1; i <= 5; i++) {
                LogUtils.info("Creating outlet #" + i);
                ExtentReport.getTest().log(Status.INFO, "Creating outlet #" + i);

                // Create request body for each outlet
                outletMultipleRequest = new OuletMultipleRequest();
                
                // Set owner_ids (using a single owner ID for example)
                int[] ownerIds = {user_id};  // You can modify this array as needed
                outletMultipleRequest.setOwner_ids(ownerIds);

                // Set other fields with dynamic values
                String uniqueMobile = "98765432" + String.format("%02d", i);
                String outletName = "Test Outlet " + i;
                
                outletMultipleRequest.setUser_id(String.valueOf(user_id));
                outletMultipleRequest.setName(outletName);
                outletMultipleRequest.setMobile(uniqueMobile);
                outletMultipleRequest.setAddress("Test Address " + i);
                outletMultipleRequest.setOutlet_type("restaurant");
                outletMultipleRequest.setOutlet_mode("online");
                outletMultipleRequest.setVeg_nonveg("veg");
                outletMultipleRequest.setApp_type("pos");
                outletMultipleRequest.setUpi_id("test.upi" + i + "@ybl");

                // Convert request to JSON for logging
                JSONObject requestJson = new JSONObject();
                requestJson.put("owner_ids", ownerIds);
                requestJson.put("user_id", String.valueOf(user_id));
                requestJson.put("name", outletName);
                requestJson.put("mobile", uniqueMobile);
                requestJson.put("address", "Test Address " + i);
                requestJson.put("outlet_type", "restaurant");
                requestJson.put("outlet_mode", "online");
                requestJson.put("veg_nonveg", "veg");
                requestJson.put("app_type", "pos");
                requestJson.put("upi_id", "test.upi" + i + "@ybl");

                LogUtils.info("Request Body for outlet #" + i + ": " + requestJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body for outlet #" + i + ": " + requestJson.toString());

                // Send request
                response = ResponseUtil.getResponseWithAuth(baseURI, outletMultipleRequest, "POST", accessToken);

                // Log detailed response information
                int statusCode = response.getStatusCode();
                String responseBody = response.asString();
                
                LogUtils.info("Response Status Code for outlet #" + i + ": " + statusCode);
                LogUtils.info("Response Body for outlet #" + i + ": " + responseBody);
                ExtentReport.getTest().log(Status.INFO, "Response Status Code for outlet #" + i + ": " + statusCode);
                ExtentReport.getTest().log(Status.INFO, "Response Body for outlet #" + i + ": " + responseBody);

                // Validate status code - accept both 200 and 201
                if (statusCode == 200 || statusCode == 201) {
                    successCount++;
                    LogUtils.success(logger, "Successfully created outlet #" + i + " - " + outletName);
                    ExtentReport.getTest().log(Status.PASS, "Successfully created outlet #" + i + " - " + outletName);
                } else {
                    String errorMsg = "Failed to create outlet #" + i + " - Status code: " + statusCode + ", Response: " + responseBody;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                }

                // Add a small delay between requests to prevent rate limiting
                Thread.sleep(2000);
            }

            // Final summary
            String summaryMsg = "Created " + successCount + " out of 20 outlets successfully";
            if (successCount == 20) {
                LogUtils.success(logger, summaryMsg);
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(summaryMsg, ExtentColor.GREEN));
            } else {
                String errorMsg = summaryMsg + " - Some outlets failed to create";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error in creating multiple outlets: " + e.getMessage();
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
