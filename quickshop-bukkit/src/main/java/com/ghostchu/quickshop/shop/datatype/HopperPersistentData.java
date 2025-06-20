package com.ghostchu.quickshop.shop.datatype;

import com.google.gson.annotations.Expose;
import java.util.UUID;
import lombok.Getter;

@Getter
public class HopperPersistentData {
    @Expose
    private final UUID player;

    public HopperPersistentData(UUID player) {
        this.player = player;
    }
}
