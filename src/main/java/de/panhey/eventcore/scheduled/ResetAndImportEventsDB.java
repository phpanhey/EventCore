package de.panhey.eventcore.scheduled;

import de.panhey.eventcore.entities.Event;
import de.panhey.eventcore.repositories.EventRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Element;

@Component
public class ResetAndImportEventsDB {

    private static final Logger logger = LoggerFactory.getLogger(ResetAndImportEventsDB.class);

    @Autowired
    EventRepository eventRepository;

    //every 4 hours
    @Scheduled(fixedDelay = 14400000)
    public void resetAndImportEventsDB() {
        eventRepository.deleteAll();
        importDB(requestBremenDEEvents());
        importDB(requestKinderzeitEvents());
        logger.info("Successfully reset and imported events to DB " + eventRepository.count());
    }

    private void importDB(List<Event> events) {
        eventRepository.saveAll(events);
    }

    private List<Event> requestBremenDEEvents() {
        JSONArray jsonResults = new JSONArray();
        List<Event> eventResults = new ArrayList<>();
        try {
            URI uri = new URI("https://login.bremen.de/api/event-search/search");

            // Create JSON payload
            Calendar cal = Calendar.getInstance();
            String today = getDateString(cal);
            cal.add(Calendar.DATE, 1);
            String tomorrow = getDateString(cal);

            String payload = new JSONObject()
                    .put("is_date_search", 1)
                    .put("dates", new JSONObject()
                            .put("0", today)
                            .put("1", tomorrow)
                    ).toString();

            HttpResponse<String> response;
            try (HttpClient client = HttpClient.newHttpClient()) {

                // Create HttpRequest
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(payload))
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            jsonResults = new JSONArray(response.body());

            logger.info("Successfully fetched events from API. Number of events: {}", jsonResults.length());

        } catch (Exception e) {
            logger.error("Error occurred while fetching events from API", e);
        }

        for (int i = 0; i < jsonResults.length(); i++) {
            JSONObject event = jsonResults.getJSONObject(i);

            if (event.isNull("title") || event.isNull("description") || event.isNull("nextDate") || event.isNull("address")) {
                continue;
            }

            String title = event.getString("title");
            String description = event.getString("description");
            String address = event.getJSONObject("address").getJSONObject("venue").getString("address");
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getLong("nextDate")), ZoneId.systemDefault());
            eventResults.add(new Event(title, description, localDateTime, localDateTime, address));
        }
        return eventResults;
    }

    private List<Event> requestKinderzeitEvents() {
        ArrayList<String> allEventsasString = new ArrayList<>();
        ArrayList<Event> allEvents = new ArrayList<>();

        int offset = 0;
        boolean hasMore = true;

        try {
            // Create HttpClient instance
            try (HttpClient client = HttpClient.newHttpClient()) {

                while (hasMore) {
                    // Build the URL with the current offset
                    Calendar currentDate = Calendar.getInstance();
                    String formattedDate = getDateString(currentDate);
                    String apiUrl = String.format("https://kinderzeit-bremen.de/api/sprocket/calendar/1192/get_calendar_events?limit=10&offset=%d&seed=null&section_id=null&dtstart=%s", offset, formattedDate);

                    // Create URI and HttpRequest
                    URI uri = new URI(apiUrl);
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(uri)
                            .GET()
                            .build();

                    // Send the request and get the response
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    // Check if the response status code is 200 OK
                    if (response.statusCode() == 200) {
                        JSONObject jsonResponse = new JSONObject(response.body());

                        // Extract "results" array and append to allEvents
                        JSONArray events = jsonResponse.getJSONArray("results");
                        for (int i = 0; i < events.length(); i++) {
                            allEventsasString.add(events.getString(i));
                        }

                        // Update hasMore based on the "has_more" flag
                        hasMore = jsonResponse.getBoolean("has_more");
                        if (hasMore) {
                            offset += 10; // Increase offset by 10 for the next request
                        }
                    } else {
                        logger.error("Failed to fetch events. HTTP response code: {}", response.statusCode());
                        break;
                    }
                }
            }

            logger.info("Successfully fetched all events from API. Total number of events: {}", allEventsasString.size());
        } catch (Exception e) {
            logger.error("Error occurred while fetching events from API", e);
        }

        for (String event : allEventsasString) {
            Document doc = Jsoup.parse(event);

            String title = Optional.ofNullable(doc.select("h3 a").first())
                    .map(Element::text)
                    .orElse(null);

            String description = Optional.ofNullable(doc.select(".mp-description span").first())
                    .map(Element::text)
                    .orElse(null);

            String dateString = Optional.ofNullable(doc.select(".mp-date .mp-start").first())
                    .map(Element::text)
                    .orElse(null);

            LocalDateTime startDate = null;
            if (dateString != null) {
                startDate = LocalDateTime.of(LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy")), LocalTime.MIDNIGHT);
            }

            String location = Optional.ofNullable(doc.select(".mp-location a").first())
                    .map(Element::text)
                    .orElse(null);

            if (title != null && description != null && startDate != null && location != null) {
                allEvents.add(new Event(title, description, startDate, startDate, location));
            }
        }
        return allEvents;
    }


    private String getDateString(Calendar cal) {
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }
}