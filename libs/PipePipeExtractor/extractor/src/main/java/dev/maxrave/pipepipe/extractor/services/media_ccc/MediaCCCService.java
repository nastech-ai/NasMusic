package dev.nastechai.pipepipe.extractor.services.media_ccc;

import dev.nastechai.pipepipe.extractor.StreamingService;
import dev.nastechai.pipepipe.extractor.channel.ChannelExtractor;
import dev.nastechai.pipepipe.extractor.channel.ChannelTabExtractor;
import dev.nastechai.pipepipe.extractor.comments.CommentsExtractor;
import dev.nastechai.pipepipe.extractor.exceptions.ExtractionException;
import dev.nastechai.pipepipe.extractor.kiosk.KioskList;
import dev.nastechai.pipepipe.extractor.linkhandler.LinkHandler;
import dev.nastechai.pipepipe.extractor.linkhandler.LinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.nastechai.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.linkhandler.SearchQueryHandler;
import dev.nastechai.pipepipe.extractor.linkhandler.SearchQueryHandlerFactory;
import dev.nastechai.pipepipe.extractor.playlist.PlaylistExtractor;
import dev.nastechai.pipepipe.extractor.search.SearchExtractor;
import dev.nastechai.pipepipe.extractor.services.media_ccc.extractors.MediaCCCConferenceExtractor;
import dev.nastechai.pipepipe.extractor.services.media_ccc.extractors.MediaCCCConferenceKiosk;
import dev.nastechai.pipepipe.extractor.services.media_ccc.extractors.MediaCCCLiveStreamExtractor;
import dev.nastechai.pipepipe.extractor.services.media_ccc.extractors.MediaCCCLiveStreamKiosk;
import dev.nastechai.pipepipe.extractor.services.media_ccc.extractors.MediaCCCParsingHelper;
import dev.nastechai.pipepipe.extractor.services.media_ccc.extractors.MediaCCCRecentKiosk;
import dev.nastechai.pipepipe.extractor.services.media_ccc.extractors.MediaCCCSearchExtractor;
import dev.nastechai.pipepipe.extractor.services.media_ccc.extractors.MediaCCCStreamExtractor;
import dev.nastechai.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCConferenceLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCConferencesListLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCLiveListLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCRecentListLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCSearchQueryHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.media_ccc.linkHandler.MediaCCCStreamLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.stream.StreamExtractor;
import dev.nastechai.pipepipe.extractor.subscription.SubscriptionExtractor;
import dev.nastechai.pipepipe.extractor.suggestion.SuggestionExtractor;

import static java.util.Arrays.asList;
import static dev.nastechai.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static dev.nastechai.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.VIDEO;

public class MediaCCCService extends StreamingService {
    public MediaCCCService(final int id) {
        super(id, "media.ccc.de", asList(AUDIO, VIDEO));
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler query) {
        return new MediaCCCSearchExtractor(this, query);
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return new MediaCCCStreamLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return new MediaCCCConferenceLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getChannelTabLHFactory() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return null;
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return new MediaCCCSearchQueryHandlerFactory();
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        if (MediaCCCParsingHelper.isLiveStreamId(linkHandler.getId())) {
            return new MediaCCCLiveStreamExtractor(this, linkHandler);
        }
        return new MediaCCCStreamExtractor(this, linkHandler);
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new MediaCCCConferenceExtractor(this, linkHandler);
    }

    @Override
    public ChannelTabExtractor getChannelTabExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return null;
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        return null;
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return null;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        try {
            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCConferenceKiosk(
                            MediaCCCService.this,
                            new MediaCCCConferencesListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCConferencesListLinkHandlerFactory(),
                    "conferences"
            );

            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCRecentKiosk(
                            MediaCCCService.this,
                            new MediaCCCRecentListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCRecentListLinkHandlerFactory(),
                    "recent"
            );

            list.addKioskEntry(
                    (streamingService, url, kioskId) -> new MediaCCCLiveStreamKiosk(
                            MediaCCCService.this,
                            new MediaCCCLiveListLinkHandlerFactory().fromUrl(url),
                            kioskId
                    ),
                    new MediaCCCLiveListLinkHandlerFactory(),
                    "live"
            );

            list.setDefaultKiosk("recent");
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return null;
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler) {
        return null;
    }

    @Override
    public String getBaseUrl() {
        return "https://media.ccc.de";
    }

}
