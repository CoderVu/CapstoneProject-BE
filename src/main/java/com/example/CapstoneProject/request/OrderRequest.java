package com.example.CapstoneProject.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private Long amount;
    private String orderId;
    private List<Long> cartIds;
    private String productId;
    private Integer quantity;
    private String size;
    private String color;
    private String appuser;
    private String apptransid;
    private Long order_id;
    private String token;
    private String deliveryAddress;
    private String deliveryPhone;
    private Double longitude;
    private Double latitude;
    private String orderInfo;
    private String lang;
    private String extraData;
    private String paymentMethod;
    private String discountCode;

}
