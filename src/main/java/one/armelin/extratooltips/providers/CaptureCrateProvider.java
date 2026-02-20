package one.armelin.extratooltips.providers;

import org.bson.BsonDocument;
import org.herolias.tooltips.api.TooltipData;
import org.herolias.tooltips.api.TooltipPriority;
import org.herolias.tooltips.api.TooltipProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CaptureCrateProvider implements TooltipProvider {
    @Nonnull
    @Override
    public String getProviderId() {
        return "extratooltips:capture_crate";
    }

    @Override
    public int getPriority() {
        return TooltipPriority.DEFAULT;
    }

    @Nullable
    @Override
    public TooltipData getTooltipData(@Nonnull String itemId, @Nullable String metadata) {
        if (!itemId.equals("Tool_Capture_Crate")) return null;
        if (metadata == null || !metadata.contains("NpcNameKey")) return null;
        BsonDocument doc = BsonDocument.parse(metadata);
        String mobId = doc.getDocument("CapturedEntity").getString("NpcNameKey").getValue();
        return TooltipData.builder()
                .hashInput("NpcNameKey:" + mobId)
                .addLine("<color is=\"#FFFFFF\"><b>Stored mob: </b> " + mobId + "</color>")
                .build();
    }
}
