package com.menumitratCommonAPITestScript;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import com.menumitra.apiRequest.MenuCategoryRequest;
import com.menumitra.superclass.APIBase;
import com.menumitra.utilityclass.ActionsMethods;
import com.menumitra.utilityclass.DataDriven;
import com.menumitra.utilityclass.EnviromentChanges;
import com.menumitra.utilityclass.ExtentReport;
import com.menumitra.utilityclass.LogUtils;
import com.menumitra.utilityclass.RequestValidator;
import com.menumitra.utilityclass.ResponseUtil;
import com.menumitra.utilityclass.TokenManagers;
import com.menumitra.utilityclass.customException;
import com.menumitra.utilityclass.validateResponseBody;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Listeners(com.menumitra.utilityclass.Listener.class)
public class MenuCategoryCreateTestScript extends APIBase 
{
 
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private int userId;
    private String accessToken;
    private String deviceToken;
    private RequestSpecification request;
    Logger logger = Logger.getLogger(MenuCategoryCreateTestScript.class);
    
    
    
    
    /**
     * 
     * 
     * Data provider for menu category create API endpoint URLs
     */
    @DataProvider(name="getMenuCategoryCreateUrl")
    public Object[][] getMenuCategoryCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Category Create API endpoint data from Excel sheet");
            //ExtentReport.getTest().log(Status.INFO, "Reading Menu Category Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            return Arrays.stream(readExcelData)
                .filter(row -> "menucategorycreate".equalsIgnoreCase(row[0].toString()))
                .toArray(Object[][]::new);
        } catch(Exception e) {
            LogUtils.error("Error While Reading Menu Category Create API endpoint data from Excel sheet");
            ExtentReport.getTest().log(Status.ERROR, "Error While Reading Menu Category Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Menu Category Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for menu category create test scenarios
     */
    @DataProvider(name="getMenuCategoryCreateData")
    public Object[][] getMenuCategoryCreateData() throws customException {
        try {
            LogUtils.info("Reading menu category create test scenario data");
           //ExtentReport.getTest().log(Status.INFO, "Reading menu category create test scenario data");
            
            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null || readExcelData.length == 0) {
                LogUtils.error("No menu category create test scenario data found in Excel sheet");
                //ExtentReport.getTest().log(Status.ERROR, "No menu category create test scenario data found in Excel sheet");
                throw new customException("No menu category create test scenario data found in Excel sheet");
            }
            
            List<Object[]> filteredData = new ArrayList<>();
            
            for (int i = 0; i < readExcelData.length; i++) {
                Object[] row = readExcelData[i];
                if (row != null && row.length >= 2 &&
                    "menucategorycreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                    "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {
                    
                    filteredData.add(row);
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            return obj;
        } catch(Exception e) {
            LogUtils.error("Error while reading menu category create test scenario data: " + e.getMessage());
            ExtentReport.getTest().log(Status.ERROR, "Error while reading menu category create test scenario data: " + e.getMessage());
            throw new customException("Error while reading menu category create test scenario data: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException 
    {
        try {
            LogUtils.info("Menu Category Create SetUp");
            ExtentReport.createTest("Menu Category Create Setup");
            ActionsMethods.login(); 
            ActionsMethods.verifyOTP();
            
            
            baseUri = EnviromentChanges.getBaseUrl();
            LogUtils.info("Base URI set to: " + baseUri);
            //ExtentReport.getTest().log(Status.INFO, "Base URI set to: " + baseUri);
            
            Object[][] menuCategoryUrl = getMenuCategoryCreateUrl();
            if (menuCategoryUrl.length > 0) 
            {
          
                String endpoint = menuCategoryUrl[0][2].toString();
                url = new URL(endpoint);
                baseUri = baseUri+""+url.getPath()+"?"+url.getQuery();
                LogUtils.info("Menu Category Create URL set to: " + baseUri);
            } else {
                LogUtils.error("No menu category create URL found in test data");
                throw new customException("No menu category create URL found in test data");
            }
            
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();
            
            if (accessToken.isEmpty()) {
                LogUtils.error("Required tokens not found");
                throw new customException("Required tokens not found");
            }
            
           
            LogUtils.info("Menu Category Create Setup completed successfully");
            
        } catch (Exception e) {
            LogUtils.error("Error during menu category create setup: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error during menu category create setup: " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    /**
     * Test method to create menu category
     */
    @Test(dataProvider="getMenuCategoryCreateData")
    private void createMenuCategoryUsingValidInputData(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting menu category create test: " + description);
            ExtentReport.createTest("Menu Category Create Using Valid Input Data");
            ExtentReport.getTest().log(Status.INFO, "Starting menu category create test: " + description);
            ExtentReport.getTest().log(Status.INFO, "Base URI: " + baseUri);

            if (apiName.equalsIgnoreCase("menucategorycreate") && testType.equalsIgnoreCase("positive")) {
                LogUtils.info("Processing menu category create request");
                LogUtils.info("Request Body: " + requestBody);
                
                requestBodyJson = new JSONObject(requestBody.replace("\\", ""));
                expectedResponse = new JSONObject(expectedResponseBody);
               
                // Create MenuCategoryRequest object
                MenuCategoryRequest categoryRequest = new MenuCategoryRequest(
                    requestBodyJson.getInt("outlet_id"),
                    requestBodyJson.getString("category_name"),
                    String.valueOf(userId)
                );

                // Add image if present
                if (requestBodyJson.has("image") && !requestBodyJson.getString("image").isEmpty()) {
                    String imagePath = requestBodyJson.getString("image");
                    LogUtils.info("Image path from request: " + imagePath);
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        LogUtils.info("Image file exists at: " + imagePath);
                        categoryRequest.withImage(imageFile);
                    } else {
                        LogUtils.error("Image file not found at: " + imagePath);
                    }
                }

                request = RestAssured.given();
                request.header("Authorization", "Bearer " + accessToken);
                request.header("Content-Type", "multipart/form-data");

                // Log request details
                LogUtils.info("Setting multipart form data:");
                LogUtils.info("outlet_id: " + categoryRequest.getOutletId());
                LogUtils.info("category_name: " + categoryRequest.getCategoryName());
                LogUtils.info("user_id: " + categoryRequest.getUserId());

                // Set multipart form data using MenuCategoryRequest
                request.multiPart("outlet_id", String.valueOf(categoryRequest.getOutletId()));
                request.multiPart("category_name", categoryRequest.getCategoryName());
                request.multiPart("user_id", categoryRequest.getUserId());
                
                // Handle image file upload if it exists
                if (categoryRequest.getImage() != null && categoryRequest.getImage().exists()) {
                    LogUtils.info("Adding image to request: " + categoryRequest.getImage().getAbsolutePath());
                    request.multiPart("image", categoryRequest.getImage());
                }

                LogUtils.info("Sending POST request to endpoint: " + baseUri);
                response = request.when().post(baseUri).then().extract().response();

                LogUtils.info("Response Status Code: " + response.getStatusCode());
                LogUtils.info("Response Body: " + response.asPrettyString());

                if (response.getStatusCode() == 200) {
                    LogUtils.success(logger, "Menu category created successfully");
                    ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu category created successfully", ExtentColor.GREEN));
                    validateResponseBody.handleResponseBody(response, expectedResponse);
                    LogUtils.info("Response validation completed successfully");
                    ExtentReport.getTest().log(Status.PASS, "Response validation completed successfully");
                } else {
                    LogUtils.failure(logger, "Menu category creation failed with status code: " + response.getStatusCode());
                    LogUtils.error("Response body: " + response.asPrettyString());
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Menu category creation failed", ExtentColor.RED));
                    ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
                    throw new customException("Menu category creation failed with status code: " + response.getStatusCode());
                }
            }
        } catch (Exception e) {
            LogUtils.error("Error during menu category create test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during menu category create test execution: " + e.getMessage());
        }
    }

    /**
     * Cleanup method to perform post-test cleanup
     * @throws customException 
     */
    @AfterClass
    private void tearDown() throws customException 
    {
    	
       /*LogUtils.info("Performing tear down");
       ExtentReport.getTest().log(Status.INFO, "Performing tear down");
       ActionsMethods.logout();
       TokenManagers.clearTokens();*/
    }
}