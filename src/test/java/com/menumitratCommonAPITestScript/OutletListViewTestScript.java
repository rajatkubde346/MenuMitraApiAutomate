package com.menumitratCommonAPITestScript;

import java.net.URL;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.Assert;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.OutletListViewRequest;
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
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.response.Response;

@Listeners(Listener.class)
public class OutletListViewTestScript extends APIBase {
    // Concrete implementation of OutletListViewRequest
    private static class ConcreteOutletListViewRequest extends OutletListViewRequest {
        private String outlet_id;
        
        public String getOutlet_id() {
            return outlet_id;
        }
        
        public void setOutlet_id(String outlet_id) {
            this.outlet_id = outlet_id;
        }
    }

    private ConcreteOutletListViewRequest outletListViewRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(OutletListViewTestScript.class);

    @DataProvider(name = "getOutletListViewUrl")
    public Object[][] getOutletListViewUrl() throws customException {
        try {
            LogUtils.info("Reading Outlet List View API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            return Arrays.stream(readExcelData)
                    .filter(row -> "outletlistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Outlet List View API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Outlet List View API endpoint data from Excel sheet");
            throw new customException("Error While Reading Outlet List View API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getOutletListViewData")
    public Object[][] getOutletListViewData() throws customException {
        try {
            LogUtils.info("Reading Outlet List View test data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            return Arrays.stream(readExcelData)
                    .filter(row -> "outletlistview".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Outlet List View test data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Outlet List View test data from Excel sheet");
            throw new customException("Error While Reading Outlet List View test data from Excel sheet");
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for outlet list view test====");
            ExtentReport.createTest("Outlet List View Setup");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
            
            Object[][] outletListViewData = getOutletListViewUrl();
            if (outletListViewData.length > 0) {
                String endpoint = outletListViewData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for outlet list view: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No outlet list view URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No outlet list view URL found in test data");
                throw new customException("No outlet list view URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            outletListViewRequest = new ConcreteOutletListViewRequest();
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        } catch (Exception e) {
            String errorMsg = "Error in setup: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getOutletListViewData")
    private void outletListView(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting outlet list view test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Outlet List View Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("outletlistview")) {
                requestBodyJson = new JSONObject(requestBody);
                
                // Set request parameters
                outletListViewRequest.setUser_id(userId);
                outletListViewRequest.setApp_source(requestBodyJson.getString("app_source"));
                if (requestBodyJson.has("outlet_id")) {
                    outletListViewRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                }
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, outletListViewRequest, httpsmethod, accessToken);
                
                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asString());
                ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());
                
                // Validate response
                int expectedStatusCode = Integer.parseInt(statusCode);
                Assert.assertEquals(response.getStatusCode(), expectedStatusCode, "Status code mismatch");
                
                if (testType.equalsIgnoreCase("positive")) {
                    expectedJsonBody = new JSONObject(expectedResponseBody);
                    actualJsonBody = new JSONObject(response.asString());
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    LogUtils.info("Positive test case passed successfully");
                    ExtentReport.getTest().log(Status.PASS, "Positive test case passed successfully");
                } else {
                    expectedJsonBody = new JSONObject(expectedResponseBody);
                    validateResponseBody.handleResponseBody(response, expectedJsonBody);
                    LogUtils.info("Negative test case passed successfully");
                    ExtentReport.getTest().log(Status.PASS, "Negative test case passed successfully");
                }
            }
        } catch (Exception e) {
            String errorMsg = "Error in test case " + testCaseid + ": " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
}
