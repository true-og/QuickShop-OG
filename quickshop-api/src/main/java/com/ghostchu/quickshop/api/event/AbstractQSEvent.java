package com.ghostchu.quickshop.api.event;

import com.ghostchu.quickshop.api.QuickShopAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractQSEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    protected AbstractQSEvent() {

        super(!Bukkit.isPrimaryThread());

    }

    protected AbstractQSEvent(boolean async) {

        super(async);

    }

    public boolean callCancellableEvent() {

        Bukkit.getPluginManager().callEvent(this);
        if (this instanceof Cancellable cancellable) {

            return cancellable.isCancelled();

        }

        return false;

    }

    @Override
    public boolean callEvent() {

        QuickShopAPI.getPluginInstance().getServer().getPluginManager().callEvent(this);
        if (this instanceof Cancellable cancellable) {

            return !cancellable.isCancelled();

        }

        return true;

    }

    @NotNull
    @Override
    public HandlerList getHandlers() {

        return HANDLERS;

    }

    public static HandlerList getHandlerList() {

        return HANDLERS;

    }

}
