package concerttours.interceptors;

import concerttours.events.BandAlbumSalesEvent;
import concerttours.model.BandModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class BandAlbumSalesInterceptor implements ValidateInterceptor, PrepareInterceptor {
   private static final long BIG_SALES = 50000L;
   private static final long NEGATIVE_SALES = 0L;

   @Autowired
   private EventService eventService;

    @Override
    public void onPrepare(final Object model, final InterceptorContext interceptorContext) throws InterceptorException {
        if(model instanceof BandModel){
            final BandModel band = (BandModel) model;
            if(hasBecomeBig(band, interceptorContext)){
                eventService.publishEvent(new BandAlbumSalesEvent(band.getCode(), band.getName(), band.getAlbumSales()));
            }
        }
    }

    @Override
    public void onValidate(final Object model, final InterceptorContext interceptorContext) throws InterceptorException {
        if(model instanceof BandModel){
            final BandModel band = (BandModel) model;
            final Long sales = band.getAlbumSales();
            if(sales !=null && sales.longValue() < NEGATIVE_SALES){
                throw new InterceptorException("Albums sales must be positive");
            }
        }
    }


    private boolean hasBecomeBig(final BandModel band, final InterceptorContext interceptorContext) {
        final Long sales = band.getAlbumSales();
        if (sales != null && sales.longValue() >= BIG_SALES) {
            if (interceptorContext.isNew(band)) {
                return true;
            } else {
                final Long oldValue = ModelContextUtils.getItemModelContext(band).getOriginalValue(BandModel.ALBUMSALES);
                if (oldValue == null || oldValue.intValue() < BIG_SALES) {
                    return true;
                }
            }

        }
        return false;

    }

}
