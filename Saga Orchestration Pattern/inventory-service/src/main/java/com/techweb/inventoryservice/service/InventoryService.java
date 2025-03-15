package com.techweb.inventoryservice.service;

import com.techweb.inventoryservice.entity.Inventory;
import com.techweb.inventoryservice.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    public boolean checkStock(String productCode, int quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductCode(productCode);
        return inventoryOpt.isPresent() && inventoryOpt.get().getStock() >= quantity;
    }

    public String reserveStock(String productCode, int quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductCode(productCode);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            if (inventory.getStock() >= quantity) {
                inventory.setStock(inventory.getStock() - quantity);
                inventoryRepository.save(inventory);
                return "Stock Reserved for Product: " + productCode;
            }
            return "Insufficient Stock for Product: " + productCode;
        }
        return "Product Not Found: " + productCode;
    }

    public String releaseStock(String productCode, int quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductCode(productCode);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setStock(inventory.getStock() + quantity);
            inventoryRepository.save(inventory);
            return "Stock Released for Product: " + productCode;
        }
        return "Product Not Found: " + productCode;
    }
}