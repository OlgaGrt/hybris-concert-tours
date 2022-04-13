package concerttours.facades.impl;

import concerttours.data.BandData;
import concerttours.data.TourSummaryData;
import concerttours.enums.MusicType;
import concerttours.facades.BandFacade;
import concerttours.model.BandModel;
import concerttours.service.BandService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DefaultBandFacade implements BandFacade {

    public static final  String BAND_LIST_FORMAT="band.list.format.name";
    private static final String BAND_DETAIL_FORMAT="band.detail.format.name";
    private BandService bandService;
    private MediaService mediaService;
    private ConfigurationService configurationService;

    @Override
    public BandData getBand(final String name) {
        if (name==null) {
            throw new IllegalArgumentException("Band name can not be null");
        }
        final BandModel band = bandService.getBandForCode(name);
        if(band==null){
            return null;
        }

        final List<String> genres = new ArrayList<>();
        if(band.getTypes() != null){
            for(final MusicType musicType: band.getTypes()){
                genres.add(musicType.getCode());
            }
        }

        final List<TourSummaryData> tourHistory = new ArrayList<>();
        if (band.getTours() != null){
            for(final ProductModel tour : band.getTours()){
                final TourSummaryData summary = new TourSummaryData();
                summary.setId(tour.getCode());
                summary.setTourName(tour.getName(Locale.ENGLISH));
                summary.setNumberOfConcerts(Integer.toString(tour.getVariants().size()));
                tourHistory.add(summary);
            }
        }

        final String mediaFormatName = configurationService.getConfiguration().getString(BAND_DETAIL_FORMAT);
        final BandData bandData = new BandData();
        final MediaFormatModel format = mediaService.getFormat(mediaFormatName);
        bandData.setId(band.getCode());
        bandData.setName(band.getName());
        bandData.setAlbumsSold(band.getAlbumSales());
        bandData.setDescription(band.getHistory());
        bandData.setGenres(genres);
        bandData.setTours(tourHistory);
        bandData.setImageURL(getImageURL(band, format));
        return bandData;
    }

    @Override
    public List<BandData> getBands() {
        final List<BandModel> bandModels = bandService.getBands();
        final List<BandData> bandFacadeData = new ArrayList<>();
        final String mediaFormatName = configurationService.getConfiguration().getString(BAND_LIST_FORMAT);
        final MediaFormatModel format = mediaService.getFormat(mediaFormatName);
        for(final BandModel bm:bandModels){
            final BandData bd = new BandData();
            bd.setId(bm.getCode());
            bd.setName(bm.getName());
            bd.setDescription(bm.getHistory());
            bd.setAlbumsSold(bm.getAlbumSales());
            bd.setImageURL(getImageURL(bm, format));
            bandFacadeData.add(bd);
        }
        return bandFacadeData;
    }

    protected String getImageURL(final BandModel bm, final MediaFormatModel fm){
        final MediaContainerModel container = bm.getImage();
        if(container!=null){
            return mediaService.getMediaByFormat(container, fm).getDownloadURL();
        }
        return null;
    }

    @Required
    public void setBandService(final BandService bandService) {
        this.bandService = bandService;
    }

    @Required
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
