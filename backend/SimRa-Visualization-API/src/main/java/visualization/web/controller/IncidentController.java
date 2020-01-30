package visualization.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visualization.service.IncidentService;
import visualization.web.resources.IncidentResource;

import java.util.List;


/*

This is the place where we communicate with the frontend regarding Incident Queries

 */

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    // get exactly one incident by rideId and adding the ?key=[0-N] as request parameter
    @GetMapping(value = "/rides/{rideId}/incidents/{key}")
    public HttpEntity<IncidentResource> getIncident(@PathVariable String rideId,
                                                    @PathVariable String key) {
        return ResponseEntity.ok(incidentService.getIncident(rideId, key));
    }

    // get all incidents of one ride by id
    @GetMapping(value = "/rides/{rideId}/incidents/all")
    public HttpEntity<List<IncidentResource>> getRideIncidents(@PathVariable String rideId) {
        return ResponseEntity.ok(incidentService.getIncidentsByFileId(rideId));
    }

    // get all incidents in range minDistance and maxDistance around a Point (longitude, latitude)
    @GetMapping(value = "/incidents")
    public HttpEntity<List<IncidentResource>> getIncidentsNear(@RequestParam(value = "lon") double longitude,
                                                               @RequestParam(value = "lat") double latitude,
                                                               @RequestParam(value = "max") int maxDistance) {
        return ResponseEntity.ok(incidentService.getIncidentsInRange(longitude, latitude, maxDistance));
    }

    //example: http://localhost:8080/incidents/area?bottomleft=13.297089,52.481744&topright=13.456360,52.547463
    @GetMapping(value = "/incidents/area")
    public HttpEntity<List<IncidentResource>> getIncidentsWithin(@RequestParam(value = "bottomleft") double[] first,
                                                                 @RequestParam(value = "topright") double[] second) {
        return ResponseEntity.ok(incidentService.getIncidentsInWithin(new GeoJsonPoint(first[0], first[1]),
                new GeoJsonPoint(first[0], second[1]),
                new GeoJsonPoint(second[0], second[1]),
                new GeoJsonPoint(second[0], first[1])));
    }

    // get all incidents with filter criteria applied
    @GetMapping(value = "/incidents/filter")
    public HttpEntity<List<IncidentResource>> getIncidentsFilteredBy(@RequestParam(value = "bottomleft") double[] first,
                                                                     @RequestParam(value = "topright") double[] second,
                                                                     @RequestParam(value = "fromTs", required = false) Long fromTs,
                                                                     @RequestParam(value = "untilTs", required = false) Long untilTs,
                                                                     @RequestParam(value = "fromMinutesOfDay", required = false) Integer fromMinutesOfDay,
                                                                     @RequestParam(value = "untilMinutesOfDay", required = false) Integer untilMinutesOfDay,
                                                                     @RequestParam(value = "weekdays", required = false) String[] weekdays,
                                                                     @RequestParam(value = "bike", required = false) Integer[] bikeTypes,
                                                                     @RequestParam(value = "child", required = false) Boolean child,
                                                                     @RequestParam(value = "trailer", required = false) Boolean trailer,
                                                                     @RequestParam(value = "incidents", required = false) Integer[] incidentTypes,
                                                                     @RequestParam(value = "participants", required = false) Boolean[] participants,
                                                                     @RequestParam(value = "scary", required = false) Boolean scary,
                                                                     @RequestParam(value = "description", required = false) Boolean description){

        return ResponseEntity.ok(incidentService.getFilteredIncidents(new GeoJsonPoint(first[0], first[1]),
                new GeoJsonPoint(first[0], second[1]),
                new GeoJsonPoint(second[0], second[1]),
                new GeoJsonPoint(second[0], first[1]), fromTs, untilTs, fromMinutesOfDay, untilMinutesOfDay, weekdays, bikeTypes, child, trailer, incidentTypes, participants, scary, description));
    }

}
