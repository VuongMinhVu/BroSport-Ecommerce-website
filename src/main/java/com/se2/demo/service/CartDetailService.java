package com.se2.demo.service;

import com.se2.demo.dto.request.CartDetailRequest;
import com.se2.demo.dto.response.CartDetailResponse;

public interface CartDetailService {
    CartDetailResponse addItemToCart(CartDetailRequest request);
    void removeItemFromCart(Integer detailId);

}