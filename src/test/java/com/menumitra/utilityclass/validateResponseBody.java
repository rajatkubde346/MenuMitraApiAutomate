package com.menumitra.utilityclass;

import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import io.restassured.response.Response;
import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for handling HTTP response codes and validating responses
 */
public class validateResponseBody {

    /**
     * Validates the response body and status code
     * 
     * @param actualResponse The actual response from the API
     * @param expectedResponse The expected response to compare against
     * @param statusCode The HTTP status code
     * @throws customException If validation fails
     */
    public static void handleResponseBody(Response actualResponse, JSONObject expectedResponse) throws customException {
        try {
        	JSONObject responseverifyOTP=new JSONObject(actualResponse.asPrettyString());
            int statusCode = actualResponse.getStatusCode();
            String responseBody = actualResponse.getBody().asString();
            String responseStatusLine = actualResponse.getStatusLine();
            long responseTime = actualResponse.getTime();
            
            LogUtils.info("Response Status Code: " + statusCode);
            LogUtils.info("Response Time: " + responseTime + "ms");
            LogUtils.info("Response Status Line: " + responseStatusLine);
            LogUtils.info("Response Body: " + responseBody);

            JSONObject actualResponseJson = new JSONObject(responseBody);

            switch (statusCode) {
                case 200:
                	if(responseverifyOTP.has("access_token"))
                	{
                	String accessTokens = responseverifyOTP.getString("access_token");
                	String accessTokenExpiredDate = expectedResponse.getString("expires_at");
                	expectedResponse.put("access_token", accessTokens);
                	expectedResponse.put("expires_at", accessTokenExpiredDate);
                    validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                	}
                    else    
                    {
                        validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    }
                    break;
                    
                case 201:
                    validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString()); 
                    break;
                    
                case 202:
                    validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 203:
                    validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 204:
                    // No content to validate
                    LogUtils.info("Received 204 No Content response");
                    break;
                    
                case 400:
                    validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 401:
                    validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 403:
                	validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 404:
                	validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 405:
                	validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 408:
                	validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                case 409:
                    validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                break;    
                case 429:
                	validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 500:
                	validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 502:
                	validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                case 503:
                	validateSuccessResponse(actualResponseJson.toString(), expectedResponse.toString());
                    break;
                    
                default:
                    LogUtils.error("Unhandled status code: " + statusCode);
                    throw new customException("Unhandled status code: " + statusCode);
            }

            // Validate response time
            if (responseTime > 5000) { // 5 seconds threshold
            	ExtentReport.getTest().log(Status.WARNING,"Response Time exceeded threshold: "+responseTime+"ms");
                LogUtils.warn("Response time exceeded threshold: " + responseTime + "ms");
            }

        } catch (Exception e) {
            LogUtils.error("Error validating response: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Error validating response: " + e.getMessage());
            throw new customException("Error validating response: " + e.getMessage());
        }
    }

    /**
     * Validates successful response
     */
    private static void validateSuccessResponse(String actualResponse, String expectedResponse) throws customException 
    {
        try
        {
            ObjectMapper objectmapper = new ObjectMapper();
            Map<String, Object> actualMap = objectmapper.readValue(actualResponse, Map.class);
            Map<String, Object> expectedMap = objectmapper.readValue(expectedResponse, Map.class);
             
            // Check if all expected fields are present in actual response
            for (Map.Entry<String, Object> entry : expectedMap.entrySet()) {
                String key = entry.getKey();
                Object expectedValue = entry.getValue();
                
                if (!actualMap.containsKey(key)) {
                    LogUtils.error("Missing field in actual response: " + key);
                    ExtentReport.getTest().log(Status.FAIL, "Missing field in actual response: " + key);
                    throw new customException("Missing field in actual response: " + key);
                }
                
                Object actualValue = actualMap.get(key);
                if (!compareValues(actualValue, expectedValue)) {
                    LogUtils.error("Value mismatch for field '" + key + "'. Expected: " + expectedValue + ", Actual: " + actualValue);
                    ExtentReport.getTest().log(Status.FAIL, "Value mismatch for field '" + key + "'. Expected: " + expectedValue + ", Actual: " + actualValue);
                    throw new customException("Value mismatch for field '" + key + "'");
                }
            }

            LogUtils.info("Success response matched as expected");
            ExtentReport.getTest().log(Status.PASS, "Success response matched as expected");
            ExtentReport.getTest().log(Status.PASS, "Expected response: " + expectedResponse);
            ExtentReport.getTest().log(Status.PASS, "Actual response: " + actualResponse);
        }
        catch (AssertionError e) 
        {
            LogUtils.error("Success response validation failed: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Mismatch in success response:\nExpected response: " + expectedResponse + "\nActual response: " + actualResponse);
            throw new customException("Success response validation failed: " + e.getMessage());
        }
        catch (Exception e) 
        {
            LogUtils.error("Error processing success response: " + e.getMessage());
            ExtentReport.getTest().log(Status.FAIL, "Exception while processing success response: " + e.getMessage());
            throw new customException("Error processing success response: " + e.getMessage());
        }
    }

    /**
     * Helper method to compare values, handling different types and null values
     */
    private static boolean compareValues(Object actual, Object expected) {
        if (actual == null && expected == null) {
            return true;
        }
        if (actual == null || expected == null) {
            return false;
        }
        
        // Convert numbers to same type for comparison
        if (actual instanceof Number && expected instanceof Number) {
            double actualDouble = ((Number) actual).doubleValue();
            double expectedDouble = ((Number) expected).doubleValue();
            return Math.abs(actualDouble - expectedDouble) < 0.0001; // Small epsilon for floating point comparison
        }
        
        // Convert to strings for case-insensitive comparison of string values
        if (actual instanceof String && expected instanceof String) {
            return ((String) actual).equalsIgnoreCase((String) expected);
        }
        
        return actual.equals(expected);
    }

    /**
     * Validates error response
     */
    private static void validateErrorResponse(JSONObject actualResponse, JSONObject expectedResponse, String errorType) 
            throws customException {
        try {
            // Validate error status
            Assert.assertEquals(actualResponse.getString("st"), expectedResponse.getString("st"),
                "Status mismatch in " + errorType + " response");
            
            // Validate error message
            Assert.assertEquals(actualResponse.getString("msg"), expectedResponse.getString("msg"),
                "Message mismatch in " + errorType + " response");
            
            // Validate error details if present
            if (expectedResponse.has("error")) {
                Assert.assertTrue(actualResponse.has("error"), "Error details missing in response");
                // Add more specific error validation as needed
            }

            LogUtils.info(errorType + " response validated successfully");
            ExtentReport.getTest().log(Status.PASS, errorType + " response validated successfully");
            
        } catch (AssertionError e) {
            LogUtils.error(errorType + " response validation failed: " + e.getMessage());
            throw new customException(errorType + " response validation failed: " + e.getMessage());
        }
    }
}
