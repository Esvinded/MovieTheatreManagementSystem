package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.entity;

public enum BookingStatus {
    PENDING,            // còn chọn ghế
    AWAITING_PAYMENT,   // đã xác nhận, chờ VNPay
    BOOKED,            // thanh toán thành công
    CANCELLED,          // user huỷ
    EXPIRED             // timeout chưa trả tiền
}
