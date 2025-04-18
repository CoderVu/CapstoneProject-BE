package com.example.CapstoneProject.controller.User;

import com.example.CapstoneProject.controller.Payment.PaymentZaloPayCheckStatusController;
import com.example.CapstoneProject.request.PaymentRequest;
import com.example.CapstoneProject.request.RateRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.service.Interface.IOrderService;
import com.example.CapstoneProject.service.Interface.IPaymentService;
import com.example.CapstoneProject.service.Interface.IRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private IRateService rateService;
    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private PaymentZaloPayCheckStatusController paymentZaloPayCheckStatusController;

    private ResponseEntity<APIResponse> checkPaymentZaloPayStatus(String appTransId) {
        return paymentZaloPayCheckStatusController.getStatus(appTransId);
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse> createOrderNow(@RequestHeader("Authorization") String token, @RequestBody PaymentRequest request) throws IOException {
        String newToken = token.substring(7);
        request.setToken(newToken);

        // Generate order code
        String orderCode = orderService.generateUniqueOrderCode();
        request.setOrderId(orderCode);
        request.setOrderInfo("Payment for order " + orderCode);
        request.setLang("en");
        request.setExtraData("additional data");
        request.setAmount(request.getAmount());

        if ("ZALOPAY".equals(request.getPaymentMethod())) {
            Map<String, Object> zalopayResponse = paymentService.createOrderZaloPay(request);

            if (Integer.parseInt(zalopayResponse.get("returncode").toString()) == 1) {
                String appTransId = zalopayResponse.get("apptransid").toString();

                // Create scheduler to check payment status
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final int[] count = {0};

                scheduler.scheduleAtFixedRate(() -> {
                    count[0]++;
                    if (count[0] > 120) { // Stop after 120 attempts (2 minutes)
                        System.out.println("Payment status check timeout for: " + appTransId);
                        scheduler.shutdown();
                        return;
                    }

                    ResponseEntity<APIResponse> statusResponse = checkPaymentZaloPayStatus(appTransId);
                    System.out.println("Checking payment status... Attempt " + count[0]);
                    System.out.println("Payment status response: " + statusResponse);

                    if (statusResponse.getStatusCode() == HttpStatus.OK && statusResponse.getBody() != null) {
                        APIResponse response = statusResponse.getBody();
                        Map<String, Object> data = (Map<String, Object>) response.getData();

                        if (Integer.parseInt(data.get("returncode").toString()) == 1) {
                            System.out.println("Payment successful for: " + appTransId);
                            orderService.createOrderNow(request);
                            scheduler.shutdown(); // Stop scheduler after successful payment
                        }
                    }
                }, 0, 1, TimeUnit.SECONDS);

                return ResponseEntity.ok(new APIResponse(200, "ZaloPay order created successfully", zalopayResponse));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse(400, "Failed to create ZaloPay order", zalopayResponse));
            }
        }

        // Handle other payment methods
        APIResponse response = orderService.createOrderNow(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/create-cart")
    public ResponseEntity<APIResponse> createOrderCart(@RequestHeader("Authorization") String token, @RequestBody PaymentRequest orderRequest) throws IOException {
        String newToken = token.substring(7);
        orderRequest.setToken(newToken);

        // Tạo mã đơn hàng
        String orderCode = orderService.generateUniqueOrderCode();
        orderRequest.setOrderId(orderCode);
        orderRequest.setOrderInfo("Payment for order " + orderCode);
        orderRequest.setLang("en");
        orderRequest.setExtraData("additional data");
        orderRequest.setAmount(orderRequest.getAmount());

        if ("ZALOPAY".equals(orderRequest.getPaymentMethod())) {
            Map<String, Object> zalopayResponse = paymentService.createOrderZaloPay(orderRequest);

            if (Integer.parseInt(zalopayResponse.get("returncode").toString()) == 1) {
                String appTransId = zalopayResponse.get("apptransid").toString();

                // Tạo scheduler để kiểm tra trạng thái thanh toán
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                final int[] count = {0};

                scheduler.scheduleAtFixedRate(() -> {
                    count[0]++;
                    if (count[0] > 120) { // Nếu kiểm tra  quá 120 lần ( 2 phút) thì dừng
                        System.out.println("Payment status check timeout for: " + appTransId);
                        scheduler.shutdown();
                        return;
                    }

                    ResponseEntity<APIResponse> statusResponse = checkPaymentZaloPayStatus(appTransId);
                    System.out.println("Checking payment status... Attempt " + count[0]);
                    System.out.println("Payment status response: " + statusResponse);

                    if (statusResponse.getStatusCode() == HttpStatus.OK && statusResponse.getBody() != null) {
                        APIResponse response = statusResponse.getBody();
                        Map<String, Object> data = (Map<String, Object>) response.getData();

                        if (Integer.parseInt(data.get("returncode").toString()) == 1) {
                            System.out.println("Payment successful for: " + appTransId);
                            orderService.createOrderFromCart(orderRequest);
                            scheduler.shutdown(); // Dừng scheduler ngay sau khi thanh toán thành công
                        }
                    }
                }, 0, 1, TimeUnit.SECONDS);

                return ResponseEntity.ok(new APIResponse(200, "ZaloPay order created successfully", zalopayResponse));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse(400, "Failed to create ZaloPay order", zalopayResponse));
            }
        }
        // Xử lý các phương thức thanh toán bằng tiền mặt

        orderService.createOrderFromCart(orderRequest);

            return ResponseEntity.ok(new APIResponse(200, "Order created", null));
    }

    @GetMapping("/history")
    public ResponseEntity<APIResponse> getOrderHistory(@RequestHeader("Authorization") String token) {
        String newToken = token.substring(7);
        APIResponse response = orderService.getHistoryOrder(newToken);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cancel")
    public ResponseEntity<APIResponse> cancelOrder(@RequestHeader("Authorization") String token, @RequestParam("orderCode") String orderId) {
        String newToken = token.substring(7);
        APIResponse response = orderService.cancelOrder(newToken, orderId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/rating")
    public ResponseEntity<APIResponse> rateProduct(
            @RequestHeader("Authorization") String token,
            @RequestParam("orderId") String orderId,
            @RequestParam("rating") Double rating,
            @RequestParam("comment") String comment,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        String newToken = token.substring(7);
        RateRequest request = new RateRequest();
        request.setToken(newToken);
        request.setOrderId(orderId);
        request.setRate(rating);
        request.setComment(comment);
        request.setImages(imageFiles);
        APIResponse response = rateService.rateProduct(request);
        return ResponseEntity.ok(response);
    }
}