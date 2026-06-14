package dev.nastechai.pipepipe.extractor.channel;

import dev.nastechai.pipepipe.extractor.InfoItem;
import dev.nastechai.pipepipe.extractor.ListExtractor;
import dev.nastechai.pipepipe.extractor.StreamingService;
import dev.nastechai.pipepipe.extractor.exceptions.ParsingException;
import dev.nastechai.pipepipe.extractor.linkhandler.ListLinkHandler;

import javax.annotation.Nonnull;

public abstract class ChannelTabExtractor extends ListExtractor<InfoItem> {

    public ChannelTabExtractor(final StreamingService service,
                               final ListLinkHandler linkHandler) {
        super(service, linkHandler);
    }

    @Nonnull
    public String getTab() {
        return getLinkHandler().getContentFilters().get(0).getName();
    }

    @Nonnull
    @Override
    public String getName() throws ParsingException {
        return getTab();
    }

}
