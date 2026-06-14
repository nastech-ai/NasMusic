package dev.nastechai.pipepipe.extractor.services.bilibili.linkHandler;

import dev.nastechai.pipepipe.extractor.exceptions.ParsingException;
import dev.nastechai.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.search.filter.FilterItem;
import dev.nastechai.pipepipe.extractor.services.bilibili.WatchDataCache;
import dev.nastechai.pipepipe.extractor.services.bilibili.linkHandler.BilibiliStreamLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.bilibili.utils;

import java.util.List;

public class BilibiliBulletCommentsLinkHandlerFactory extends ListLinkHandlerFactory {

    @Override
    public String getId(String url) throws ParsingException {
        return new BilibiliStreamLinkHandlerFactory().getId(url);
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        try {
            getId(url);
            return true;
        } catch (final ParsingException e) {
            return false;
        }
    }

    @Override
    public String getUrl(String id, final List<FilterItem> contentFilter,
                         final List<FilterItem> sortFilter) throws ParsingException {
        return new BilibiliStreamLinkHandlerFactory().getUrl(id);
    }
}
