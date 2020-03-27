package com.example.concordia_campus_guide.Helper.RoutesHelpers;

import android.os.AsyncTask;
import android.util.Log;
import com.example.concordia_campus_guide.ClassConstants;
import com.example.concordia_campus_guide.GoogleMapsServicesTools.GoogleMapsServicesModels.DirectionsResult;
import com.example.concordia_campus_guide.GoogleMapsServicesTools.GoogleMapsServicesModels.DirectionsRoute;
import com.example.concordia_campus_guide.GoogleMapsServicesTools.GoogleMapsServicesModels.DirectionsStep;
import com.example.concordia_campus_guide.GoogleMapsServicesTools.GoogleMapsServicesModels.TravelMode;
import com.example.concordia_campus_guide.Models.Routes.Bus;
import com.example.concordia_campus_guide.Models.Routes.Car;
import com.example.concordia_campus_guide.Models.Routes.Route;
import com.example.concordia_campus_guide.Models.Routes.Subway;
import com.example.concordia_campus_guide.Models.Routes.Train;
import com.example.concordia_campus_guide.Models.Routes.TransportType;
import com.example.concordia_campus_guide.Models.Routes.Walk;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is an AsyncTask because we want to avoid freezing the main UI thread when parsing the Google Maps Directions API's response
 */
public class DirectionsApiDataParser extends AsyncTask<DirectionsApiDataRetrieval, Integer, DirectionsResult> {

    DirectionsApiDataRetrieval dataRetrieval = null;

    /**
     * Parsing the JSON string data to map it to a DirectionsResult model
     *
     * @param obj: DirectionsApiDataRetrieval object
     */
    @Override
    protected DirectionsResult doInBackground(DirectionsApiDataRetrieval... obj) {
        this.dataRetrieval = obj[0];
        DirectionsResult directionsResult = null;
        try {
            Log.d(DirectionsApiDataParser.class.getName(), "Mapping data to models");
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();
            directionsResult = gson.fromJson(dataRetrieval.data, DirectionsResult.class);
        } catch (Exception e) {
            Log.e(DirectionsApiDataParser.class.getName(), "Exception using Gson to map JSON to Models: " + e.toString());
        }
        return directionsResult;
    }

    /**
     * On the main UI thread, after the parsing process is done, set the DirectionsResult variable in the RoutesActivity to the result obtained
     *
     * @param result: DirectionsResult object
     *
     */
    @Override
    protected void onPostExecute(DirectionsResult result) {
        dataRetrieval.caller.setDirectionsResult(result);
        dataRetrieval.caller.setRouteOptions(extractRelevantInfoFromDirectionsResultObj(result));
    }

    public List<Route> extractRelevantInfoFromDirectionsResultObj(DirectionsResult result) {
        List<Route> routeOptions = new ArrayList<>();
        for(DirectionsRoute directionsRoute: result.routes) {
            Route route = null;
            if(dataRetrieval.transportType.equals(ClassConstants.DRIVING)) {
                route = new Route(directionsRoute.legs[0].duration.text, directionsRoute.summary, ClassConstants.DRIVING);
                routeOptions.add(route);
            }
            else if (dataRetrieval.transportType.equals(ClassConstants.WALKING)) {
                route = new Route(directionsRoute.legs[0].duration.text, directionsRoute.summary, ClassConstants.WALKING);
                routeOptions.add(route);
            }
            else { // TravelMode is TRANSIT

                if(directionsRoute.legs[0].departureTime != null && directionsRoute.legs[0].arrivalTime != null)
                    route = new Route(directionsRoute.legs[0].departureTime.text, directionsRoute.legs[0].arrivalTime.text, directionsRoute.legs[0].duration.text, ClassConstants.TRANSIT);
                else
                    route = new Route(directionsRoute.legs[0].duration.text, ClassConstants.TRANSIT);

                routeOptions.add(route);
                DirectionsStep[] steps = directionsRoute.legs[0].steps;
                for(DirectionsStep step: steps) {
                    route.getSteps().add(getTransportType(step));
                }
            }
        }
        return routeOptions;
    }

    private TransportType getTransportType(DirectionsStep step) {
        TravelMode mode = step.travelMode;

        switch (mode) {
            case DRIVING:
                return new Car(step);
            case WALKING:
                return new Walk(step);
            case BICYCLING:
                return null;
            case TRANSIT:
                if(step.transitDetails.line.vehicle.name.equalsIgnoreCase("bus")) {
                    return new Bus(step);
                }
                else if (step.transitDetails.line.vehicle.name.equalsIgnoreCase("subway")) {
                    return new Subway(step);
                }
                else if (step.transitDetails.line.vehicle.name.equalsIgnoreCase("train")) {
                    return new Train(step);
                }
        }
        return null;
    }
}