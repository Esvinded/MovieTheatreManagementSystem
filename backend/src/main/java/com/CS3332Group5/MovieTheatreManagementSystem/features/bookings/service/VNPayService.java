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
        try {
            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "pay");
            params.put("vnp_TmnCode", vnpTmnCode);
            params.put("vnp_Amount", String.valueOf(amount * 100)); // Amount in VND * 100
            params.put("vnp_CurrCode", "VND");
            params.put("vnp_TxnRef", String.valueOf(bookingId));
            params.put("vnp_OrderInfo", orderInfo);
            params.put("vnp_OrderType", "170000"); // Movie ticket
            params.put("vnp_Locale", "vn");
            params.put("vnp_ReturnUrl", vnpReturnUrl);
            params.put("vnp_IpAddr", getClientIp(request));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String createDate = formatter.format(new Date());
            params.put("vnp_CreateDate", createDate);
            Calendar expire = Calendar.getInstance();
            expire.add(Calendar.MINUTE, 15);
            String expireDate = formatter.format(expire.getTime());
            params.put("vnp_ExpireDate", expireDate);

            // 1. Sort params
            SortedMap<String, String> sortedParams = new TreeMap<>(params);

            // 2. Build hashData and query string
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                    query.append('&');
                }
                hashData.append(entry.getKey()).append('=')
                    .append(URLEncoder.encode(entry.getValue(), java.nio.charset.StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(entry.getKey(), java.nio.charset.StandardCharsets.US_ASCII.toString()))
                    .append('=')
                    .append(URLEncoder.encode(entry.getValue(), java.nio.charset.StandardCharsets.US_ASCII.toString()));
            }

            // 3. Generate HMAC SHA512
            String secureHash = hmacSHA512(vnpHashSecret, hashData.toString());

            // 4. Build final URL
            query.append("&vnp_SecureHash=").append(secureHash);
            return vnpPayUrl + "?" + query.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating VNPay URL", e);
        }
    }

    // Kiểm tra tính hợp lệ của response từ VNPay
    public boolean validatePaymentResponse(Map<String, String> params) {
        // Lấy và loại bỏ secureHash khỏi params
        String receivedHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        // Sắp xếp key và build lại raw hashData (phải encode giống như khi tạo payment URL)
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder hashData = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key);
            if (value == null || value.isEmpty()) continue;
            if (hashData.length() > 0) hashData.append('&');
            hashData.append(key).append('=')
                .append(URLEncoder.encode(value, java.nio.charset.StandardCharsets.US_ASCII));
        }
        // Tính HMAC và so sánh
        String computedHash = hmacSHA512(vnpHashSecret, hashData.toString());
        return computedHash.equalsIgnoreCase(receivedHash);
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

    /**
     * Utility: For testing, print the VNPay return URL to the backend log so frontend/dev can see it.
     */
    public void logReturnUrlForTest(String url) {
        System.out.println("[VNPay Return URL for test]: " + url);
    }
}
