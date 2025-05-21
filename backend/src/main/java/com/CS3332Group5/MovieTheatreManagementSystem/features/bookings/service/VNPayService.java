package com.CS3332Group5.MovieTheatreManagementSystem.features.bookings.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    @Value("${vnpay.tmnCode}")
    private String vnpTmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnpHashSecret;

    @Value("${vnpay.payUrl}")
    private String vnpPayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnpReturnUrl;

    // Tạo URL thanh toán và trả về cho front-end
    public String createPaymentUrl(Long bookingId, Long amount, String orderInfo, HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_TxnRef", String.valueOf(bookingId));
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "170000");
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_Locale", "vn");
        params.put("vnp_IpAddr", getClientIp(request));
        params.put("vnp_ReturnUrl", vnpReturnUrl);

        // Thời gian tạo và hết hạn thanh toán
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = fmt.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String expireDate = fmt.format(cld.getTime());
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);

        // Sắp xếp key và build raw hashData + query string
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        keys.forEach(key -> {
            String value = params.get(key);
            if (value == null || value.isEmpty()) return;
            if (hashData.length() > 0) hashData.append('&');
            hashData.append(key).append('=').append(value);
            try {
                if (query.length() > 0) query.append('&');
                query.append(URLEncoder.encode(key, StandardCharsets.UTF_8.name()))
                     .append('=')
                     .append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
            } catch (Exception e) {
                throw new RuntimeException("Error encoding VNPay parameter: " + key, e);
            }
        });

        // Tính HMAC-SHA512
        String secureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return vnpPayUrl + "?" + query.toString();
    }

    // Kiểm tra tính hợp lệ của response từ VNPay
    public boolean validatePaymentResponse(Map<String, String> params) {
        // Lấy và loại bỏ secureHash khỏi params
        String receivedHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        // Sắp xếp key và build lại raw hashData
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder hashData = new StringBuilder();
        keys.forEach(key -> {
            String value = params.get(key);
            if (value == null || value.isEmpty()) return;
            if (hashData.length() > 0) hashData.append('&');
            hashData.append(key).append('=').append(value);
        });

        // Tính HMAC và so sánh
        String computedHash = hmacSHA512(vnpHashSecret, hashData.toString());
        return computedHash.equals(receivedHash);
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(spec);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA512", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        return (ip == null || ip.isEmpty()) ? request.getRemoteAddr() : ip;
    }
}
