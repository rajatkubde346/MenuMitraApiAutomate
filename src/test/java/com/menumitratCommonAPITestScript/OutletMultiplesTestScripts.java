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
public class OutletMultiplesTestScripts extends APIBase {
    private JSONObject requestBodyJson;
    private Response response;
    private String baseURI;
    private String accessToken;
    private OuletMultipleRequest outletMultipleRequest;
    private URL url;
    private JSONObject actualJsonBody;
    private int user_id;
    Logger logger = LogUtils.getLogger(OutletMultiplesTestScripts.class);

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

    // @Test(dataProvider = "getOutletMultipleData")
    // public void outletMultipleTest(String apiName, String testCaseid, String testType, String description,
    //         String httpsmethod, String requestBody, String expectedResponseBody, String statusCode) throws customException {
    //     try {
    //         LogUtils.info("Starting outlet multiple test case: " + testCaseid);
    //         ExtentReport.createTest("Outlet Multiple Test - " + testCaseid);
    //         ExtentReport.getTest().log(Status.INFO, "Test Description: " + description);

    //         if (apiName.equalsIgnoreCase("outletmultiple")) {
    //             requestBodyJson = new JSONObject(requestBody);

    //             // Set owner_ids
    //             int[] ownerIds = new int[requestBodyJson.getJSONArray("owner_ids").length()];
    //             for (int i = 0; i < ownerIds.length; i++) {
    //                 ownerIds[i] = requestBodyJson.getJSONArray("owner_ids").getInt(i);
    //             }
    //             outletMultipleRequest.setOwner_ids(ownerIds);

    //             // Set other fields
    //             outletMultipleRequest.setUser_id(String.valueOf(user_id));
    //             outletMultipleRequest.setName(requestBodyJson.getString("name"));
    //             outletMultipleRequest.setMobile(requestBodyJson.getString("mobile"));
    //             outletMultipleRequest.setAddress(requestBodyJson.getString("address"));
    //             outletMultipleRequest.setOutlet_type(requestBodyJson.getString("outlet_type"));
    //             outletMultipleRequest.setOutlet_mode(requestBodyJson.getString("outlet_mode"));
    //             outletMultipleRequest.setVeg_nonveg(requestBodyJson.getString("veg_nonveg"));
    //             outletMultipleRequest.setApp_type(requestBodyJson.getString("app_type"));
    //             outletMultipleRequest.setUpi_id(requestBodyJson.getString("upi_id"));

    //             LogUtils.info("Request Body: " + requestBodyJson.toString());
    //             ExtentReport.getTest().log(Status.INFO, "Request Body: " + requestBodyJson.toString());

    //             response = ResponseUtil.getResponseWithAuth(baseURI, outletMultipleRequest, httpsmethod, accessToken);

    //             LogUtils.info("Response Status Code: " + response.getStatusCode());
    //             LogUtils.info("Response Body: " + response.asString());
    //             ExtentReport.getTest().log(Status.INFO, "Response Status Code: " + response.getStatusCode());
    //             ExtentReport.getTest().log(Status.INFO, "Response Body: " + response.asString());

    //             // Validate status code - accept both 200 and 201
    //             int actualStatusCode = response.getStatusCode();
    //             if (actualStatusCode != 200 && actualStatusCode != 201) {
    //                 String errorMsg = "Status code mismatch - Expected: 200 or 201, Actual: " + actualStatusCode;
    //                 LogUtils.failure(logger, errorMsg);
    //                 ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
    //                 throw new customException(errorMsg);
    //             }

    //             // Only show response without validation
    //             actualJsonBody = new JSONObject(response.asString());
    //             LogUtils.info("Outlet multiple response received successfully");
    //             ExtentReport.getTest().log(Status.PASS, "Outlet multiple response received successfully");
    //             ExtentReport.getTest().log(Status.PASS, "Response: " + response.asPrettyString());

