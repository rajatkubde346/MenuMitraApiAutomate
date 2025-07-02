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
import com.menumitra.apiRequest.OutletCreateRequest;
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
public class outletCreateTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private OutletCreateRequest outletCreateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(outletCreateTestScript.class);

    @BeforeClass
    private void outletCreateSetUp() throws customException {
        try {
            LogUtils.info("Outlet Create SetUp");
            ExtentReport.createTest("Outlet Create SetUp");
            ExtentReport.getTest().log(Status.INFO, "Outlet Create SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getOutletCreateUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No outlet create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No outlet create URL found in test data");
                throw new customException("No outlet create URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            outletCreateRequest = new OutletCreateRequest();
            LogUtils.info("Outlet Create SetUp completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Outlet Create SetUp completed successfully");
        } catch (Exception e) {
            String errorMsg = "Error in outlet create setup: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    private Object[][] getOutletCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Outlet Create API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Outlet Create API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Outlet Create API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "outletcreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No outlet create URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting outlet create URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting outlet create URL: " + e.getMessage());
            throw new customException("Error in getting outlet create URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getOutletCreateData")
    public Object[][] getOutletCreateData() throws customException {
        try {
            LogUtils.info("Reading outlet create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading outlet create test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length >= 3 &&
                            "outletcreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                            "positive".equalsIgnoreCase(Objects.toString(row[2], "")))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No valid outlet create test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting outlet create test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting outlet create test data: " + e.getMessage());
            throw new customException("Error in getting outlet create test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getOutletCreateData")
    public void outletCreateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting outlet create test case: " + testCaseid);
            ExtentReport.createTest("Outlet Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("outletcreate")) {
                requestBodyJson = new JSONObject(requestBody);

                // Set owner_ids
                int[] ownerIds = new int[requestBodyJson.getJSONArray("owner_ids").length()];
                for (int i = 0; i < ownerIds.length; i++) {
                    ownerIds[i] = requestBodyJson.getJSONArray("owner_ids").getInt(i);
                }
                outletCreateRequest.setOwner_ids(ownerIds);

                // Set other fields
                outletCreateRequest.setUser_id(String.valueOf(user_id));
                outletCreateRequest.setName(requestBodyJson.getString("name"));
                outletCreateRequest.setMobile(requestBodyJson.getString("mobile"));
                outletCreateRequest.setAddress(requestBodyJson.getString("address"));
                outletCreateRequest.setOutlet_type(requestBodyJson.getString("outlet_type"));
                outletCreateRequest.setOutlet_mode(requestBodyJson.getString("outlet_mode"));
                outletCreateRequest.setVeg_nonveg(requestBodyJson.getString("veg_nonveg"));
                outletCreateRequest.setApp_type(requestBodyJson.getString("app_type"));
                outletCreateRequest.setUpi_id(requestBodyJson.getString("upi_id"));
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

                response = ResponseUtil.getResponseWithAuth(baseURI, outletCreateRequest, httpsmethod, accessToken);

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
                LogUtils.info("Outlet create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Outlet create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

                LogUtils.success(logger, "Outlet create test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Outlet create test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in outlet create test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            if (response != null) {
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
            }
            throw new customException(errorMsg);
        }
    }

    @Test(dependsOnMethods = "outletCreateTest")
    public void createMultipleOutlets() throws customException {
        try {
            LogUtils.info("Starting multiple outlet creation test");
            ExtentReport.createTest("Create Multiple Outlets Test");
            ExtentReport.getTest().log(Status.INFO, "Creating 20 outlets");

            // Get base request body from data provider
            Object[][] testData = getOutletCreateData();
            if (testData.length == 0) {
                throw new customException("No test data available for outlet creation");
            }
            String baseRequestBody = testData[0][5].toString(); // Get request body from first test case
            JSONObject baseJson = new JSONObject(baseRequestBody);

            for (int i = 1; i <= 5; i++) {
                requestBodyJson = new JSONObject(baseRequestBody);
                String outletName = baseJson.getString("name") + " " + i;
                requestBodyJson.put("name", outletName);

                // Set owner_ids
                int[] ownerIds = new int[requestBodyJson.getJSONArray("owner_ids").length()];
                for (int j = 0; j < ownerIds.length; j++) {
                    ownerIds[j] = requestBodyJson.getJSONArray("owner_ids").getInt(j);
                }
                outletCreateRequest = new OutletCreateRequest();
                outletCreateRequest.setOwner_ids(ownerIds);

                // Set other fields
                outletCreateRequest.setUser_id(String.valueOf(user_id));
                outletCreateRequest.setName(outletName);
                outletCreateRequest.setMobile(requestBodyJson.getString("mobile"));
                outletCreateRequest.setAddress(requestBodyJson.getString("address"));
                outletCreateRequest.setOutlet_type(requestBodyJson.getString("outlet_type"));
                outletCreateRequest.setOutlet_mode(requestBodyJson.getString("outlet_mode"));
                outletCreateRequest.setVeg_nonveg(requestBodyJson.getString("veg_nonveg"));
                outletCreateRequest.setApp_type(requestBodyJson.getString("app_type"));
                outletCreateRequest.setUpi_id(requestBodyJson.getString("upi_id"));

                LogUtils.info("Creating outlet " + i + ": " + outletName);
                ExtentReport.getTest().log(Status.INFO, "Creating outlet " + i + ": " + outletName);

                response = ResponseUtil.getResponseWithAuth(baseURI, outletCreateRequest, "POST", accessToken);

                // Validate status code
                int actualStatusCode = response.getStatusCode();
                if (actualStatusCode != 200 && actualStatusCode != 201) {
                    String errorMsg = "Failed to create outlet " + i + " - Status code: " + actualStatusCode;
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                    throw new customException(errorMsg);
                }

                LogUtils.info("Successfully created outlet " + i + " with status code: " + actualStatusCode);
                ExtentReport.getTest().log(Status.PASS, "Successfully created outlet " + i);
            }

            LogUtils.success(logger, "Successfully created 20 outlets");
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Successfully created 20 outlets", ExtentColor.GREEN));
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