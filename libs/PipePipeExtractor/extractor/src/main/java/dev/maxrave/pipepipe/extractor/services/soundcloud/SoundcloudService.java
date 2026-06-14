package dev.nastechai.pipepipe.extractor.services.soundcloud;

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
import dev.nastechai.pipepipe.extractor.localization.ContentCountry;
import dev.nastechai.pipepipe.extractor.playlist.PlaylistExtractor;
import dev.nastechai.pipepipe.extractor.search.SearchExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.extractors.SoundcloudChannelExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.extractors.SoundcloudChannelTabExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.extractors.SoundcloudChartsExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.extractors.SoundcloudCommentsExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.extractors.SoundcloudPlaylistExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.extractors.SoundcloudSearchExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.extractors.SoundcloudStreamExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.extractors.SoundcloudSubscriptionExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.extractors.SoundcloudSuggestionExtractor;
import dev.nastechai.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudChannelLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudChannelTabLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudChartsLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudCommentsLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudPlaylistLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudSearchQueryHandlerFactory;
import dev.nastechai.pipepipe.extractor.services.soundcloud.linkHandler.SoundcloudStreamLinkHandlerFactory;
import dev.nastechai.pipepipe.extractor.stream.StreamExtractor;
import dev.nastechai.pipepipe.extractor.subscription.SubscriptionExtractor;

import java.util.List;

import static java.util.Arrays.asList;
import static dev.nastechai.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.AUDIO;
import static dev.nastechai.pipepipe.extractor.StreamingService.ServiceInfo.MediaCapability.COMMENTS;

public class SoundcloudService extends StreamingService {

    public SoundcloudService(final int id) {
        super(id, "SoundCloud", asList(AUDIO, COMMENTS));
    }

    @Override
    public String getBaseUrl() {
        return "https://soundcloud.com";
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        return SoundcloudSearchQueryHandlerFactory.getInstance();
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return SoundcloudStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return SoundcloudChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelTabLHFactory() {
        return SoundcloudChannelTabLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return SoundcloudPlaylistLinkHandlerFactory.getInstance();
    }

    @Override
    public List<ContentCountry> getSupportedCountries() {
        // Country selector here: https://soundcloud.com/charts/top?genre=all-music
        return ContentCountry.listFrom(
                "AU", "CA", "DE", "FR", "GB", "IE", "NL", "NZ", "US"
        );
    }

    @Override
    public StreamExtractor getStreamExtractor(final LinkHandler linkHandler) {
        return new SoundcloudStreamExtractor(this, linkHandler);
    }

    @Override
    public ChannelExtractor getChannelExtractor(final ListLinkHandler linkHandler) {
        return new SoundcloudChannelExtractor(this, linkHandler);
    }

    @Override
    public ChannelTabExtractor getChannelTabExtractor(final ListLinkHandler linkHandler) {
        return new SoundcloudChannelTabExtractor(this, linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(final ListLinkHandler linkHandler) {
        return new SoundcloudPlaylistExtractor(this, linkHandler);
    }

    @Override
    public SearchExtractor getSearchExtractor(final SearchQueryHandler queryHandler) {
        return new SoundcloudSearchExtractor(this, queryHandler);
    }

    @Override
    public SoundcloudSuggestionExtractor getSuggestionExtractor() {
        return new SoundcloudSuggestionExtractor(this);
    }
    @Override
    public KioskList getKioskList() throws ExtractionException {
        final KioskList.KioskExtractorFactory chartsFactory = (streamingService, url, id) ->
                new SoundcloudChartsExtractor(SoundcloudService.this,
                        new SoundcloudChartsLinkHandlerFactory().fromUrl(url), id);

        final KioskList list = new KioskList(this);

        // add kiosks here e.g.:
        final SoundcloudChartsLinkHandlerFactory h = new SoundcloudChartsLinkHandlerFactory();
        try {
            list.addKioskEntry(chartsFactory, h, "Top 50");
            list.addKioskEntry(chartsFactory, h, "New & hot");
            list.setDefaultKiosk("New & hot");
        } catch (final Exception e) {
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return new SoundcloudSubscriptionExtractor(this);
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return SoundcloudCommentsLinkHandlerFactory.getInstance();
    }

    @Override
    public CommentsExtractor getCommentsExtractor(final ListLinkHandler linkHandler)
            throws ExtractionException {
        return new SoundcloudCommentsExtractor(this, linkHandler);
    }
}
