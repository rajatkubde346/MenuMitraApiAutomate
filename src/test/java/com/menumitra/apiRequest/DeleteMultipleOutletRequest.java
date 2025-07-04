package com.menumitra.apiRequest;

public class DeleteMultipleOutletRequest {
    private int user_id;
    private String action;
    private String app_source;
    private int[] outlet_ids;
    
    public int getUser_id() {
        return user_id;
    }
    
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getApp_source() {
        return app_source;
    }
    
    public void setApp_source(String app_source) {
        this.app_source = app_source;
    }
    
    public int[] getOutlet_ids() {
        return outlet_ids;
    }
    
    public void setOutlet_ids(int[] outlet_ids) {
        this.outlet_ids = outlet_ids;
    }
}
