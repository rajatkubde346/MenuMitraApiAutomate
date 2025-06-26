package com.menumitra.apiRequest;

import java.util.List;

public class BookRequest {
    private int user_id;
    private int outlet_id;
    private String app_source;
    private List<MenuItem> menu_items;
    private BookingDetails booking_details;
    private PaymentDetails payment_details;

    // Getters and Setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getOutlet_id() {
        return outlet_id;
    }

    public void setOutlet_id(int outlet_id) {
        this.outlet_id = outlet_id;
    }

    public String getApp_source() {
        return app_source;
    }

    public void setApp_source(String app_source) {
        this.app_source = app_source;
    }

    public List<MenuItem> getMenu_items() {
        return menu_items;
    }

    public void setMenu_items(List<MenuItem> menu_items) {
        this.menu_items = menu_items;
    }

    public BookingDetails getBooking_details() {
        return booking_details;
    }

    public void setBooking_details(BookingDetails booking_details) {
        this.booking_details = booking_details;
    }

    public PaymentDetails getPayment_details() {
        return payment_details;
    }

    public void setPayment_details(PaymentDetails payment_details) {
        this.payment_details = payment_details;
    }

    // Inner classes
    public static class MenuItem {
        private int menu_id;
        private int quantity;
        private double price;
        private String portion_name;
        private String special_note;

        public int getMenu_id() {
            return menu_id;
        }

        public void setMenu_id(int menu_id) {
            this.menu_id = menu_id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getPortion_name() {
            return portion_name;
        }

        public void setPortion_name(String portion_name) {
            this.portion_name = portion_name;
        }

        public String getSpecial_note() {
            return special_note;
        }

        public void setSpecial_note(String special_note) {
            this.special_note = special_note;
        }
    }

    public static class BookingDetails {
        private String delivery_datetime;
        private String expected_datetime;
        private String customer_name;
        private String customer_mobile;
        private String special_message;
        private String comment;

        public String getDelivery_datetime() {
            return delivery_datetime;
        }

        public void setDelivery_datetime(String delivery_datetime) {
            this.delivery_datetime = delivery_datetime;
        }

        public String getExpected_datetime() {
            return expected_datetime;
        }

        public void setExpected_datetime(String expected_datetime) {
            this.expected_datetime = expected_datetime;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getCustomer_mobile() {
            return customer_mobile;
        }

        public void setCustomer_mobile(String customer_mobile) {
            this.customer_mobile = customer_mobile;
        }

        public String getSpecial_message() {
            return special_message;
        }

        public void setSpecial_message(String special_message) {
            this.special_message = special_message;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class PaymentDetails {
        private double advance_amount;
        private String payment_method;

        public double getAdvance_amount() {
            return advance_amount;
        }

        public void setAdvance_amount(double advance_amount) {
            this.advance_amount = advance_amount;
        }

        public String getPayment_method() {
            return payment_method;
        }

        public void setPayment_method(String payment_method) {
            this.payment_method = payment_method;
        }
    }
}
