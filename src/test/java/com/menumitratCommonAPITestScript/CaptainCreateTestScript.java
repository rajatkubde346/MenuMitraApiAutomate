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
import com.menumitra.apiRequest.CaptainRequest;
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
public class CaptainCreateTestScript extends APIBase
{
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private CaptainRequest captainCreateRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    private JSONObject expectedResponseJson;
    Logger logger = LogUtils.getLogger(CaptainCreateTestScript.class);
   
    @BeforeClass
    private void captainCreateSetUp() throws customException
    {
        try
        {
            LogUtils.info("Captain Create SetUp");
            ExtentReport.createTest("Captain Create SetUp");
            ExtentReport.getTest().log(Status.INFO,"Captain Create SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();
            
            Object[][] getUrl = getCaptainCreateUrl();
            if (getUrl.length > 0) 
            {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No captain create URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No captain create URL found in test data");
                throw new customException("No captain create URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            user_id=TokenManagers.getUserId();
            if(accessToken.isEmpty())
            {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }
            
            captainCreateRequest = new CaptainRequest();
            
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in captain create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in captain create setup: " + e.getMessage());
            throw new customException("Error in captain create setup: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getCaptainCreateUrl")
    private Object[][] getCaptainCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Captain Create API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Captain Create API endpoint data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");
            
            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Captain Create API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }
            
            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "captaincreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
            
            if (filteredData.length == 0) {
                String errorMsg = "No captain create URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
            
            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting captain create URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting captain create URL: " + e.getMessage());
            throw new customException("Error in getting captain create URL: " + e.getMessage());
        }
    }
    
    @DataProvider(name = "getCaptainCreateData")
    public Object[][] getCaptainCreateData() throws customException {
        try {
            LogUtils.info("Reading captain create test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading captain create test scenario data");
            
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
                        "captaincreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }
            
            if (filteredData.isEmpty()) {
                String errorMsg = "No valid captain create test data found after filtering";
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
            LogUtils.failure(logger, "Error in getting captain create test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting captain create test data: " + e.getMessage());
            throw new customException("Error in getting captain create test data: " + e.getMessage());
        }
    }
    
    @Test(dataProvider = "getCaptainCreateData")
    public void captainCreateTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting captain create test case: " + testCaseid);
            ExtentReport.createTest("Captain Create Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            if (apiName.equalsIgnoreCase("captaincreate")) {
                requestBodyJson = new JSONObject(requestBody);
                
                captainCreateRequest.setUser_id(String.valueOf(user_id));
                captainCreateRequest.setOutlet_id(requestBodyJson.getString("outlet_id"));
                captainCreateRequest.setName(requestBodyJson.getString("name"));
                captainCreateRequest.setMobile(requestBodyJson.getString("mobile"));
                captainCreateRequest.setAddress(requestBodyJson.getString("address"));
                captainCreateRequest.setAadhar_number(requestBodyJson.getString("aadhar_number"));
                captainCreateRequest.setDob(requestBodyJson.getString("dob"));
                captainCreateRequest.setEmail(requestBodyJson.getString("email"));
                captainCreateRequest.setApp_source(requestBodyJson.getString("app_source"));
                
                // Convert JSONArray to int[]
                org.json.JSONArray jsonArray = requestBodyJson.getJSONArray("functionality_ids");
                int[] functionalityIds = new int[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    functionalityIds[i] = jsonArray.getInt(i);
                }
                captainCreateRequest.setFunctionality_ids(functionalityIds);
                
                LogUtils.info("Request Body: " + requestBodyJson.toString());
                ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());
                
                response = ResponseUtil.getResponseWithAuth(baseURI, captainCreateRequest, httpsmethod, accessToken);
                
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
                LogUtils.info("Captain create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Captain create response received successfully");
                ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());
                
                LogUtils.success(logger, "Captain create test completed successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Captain create test completed successfully", ExtentColor.GREEN));
            }
        } catch (Exception e) {
            String errorMsg = "Error in captain create test: " + e.getMessage();
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