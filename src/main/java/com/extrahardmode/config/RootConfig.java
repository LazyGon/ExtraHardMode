/*
 * This file is part of
 * ExtraHardMode Server Plugin for Minecraft
 *
 * Copyright (C) 2012 Ryan Hamshire
 * Copyright (C) 2013 Diemex
 *
 * ExtraHardMode is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ExtraHardMode is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero Public License
 * along with ExtraHardMode.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.extrahardmode.config;

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.service.config.ConfigNode;
import com.extrahardmode.service.config.Header;
import com.extrahardmode.service.config.MultiWorldConfig;
import com.extrahardmode.service.config.YamlCommentWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 */
public class RootConfig extends MultiWorldConfig {

    public RootConfig(ExtraHardMode plugin) {
        super(plugin);
    }

    @Override
    public void starting() {
        load();
    }

    @Override
    public void closing() {
    }

    @Override
    public void load() {
        init();

        Path mainFile = plugin.getDataFolder().toPath().resolve("config.yml");
        if (Files.notExists(mainFile)) {
            try {
                Files.createDirectories(mainFile.getParent());
                Files.createFile(mainFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't create config.yml", e);
            }
        }

        EHMConfig mainEhmConfig = new EHMConfig(mainFile.toFile());
        mainEhmConfig.registerNodes(RootNode.values());
        mainEhmConfig.load();

        // Load config.yml
        if (mainEhmConfig.isEnabledForAll()) {
            enabledForAll = true;
        }
        for (Map.Entry<ConfigNode, Object> node : mainEhmConfig.getLoadedNodes().entrySet()) {
            for (String world : mainEhmConfig.getWorlds()) {
                set(world, node.getKey(), node.getValue());
            }
        }
        // Save files
        mainEhmConfig.setHeader(createHeader());
        mainEhmConfig.save();

        // Prepare comments
        Map<String, String[]> comments = new HashMap<>();
        for (RootNode node : RootNode.values()) {
            if (node.getComments() != null) {
                comments.put(node.getPath(), node.getComments());
            }
        }

        if (mainEhmConfig.printComments()) {
            YamlCommentWriter.write(mainEhmConfig.getConfigFile(), comments);
        }
    }

    private Header createHeader() {
        Header header = new Header();
        header.setHeading("ExtraHardMode Config");
        String[] lines = new String[] {
                "",
                "1. The config cleans itself, so if something resets you probably did something wrong",
                "2. Generally if you can specify a block you can add meta after an @",
                "   F.e: STEP@3 = cobblestone slab. STEP@3,11 matches normal&upside cobble slabs",
                "   If you specify meta it will only match cobble slabs and not the other slabs.",
                "   If you don't specify meta it matches all slabs.",
                "   You can use numerical block ids as well, they will be converted to bukkit names",
                "3. If your empty lists reset, put [] instead",
                "4. This config changes regularly, so you might want to revisit it after an update.",
                "5. Lots of the configuration is user requested so if you need something just ask",
                "6. Remember to use /ehm reload after you changed the config instead of /reload",
                "",
                "Happy Configuring!"};
        header.addLines(lines);
        return header;
    }

}