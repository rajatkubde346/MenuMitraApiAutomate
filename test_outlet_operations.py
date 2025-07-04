import pytest
import json
import requests
import logging
import pandas as pd
from typing import List, Dict
from pathlib import Path

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class OutletOperations:
    def __init__(self):
        self.base_url = None
        self.access_token = None
        self.user_id = None
        self.session = requests.Session()
    
    def setup(self):
        """Initialize the test setup with authentication and configuration"""
        try:
            # Load configuration from config file
            config_path = Path("src/resources/configFile/config.properties")
            # TODO: Implement config loading
            self.base_url = "YOUR_BASE_URL"  # Replace with actual base URL
            
            # Perform login and get access token
            self.login()
            self.verify_otp()
            logger.info("Setup completed successfully")
            
        except Exception as e:
            logger.error(f"Setup failed: {str(e)}")
            raise
    
    def login(self):
        """Perform login and get access token"""
        try:
            # TODO: Implement actual login logic
            login_endpoint = f"{self.base_url}/login"
            login_data = {
                "username": "your_username",
                "password": "your_password"
            }
            response = self.session.post(login_endpoint, json=login_data)
            response.raise_for_status()
            
            data = response.json()
            self.access_token = data.get("token")
            self.user_id = data.get("user_id")
            logger.info("Login successful")
            
        except Exception as e:
            logger.error(f"Login failed: {str(e)}")
            raise
    
    def verify_otp(self):
        """Verify OTP after login"""
        try:
            # TODO: Implement actual OTP verification logic
            verify_endpoint = f"{self.base_url}/verify-otp"
            otp_data = {
                "user_id": self.user_id,
                "otp": "123456"  # Replace with actual OTP logic
            }
            response = self.session.post(verify_endpoint, json=otp_data)
            response.raise_for_status()
            logger.info("OTP verification successful")
            
        except Exception as e:
            logger.error(f"OTP verification failed: {str(e)}")
            raise

    def get_outlet_list(self, app_source: str, outlet_id: str = None) -> List[Dict]:
        """Get list of outlets"""
        try:
            headers = {
                "Authorization": f"Bearer {self.access_token}",
                "Content-Type": "application/json"
            }
            
            payload = {
                "user_id": self.user_id,
                "app_source": app_source
            }
            if outlet_id:
                payload["outlet_id"] = outlet_id
                
            response = self.session.get(
                f"{self.base_url}/outlet/list",
                headers=headers,
                json=payload
            )
            response.raise_for_status()
            
            return response.json().get("outlets", [])
            
        except Exception as e:
            logger.error(f"Failed to get outlet list: {str(e)}")
            raise

    def delete_outlets_bulk(self, outlet_ids: List[str]) -> bool:
        """Delete multiple outlets in bulk"""
        try:
            headers = {
                "Authorization": f"Bearer {self.access_token}",
                "Content-Type": "application/json"
            }
            
            payload = {
                "user_id": self.user_id,
                "outlet_ids": outlet_ids
            }
            
            response = self.session.delete(
                f"{self.base_url}/outlet/bulk-delete",
                headers=headers,
                json=payload
            )
            response.raise_for_status()
            
            return response.json().get("success", False)
            
        except Exception as e:
            logger.error(f"Failed to delete outlets: {str(e)}")
            raise

class TestOutletOperations:
    @pytest.fixture(scope="class")
    def outlet_ops(self):
        ops = OutletOperations()
        ops.setup()
        return ops
    
    def test_get_outlet_list(self, outlet_ops):
        """Test getting outlet list"""
        try:
            outlets = outlet_ops.get_outlet_list(app_source="web")
            assert isinstance(outlets, list)
            logger.info(f"Successfully retrieved {len(outlets)} outlets")
            
        except Exception as e:
            logger.error(f"Test failed: {str(e)}")
            raise
    
    def test_bulk_delete_outlets(self, outlet_ops):
        """Test deleting outlets in bulk (50 at a time)"""
        try:
            # Get list of all outlets
            outlets = outlet_ops.get_outlet_list(app_source="web")
            
            # Process outlets in chunks of 50
            chunk_size = 50
            for i in range(0, len(outlets), chunk_size):
                chunk = outlets[i:i + chunk_size]
                outlet_ids = [outlet["outlet_id"] for outlet in chunk]
                
                # Delete the chunk of outlets
                success = outlet_ops.delete_outlets_bulk(outlet_ids)
                assert success, f"Failed to delete outlets chunk {i//chunk_size + 1}"
                
                logger.info(f"Successfully deleted chunk {i//chunk_size + 1} "
                          f"({len(outlet_ids)} outlets)")
            
            # Verify deletion
            remaining_outlets = outlet_ops.get_outlet_list(app_source="web")
            assert len(remaining_outlets) == 0, "Not all outlets were deleted"
            
        except Exception as e:
            logger.error(f"Test failed: {str(e)}")
            raise

if __name__ == "__main__":
    pytest.main(["-v", __file__]) 