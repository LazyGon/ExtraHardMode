package com.extrahardmode.events.fakeevents;

import java.util.List;
import org.bukkit.ExplosionResult;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author Diemex
 */
public class FakeEntityExplodeEvent extends EntityExplodeEvent {
    public FakeEntityExplodeEvent(Entity what, Location location, List<Block> blocks, float yields) {
        super(what, location, blocks, yields, ExplosionResult.KEEP);
    }
}
