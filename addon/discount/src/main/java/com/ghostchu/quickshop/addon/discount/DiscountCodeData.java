package com.ghostchu.quickshop.addon.discount;

import com.ghostchu.quickshop.addon.discount.type.CodeType;
import com.ghostchu.quickshop.addon.discount.type.RateType;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCodeData {

    private UUID owner;
    private String code;
    private CodeType codeType;
    private RateType rateType;
    private String rate;
    private long expire;
    private double threshold;
    private int maxUsage;
    private Map<UUID, Integer> usages;
    private Set<Long> shopScope;

}
