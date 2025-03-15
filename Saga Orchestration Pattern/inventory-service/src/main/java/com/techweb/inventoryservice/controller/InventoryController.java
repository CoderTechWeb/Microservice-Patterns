package com.techweb.inventoryservice.controller;

import com.techweb.inventoryservice.dto.InventoryRequest;
import com.techweb.inventoryservice.dto.InventoryResponse;
import com.techweb.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/check")
    public ResponseEntity<InventoryResponse> checkStock(@RequestParam String productCode, @RequestParam int quantity) {
        boolean inStock = inventoryService.checkStock(productCode, quantity);
        return ResponseEntity.ok(new InventoryResponse(productCode, inStock));
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveStock(@RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.reserveStock(request.getProductCode(), request.getQuantity()));
    }

    @PostMapping("/release")
    public ResponseEntity<String> releaseStock(@RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.releaseStock(request.getProductCode(), request.getQuantity()));
    }
}