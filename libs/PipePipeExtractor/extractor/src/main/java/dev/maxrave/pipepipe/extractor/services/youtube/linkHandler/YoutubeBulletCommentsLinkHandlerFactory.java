package dev.nastechai.pipepipe.extractor.services.youtube.linkHandler;

import dev.nastechai.pipepipe.extractor.exceptions.ParsingException;
import dev.nastechai.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.search.filter.FilterItem;

import java.util.List;

public class YoutubeBulletCommentsLinkHandlerFactory extends ListLinkHandlerFactory {
    YoutubeStreamLinkHandlerFactory factory = new YoutubeStreamLinkHandlerFactory();
    @Override
    public String getId(String url) throws ParsingException {
        return factory.getId(url);
    }

    @Override
    public boolean onAcceptUrl(String url) throws ParsingException {
        return factory.onAcceptUrl(url);
    }

    @Override
    public String getUrl(String id, List<FilterItem> contentFilter, List<FilterItem> sortFilter) throws ParsingException {
        return factory.getUrl(id);
    }
}
