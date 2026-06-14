package dev.nastechai.pipepipe.extractor.services.peertube;

import dev.nastechai.pipepipe.extractor.search.filter.FilterItem;

import static dev.nastechai.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static dev.nastechai.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.VIDEO;
import static java.util.Arrays.asList;

import dev.nastechai.pipepipe.extractor.StreamingService;
import dev.nastechai.pipepipe.extractor.channel.ChannelExtractor;
import dev.nastechai.pipepipe.extractor.channel.ChannelTabExtractor;
import dev.nastechai.pipepipe.extractor.comments.CommentsExtractor;
import dev.nastechai.pipepipe.extractor.exceptions.ExtractionException;
import dev.nastechai.pipepipe.extractor.exceptions.ParsingException;
import dev.nastechai.pipepipe.extractor.kiosk.KioskList;
import dev.nastechai.pipepipe.extractor.linkhandler.ChannelTabs;
import dev.nastechai.pipepipe.extractor.linkhandler.LinkHandler;
import dev.nastechai.pipepipe.extractor.linkhandler.LinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.linkhandler.ListLinkHandler;
import dev.nastechai.pipepipe.extractor.linkhandler.ListLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.linkhandler.SearchQueryHandler;
import dev.nastechai.pipepipe.extractor.linkhandler.SearchQueryHandlerFactory;
import dev.nastechai.pipepipe.extractor.playlist.PlaylistExtractor;
import dev.nastechai.pipepipe.extractor.search.SearchExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubeAccountExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubeAccountTabExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubeChannelExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubeChannelTabExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubeCommentsExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubePlaylistExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubeSearchExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubeStreamExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubeSuggestionExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.extractors.PeertubeTrendingExtractor;
import dev.nastechai.pipepipe.extractor.services.peertube.linkHandler.PeertubeChannelLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.peertube.linkHandler.PeertubeChannelTabLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.peertube.linkHandler.PeertubeCommentsLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.peertube.linkHandler.PeertubePlaylistLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.peertube.linkHandler.PeertubeSearchQueryHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.peertube.linkHandler.PeertubeStreamLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.peertube.linkHandler.PeertubeTrendingLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.stream.StreamExtractor;
import dev.nastechai.pipepipe.extractor.subscription.SubscriptionExtractor;
import dev.nastechai.pipepipe.extractor.suggestion.SuggestionExtractor;

import java.util.List;
import java.util.Optional;

public class PeertubeService extends StreamingService {

    private PeertubeInstance instance;

    public PeertubeService(final int id) {
        this(id, PeertubeInstance.DEFAULT_INSTANCE);
    }

    public PeertubeService(final int id, final PeertubeInstance instance) {
        super(id, "PeerTube", asList(VIDEO, COMMENTS));
        this.instance = instance;
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return PeertubeStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return PeertubeChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelTabLHFactory() {
        return PeertubeChannelTabLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return PeertubePlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return PeertubeSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return PeertubeCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler queryHandler) {
        final List<FilterItem> selectedSortFilter = queryHandler.getSortFilter();

        final Optional<FilterItem> sepiaFilter = PeertubeHelpers.getSepiaFilter(selectedSortFilter);
        return new PeertubeSearchExtractor(this, queryHandler, sepiaFilter.isPresent());
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return new PeertubeSuggestionExtractor(this);
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {

        if (linkHandler.getUrl().contains("/video-channels/")) {
            return new PeertubeChannelExtractor(this, linkHandler);
        } else {
            return new PeertubeAccountExtractor(this, linkHandler);
        }
    }

    @Override
    public ChannelTabExtractor getChannelTabExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        final String tab = linkHandler.getContentFilters().get(0).getName();
        switch (tab) {
            case ChannelTabs.CHANNELS:
                return new PeertubeAccountTabExtractor(this, linkHandler);
            case ChannelTabs.PLAYLISTS:
                return new PeertubeChannelTabExtractor(this, linkHandler);
        }
        throw new ParsingException("tab " + tab + " not supported");
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return new PeertubePlaylistExtractor(this, linkHandler);
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler)
            throws ExtractionException {
        return new PeertubeStreamExtractor(this, linkHandler);
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return new PeertubeCommentsExtractor(this, linkHandler);
    }

    @Override
    public String getBaseUrl() {
        return instance.getUrl();
    }

    public PeertubeInstance getInstance() {
        return this.instance;
    }

    public void setInstance(final PeertubeInstance instance) {
        this.instance = instance;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList.KioskExtractorFactory kioskFactory = (streamingService, url, id) ->
                new PeertubeTrendingExtractor(
                        PeertubeService.this,
                        new PeertubeTrendingLinkHandlerFactory().fromId(id),
                        id
                );

        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        final PeertubeTrendingLinkHandlerFactory h = new PeertubeTrendingLinkHandlerFactory();
        try {
            list.addKioskEntry(kioskFactory, h, PeertubeTrendingLinkHandlerFactory.KIOSK_TRENDING);
            list.addKioskEntry(kioskFactory, h,
                    PeertubeTrendingLinkHandlerFactory.KIOSK_MOST_LIKED);
            list.addKioskEntry(kioskFactory, h, PeertubeTrendingLinkHandlerFactory.KIOSK_RECENT);
            list.addKioskEntry(kioskFactory, h, PeertubeTrendingLinkHandlerFactory.KIOSK_LOCAL);
            list.setDefaultKiosk(PeertubeTrendingLinkHandlerFactory.KIOSK_TRENDING);
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }


}
