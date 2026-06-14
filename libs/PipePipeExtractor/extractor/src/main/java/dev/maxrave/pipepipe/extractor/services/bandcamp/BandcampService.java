// Created by Fynn Godau 2019, licensed GNU GPL version 3 or later

package dev.nastechai.pipepipe.extractor.services.bandcamp;

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
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampChannelExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampChannelTabExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampCommentsExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampExtractorHelper;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampFeaturedExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampPlaylistExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampRadioExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampRadioStreamExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampSearchExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampStreamExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampSuggestionExtractor;
import dev.nastechai.pipepipe.extractor.services.bandcamp.linkHandler.BandcampChannelLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.bandcamp.linkHandler.BandcampChannelTabLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.bandcamp.linkHandler.BandcampCommentsLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.bandcamp.linkHandler.BandcampFeaturedLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.bandcamp.linkHandler.BandcampPlaylistLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.bandcamp.linkHandler.BandcampSearchQueryHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.bandcamp.linkHandler.BandcampStreamLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.stream.StreamExtractor;
import dev.nastechai.pipepipe.extractor.subscription.SubscriptionExtractor;
import dev.nastechai.pipepipe.extractor.suggestion.SuggestionExtractor;

import java.util.Arrays;

import static dev.nastechai.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static dev.nastechai.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.COMMENTS;
import static dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampExtractorHelper.BASE_URL;
import static dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampFeaturedExtractor.FEATURED_API_URL;
import static dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampFeaturedExtractor.KIOSK_FEATURED;
import static dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampRadioExtractor.KIOSK_RADIO;
import static dev.nastechai.pipepipe.extractor.services.bandcamp.extractors.BandcampRadioExtractor.RADIO_API_URL;

public class BandcampService extends StreamingService {

    public BandcampService(final int id) {
        super(id, "Bandcamp", Arrays.asList(AUDIO, COMMENTS));
    }

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return new BandcampStreamLinkHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return BandcampChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelTabLHFactory() {
        return BandcampChannelTabLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return new BandcampPlaylistLinkHandlerFactory();
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return new BandcampSearchQueryHandlerFactory();
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return new BandcampCommentsLinkHandlerFactory();
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler queryHandler) {
        return new BandcampSearchExtractor(this, queryHandler);
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return new BandcampSuggestionExtractor(this);
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {

        final KioskList kioskList = new KioskList(this);

        try {
            kioskList.addKioskEntry(
                    (streamingService, url, kioskId) -> new BandcampFeaturedExtractor(
                            BandcampService.this,
                            new BandcampFeaturedLinkHandlerFactory().fromUrl(FEATURED_API_URL),
                            kioskId
                    ),
                    new BandcampFeaturedLinkHandlerFactory(),
                    KIOSK_FEATURED
            );

            kioskList.addKioskEntry(
                    (streamingService, url, kioskId) -> new BandcampRadioExtractor(
                            BandcampService.this,
                            new BandcampFeaturedLinkHandlerFactory().fromUrl(RADIO_API_URL),
                            kioskId
                    ),
                    new BandcampFeaturedLinkHandlerFactory(),
                    KIOSK_RADIO
            );

            kioskList.setDefaultKiosk(KIOSK_FEATURED);

        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return kioskList;
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new BandcampChannelExtractor(this, linkHandler);
    }

    @Override
    public ChannelTabExtractor getChannelTabExtractor(final ListLinkHandler linkHandler) {
        return new BandcampChannelTabExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        return new BandcampPlaylistExtractor(this, linkHandler);
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        if (BandcampExtractorHelper.isRadioUrl(linkHandler.getUrl())) {
            return new BandcampRadioStreamExtractor(this, linkHandler);
        }
        return new BandcampStreamExtractor(this, linkHandler);
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler) {
        return new BandcampCommentsExtractor(this, linkHandler);
    }
}
