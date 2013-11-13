package org.bahmni.feed.openerp.client;

import org.bahmni.feed.openerp.OpenERPAtomFeedProperties;
import org.bahmni.feed.openerp.worker.WorkerFactory;
import org.bahmni.openerp.web.client.OpenERPClient;
import org.ict4h.atomfeed.client.factory.AtomFeedProperties;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FeedClientFactory {

    private OpenMRSWebClient webClient;

    public FeedClientFactory(OpenMRSWebClient webClient){
        this.webClient = webClient;
    }

    public AtomFeedClient getFeedClient(OpenERPAtomFeedProperties atomFeedProperties,JdbcConnectionProvider jdbcConnectionProvider,
                                        String feedName, OpenERPClient openERPClient, AllFeeds allFeeds, AllMarkers allMarkers, AllFailedEvents allFailedEvents, String jobName)  {
        String feedUri = atomFeedProperties.getFeedUri(feedName);
        try {
            String urlPrefix = getURLPrefix(atomFeedProperties);
            EventWorker eventWorker = new WorkerFactory(webClient).getWorker(jobName, atomFeedProperties.getFeedUri(feedName),openERPClient,
                    urlPrefix);
            return new AtomFeedClient(allFeeds, allMarkers, allFailedEvents, atomFeedProperties(), jdbcConnectionProvider, new URI(feedUri), eventWorker) ;
        } catch (URISyntaxException e) {
            throw new RuntimeException("error for uri:" + feedUri);
        }
    }

    static String getURLPrefix(OpenERPAtomFeedProperties atomFeedProperties) {
        String authenticationURI = atomFeedProperties.getAuthenticationURI();
        try {
            URL openMRSAuthURL = new URL(authenticationURI);
            return String.format("%s://%s", openMRSAuthURL.getProtocol(), openMRSAuthURL.getAuthority());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Is not a valid URI - " + authenticationURI);
        }
    }


    static AtomFeedProperties atomFeedProperties() {
        AtomFeedProperties atomFeedProperties = new AtomFeedProperties();
        atomFeedProperties.setControlsEventProcessing(true);
        return atomFeedProperties;
    }

}