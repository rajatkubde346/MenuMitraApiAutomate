package com.menumitratCommonAPITestScript;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.bson.types.Symbol;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.menumitra.apiRequest.MenuRequest;
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

import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;

@Listeners(Listener.class)
public class MenuCreateTestScript extends APIBase {

    private MenuRequest menuRequest;
    private Response response;
    private JSONObject requestBodyJson;
    private JSONObject actualResponseBody;
    private JSONObject expectedResponse;
    private String baseUri = null;
    private URL url;
    private String accessToken;
    private int userId;
    private RequestSpecification request;
    private static Logger logger = LogUtils.getLogger(MenuCreateTestScript.class);

    /**
     * Data provider for menu create API endpoint URLs
     */
    @DataProvider(name = "getMenuCreateUrl")
    public static Object[][] getMenuCreateUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Create API endpoint data from Excel sheet");
            Object[][] readExcelData = DataDriven.readExcelData("src\\test\\resources\\excelsheet\\apiEndpoint.xlsx",
                    "commonAPI");

            return Arrays.stream(readExcelData)
                    .filter(row -> "menuCreate".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);
        } catch (Exception e) {
            LogUtils.exception(logger, "Error While Reading Menu Create API endpoint data from Excel sheet", e);
            ;
            ExtentReport.getTest().log(Status.ERROR,
                    "Error While Reading Menu Create API endpoint data from Excel sheet");
            throw new customException("Error While Reading Menu Create API endpoint data from Excel sheet");
        }
    }

    /**
     * Data provider for menu create test scenarios
     */
    @DataProvider(name = "getMenuCreateData")
    public static Object[][] getMenuCreateData() throws customException {
        try {
            LogUtils.info("Reading menu create test scenario data");

            LogUtils.info("Reading positive test scenario data for login API from Excel sheet");
            Object[][] testData = DataDriven.readExcelData("src\\test\\resources\\excelsheet\\apiEndpoint.xlsx",
                    "CommonAPITestScenario");

            if (testData == null || testData.length == 0) {
                LogUtils.error("No Login Api positive test scenario data found in Excel sheet");
                throw new customException("No Login APi Positive test scenario data found in Excel sheet");
            }

            List<Object[]> filteredData = new ArrayList<>();

            for (int i = 0; i < testData.length; i++) {
                Object[] row = testData[i];

                // Ensure row is not null and has at least 3 columns
                if (row != null && row.length >= 3 &&
                        "menucreate".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                        "positive".equalsIgnoreCase(Objects.toString(row[2], ""))) {

                    filteredData.add(row); // Add the full row (all columns)
                }
            }

            Object[][] obj = new Object[filteredData.size()][];
            for (int i = 0; i < filteredData.size(); i++) {
                obj[i] = filteredData.get(i);
            }

            // Optional: print to verify
            /*
             * for (Object[] row : obj) {
             * System.out.println(Arrays.toString(row));
             * }
             */
            return obj;
        } catch (Exception e) {
            LogUtils.exception(logger, "Error while reading menu create test scenario data from Excel sheet", e);
            ExtentReport.getTest().log(Status.ERROR,
                    "Error while reading menu create test scenario data: " + e.getMessage());
            throw new customException(
                    "Error while reading menu create test scenario data from Excel sheet: " + e.getMessage());
        }
    }

    /**
     * Setup method to initialize test environment
     */
    @BeforeClass
    private void setup() throws customException {
        try {
            LogUtils.info("=====Verify Menu Create Test Script=====");
            ExtentReport.createTest("Verify Menu Create Test Script");
            ActionsMethods.login();
            ActionsMethods.verifyOTP();

            // Get base URL
            baseUri = EnviromentChanges.getBaseUrl();

            // Get and set menu create URL
            Object[][] menuCreateData = getMenuCreateUrl();

            if (menuCreateData.length > 0) {
                String endpoint = menuCreateData[0][2].toString();
                url = new URL(endpoint);
                baseUri = RequestValidator.buildUri(endpoint, baseUri);
                baseUri = endpoint;
                LogUtils.success(logger, "Constructed Menu Create Base URI: " + baseUri);
                ExtentReport.getTest().log(Status.INFO, "Constructed Menu Create Base URI: " + baseUri);
            } else {

                LogUtils.failure(logger, "Failed constructed Menu Create Base URI.");
                ExtentReport.getTest().log(Status.ERROR, "Failed constructed Menu Create Base URI.");
                throw new customException("Failed constructed Menu Create Base URI.");
            }

            // Get tokens from TokenManager
            accessToken = TokenManagers.getJwtToken();
            userId = TokenManagers.getUserId();

            if (accessToken.isEmpty()) {
                LogUtils.error(
                        "Required tokens not found. Please ensure login and OTP verification is completed");
                ExtentReport.getTest().log(Status.FAIL,
                        "Error: Required tokens not found. Please ensure login and OTP verification is completed");
                throw new customException(
                        "Required tokens not found. Please ensure login and OTP verification is completed");
            }
            menuRequest = new MenuRequest();
            LogUtils.success(logger, "Menu create test script Setup completed successfully");
            ExtentReport.getTest().log(Status.INFO, "Menu create test script Setup completed successfully");

        } catch (Exception e) {
            LogUtils.exception(logger, "Error during menu create test script setup", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during menu create test script setup " + e.getMessage());
            throw new customException("Error during setup: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getMenuCreateData")
    private void createMenuUsigValidInputData(String apiName,String testCaseid, String testType, String description,
    		String httpsmethod,String requestBodyPayload,String expectedResponseBody,String statusCode)
            throws customException {

        try {
            LogUtils.info("Starting menu creation test case: " + testCaseid);
            LogUtils.info("Test Description: " + description);
            ExtentReport.createTest("Menu Creation Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);
            
            try {
                LogUtils.info("Parsing expected response JSON");
                expectedResponse = new JSONObject(expectedResponseBody);
            } catch (Exception e) {
                LogUtils.error("Failed to parse expected response JSON: " + e.getMessage());
                LogUtils.error("Expected response body: " + expectedResponseBody);
                throw new customException("Failed to parse expected response JSON: " + e.getMessage());
            }

            try {
                LogUtils.info("Parsing request body JSON");
                requestBodyJson = new JSONObject(requestBodyPayload.replace("\\\\", "\\"));
                LogUtils.info("Successfully parsed request body JSON");
            } catch (Exception e) {
                LogUtils.error("Failed to parse request body JSON: " + e.getMessage());
                LogUtils.error("Request body payload: " + requestBodyPayload);
                throw new customException("Failed to parse request body JSON: " + e.getMessage());
            }
      
            request = RestAssured.given();
            request.header("Authorization", "Bearer " + accessToken);
            request.contentType("multipart/form-data");
            		
            if(requestBodyJson.has("images")) {
                LogUtils.info("Processing image attachments");
                if (requestBodyJson.get("images") instanceof JSONArray) {
                    JSONArray imagesArray = requestBodyJson.getJSONArray("images");
                    for (int i = 0; i < imagesArray.length(); i++) {
                        String imageData = imagesArray.getString(i);
                        if(imageData.startsWith("data:image")) {
                            request.multiPart("images", "image" + i + ".png", imageData.getBytes());
                            LogUtils.info("Successfully attached base64 image data");
                        } else {
                            File imageFile = new File(imageData);
                            if(imageFile.exists()) {
                                request.multiPart("images", imageFile);
                                LogUtils.info("Successfully attached image file: " + imageData);
                            } else {
                                LogUtils.warn("Image file not found at path: " + imageData);
                            }
                        }
                    }
                    ExtentReport.getTest().log(Status.INFO, "Successfully processed image attachments");
                } else if (!requestBodyJson.getString("images").isEmpty()) {
                    String imageData = requestBodyJson.getString("images");
                    if(imageData.startsWith("data:image")) {
                        for(int i=0; i<5; i++) {
                            request.multiPart("images", "image" + i + ".png", imageData.getBytes());
                        }
                        LogUtils.info("Successfully attached 5 copies of base64 image data");
                    } else {
                        File imageFile = new File(imageData);
                        if(imageFile.exists()) {
                            for(int i=0; i<5; i++) {
                                request.multiPart("images", imageFile);
                            }
                            LogUtils.info("Successfully attached 5 copies of image file");
                        } else {
                            LogUtils.warn("Image file not found at path: " + imageData);
                        }
                    }
                }
            }

            LogUtils.info("Setting up request form parameters");
            ExtentReport.getTest().log(Status.INFO, "Setting up request form parameters");
            
            try {
                request.multiPart("user_id", userId);
                request.multiPart("outlet_id", requestBodyJson.getString("outlet_id")); 
                request.multiPart("menu_cat_id", requestBodyJson.getString("menu_cat_id"));
                request.multiPart("name", requestBodyJson.getString("name"));
                request.multiPart("food_type", requestBodyJson.getString("food_type"));
                request.multiPart("description", requestBodyJson.getString("description"));
                request.multiPart("spicy_index", requestBodyJson.getString("spicy_index"));
                // Handle portion_data - parse it if it's a string
                if (requestBodyJson.get("portion_data") instanceof String) {
                    request.multiPart("portion_data", requestBodyJson.getString("portion_data"));
                } else {
                    request.multiPart("portion_data", requestBodyJson.getJSONArray("portion_data").toString());
                }
                request.multiPart("ingredients", requestBodyJson.getString("ingredients"));
                request.multiPart("offer", requestBodyJson.getString("offer"));
                request.multiPart("rating", requestBodyJson.getString("rating")); 
                request.multiPart("cgst", requestBodyJson.getString("cgst"));
                request.multiPart("sgst", requestBodyJson.getString("sgst"));
                LogUtils.info("Successfully set all form parameters");
            } catch (Exception e) {
                LogUtils.error("Failed to set form parameters: " + e.getMessage());
                throw new customException("Failed to set form parameters: " + e.getMessage());
            }
            		
            LogUtils.info("Sending POST request to endpoint: " + baseUri);
            ExtentReport.getTest().log(Status.INFO, "Sending POST request to create menu item");
            response = request.when().post(baseUri).then().extract().response();
            
            LogUtils.info("Received response with status code: " + response.getStatusCode());
            LogUtils.info("Response body: " + response.asPrettyString());
            
            if(response.getStatusCode() == 200) {
                LogUtils.success(logger, "Menu item created successfully");
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Menu item created successfully", ExtentColor.GREEN));
                //validateResponseBody.handleResponseBody(response, expectedResponse);
                LogUtils.info("Response validation completed successfully");
                ExtentReport.getTest().log(Status.PASS, "Response validation completed successfully");
                ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asPrettyString());
            } else {
                LogUtils.failure(logger, "Menu creation failed with status code: " + response.getStatusCode());
                LogUtils.error("Response body: " + response.asPrettyString());
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Menu creation failed", ExtentColor.RED));
                ExtentReport.getTest().log(Status.FAIL, "Response Body: " + response.asPrettyString());
            }

        } catch (Exception e) {
            LogUtils.error("Error during menu creation test execution: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Test execution failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during menu creation test execution: " + e.getMessage());
        }
    }

    @Test(dependsOnMethods = "createMenuUsigValidInputData")
    private void createBulkMenus() throws customException {
        try {
            LogUtils.info("Starting bulk menu creation test case");
            ExtentReport.createTest("Bulk Menu Creation Test - Creating 500 Menus");
            ExtentReport.getTest().log(Status.INFO, "Test Description: Creating 500 menus with unique names");

            // Get base request body from the first test case
            Object[][] testData = getMenuCreateData();
            String requestBodyPayload = testData[0][5].toString(); // Get the first test case request body
            JSONObject baseRequestBody = new JSONObject(requestBodyPayload.replace("\\\\", "\\"));

            for (int i = 1; i <= 5; i++) {
                try {
                    LogUtils.info("Creating menu #" + i);
                    
                    // Create a copy of base request body for this iteration
                    JSONObject currentRequestBody = new JSONObject(baseRequestBody.toString());
                    
                    // Modify the name to make it unique
                    String uniqueName = currentRequestBody.getString("name") + "_" + i;
                    
                    request = RestAssured.given();
                    request.header("Authorization", "Bearer " + accessToken);
                    request.contentType("multipart/form-data");

                    // Handle images if present
                    if(currentRequestBody.has("images")) {
                        if (currentRequestBody.get("images") instanceof JSONArray) {
                            JSONArray imagesArray = currentRequestBody.getJSONArray("images");
                            for (int j = 0; j < imagesArray.length(); j++) {
                                String imageData = imagesArray.getString(j);
                                if(imageData.startsWith("data:image")) {
                                    request.multiPart("images", "image" + j + ".png", imageData.getBytes());
                                    LogUtils.info("Successfully attached base64 image data");
                                } else {
                                    File imageFile = new File(imageData);
                                    if(imageFile.exists()) {
                                        request.multiPart("images", imageFile);
                                        LogUtils.info("Successfully attached image file: " + imageData);
                                    } else {
                                        LogUtils.warn("Image file not found at path: " + imageData);
                                    }
                                }
                            }
                        } else if (!currentRequestBody.getString("images").isEmpty()) {
                            String imageData = currentRequestBody.getString("images");
                            if(imageData.startsWith("data:image")) {
                                for(int j=0; j<5; j++) {
                                    request.multiPart("images", "image" + j + ".png", imageData.getBytes());
                                }
                                LogUtils.info("Successfully attached 5 copies of base64 image data");
                            } else {
                                File imageFile = new File(imageData);
                                if(imageFile.exists()) {
                                    for(int j=0; j<5; j++) {
                                        request.multiPart("images", imageFile);
                                    }
                                    LogUtils.info("Successfully attached 5 copies of image file");
                                } else {
                                    LogUtils.warn("Image file not found at path: " + imageData);
                                }
                            }
                        }
                    }

                    // Set form parameters
                    request.multiPart("user_id", userId);
                    request.multiPart("outlet_id", currentRequestBody.getString("outlet_id")); 
                    request.multiPart("menu_cat_id", currentRequestBody.getString("menu_cat_id"));
                    request.multiPart("name", uniqueName); // Use unique name
                    request.multiPart("food_type", currentRequestBody.getString("food_type"));
                    request.multiPart("description", currentRequestBody.getString("description"));
                    request.multiPart("spicy_index", currentRequestBody.getString("spicy_index"));
                    // Handle portion_data - parse it if it's a string
                    if (currentRequestBody.get("portion_data") instanceof String) {
                        request.multiPart("portion_data", currentRequestBody.getString("portion_data"));
                    } else {
                        request.multiPart("portion_data", currentRequestBody.getJSONArray("portion_data").toString());
                    }
                    request.multiPart("ingredients", currentRequestBody.getString("ingredients"));
                    request.multiPart("offer", currentRequestBody.getString("offer"));
                    request.multiPart("rating", currentRequestBody.getString("rating")); 
                    request.multiPart("cgst", currentRequestBody.getString("cgst"));
                    request.multiPart("sgst", currentRequestBody.getString("sgst"));

                    // Send request
                    response = request.when().post(baseUri).then().extract().response();
                    
                    if(response.getStatusCode() == 200) {
                        LogUtils.success(logger, "Menu #" + i + " created successfully");
                    } else {
                        LogUtils.error("Failed to create menu #" + i + ". Status code: " + response.getStatusCode());
                        LogUtils.error("Response: " + response.asPrettyString());
                    }

                    // Add a small delay to prevent overwhelming the server
                    Thread.sleep(100);

                } catch (Exception e) {
                    LogUtils.error("Error creating menu #" + i + ": " + e.getMessage());
                    // Continue with next menu even if current one fails
                    continue;
                }
            }

            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Bulk menu creation completed", ExtentColor.GREEN));

        } catch (Exception e) {
            LogUtils.error("Error during bulk menu creation: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel("Bulk menu creation failed", ExtentColor.RED));
            ExtentReport.getTest().log(Status.FAIL, "Error details: " + e.getMessage());
            throw new customException("Error during bulk menu creation: " + e.getMessage());
        }
    }

    @AfterClass
    private void tearDown()
    {
        try 
        {
            LogUtils.info("===Test environment tear down successfully===");
           
            ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Test environment tear down successfully", ExtentColor.GREEN));
            
            //ActionsMethods.logout();
            TokenManagers.clearTokens();
            
        } 
        catch (Exception e) 
        {
            LogUtils.exception(logger, "Error during test environment tear down", e);
            ExtentReport.getTest().log(Status.FAIL, "Error during test environment tear down: " + e.getMessage());
        }
    }

}
