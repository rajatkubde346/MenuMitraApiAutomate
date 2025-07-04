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
    public void createMultipleOutletsTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting creation of multiple outlets");
            ExtentReport.createTest("Create Multiple Outlets Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("outletmultiple")) {
                requestBodyJson = new JSONObject(requestBody);
                int successCount = 0;
                int totalOutlets = 2;  // Define total outlets to create

                for (int i = 1; i <= totalOutlets; i++) {
                    LogUtils.info("Creating outlet #" + i);
                    ExtentReport.getTest().log(Status.INFO, "Creating outlet #" + i);

                    outletMultipleRequest = new OuletMultipleRequest();
                    
                    // Set owner_ids from Excel data
                    int[] ownerIds = new int[requestBodyJson.getJSONArray("owner_ids").length()];
                    for (int j = 0; j < ownerIds.length; j++) {
                        ownerIds[j] = requestBodyJson.getJSONArray("owner_ids").getInt(j);
                    }
                    outletMultipleRequest.setOwner_ids(ownerIds);

                    // Generate unique values for this outlet
                    String uniqueMobile = String.format("9%09d", i);  // Creates a 10-digit number starting with 9
                    String outletName = requestBodyJson.getString("name") + " " + i;
                    String outletAddress = requestBodyJson.getString("address") + " " + i;
                    
                    // Set fields using Excel data and generated unique values
                    outletMultipleRequest.setUser_id(String.valueOf(user_id));
                    outletMultipleRequest.setName(outletName);
                    outletMultipleRequest.setMobile(uniqueMobile);
                    outletMultipleRequest.setAddress(outletAddress);
                    outletMultipleRequest.setOutlet_type(requestBodyJson.getString("outlet_type"));
                    outletMultipleRequest.setOutlet_mode(requestBodyJson.getString("outlet_mode"));
                    outletMultipleRequest.setVeg_nonveg(requestBodyJson.getString("veg_nonveg"));
                    outletMultipleRequest.setApp_type(requestBodyJson.getString("app_type"));
                    outletMultipleRequest.setUpi_id(requestBodyJson.getString("upi_id"));

                    // Create JSON for logging
                    JSONObject currentRequestJson = new JSONObject();
                    currentRequestJson.put("owner_ids", ownerIds);
                    currentRequestJson.put("user_id", String.valueOf(user_id));
                    currentRequestJson.put("name", outletName);
                    currentRequestJson.put("mobile", uniqueMobile);
                    currentRequestJson.put("address", outletAddress);
                    currentRequestJson.put("outlet_type", requestBodyJson.getString("outlet_type"));
                    currentRequestJson.put("outlet_mode", requestBodyJson.getString("outlet_mode"));
                    currentRequestJson.put("veg_nonveg", requestBodyJson.getString("veg_nonveg"));
                    currentRequestJson.put("app_type", requestBodyJson.getString("app_type"));
                    currentRequestJson.put("upi_id", requestBodyJson.getString("upi_id"));

                    LogUtils.info("Request Body for outlet #" + i + ": " + currentRequestJson.toString());
                    ExtentReport.getTest().log(Status.INFO, "Request Body for outlet #" + i + ": " + currentRequestJson.toString());

                    response = ResponseUtil.getResponseWithAuth(baseURI, outletMultipleRequest, httpsmethod, accessToken);

                    LogUtils.info("Response Status Code for outlet #" + i + ": " + response.getStatusCode());
                    LogUtils.info("Response Body for outlet #" + i + ": " + response.asString());
                    ExtentReport.getTest().log(Status.INFO, "Response Status Code for outlet #" + i + ": " + response.getStatusCode());
                    ExtentReport.getTest().log(Status.INFO, "Response Body for outlet #" + i + ": " + response.asString());

                    // Validate status code - accept both 200 and 201
                    int actualStatusCode = response.getStatusCode();
                    if (actualStatusCode == 200 || actualStatusCode == 201) {
                        successCount++;
                        LogUtils.success(logger, "Successfully created outlet #" + i + " - " + outletName);
                        ExtentReport.getTest().log(Status.PASS, "Successfully created outlet #" + i + " - " + outletName);
                    } else {
                        String errorMsg = "Failed to create outlet #" + i + " - Status code: " + actualStatusCode + ", Response: " + response.asString();
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    }

                    // Add a small delay between requests to prevent rate limiting
                    Thread.sleep(2000);
                }

                // Final summary
                String summaryMsg = "Created " + successCount + " out of " + totalOutlets + " outlets successfully";
                if (successCount == totalOutlets) {
                    LogUtils.success(logger, summaryMsg);
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(summaryMsg, ExtentColor.GREEN));
                } else {
                    String errorMsg = summaryMsg + " - Some outlets failed to create";
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }
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
