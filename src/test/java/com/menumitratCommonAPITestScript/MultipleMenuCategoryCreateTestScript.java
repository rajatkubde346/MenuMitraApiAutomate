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
import com.menumitra.apiRequest.MenuCategoryRequest;
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
public class MultipleMenuCategoryCreateTestScript extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private MenuCategoryRequest menuCategoryRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(MultipleMenuCategoryCreateTestScript.class);

    @BeforeClass
    private void menuCategoryMultipleSetUp() throws customException {
        try {
            LogUtils.info("Menu Category Multiple SetUp");
            ExtentReport.createTest("Menu Category Multiple SetUp");
            ExtentReport.getTest().log(Status.INFO, "Menu Category Multiple SetUp");

            ActionsMethods.login();
            ActionsMethods.verifyOTP();
            baseURI = EnviromentChanges.getBaseUrl();

            Object[][] getUrl = getMenuCategoryMultipleUrl();
            if (getUrl.length > 0) {
                String endpoint = getUrl[0][2].toString();
                url = new URL(endpoint);
                baseURI = RequestValidator.buildUri(endpoint, baseURI);
                LogUtils.info("Constructed base URI: " + baseURI);
                ExtentReport.getTest().log(Status.INFO, "Constructed base URI: " + baseURI);
            } else {
                LogUtils.failure(logger, "No menu category multiple URL found in test data");
                ExtentReport.getTest().log(Status.FAIL, "No menu category multiple URL found in test data");
                throw new customException("No menu category multiple URL found in test data");
            }

            accessToken = TokenManagers.getJwtToken();
            user_id = TokenManagers.getUserId();
            if (accessToken.isEmpty()) {
                LogUtils.failure(logger, "Failed to get access token");
                ExtentReport.getTest().log(Status.FAIL, "Failed to get access token");
                throw new customException("Failed to get access token");
            }

            LogUtils.info("Menu Category Multiple SetUp completed successfully");
            ExtentReport.getTest().log(Status.PASS, "Menu Category Multiple SetUp completed successfully");
        } catch (Exception e) {
            String errorMsg = "Error in menu category multiple setup: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }

    private Object[][] getMenuCategoryMultipleUrl() throws customException {
        try {
            LogUtils.info("Reading Menu Category Multiple API endpoint data");
            ExtentReport.getTest().log(Status.INFO, "Reading Menu Category Multiple API endpoint data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "commonAPI");

            if (readExcelData == null || readExcelData.length == 0) {
                String errorMsg = "No Menu Category Multiple API endpoint data found in Excel sheet";
                LogUtils.error(errorMsg);
                ExtentReport.getTest().log(Status.FAIL, errorMsg);
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> "menucategorymultiple".equalsIgnoreCase(row[0].toString()))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No menu category multiple URL data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting menu category multiple URL: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting menu category multiple URL: " + e.getMessage());
            throw new customException("Error in getting menu category multiple URL: " + e.getMessage());
        }
    }

    @DataProvider(name = "getMenuCategoryMultipleData")
    public Object[][] getMenuCategoryMultipleData() throws customException {
        try {
            LogUtils.info("Reading menu category multiple test scenario data");
            ExtentReport.getTest().log(Status.INFO, "Reading menu category multiple test scenario data");

            Object[][] readExcelData = DataDriven.readExcelData(excelSheetPathForGetApis, "CommonAPITestScenario");
            if (readExcelData == null) {
                String errorMsg = "Error fetching data from Excel sheet - Data is null";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            Object[][] filteredData = Arrays.stream(readExcelData)
                    .filter(row -> row != null && row.length >= 3 &&
                            "menucategorymultiple".equalsIgnoreCase(Objects.toString(row[0], "")) &&
                            "positive".equalsIgnoreCase(Objects.toString(row[2], "")))
                    .toArray(Object[][]::new);

            if (filteredData.length == 0) {
                String errorMsg = "No valid menu category multiple test data found after filtering";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }

            return filteredData;
        } catch (Exception e) {
            LogUtils.failure(logger, "Error in getting menu category multiple test data: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error in getting menu category multiple test data: " + e.getMessage());
            throw new customException("Error in getting menu category multiple test data: " + e.getMessage());
        }
    }

    @Test(dataProvider = "getMenuCategoryMultipleData")
    public void createMultipleMenuCategoriesTest(String apiName, String testCaseid, String testType, String description,
            String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
        try {
            LogUtils.info("Starting creation of multiple menu categories");
            ExtentReport.createTest("Create Multiple Menu Categories Test - " + testCaseid);
            ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

            if (apiName.equalsIgnoreCase("menucategorymultiple")) {
                requestBodyJson = new JSONObject(requestBody);
                int successCount = 0;
                int totalCategories = 20;  // Define total categories to create

                for (int i = 1; i <= totalCategories; i++) {
                    LogUtils.info("Creating menu category #" + i);
                    ExtentReport.getTest().log(Status.INFO, "Creating menu category #" + i);

                    // Create new request for each category
                    int outletId = requestBodyJson.getInt("outlet_id");
                    String baseCategoryName = requestBodyJson.getString("category_name");
                    String categoryName = baseCategoryName + " " + i;  // Make category name unique
                    
                    menuCategoryRequest = new MenuCategoryRequest(outletId, categoryName, String.valueOf(user_id));
                    
                    // Set optional fields if provided in Excel data
                    if (requestBodyJson.has("image_path")) {
                        menuCategoryRequest.withImage(requestBodyJson.getString("image_path"));
                    }
                    if (requestBodyJson.has("app_source")) {
                        menuCategoryRequest.setAppSource(requestBodyJson.getString("app_source"));
                    }

                    // Create JSON for logging
                    JSONObject currentRequestJson = new JSONObject();
                    currentRequestJson.put("outlet_id", outletId);
                    currentRequestJson.put("category_name", categoryName);
                    currentRequestJson.put("user_id", String.valueOf(user_id));
                    if (menuCategoryRequest.getImage() != null) {
                        currentRequestJson.put("image", menuCategoryRequest.getImage().getPath());
                    }
                    if (menuCategoryRequest.getAppSource() != null) {
                        currentRequestJson.put("app_source", menuCategoryRequest.getAppSource());
                    }

                    LogUtils.info("Request body for category #" + i + ": " + currentRequestJson.toString());
                    ExtentReport.getTest().log(Status.INFO, "Request body for category #" + i + ": " + currentRequestJson.toString());

                    try {
                        response = ResponseUtil.getResponseWithAuth(baseURI, menuCategoryRequest, httpsmethod, accessToken);
                        int responseCode = response.getStatusCode();

                        if (responseCode == 200 || responseCode == 201) {
                            successCount++;
                            LogUtils.info("Successfully created menu category #" + i);
                            ExtentReport.getTest().log(Status.PASS, "Successfully created menu category #" + i);
                        } else {
                            String errorMsg = "Failed to create menu category #" + i + " - Status code: " + responseCode + ", Response: " + response.getBody().asString();
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, errorMsg);
                        }
                    } catch (Exception e) {
                        String errorMsg = "Error creating menu category #" + i + ": " + e.getMessage();
                        LogUtils.failure(logger, errorMsg);
                        ExtentReport.getTest().log(Status.FAIL, errorMsg);
                    }
                }

                // Log final results
                String resultMsg = String.format("Created %d out of %d menu categories successfully", successCount, totalCategories);
                if (successCount < totalCategories) {
                    resultMsg += " - Some categories failed to create";
                    LogUtils.failure(logger, resultMsg);
                    ExtentReport.getTest().log(Status.FAIL, resultMsg);
                    throw new customException(resultMsg);
                } else {
                    LogUtils.info(resultMsg);
                    ExtentReport.getTest().log(Status.PASS, resultMsg);
                }
            }
        } catch (Exception e) {
            String errorMsg = "Error in menu category multiple test: " + e.getMessage();
            LogUtils.exception(logger, errorMsg, e);
            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
            throw new customException(errorMsg);
        }
    }
}
