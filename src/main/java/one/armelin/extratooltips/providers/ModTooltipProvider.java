package one.armelin.extratooltips.providers;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import one.armelin.extratooltips.ExtraTooltips;
import org.herolias.tooltips.api.TooltipData;
import org.herolias.tooltips.api.TooltipPriority;
import org.herolias.tooltips.api.TooltipProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModTooltipProvider implements TooltipProvider {
    @Nonnull
    @Override
    public String getProviderId() {
        return "extratooltips:mod";
    }

    @Override
    public int getPriority() {
        return TooltipPriority.LAST;
    }

    @Nullable
    @Override
    public TooltipData getTooltipData(@Nonnull String itemId, @Nullable String metadata) {
        String assetPackId = Item.getAssetMap().getAssetPack(itemId);
        if (assetPackId != null) {
            AssetPack assetPack = AssetModule.get().getAssetPack(assetPackId);
            if (assetPack != null) {
                PluginManifest manifest = assetPack.getManifest();
                String modName = manifest.getName();
                if (modName.equals("Hytale") && !ExtraTooltips.config.showVanillaModLabel) {
                    return null;// Skip mod label for vanilla items if disabled in config
                }
                return TooltipData.builder()
                        .hashInput("mod:" + modName)
                        .addLine("<color is=\"#AAAAAA\"><b>Mod: </b> " + modName + "</color>")
                        .build();
            }
        }
        return null;
    }
}
