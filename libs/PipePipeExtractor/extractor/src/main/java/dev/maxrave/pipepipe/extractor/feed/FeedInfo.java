package dev.nastechai.pipepipe.extractor.feed;

import dev.nastechai.pipepipe.extractor.search.filter.FilterItem;

import dev.nastechai.pipepipe.extractor.ListExtractor.InfoItemsPage;
import dev.nastechai.pipepipe.extractor.ListInfo;
import dev.nastechai.pipepipe.extractor.NewPipe;
import dev.nastechai.pipepipe.extractor.StreamingService;
import dev.nastechai.pipepipe.extractor.exceptions.ExtractionException;
import dev.nastechai.pipepipe.extractor.stream.StreamInfoItem;
import dev.nastechai.pipepipe.extractor.utils.ExtractorHelper;


import java.io.IOException;
import java.util.List;

public class FeedInfo extends ListInfo<StreamInfoItem> {

    public FeedInfo(final int serviceId,
                    final String id,
                    final String url,
                    final String originalUrl,
                    final String name,
                    final List<FilterItem> contentFilter,
                    final List<FilterItem> sortFilter) {
        super(serviceId, id, url, originalUrl, name, contentFilter, sortFilter);
    }

    public static FeedInfo getInfo(final String url) throws IOException, ExtractionException {
        return getInfo(NewPipe.getServiceByUrl(url), url);
    }

    public static FeedInfo getInfo(final StreamingService service, final String url)
            throws IOException, ExtractionException {
        final FeedExtractor extractor = service.getFeedExtractor(url);

        if (extractor == null) {
            throw new IllegalArgumentException("Service \"" + service.getServiceInfo().getName()
                    + "\" doesn't support FeedExtractor.");
        }

        extractor.fetchPage();
        return getInfo(extractor);
    }

    public static FeedInfo getInfo(final FeedExtractor extractor)
            throws IOException, ExtractionException {
        extractor.fetchPage();

        final int serviceId = extractor.getServiceId();
        final String id = extractor.getId();
        final String url = extractor.getUrl();
        final String originalUrl = extractor.getOriginalUrl();
        final String name = extractor.getName();

        final FeedInfo info = new FeedInfo(serviceId, id, url, originalUrl, name, null, null);

        final InfoItemsPage<StreamInfoItem> itemsPage
                = ExtractorHelper.getItemsPageOrLogError(info, extractor);
        info.setRelatedItems(itemsPage.getItems());
        info.setNextPage(itemsPage.getNextPage());

        return info;
    }
}
