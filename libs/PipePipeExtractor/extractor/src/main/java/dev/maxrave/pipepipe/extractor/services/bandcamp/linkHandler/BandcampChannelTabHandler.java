package dev.nastechai.pipepipe.extractor.services.bandcamp.linkHandler;

import com.grack.nanojson.JsonArray;
import dev.nastechai.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.nastechai.pipepipe.extractor.search.filter.Filter;
import dev.nastechai.pipepipe.extractor.search.filter.FilterItem;

import java.util.Collections;

public class BandcampChannelTabHandler extends ListLinkHandler {
    private final JsonArray discographs;

    public BandcampChannelTabHandler(final String url, final String id, final String tab,
                                     final JsonArray discographs) {
        super(url, url, id, Collections.singletonList(new FilterItem(Filter.ITEM_IDENTIFIER_UNKNOWN, tab)), null);
        this.discographs = discographs;
    }

    public JsonArray getDiscographs() {
        return discographs;
    }
}
