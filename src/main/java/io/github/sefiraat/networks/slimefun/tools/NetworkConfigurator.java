package io.github.sefiraat.networks.slimefun.tools;

import com.jeff_media.morepersistentdatatypes.DataType;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.api.enums.TransportMode;
import com.ytdd9527.networksexpansion.core.items.machines.AdvancedDirectional;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.NetworkUtils;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.sefiraat.networks.utils.Theme;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class NetworkConfigurator extends SlimefunItem {

    public NetworkConfigurator(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
        addItemHandler(
                (ItemUseHandler) e -> {
                    final Player player = e.getPlayer();
                    final Optional<Block> optional = e.getClickedBlock();
                    if (optional.isPresent()) {
                        final Block block = optional.get();
                        final SlimefunItem slimefunItem = StorageCacheUtils.getSfItem(block.getLocation());

                        if (Slimefun.getProtectionManager().hasPermission(player, block, Interaction.INTERACT_BLOCK)) {
                            if (slimefunItem instanceof NetworkDirectional directional) {
                                final BlockMenu blockMenu = StorageCacheUtils.getMenu(block.getLocation());
                                if (blockMenu == null) {
                                    return;
                                }
                                if (player.isSneaking()) {
                                    if (slimefunItem instanceof AdvancedDirectional advancedDirectional) {
                                        ItemMeta itemMeta = e.getItem().getItemMeta();
                                        int amount = advancedDirectional.getCurrentNumber(blockMenu.getLocation());
                                        DataTypeMethods.setCustom(itemMeta, Keys.AMOUNT, DataType.INTEGER, amount);
                                        player.sendMessage(Theme.SUCCESS + "已保存传输数量为 " + amount);
                                        TransportMode transportMode = advancedDirectional.getCurrentTransportMode(blockMenu.getLocation());
                                        DataTypeMethods.setCustom(itemMeta, Keys.TRANSFER_MODE, DataType.STRING, String.valueOf(transportMode));
                                        player.sendMessage(Theme.SUCCESS + "已保存传输模式为 " + transportMode);
                                        e.getItem().setItemMeta(itemMeta);
                                    }
                                    setConfigurator(directional, e.getItem(), blockMenu, player);
                                } else {
                                    if (slimefunItem instanceof AdvancedDirectional advancedDirectional) {
                                        ItemMeta itemMeta = e.getItem().getItemMeta();
                                        Integer amount = DataTypeMethods.getCustom(itemMeta, Keys.AMOUNT, DataType.INTEGER);
                                        if (amount != null) {
                                            advancedDirectional.setCurrentNumber(blockMenu.getLocation(), amount);
                                            player.sendMessage(Theme.SUCCESS + "已设置传输数量为 " + amount);
                                        }
                                        String transportMode = DataTypeMethods.getCustom(itemMeta, Keys.TRANSFER_MODE, DataType.STRING);
                                        if (transportMode != null) {
                                            advancedDirectional.setTransportMode(blockMenu.getLocation(), TransportMode.valueOf(transportMode));
                                            player.sendMessage(Theme.SUCCESS + "已设置传输模式为 " + transportMode);
                                        }
                                    }
                                    NetworkUtils.applyConfig(directional, e.getItem(), blockMenu, player);
                                }
                            } else {
                                player.sendMessage(Theme.ERROR + "你必须指向一个带方向选择的网络方块");
                            }
                        }
                    }
                    e.cancel();
                }
        );
    }

    private void setConfigurator(@Nonnull NetworkDirectional directional, @Nonnull ItemStack itemStack, @Nonnull BlockMenu blockMenu, @Nonnull Player player) {
        BlockFace blockFace = NetworkDirectional.getSelectedFace(blockMenu.getLocation());
        if (blockFace == null) {
            blockFace = AdvancedDirectional.getSelectedFace(blockMenu.getLocation());
        }
        if (blockFace == null) {
            player.sendMessage(Theme.ERROR + "该方块没有指定朝向");
            return;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (directional.getItemSlots().length > 0) {
            final ItemStack[] itemStacks = new ItemStack[directional.getItemSlots().length];

            int i = 0;
            for (int slot : directional.getItemSlots()) {
                final ItemStack possibleStack = blockMenu.getItemInSlot(slot);
                if (possibleStack != null) {
                    itemStacks[i] = StackUtils.getAsQuantity(blockMenu.getItemInSlot(slot), 1);
                }
                i++;
            }
            DataTypeMethods.setCustom(itemMeta, Keys.ITEM, DataType.ITEM_STACK_ARRAY, itemStacks);
        } else {
            PersistentDataAPI.remove(itemMeta, Keys.ITEM);
        }

        DataTypeMethods.setCustom(itemMeta, Keys.FACE, DataType.STRING, blockFace.name());
        itemStack.setItemMeta(itemMeta);
        player.sendMessage(Theme.SUCCESS + "已复制设置");
    }
}