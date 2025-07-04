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
import com.menumitra.apiRequest.DeleteMultipleOutletRequest;
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
public class DeleteMultipleOutletTestScripts extends APIBase {
    private DeleteMultipleOutletRequest deleteMultipleOutletRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private String baseURI;
    private URL url;
    private int userId;
    private String accessToken;
    private JSONObject expectedJsonBody;
    private JSONObject actualJsonBody;
    Logger logger = LogUtils.getLogger(DeleteMultipleOutletTestScripts.class);

    @DataProvider(name = "getDeleteMultipleOutletUrl")
    public Object[][] getDeleteMultipleOutletUrl() throws customException {
        try {
            LogUtils.info("Reading Delete Multiple Outlet API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            return Arrays.stream(readExcelData)
                    .filter(row -> "deletemultipleoutlet".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Delete Multiple Outlet API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Delete Multiple Outlet API endpoint data from Excel sheet");
            throw new customException("Error While Reading Delete Multiple Outlet API endpoint data from Excel sheet");
        }
    }

    @DataProvider(name = "getDeleteMultipleOutletData")
    public Object[][] getDeleteMultipleOutletData() throws customException {
        try {
            LogUtils.info("Reading Delete Multiple Outlet test data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            return Arrays.stream(readExcelData)
                    .filter(row -> "deletemultipleoutlet".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.error("Error While Reading Delete Multiple Outlet test data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Delete Multiple Outlet test data from Excel sheet");
            throw new customException("Error While Reading Delete Multiple Outlet test data from Excel sheet");
        }
    }

    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("====Starting setup for delete multiple outlet test====");
            ExtentReport.createTest("Delete Multiple Outlet Setup");
            
            LogUtils.info("Initiating login process");
            ActionsMethods.login();
            LogUtils.info("Login successful, proceeding with OTP verification");
            ActionsMethods.verifyOTP();
            
            baseURI = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URL retrieved: " + baseURI);
            
            Object[][] deleteMultipleOutletData = getDeleteMultipleOutletUrl();
            if (deleteMultipleOutletData.length > 0) {
                String endpoint = deleteMultipleOutletData[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI for delete multiple outlet: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No delete multiple outlet URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No delete multiple outlet URL found in test data");
                throw new customException("No delete multiple outlet URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            deleteMultipleOutletRequest = new DeleteMultipleOutletRequest();
            LogUtils.info("Setup completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Setup completed successfully");
        } catch (Exception e) {
            String errorMsg = "Error in setup: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    @Test(dataProvider = "getDeleteMultipleOutletData")
    private void deleteMultipleOutlets(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode)
            throws customException {
        try {
            LogUtils.info("Starting delete multiple outlet test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Delete Multiple Outlet Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("deletemultipleoutlet")) {
                requestBodyJson = new JSONObject(requestBody);
                
                // Set request parameters
                deleteMultipleOutletRequest.setUser_id(userId);
                deleteMultipleOutletRequest.setApp_source(requestBodyJson.getString("app_source"));
                deleteMultipleOutletRequest.setAction(requestBodyJson.getString("action"));
                
                if (requestBodyJson.has("outlet_ids")) {
                    int[] outletIds = Arrays.stream(requestBodyJson.getJSONArray("outlet_ids").toString()
                            .replace("[", "").replace("]", "").split(","))
                            .map(String::trim)
                            .mapToInt(Integer::parseInt)
                            .toArray();
                    deleteMultipleOutletRequest.setOutlet_ids(outletIds);
                }
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, deleteMultipleOutletRequest, httpsmethod, accessToken);
                
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
