package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mining")
@Transactional
public class MiningController {
    @Autowired
    private PersonJpaRepository personRepository;
    
    @Autowired
    private MiningUserRepository miningUserRepository;
    
    @Autowired
    private GPURepository gpuRepository;

    @Autowired
    private MiningService miningService;

    private MiningUser getOrCreateMiningUser() {
        // Get authentication details
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String uid = auth.getName();
        
        System.out.println("DEBUG - Auth Details:");
        System.out.println("  UID: " + uid);
        System.out.println("  Principal: " + auth.getPrincipal());
        System.out.println("  Authorities: " + auth.getAuthorities());

        if ("anonymousUser".equals(uid)) {
            throw new RuntimeException("Not authenticated");
        }

        // Find person by UID instead of email
        Person person = personRepository.findByUid(uid);
        if (person == null) {
            System.out.println("DEBUG - No person found for UID: " + uid);
            throw new RuntimeException("User not found: " + uid);
        }        

        System.out.println("DEBUG - Found person with UID: " + person.getUid());

        // Find existing mining user
        Optional<MiningUser> existingUser = miningUserRepository.findByPerson(person);
        if (existingUser.isPresent()) {
            System.out.println("DEBUG - Found existing mining user");
            return existingUser.get();
        }

        // Create new mining user using constructor
        System.out.println("DEBUG - Creating new mining user");
        MiningUser newUser = new MiningUser(person);
        
        // Add starter GPU if exists
        GPU starterGpu = gpuRepository.findById(1L).orElse(null);
        if (starterGpu != null) {
            newUser.addGPU(starterGpu);
            System.out.println("DEBUG - Added starter GPU: " + starterGpu.getName());
        } else {
            System.out.println("DEBUG - No starter GPU found");
        }

        return miningUserRepository.save(newUser);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getMiningStats() {
        try {
            MiningUser user = getOrCreateMiningUser();
            miningService.calculateProfitability(user);

            Map<String, Object> stats = new HashMap<>();
            double pendingBTC = user.getPendingBalance();
            double confirmedBTC = user.getBtcBalance();
            
            stats.put("btcBalance", String.format("%.8f", confirmedBTC));
            stats.put("pendingBalance", String.format("%.8f", pendingBTC));
            stats.put("totalBalanceUSD", String.format("%.2f", (pendingBTC + confirmedBTC) * MiningService.BTC_PRICE));
            
            stats.put("hashrate", String.format("%.2f", user.getCurrentHashrate()));
            stats.put("shares", user.getShares());
            stats.put("isMining", user.isMining());
            stats.put("currentPool", user.getCurrentPool());
            stats.put("powerConsumption", user.getPowerConsumption());
            stats.put("averageTemperature", user.getAverageTemperature());
            stats.put("dailyRevenue", user.getDailyRevenue());
            stats.put("powerCost", user.getPowerCost());

            List<Map<String, Object>> allGpus = user.getOwnedGPUs().stream()
                .map(gpu -> {
                    Map<String, Object> gpuInfo = new HashMap<>();
                    gpuInfo.put("id", gpu.getId());
                    gpuInfo.put("name", gpu.getName());
                    gpuInfo.put("hashrate", gpu.getHashRate());
                    gpuInfo.put("power", gpu.getPowerConsumption());
                    gpuInfo.put("temp", gpu.getTemp());
                    gpuInfo.put("isActive", user.getActiveGPUs().contains(gpu));
                    return gpuInfo;
                })
                .collect(Collectors.toList());
            
            stats.put("gpus", allGpus);
            stats.put("activeGPUsCount", allGpus.stream().filter(g -> (Boolean)g.get("isActive")).count());

            List<Map<String, Object>> activeGpus = user.getActiveGPUs().stream()
                .map(gpu -> {
                    Map<String, Object> gpuInfo = new HashMap<>();
                    gpuInfo.put("id", gpu.getId());
                    gpuInfo.put("name", gpu.getName());
                    gpuInfo.put("hashrate", gpu.getHashRate());
                    return gpuInfo;
                })
                .collect(Collectors.toList());
            stats.put("activeGPUs", activeGpus);

            System.out.println("**Final Return Data** : " + stats);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/shop")
    public ResponseEntity<?> getGPUShop() {
        try {
            MiningUser user = getOrCreateMiningUser();
            List<GPU> allGpus = gpuRepository.findAll();
            
            // Convert GPUs to maps with additional ownership info
            List<Map<String, Object>> gpuList = new ArrayList<>();
            for (GPU gpu : allGpus) {
                Map<String, Object> gpuInfo = new HashMap<>();
                gpuInfo.put("id", gpu.getId());
                gpuInfo.put("name", gpu.getName());
                gpuInfo.put("hashRate", gpu.getHashRate());
                gpuInfo.put("powerConsumption", gpu.getPowerConsumption());
                gpuInfo.put("temp", gpu.getTemp());
                gpuInfo.put("price", gpu.getPrice());
                gpuInfo.put("category", gpu.getCategory());
                gpuInfo.put("owned", user.getGpus().contains(gpu));
                
                gpuList.add(gpuInfo);
            }
            
            return ResponseEntity.ok(gpuList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/gpu/buy/{gpuId}")
    public ResponseEntity<?> buyGPU(@PathVariable Long gpuId) {
        try {
            MiningUser user = getOrCreateMiningUser();
            GPU gpu = gpuRepository.findById(gpuId)
                .orElseThrow(() -> new RuntimeException("GPU not found"));
            
            // Check if user already owns this GPU
            if (user.ownsGPUById(gpuId)) {
                System.out.println("User already owns GPU with ID: " + gpuId); // Debug log
                return ResponseEntity.badRequest()
                    .body(Map.of(
                        "success", false,
                        "message", "You already own this GPU"
                    ));
            }
            
            user.addGPU(gpu);
            miningUserRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully purchased " + gpu.getName()
            ));
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleMining() {
        try {
            MiningUser user = getOrCreateMiningUser();
            user.setMining(!user.isMining());
            miningUserRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isMining", user.isMining());
            response.put("message", user.isMining() ? "Mining started" : "Mining stopped");
            response.put("currentHashrate", user.getCurrentHashrate());
            response.put("activeGPUs", user.getActiveGPUs().size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/gpu/toggle/{gpuId}")
    public ResponseEntity<?> toggleGPU(@PathVariable Long gpuId) {
        try {
            MiningUser user = getOrCreateMiningUser();
            GPU gpu = gpuRepository.findById(gpuId)
                .orElseThrow(() -> new RuntimeException("GPU not found"));
    
            // Check if user owns this GPU
            if (!user.ownsGPUById(gpuId)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "You don't own this GPU"));
            }
    
            boolean isActive = user.toggleGPU(gpu);
            miningUserRepository.save(user);
    
            return ResponseEntity.ok(Map.of("success", true, "message", isActive ? "GPU activated" : "GPU deactivated", "isActive", isActive));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/state")
    public ResponseEntity<?> getMiningState() {
        try {
            MiningUser user = getOrCreateMiningUser();
            miningService.calculateProfitability(user);

            Map<String, Object> stats = new HashMap<>();
            double pendingBTC = user.getPendingBalance();
            double confirmedBTC = user.getBtcBalance();
            
            // Balance information with USD values
            stats.put("btcBalance", String.format("%.8f", confirmedBTC));
            stats.put("btcBalanceUSD", String.format("%.2f", confirmedBTC * MiningService.BTC_PRICE));
            stats.put("pendingBalance", String.format("%.8f", pendingBTC));
            stats.put("pendingBalanceUSD", String.format("%.2f", pendingBTC * MiningService.BTC_PRICE));
            stats.put("totalBalanceUSD", String.format("%.2f", (pendingBTC + confirmedBTC) * MiningService.BTC_PRICE));
            
            // Mining status
            stats.put("hashrate", String.format("%.2f", user.getCurrentHashrate()));
            stats.put("shares", user.getShares());
            stats.put("isMining", user.isMining());
            stats.put("currentPool", user.getCurrentPool());
            
            // Power and temperature
            stats.put("powerConsumption", user.getPowerConsumption());
            stats.put("averageTemperature", user.getAverageTemperature());
            
            // Profitability
            stats.put("dailyRevenueBTC", String.format("%.8f", user.getDailyRevenue() / MiningService.BTC_PRICE));
            stats.put("dailyRevenueUSD", String.format("%.2f", user.getDailyRevenue()));
            stats.put("powerCost", user.getPowerCost());
            stats.put("netProfitUSD", String.format("%.2f", user.getDailyRevenue() - user.getPowerCost()));

            // GPU information
            List<Map<String, Object>> allGpus = user.getOwnedGPUs().stream()
                .map(gpu -> {
                    Map<String, Object> gpuInfo = new HashMap<>();
                    gpuInfo.put("id", gpu.getId());
                    gpuInfo.put("name", gpu.getName());
                    gpuInfo.put("hashrate", gpu.getHashRate());
                    gpuInfo.put("power", gpu.getPowerConsumption());
                    gpuInfo.put("temp", gpu.getTemp());
                    gpuInfo.put("isActive", user.getActiveGPUs().contains(gpu));
                    return gpuInfo;
                })
                .collect(Collectors.toList());
            
            stats.put("gpus", allGpus);
            stats.put("activeGPUs", user.getActiveGPUs());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/testMining")
    public ResponseEntity<?> testMining() {
        try {
            MiningUser user = getOrCreateMiningUser(); // Get or create the mining user
            user.setMining(true); // Ensure the user is set to mining

            // Simulate a hashrate for testing
            user.setCurrentHashrate(60.55); // Set hashrate to 60.55 MH/s

            // Call the mining process
            miningService.processMining(); // This should update the user's balance and shares

            // Prepare the response
            Map<String, Object> response = new HashMap<>();
            response.put("btcBalance", user.getBtcBalance());
            response.put("pendingBalance", user.getPendingBalance());
            response.put("shares", user.getShares());
            response.put("message", "Mining test completed successfully.");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mining-status")
    public ResponseEntity<?> getMiningStatus() {
        try {
            MiningUser user = getOrCreateMiningUser();
            
            Map<String, Object> status = new HashMap<>();
            double pendingBTC = user.getPendingBalance();
            double confirmedBTC = user.getBtcBalance();
            
            status.put("pendingBalance", String.format("%.8f", pendingBTC));
            status.put("pendingBalanceUSD", String.format("%.2f", pendingBTC * MiningService.BTC_PRICE));
            status.put("confirmedBalance", String.format("%.8f", confirmedBTC));
            status.put("confirmedBalanceUSD", String.format("%.2f", confirmedBTC * MiningService.BTC_PRICE));
            status.put("totalBalanceUSD", String.format("%.2f", (pendingBTC + confirmedBTC) * MiningService.BTC_PRICE));
            status.put("isMining", user.isMining());
            status.put("activeGPUs", user.getActiveGPUs().size());
            status.put("currentHashrate", String.format("%.2f", user.getCurrentHashrate()));
            status.put("lastShare", user.getShares());
            status.put("estimated24hBTC", String.format("%.8f", user.getDailyRevenue() / MiningService.BTC_PRICE));
            status.put("estimated24hUSD", String.format("%.2f", user.getDailyRevenue()));
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/test-mining-system")
    public ResponseEntity<?> testMiningSystem() {
        try {
            MiningUser user = getOrCreateMiningUser();
            
            // Print initial state
            System.out.println("\n=== Mining System Test ===");
            System.out.println("Initial state:");
            System.out.println("- Mining active: " + user.isMining());
            System.out.println("- Active GPUs: " + user.getActiveGPUs().size());
            System.out.println("- GPU Details:");
            user.getActiveGPUs().forEach(gpu -> {
                System.out.println("  * " + gpu.getName() + " - " + gpu.getHashRate() + " MH/s");
            });
            System.out.println("- Current hashrate: " + user.getCurrentHashrate());
            System.out.println("- Initial balance: " + user.getBtcBalance());
            System.out.println("- Initial pending: " + user.getPendingBalance());
            
            // Force mining on and ensure GPUs are active
            user.setMining(true);
            if (user.getActiveGPUs().isEmpty() && !user.getOwnedGPUs().isEmpty()) {
                // Activate the first GPU if none are active
                user.toggleGPU(user.getOwnedGPUs().get(0));
            }
            miningUserRepository.save(user);
            
            // Manually trigger mining process
            miningService.processMining();
            
            // Wait for one mining cycle
            Thread.sleep(MiningService.MINING_INTERVAL);
            
            // Refresh user data
            user = getOrCreateMiningUser();
            
            // Print final state
            System.out.println("\nFinal state:");
            System.out.println("- Mining active: " + user.isMining());
            System.out.println("- Active GPUs: " + user.getActiveGPUs().size());
            System.out.println("- Current hashrate: " + user.getCurrentHashrate());
            System.out.println("- Final balance: " + user.getBtcBalance());
            System.out.println("- Final pending: " + user.getPendingBalance());
            System.out.println("=== Test Complete ===\n");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("initialBalance", user.getBtcBalance());
            response.put("finalPending", user.getPendingBalance());
            response.put("hashrate", user.getCurrentHashrate());
            response.put("activeGPUs", user.getActiveGPUs().size());
            response.put("ownedGPUs", user.getOwnedGPUs().size());
            response.put("miningEnabled", user.isMining());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Mining test failed: " + e.getMessage()));
        }
    }

    @GetMapping("/debug-values")
    public ResponseEntity<?> debugValues() {
        try {
            MiningUser user = getOrCreateMiningUser();
            Map<String, Object> debug = new HashMap<>();
            
            // Raw values
            debug.put("rawPendingBTC", user.getPendingBalance());
            debug.put("rawConfirmedBTC", user.getBtcBalance());
            debug.put("btcPrice", MiningService.BTC_PRICE);
            
            // Calculated values
            debug.put("pendingUSD", user.getPendingBalance() * MiningService.BTC_PRICE);
            debug.put("confirmedUSD", user.getBtcBalance() * MiningService.BTC_PRICE);
            debug.put("totalUSD", (user.getPendingBalance() + user.getBtcBalance()) * MiningService.BTC_PRICE);
            
            // Formatted values
            debug.put("formattedPendingUSD", String.format("%.2f", user.getPendingBalance() * MiningService.BTC_PRICE));
            debug.put("formattedConfirmedUSD", String.format("%.2f", user.getBtcBalance() * MiningService.BTC_PRICE));
            debug.put("formattedTotalUSD", String.format("%.2f", (user.getPendingBalance() + user.getBtcBalance()) * MiningService.BTC_PRICE));
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
