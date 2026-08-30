package com.example.gym_crm.common.health;

import org.springframework.stereotype.Component;

@Component
public class MemorySnapshot {

    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}