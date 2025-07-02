package com.menumitra.apiRequest;

public class OutletCreateRequest {
    private int[] owner_ids;
    private String user_id;
    private String name;
    private String mobile;
    private String address;
    private String outlet_type;
    private String outlet_mode;
    private String veg_nonveg;
    private String upi_id;
    private String app_type;

    public int[] getOwner_ids() {
        return owner_ids;
    }

    public void setOwner_ids(int[] owner_ids) {
        this.owner_ids = owner_ids;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOutlet_type() {
        return outlet_type;
    }

    public void setOutlet_type(String outlet_type) {
        this.outlet_type = outlet_type;
    }

    public String getOutlet_mode() {
        return outlet_mode;
    }

    public void setOutlet_mode(String outlet_mode) {
        this.outlet_mode = outlet_mode;
    }

    public String getVeg_nonveg() {
        return veg_nonveg;
    }

    public void setVeg_nonveg(String veg_nonveg) {
        this.veg_nonveg = veg_nonveg;
    }

    public String getUpi_id() {
        return upi_id;
    }

    public void setUpi_id(String upi_id) {
        this.upi_id = upi_id;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }
}
