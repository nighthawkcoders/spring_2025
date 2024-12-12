package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;

@Service
public class MiningService {
    @Autowired
    private MiningUserRepository miningUserRepository;
    
    @Autowired
    private GPURepository gpuRepository;

    @Scheduled(fixedRate = 60000) // Every minute
    public void processMining() {
        List<MiningUser> activeMiners = miningUserRepository.findAll().stream()
            .filter(MiningUser::isMining)
            .collect(Collectors.toList());
            
        for (MiningUser miner : activeMiners) {
            double hashrate = miner.getCurrentHashrate();
            double btcMined = hashrate * 60 / (1e12); // Example calculation
            miner.setPendingBalance(miner.getPendingBalance() + btcMined);
            miner.setShares(miner.getShares() + 1);
            miningUserRepository.save(miner);
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
}