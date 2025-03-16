package com.techweb.inventoryservice.service;

import com.techweb.inventoryservice.entity.Inventory;
import com.techweb.inventoryservice.repository.InventoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @PostConstruct
    public void initInventory() {
        if (inventoryRepository.count() == 0) { // Avoid duplicate inserts
            List<Inventory> inventoryList = Arrays.asList(
                    new Inventory(null, "Laptop", 10),
                    new Inventory(null, "Mouse", 20),
                    new Inventory(null, "Mobile", 15),
                    new Inventory(null, "TV", 30)
            );

            inventoryRepository.saveAll(inventoryList);
            System.out.println("✅ Dummy Inventory Data Initialized!");
        }
    }

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
            throw new RuntimeException("Insufficient Stock for Product: " + productCode);
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