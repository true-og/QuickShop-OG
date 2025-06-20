package com.ghostchu.quickshop.database.bean;

import java.util.List;
import lombok.Data;

@Data
public class IsolatedScanResult<T> {
    private final List<T> total;
    private final List<T> isolated;
}
