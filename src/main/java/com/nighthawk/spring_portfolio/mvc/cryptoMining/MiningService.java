package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.Date;

@Service
public class MiningService {
    @Autowired
    private MiningUserRepository miningUserRepository;
    
    @Autowired
    private GPURepository gpuRepository;

    // Fine-tune constants
    public static final double HASH_TO_BTC_RATE = 0.0001; // Current rate
    public static final double DIFFICULTY_FACTOR = 1.0;
    public static final int MINING_INTERVAL = 10000; // 10 seconds
    public static final int BALANCE_TRANSFER_INTERVAL = 10000; // 10 seconds
    public static final double ELECTRICITY_RATE = 0.12; // USD per kWh
    public static final double BTC_PRICE = 45000.0;

    @Scheduled(fixedRate = MINING_INTERVAL)
    @Transactional
    public void processMining() {
        System.out.println("\n=== Mining Process Debug Log ===");
        System.out.println("Time: " + new Date());
        
        List<MiningUser> activeMiners = miningUserRepository.findAll().stream()
            .filter(user -> user.isMining() && !user.getActiveGPUs().isEmpty())
            .collect(Collectors.toList());

        activeMiners.forEach(miner -> {
            double hashrate = miner.getCurrentHashrate();
            if (hashrate <= 0) {
                return;
            }

            // Calculate mining reward
            double timeInHours = MINING_INTERVAL / (1000.0 * 60.0 * 60.0);
            double btcMined = hashrate * HASH_TO_BTC_RATE * timeInHours;
            
            // Update pending balance
            double oldPending = miner.getPendingBalance();
            double newPending = oldPending + btcMined;
            miner.setPendingBalance(newPending);
            miner.setShares(miner.getShares() + 1);
            
            // Detailed mining log with USD values
            System.out.println("\nMining Session Details:");
            System.out.println("Miner: " + miner.getPerson().getEmail());
            System.out.println("GPU: " + miner.getActiveGPUs().get(0).getName());
            System.out.println("Hashrate: " + String.format("%.2f", hashrate) + " MH/s");
            System.out.println("\nMining Results:");
            System.out.println("BTC Mined: " + String.format("%.8f", btcMined) + " BTC");
            System.out.println("USD Value: $" + String.format("%.4f", btcMined * BTC_PRICE));
            System.out.println("\nCurrent Balances:");
            System.out.println(String.format("Pending: %.8f BTC ($%.4f)", newPending, newPending * BTC_PRICE));
            System.out.println(String.format("Confirmed: %.8f BTC ($%.4f)", miner.getBtcBalance(), miner.getBtcBalance() * BTC_PRICE));
            System.out.println("Total Value: $" + String.format("%.4f", (newPending + miner.getBtcBalance()) * BTC_PRICE));
            
            // Update profitability calculations
            calculateProfitability(miner);
            
            miningUserRepository.save(miner);
        });
    }

    @Scheduled(fixedRate = BALANCE_TRANSFER_INTERVAL)
    @Transactional
    public void processPendingBalances() {
        System.out.println("\n=== Processing Pending Balances ===");
        System.out.println("Time: " + new Date());
        
        List<MiningUser> miners = miningUserRepository.findAll();
        for (MiningUser miner : miners) {
            double pending = miner.getPendingBalance();
            if (pending > 0) {
                double oldBalance = miner.getBtcBalance();
                double newBalance = oldBalance + pending;
                
                System.out.println("\nTransferring Balance for: " + miner.getPerson().getEmail());
                System.out.println(String.format("Transferring: %.8f BTC ($%.4f)", pending, pending * BTC_PRICE));
                System.out.println("From Pending to Confirmed Balance");
                
                miner.setBtcBalance(newBalance);
                miner.setPendingBalance(0.0);
                
                System.out.println("\nNew Balances:");
                System.out.println(String.format("Confirmed: %.8f BTC ($%.4f)", newBalance, newBalance * BTC_PRICE));
                System.out.println("Pending: 0.00000000 BTC ($0.0000)");
                
                miningUserRepository.save(miner);
            }
        }
        System.out.println("\n=== Balance Processing Complete ===");
    }

    public Map<String, Object> buyGPU(MiningUser user, Long gpuId) {
        GPU gpu = gpuRepository.findById(gpuId).orElse(null);
        if (gpu == null) {
            return Map.of("success", false, "message", "GPU not found");
        }

        user.addGPU(gpu);
        miningUserRepository.save(user);

        return Map.of(
            "success", true,
            "message", "Successfully purchased " + gpu.getName(),
            "newBalance", user.getBtcBalance()
        );
    }

    public void calculateProfitability(MiningUser miner) {
        double hashrate = miner.getCurrentHashrate();
        
        // Calculate daily earnings (24 hours)
        double dailyBTC = hashrate * HASH_TO_BTC_RATE * 24.0;
        double dailyUSD = dailyBTC * BTC_PRICE;
        
        // Calculate power costs
        double dailyPowerKWH = miner.getActiveGPUs().stream()
            .mapToDouble(gpu -> gpu.getPowerConsumption() * 24.0 / 1000.0)
            .sum();
        double dailyPowerCost = dailyPowerKWH * ELECTRICITY_RATE;
        
        // Set values
        miner.setDailyRevenue(dailyUSD);
        miner.setPowerCost(dailyPowerCost);
        
        // Log profitability details
        System.out.println("\nDaily Mining Projections:");
        System.out.println("GPU: " + miner.getActiveGPUs().get(0).getName());
        System.out.println("Hashrate: " + String.format("%.2f", hashrate) + " MH/s");
        System.out.println("BTC Revenue: " + String.format("%.8f", dailyBTC) + " BTC");
        System.out.println("USD Revenue: $" + String.format("%.2f", dailyUSD));
        System.out.println("Power Cost: $" + String.format("%.2f", dailyPowerCost));
        System.out.println("Net Profit: $" + String.format("%.2f", dailyUSD - dailyPowerCost));
        System.out.println("ROI (days): " + String.format("%.1f", 
            (miner.getActiveGPUs().get(0).getPrice() / (dailyUSD - dailyPowerCost))));
    }
}