    //             LogUtils.success(logger, "Outlet multiple test completed successfully");
    //             ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel("Outlet multiple test completed successfully", ExtentColor.GREEN));
    //         }
    //     } catch (Exception e) {
    //         String errorMsg = "Error in outlet multiple test: " + e.getMessage();
    //         LogUtils.exception(logger, errorMsg, e);
    //         ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
    //         if (response != null) {
    //             ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
    //             ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
    //         }
    //         throw new customException(errorMsg);
    //     }
    // }

    // @Test
    // public void createMultipleOutletsTest() throws customException {
    //     try {
    //         LogUtils.info("Starting creation of 20 outlets");
    //         ExtentReport.createTest("Create Multiple Outlets Test");
    //         ExtentReport.getTest().log(Status.INFO, "Starting creation of 20 outlets");

    //         int successCount = 0;
    //         for (int i = 1; i <= 5; i++) {
    //             LogUtils.info("Creating outlet #" + i);
    //             ExtentReport.getTest().log(Status.INFO, "Creating outlet #" + i);

    //             // Create request body for each outlet
    //             outletMultipleRequest = new OuletMultipleRequest();
                
    //             // Set owner_ids (using a single owner ID for example)
    //             int[] ownerIds = {user_id};  // You can modify this array as needed
    //             outletMultipleRequest.setOwner_ids(ownerIds);

    //             // Set other fields with dynamic values
    //             String uniqueMobile = "98765432" + String.format("%02d", i);
    //             String outletName = "Test Outlet " + i;
                
    //             outletMultipleRequest.setUser_id(String.valueOf(user_id));
    //             outletMultipleRequest.setName(outletName);
    //             outletMultipleRequest.setMobile(uniqueMobile);
    //             outletMultipleRequest.setAddress("Test Address " + i);
    //             outletMultipleRequest.setOutlet_type("restaurant");
    //             outletMultipleRequest.setOutlet_mode("online");
    //             outletMultipleRequest.setVeg_nonveg("veg");
    //             outletMultipleRequest.setApp_type("pos");
    //             outletMultipleRequest.setUpi_id("test.upi" + i + "@ybl");

    //             // Convert request to JSON for logging
    //             JSONObject requestJson = new JSONObject();
    //             requestJson.put("owner_ids", ownerIds);
    //             requestJson.put("user_id", String.valueOf(user_id));
    //             requestJson.put("name", outletName);
    //             requestJson.put("mobile", uniqueMobile);
    //             requestJson.put("address", "Test Address " + i);
    //             requestJson.put("outlet_type", "restaurant");
    //             requestJson.put("outlet_mode", "online");
    //             requestJson.put("veg_nonveg", "veg");
    //             requestJson.put("app_type", "pos");
    //             requestJson.put("upi_id", "test.upi" + i + "@ybl");

    //             LogUtils.info("Request Body for outlet #" + i + ": " + requestJson.toString());
    //             ExtentReport.getTest().log(Status.INFO, "Request Body for outlet #" + i + ": " + requestJson.toString());

    //             // Send request
    //             response = ResponseUtil.getResponseWithAuth(baseURI, outletMultipleRequest, "POST", accessToken);

    //             // Log detailed response information
    //             int statusCode = response.getStatusCode();
    //             String responseBody = response.asString();
                
    //             LogUtils.info("Response Status Code for outlet #" + i + ": " + statusCode);
    //             LogUtils.info("Response Body for outlet #" + i + ": " + responseBody);
    //             ExtentReport.getTest().log(Status.INFO, "Response Status Code for outlet #" + i + ": " + statusCode);
    //             ExtentReport.getTest().log(Status.INFO, "Response Body for outlet #" + i + ": " + responseBody);

    //             // Validate status code - accept both 200 and 201
    //             if (statusCode == 200 || statusCode == 201) {
    //                 successCount++;
    //                 LogUtils.success(logger, "Successfully created outlet #" + i + " - " + outletName);
    //                 ExtentReport.getTest().log(Status.PASS, "Successfully created outlet #" + i + " - " + outletName);
    //             } else {
    //                 String errorMsg = "Failed to create outlet #" + i + " - Status code: " + statusCode + ", Response: " + responseBody;
    //                 LogUtils.failure(logger, errorMsg);
    //                 ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
    //             }

