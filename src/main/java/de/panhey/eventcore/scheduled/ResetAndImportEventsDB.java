package de.panhey.eventcore.scheduled;

import de.panhey.eventcore.entities.Event;
import de.panhey.eventcore.repositories.EventRepository;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

@Component
public class ResetAndImportEventsDB {

    private static final Logger logger = LoggerFactory.getLogger(ResetAndImportEventsDB.class);

    @Autowired
    EventRepository eventRepository;

    //every 4 hours
    @Scheduled(fixedDelay = 14400000)
    public void resetAndImportEventsDB() {
        eventRepository.deleteAll();
        requestBremenDEEvents();
        importDB(requestBremenDEEvents());
        logger.info("Successfully reset and imported events to DB " + eventRepository.count());
    }

    private void importDB(JSONArray res) {
        for (int i = 0; i < res.length(); i++) {
            JSONObject event = res.getJSONObject(i);

            if (event.isNull("title") || event.isNull("description") || event.isNull("nextDate") || event.isNull("address")) {
                continue;
            }

            String title = event.getString("title");
            String description = event.getString("description");
            String address = event.getJSONObject("address").getJSONObject("venue").getString("address");
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getLong("nextDate")), ZoneId.systemDefault());
            Event e = new Event(title, description, localDateTime, localDateTime, address);

            eventRepository.save(e);
        }
    }

    private JSONArray requestBremenDEEvents() {
        JSONArray res = new JSONArray();
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

            res = new JSONArray(response.body());

            logger.info("Successfully fetched events from API. Number of events: {}", res.length());

        } catch (Exception e) {
            logger.error("Error occurred while fetching events from API", e);
        }
        return res;
    }

    private String getDateString(Calendar cal) {
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }
}