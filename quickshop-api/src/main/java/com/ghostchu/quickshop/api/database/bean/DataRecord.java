package com.ghostchu.quickshop.api.database.bean;

import com.ghostchu.quickshop.api.obj.QUser;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DataRecord {

    @NotNull
    Date getCreateTime();

    @Nullable
    String getCurrency();

    @NotNull
    String getExtra();

    @Nullable
    String getInventorySymbolLink();

    @Nullable
    String getInventoryWrapper();

    @NotNull
    String getItem();

    @Nullable
    String getName();

    @NotNull
    QUser getOwner();

    @NotNull
    String getPermissions();

    double getPrice();

    @Nullable
    QUser getTaxAccount();

    int getType();

    boolean isHologram();

    boolean isUnlimited();

    @NotNull
    String getBenefit();

}