    //             // Add a small delay between requests to prevent rate limiting
    //             Thread.sleep(2000);
    //         }

    //         // Final summary
    //         String summaryMsg = "Created " + successCount + " out of 20 outlets successfully";
    //         if (successCount == 20) {
    //             LogUtils.success(logger, summaryMsg);
    //             ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(summaryMsg, ExtentColor.GREEN));
    //         } else {
    //             String errorMsg = summaryMsg + " - Some outlets failed to create";
    //             LogUtils.failure(logger, errorMsg);
    //             ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
    //             throw new customException(errorMsg);
    //         }
    //     } catch (Exception e) {
    //         String errorMsg = "Error in creating multiple outlets: " + e.getMessage();
    //         LogUtils.exception(logger, errorMsg, e);
    //         ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
    //         if (response != null) {
    //             ExtentReport.getTest().log(Status.FAIL, "Failed Response Status Code: " + response.getStatusCode());
    //             ExtentReport.getTest().log(Status.FAIL, "Failed Response Body: " + response.asString());
    //         }
    //         throw new customException(errorMsg);
    //     }
    // }

    @Test
    public void createFiveHundredOutletsTest() throws customException {
        try {
            LogUtils.info("Starting creation of 500 outlets");
            ExtentReport.createTest("Create 500 Outlets Test");
            ExtentReport.getTest().log(Status.INFO, "Starting creation of 500 outlets");

            // Arrays for generating unique names
            final String[] VEG_PREFIXES = {
                "Greenleaf", "Fresh Harvest", "Garden Fresh", "Green Bites", "Veggie Delight",
                "Nature's Table", "Green Earth", "Pure Veg", "Healthy Bites", "Veg Paradise",
                "Organic Bites", "Green Valley", "Fresh Fields", "Veggie World", "Plant Power"
            };
            
            final String[] NONVEG_PREFIXES = {
                "Grill House", "Meat & More", "BBQ Junction", "Flame Kitchen", "Smokey's",
                "Meat Master", "Carnivore's", "Prime Cuts", "Gourmet Grill", "Sizzlers",
                "Barbecue Bay", "Meat Point", "Roast House", "Grilled Glory", "Flame Master"
            };
            
            final String[] CAFE_PREFIXES = {
                "Cafe Aroma", "Coffee Culture", "Bean Scene", "Brew House", "Coffee Corner",
                "Cafe Bliss", "Urban Brew", "Coffee Tales", "Cafe Connect", "Espresso Edge",
                "Brew Bar", "Coffee Canvas", "Cafe Craft", "Bean Buzz", "Roasted Routes"
            };
            
            final String[] FASTFOOD_PREFIXES = {
                "Quick Bites", "Fast Feast", "Express Eats", "Snack Shack", "Food Express",
                "Quick Serve", "Fast Lane", "Speedy Bites", "Quick Stop", "Flash Foods",
                "Rapid Bites", "Swift Serve", "Quick Feast", "Express Dine", "Fast Track"
            };
            
            final String[] FINEDINING_PREFIXES = {
                "Royal Plate", "Elite Dining", "Gourmet Gallery", "Fine Feast", "Luxury Bites",
                "Premium Plate", "Elegant Eats", "Dining Royale", "Culinary Crown", "Grand Gourmet",
                "Imperial Dining", "Regal Bites", "Luxe Dining", "Crown Kitchen", "Elite Eats"
            };
            
            final String[] LOCATION_SUFFIXES = {
                "Plaza", "Square", "Corner", "Junction", "Hub",
                "Street", "Avenue", "Point", "Place", "Center",
                "Circle", "Court", "Heights", "Gardens", "Park"
            };

            // Cities and their coordinates
            final String[][] CITIES = {
                {"Bangalore", "12.9716", "77.5946", "560001", "Karnataka"},
                {"Mumbai", "19.0760", "72.8777", "400001", "Maharashtra"},
                {"Delhi", "28.6139", "77.2090", "110001", "Delhi"},
                {"Chennai", "13.0827", "80.2707", "600001", "Tamil Nadu"},
                {"Hyderabad", "17.3850", "78.4867", "500001", "Telangana"},
                {"Pune", "18.5204", "73.8567", "411001", "Maharashtra"},
                {"Kolkata", "22.5726", "88.3639", "700001", "West Bengal"},
                {"Ahmedabad", "23.0225", "72.5714", "380001", "Gujarat"},
                {"Jaipur", "26.9124", "75.7873", "302001", "Rajasthan"},
                {"Lucknow", "26.8467", "80.9462", "226001", "Uttar Pradesh"}
            };

            final String[] AREAS = {
                "Central Business District", "Tech Park", "Old City", "Lake View",
                "Hill Side", "Market Area", "Garden City", "Metro Station",
                "Beach Road", "Mall Road", "Main Street", "Commercial Hub",
                "Heritage Zone", "Business Center", "Shopping District"
            };

            final String[] LANDMARKS = {
                "Near City Mall", "Opposite Central Park", "Next to Metro Station",
                "Behind Town Hall", "Near Lake View", "Besides Shopping Complex",
                "Near Business Hub", "Opposite Hospital", "Next to School",
                "Near Temple", "Besides Market", "Near Stadium"
            };

            int successCount = 0;
            int retryCount = 0;
            final int MAX_RETRIES = 3;
            
            final int[] VALID_FIRST_DIGITS = {6, 7, 8, 9};
            int currentFirstDigitIndex = 0;
            
            long baseNumber = (System.currentTimeMillis() % 90000) + 10000;
            
            for (int i = 1; i <= 1; i++) {
                boolean success = false;
                retryCount = 0;
                
                while (!success && retryCount < MAX_RETRIES) {
                    try {
                        LogUtils.info("Creating outlet #" + i + (retryCount > 0 ? " (Retry " + retryCount + ")" : ""));
                        ExtentReport.getTest().log(Status.INFO, "Creating outlet #" + i + (retryCount > 0 ? " (Retry " + retryCount + ")" : ""));

                        outletMultipleRequest = new OuletMultipleRequest();
                        
                        int[] ownerIds = {user_id};
                        outletMultipleRequest.setOwner_ids(ownerIds);

                        String uniqueMobile = String.format("%d%05d%04d", 
                            VALID_FIRST_DIGITS[currentFirstDigitIndex],
                            baseNumber % 100000, 
                            i);
                        
                        if (uniqueMobile.length() != 10) {
                            throw new customException("Generated mobile number " + uniqueMobile + " is not 10 digits");
                        }
                        
                        int firstDigit = Character.getNumericValue(uniqueMobile.charAt(0));
                        if (firstDigit < 6 || firstDigit > 9) {
                            throw new customException("Generated mobile number " + uniqueMobile + " does not start with 6,7,8,or 9");
                        }
                        
                        outletMultipleRequest.setUser_id(String.valueOf(user_id));
                        outletMultipleRequest.setMobile(uniqueMobile);

                        // Select random city
                        String[] cityInfo = CITIES[i % CITIES.length];
                        String cityName = cityInfo[0];
                        String latitude = cityInfo[1];
                        String longitude = cityInfo[2];
                        String pincode = cityInfo[3];
                        String state = cityInfo[4];

                        // Generate unique name based on outlet type
                        String outletName;
                        String area = AREAS[i % AREAS.length];
                        String landmark = LANDMARKS[i % LANDMARKS.length];
                        String locationSuffix = LOCATION_SUFFIXES[i % LOCATION_SUFFIXES.length];
                        
                        if (i % 5 == 0) {
                            String prefix = VEG_PREFIXES[i % VEG_PREFIXES.length];
                            outletName = prefix + " " + locationSuffix;
                            outletMultipleRequest.setName(outletName);
                            outletMultipleRequest.setOutlet_type("hotel");
                            outletMultipleRequest.setVeg_nonveg("veg");
                        } else if (i % 5 == 1) {
                            String prefix = NONVEG_PREFIXES[i % NONVEG_PREFIXES.length];
                            outletName = prefix + " " + locationSuffix;
                            outletMultipleRequest.setName(outletName);
                            outletMultipleRequest.setOutlet_type("hotel");
                            outletMultipleRequest.setVeg_nonveg("nonveg");
                        } else if (i % 5 == 2) {
                            String prefix = CAFE_PREFIXES[i % CAFE_PREFIXES.length];
                            outletName = prefix + " " + locationSuffix;
                            outletMultipleRequest.setName(outletName);
                            outletMultipleRequest.setOutlet_type("cafe");
                            outletMultipleRequest.setVeg_nonveg(i % 2 == 0 ? "veg" : "nonveg");
                        } else if (i % 5 == 3) {
                            String prefix = FASTFOOD_PREFIXES[i % FASTFOOD_PREFIXES.length];
                            outletName = prefix + " " + locationSuffix;
                            outletMultipleRequest.setName(outletName);
                            outletMultipleRequest.setOutlet_type("fastfood");
                            outletMultipleRequest.setVeg_nonveg(i % 2 == 0 ? "veg" : "nonveg");
                        } else {
                            String prefix = FINEDINING_PREFIXES[i % FINEDINING_PREFIXES.length];
                            outletName = prefix + " " + locationSuffix;
                            outletMultipleRequest.setName(outletName);
                            outletMultipleRequest.setOutlet_type("finedining");
                            outletMultipleRequest.setVeg_nonveg(i % 2 == 0 ? "veg" : "nonveg");
                        }

                        // Create detailed address
                        String address = String.format("%s, %s, %s, %s", outletName, area, landmark, cityName);
                        outletMultipleRequest.setAddress(address);

                        // Common settings for all outlets
                        outletMultipleRequest.setOutlet_mode("online");
                        outletMultipleRequest.setApp_type("pos");
                        
                        // Format UPI ID without special characters
                        String upiId = outletName.toLowerCase()
                            .replaceAll("[^a-z0-9]", "")
                            .substring(0, Math.min(8, outletName.length())) + "@ybl";
                        outletMultipleRequest.setUpi_id(upiId);

                        // Additional required fields
                        outletMultipleRequest.setEmail(upiId.replace("@ybl", "@example.com"));
                        outletMultipleRequest.setGst_no("29ABCDE" + String.format("%04d", i) + "1Z" + (char)('A' + (i % 26)));
                        outletMultipleRequest.setPincode(pincode);
                        outletMultipleRequest.setState(state);
                        outletMultipleRequest.setCity(cityName);
                        outletMultipleRequest.setCountry("India");
                        outletMultipleRequest.setLatitude(latitude);
                        outletMultipleRequest.setLongitude(longitude);

                        // Convert request to JSON for logging
                        JSONObject requestJson = new JSONObject();
                        requestJson.put("owner_ids", ownerIds);
                        requestJson.put("user_id", String.valueOf(user_id));
                        requestJson.put("name", outletMultipleRequest.getName());
                        requestJson.put("mobile", outletMultipleRequest.getMobile());
                        requestJson.put("address", outletMultipleRequest.getAddress());
                        requestJson.put("outlet_type", outletMultipleRequest.getOutlet_type());
                        requestJson.put("outlet_mode", outletMultipleRequest.getOutlet_mode());
                        requestJson.put("veg_nonveg", outletMultipleRequest.getVeg_nonveg());
                        requestJson.put("app_type", outletMultipleRequest.getApp_type());
                        requestJson.put("upi_id", outletMultipleRequest.getUpi_id());

                        LogUtils.info("Request Body for outlet #" + i + ": " + requestJson.toString());
                        ExtentReport.getTest().log(Status.INFO, "Request Body for outlet #" + i + ": " + requestJson.toString());

                        // Send request
                        response = ResponseUtil.getResponseWithAuth(baseURI, outletMultipleRequest, "POST", accessToken);

                        // Log response information
                        int statusCode = response.getStatusCode();
                        String responseBody = response.asString();
                        
                        LogUtils.info("Response Status Code for outlet #" + i + ": " + statusCode);
                        LogUtils.info("Response Body for outlet #" + i + ": " + responseBody);
                        ExtentReport.getTest().log(Status.INFO, "Response Status Code for outlet #" + i + ": " + statusCode);
                        ExtentReport.getTest().log(Status.INFO, "Response Body for outlet #" + i + ": " + responseBody);

                        // Validate status code
                        if (statusCode == 200 || statusCode == 201) {
                            successCount++;
                            success = true;
                            LogUtils.success(logger, "Successfully created outlet #" + i + " - " + outletMultipleRequest.getName());
                            ExtentReport.getTest().log(Status.PASS, "Successfully created outlet #" + i + " - " + outletMultipleRequest.getName());
                        } else {
                            String errorMsg = "Failed to create outlet #" + i + " - Status code: " + statusCode + ", Response: " + responseBody;
                            LogUtils.failure(logger, errorMsg);
                            ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                            
                            // Handle different error cases
                            if (responseBody.contains("Mobile must be 10 digits")) {
                                throw new customException("Invalid mobile number format: " + uniqueMobile);
                            } else if (responseBody.contains("must start with digits 6, 7, 8, or 9")) {
                                // Try next valid first digit
                                currentFirstDigitIndex = (currentFirstDigitIndex + 1) % VALID_FIRST_DIGITS.length;
                                retryCount++;
                                LogUtils.info("Retrying with new first digit: " + VALID_FIRST_DIGITS[currentFirstDigitIndex]);
                                continue;
                            } else if (responseBody.contains("Mobile number already exists")) {
                                // Try next base number with same first digit
                                baseNumber = (baseNumber + 1) % 100000;
                                if (baseNumber < 10000) baseNumber += 10000;
                                retryCount++;
                                LogUtils.info("Retrying with new base number...");
                                continue;
                            }
                        }
                    } catch (Exception e) {
                        LogUtils.error("Error in attempt " + (retryCount + 1) + " for outlet #" + i + ": " + e.getMessage());
                        retryCount++;
                        if (retryCount >= MAX_RETRIES) {
                            throw e;
                        }
                        // Try next combination
                        currentFirstDigitIndex = (currentFirstDigitIndex + 1) % VALID_FIRST_DIGITS.length;
                        if (currentFirstDigitIndex == 0) {
                            // If we've tried all first digits, increment base number
                            baseNumber = (baseNumber + 1) % 100000;
                            if (baseNumber < 10000) baseNumber += 10000;
                        }
                        continue;
                    }
                    
                    // Add delay between requests to prevent rate limiting
                    Thread.sleep(1000);
                }
                
                // If all retries failed for this outlet
                if (!success && retryCount >= MAX_RETRIES) {
                    String errorMsg = "Failed to create outlet #" + i + " after " + MAX_RETRIES + " retries";
                    LogUtils.failure(logger, errorMsg);
                    ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                }

                // Log progress every 50 outlets
                if (i % 50 == 0) {
                    String progressMsg = String.format("Progress: Created %d out of 500 outlets. Success rate: %.2f%%", 
                        i, (successCount * 100.0 / i));
                    LogUtils.info(progressMsg);
                    ExtentReport.getTest().log(Status.INFO, progressMsg);
                }
            }

            // Final summary
            String summaryMsg = String.format("Created %d out of 500 outlets successfully (%.2f%% success rate)", 
                successCount, (successCount * 100.0 / 500));
            if (successCount > 0) {
                LogUtils.success(logger, summaryMsg);
                ExtentReport.getTest().log(Status.PASS, MarkupHelper.createLabel(summaryMsg, ExtentColor.GREEN));
            } else {
                String errorMsg = summaryMsg + " - All outlets failed to create";
                LogUtils.failure(logger, errorMsg);
                ExtentReport.getTest().log(Status.FAIL, MarkupHelper.createLabel(errorMsg, ExtentColor.RED));
                throw new customException(errorMsg);
            }
        } catch (Exception e) {
            String errorMsg = "Error in creating 500 outlets: " + e.getMessage();
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
