package one.armelin.extratooltips.providers;

import org.bson.BsonDocument;
import org.herolias.tooltips.api.TooltipData;
import org.herolias.tooltips.api.TooltipPriority;
import org.herolias.tooltips.api.TooltipProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SwabTooltipProvider implements TooltipProvider {
    @Nonnull
    @Override
    public String getProviderId() {
        return "extratooltips:mobfarmtools_swab";
    }

    @Override
    public int getPriority() {
        return TooltipPriority.DEFAULT;
    }

    @Nullable
    @Override
    public TooltipData getTooltipData(@Nonnull String itemId, @Nullable String metadata) {
        if (!itemId.equals("Mob_Swab_Used")) return null;
        if (metadata == null || !metadata.contains("SwabbedMob")) return null;
        BsonDocument doc = BsonDocument.parse(metadata);
        String mobId = doc.getDocument("SwabbedMob").getString("MobId").getValue();
        return TooltipData.builder()
                .hashInput("SwabbedMob:" + mobId)
                .addLine("<color is=\"#FFFFFF\"><b>Stored mob: </b> " + mobId + "</color>")
                .build();
    }
}
