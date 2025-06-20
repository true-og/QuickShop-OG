package com.ghostchu.quickshop.util;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Profile {
    private final UUID uniqueId;
    private final String name;
}
