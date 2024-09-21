package com.extrahardmode;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;

/**
 * Created on 10/17/2018.
 *
 * @author RoboMWM
 */
public enum LooseTags {
    TORCH;

    private final Set<Material> materials = new HashSet<>();

    LooseTags() {
        for (Material material : Material.values()) {
            if (material.name().contains(this.name()) && !material.name().contains("LEGACY")) {
                materials.add(material);
            }
        }
    }

    public boolean isTagged(Material material) {
        return materials.contains(material);
    }
}
