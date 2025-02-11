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

    private static final double HASH_TO_BTC = 0.0001; // 初始系数
    private static final double COST_PER_KWH = 0.12;
    private static final double REVENUE_COEFFICIENT = 0.000001; // 可配置参数
    private static final double ELECTRICITY_RATE = 0.12; // 美元/千瓦时
    private static final double BTC_PRICE = 45000.0; // BTC价格

    @Scheduled(fixedRate = 10000) // 改为10秒方便测试
    @Transactional
    public void processMining() {
        System.out.println("\n=== Mining Process Started ===");
        System.out.println("Current Time: " + new Date());
        
        List<MiningUser> activeMiners = miningUserRepository.findAll().stream()
            .filter(user -> {
                boolean isMining = user.isMining();
                System.out.println("User " + user.getPerson().getEmail() + 
                    " | Mining: " + isMining +
                    " | Active GPUs: " + user.getActiveGPUs().size());
                return isMining;
            })
            .collect(Collectors.toList());

        System.out.println("Active Miners Count: " + activeMiners.size());
        
        activeMiners.forEach(miner -> {
            double btcMined = miner.getCurrentHashrate() * 0.0001;
            miner.setPendingBalance(miner.getPendingBalance() + btcMined);
        });
        
        System.out.println("=== Mining Process Completed ===\n");
    }

    @Scheduled(fixedRate = 30000) // 改为30秒测试
    @Transactional
    public void processPendingBalances() {
        List<MiningUser> miners = miningUserRepository.findAll();
        
        for (MiningUser miner : miners) {
            double pending = miner.getPendingBalance();
            if (pending > 0) {
                miner.setBtcBalance(miner.getBtcBalance() + pending);
                miner.setPendingBalance(0.0);
                
                System.out.println("[" + new Date() + "] Balance Update for user: " + miner.getPerson().getEmail());
                System.out.println("Transferred pending balance: " + pending);
                System.out.println("New BTC Balance: " + miner.getBtcBalance());
                
                miningUserRepository.save(miner);
            }
        }
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
        double totalRevenue = 0.0;
        double totalPowerCost = 0.0;
        
        for (GPU gpu : miner.getActiveGPUs()) {
            double dailyRevenue = gpu.getHashRate() * 86400 * 0.00000001;
            double dailyPowerCost = (gpu.getPowerConsumption() * 24) * COST_PER_KWH;
            
            totalRevenue += dailyRevenue;
            totalPowerCost += dailyPowerCost;
        }
        
        miner.setDailyRevenue(totalRevenue);
        miner.setPowerCost(totalPowerCost);
    }
}