package com.extrahardmode.features;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.events.*;
import com.extrahardmode.module.BlockModule;
import com.extrahardmode.module.MessagingModule;
import com.extrahardmode.module.PlayerModule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


/**
 * @author Diemex
 */
public class Tutorial implements Listener
{
    private final ExtraHardMode plugin;

    private final MessagingModule messenger;

    private final BlockModule blockModule;

    private final PlayerModule playerModule;


    public Tutorial(ExtraHardMode plugin)
    {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.messenger = plugin.getModuleForClass(MessagingModule.class);
        this.blockModule = plugin.getModuleForClass(BlockModule.class);
        this.playerModule = plugin.getModuleForClass(PlayerModule.class);
    }

    /**
     * When an Entity targets another Entity
     * <p/>
     * Display some warnings to a Player when he is targetted by a dangerous mob
     */
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event)
    {
        if (event.getTarget() instanceof Player)
        {
            final Player player = (Player) event.getTarget();
            final World world = player.getWorld();

            switch (event.getEntity().getType())
            {
                case CREEPER:
                {
                    Creeper creeper = (Creeper) event.getEntity();
                    if (creeper.isPowered())
                        messenger.sendTutorial(player, MessageNode.CHARGED_CREEPER_TARGET);
                    break;
                }
                case BLAZE:
                {
                    switch (world.getEnvironment())
                    {
                        case NORMAL:
                            messenger.sendTutorial(player, MessageNode.BLAZE_TARGET_NORMAL);
                            break;
                        case NETHER:
                            messenger.sendTutorial(player, MessageNode.BLAZE_TARGET_NETHER);
                            break;
                    }
                    break;
                }
                case GHAST:
                {
                    messenger.sendTutorial(player, MessageNode.GHAST_TARGET);
                    break;
                }
                case PIG_ZOMBIE:
                {
                    messenger.sendTutorial(player, MessageNode.PIGZOMBIE_TARGET);
                    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            messenger.sendTutorial(player, MessageNode.PIGZOMBIE_TARGET_WART);
                        }
                    }, 300L);
                    break;
                }
                case MAGMA_CUBE:
                {
                    messenger.sendTutorial(player, MessageNode.PIGZOMBIE_TARGET);
                    break;
                }
                case SKELETON:
                {
                    //TODO shoot silverfish
                    break;
                }
                case SPIDER:
                {
                    //TODO web
                    break;
                }
                case WITCH:
                {
                    //TODO zombies, poison explosions
                    break;
                }
                case ENDERMAN:
                {
                    //TODO you picked the wrong fight
                    break;
                }
                case ZOMBIE:
                {
                    //TODO SlowPlayers
                    break;
                }
            }
        }
    }


    /**
     * When an Enderman teleports a Player for the first time, cancel the Event and inform the Player of his imminent
     * death
     */
    @EventHandler
    public void onEndermanTeleport(EhmEndermanTeleportEvent event)
    {
        final Player player = event.getPlayer();
        final Enderman enderman = event.getEnderman();

        messenger.sendTutorial(player, MessageNode.ENDERMAN_GENERAL);
        if (playerModule.getArmorPoints(player) < 0.32) //basically leather armor
        {
            enderman.setTarget(null); //give new Players a chance if they don't know yet
            messenger.sendTutorial(player, MessageNode.ENDERMAN_SUICIDAL);
        }
    }


    /**
     * Inform Players about the respawning Zombies
     */
    @EventHandler(ignoreCancelled = true)
    public void onZombieRespawn(EhmZombieRespawnEvent event)
    {
        final Player player = event.getPlayer();
        if (player != null)
        {
            messenger.sendTutorial(player, MessageNode.ZOMBIE_RESPAWN);
        }
    }


    /**
     * Warn players before entering the nether
     */
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event)
    {
        final Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER)
        {
            messenger.sendTutorial(player, MessageNode.NETHER_WARNING);
        }
    }


    /**
     * Inform Players about creepers dropping tnt
     */
    @EventHandler
    public void onCreeperDropTnt(EhmCreeperDropTntEvent event)
    {
        final Player player = event.getPlayer();
        if (player != null)
        {
            messenger.sendTutorial(player, MessageNode.CREEPER_DROP_TNT);
        }
    }


    /**
     * When a Skeleton deflects an arrow
     */
    @EventHandler(ignoreCancelled = true)
    public void onSkeletonDeflect(EhmSkeletonDeflectEvent event)
    {
        if (event.getShooter() != null)
        {
            final Player player = event.getShooter();
            messenger.sendTutorial(player, MessageNode.SKELETON_DEFLECT);
        }
    }


    /**
     * Let Players know that they can use ice
     */
    @EventHandler
    public void onPlayerFillBucket(PlayerBucketFillEvent event)
    {
        final Player player = event.getPlayer();
        messenger.sendTutorial(player, MessageNode.BUCKET_FILL);
    }


    /**
     * Messages when planting with antifarming
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        //Too dark
        if (block.getType() == Material.SOIL)
        {
            Block above = block.getRelative(BlockFace.UP);
            if (above.getLightFromSky() < 10)
            {
                messenger.sendTutorial(player, MessageNode.ANTIFARMING_NO_LIGHT);
            }
        }

        Block below = block.getRelative(BlockFace.DOWN);

        //Unwatered
        if (blockModule.isPlant(block.getType()) && below.getState().getData().getData() == (byte) 0)
        {
            messenger.sendTutorial(player, MessageNode.ANTIFARMING_UNWATERD);
        }

        //Warn players before they build big farms in the desert
        if (block.getType() == Material.DIRT)
        {
            switch (block.getBiome())
            {
                case DESERT:
                case DESERT_HILLS:
                {
                    messenger.sendTutorial(player, MessageNode.ANTIFARMING_DESSERT_WARNING);
                    break;
                }
            }
        }
    }


    /**
     * Inform about Silverfish
     */
    @EventHandler
    public void onShootSilverfish(EhmSkeletonShootSilverfishEvent event)
    {
        event.getSilverfish().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 1, 1000));
        //event.getSilverfish().setFireTicks(100);
    }


    /**
     * Inform about not being able to extinguish fire with bare hands
     */
    @EventHandler
    public void onExtinguishFire(EhmPlayerExtinguishFireEvent event)
    {
        messenger.sendTutorial(event.getPlayer(), MessageNode.EXTINGUISH_FIRE);
    }

    //TODO Farming: NetherWart, Mushrooms

    //TODO OnSheepDye
}
