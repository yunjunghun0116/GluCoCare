package com.glucocare.server.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glucocare.server.client.dto.PredictionResult;
import com.glucocare.server.exception.ApplicationException;
import com.glucocare.server.exception.ErrorMessage;
import com.glucocare.server.feature.glucose.domain.GlucoseHistory;
import com.glucocare.server.feature.glucose.dto.PredictGlucoseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PredictClient {
    private static final String PREDICT_SERVER_URL = "https://assured-mastodon-basically.ngrok-free.app";
    private static final Long PREDICT_ID = 99999999L;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public List<PredictGlucoseResponse> predictFutureGlucose(List<GlucoseHistory> glucoseHistories) {
        var points = glucoseHistories.stream()
                                     .map(g -> Map.of("dateTime", g.getDateTime(), "sgv", g.getSgv()))
                                     .toList();

        var body = Map.of("points", points, "sigma_scale", 1.2, "clip_min", 40, "clip_max", 400);

        var response = restClient.post()
                                 .uri(URI.create(PREDICT_SERVER_URL + "/predict"))
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(body)
                                 .retrieve()
                                 .body(new ParameterizedTypeReference<Map<String, Object>>() {
                                 });
        if (response == null || !response.containsKey("result")) {
            throw new ApplicationException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
        var dto = objectMapper.convertValue(response, PredictionResult.class);
        var predictions = dto.result()
                             .predictions();

        var timeKeys = List.of(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60);
        var predictGlucoseList = new ArrayList<PredictGlucoseResponse>();
        var recentGlucoseHistory = glucoseHistories.getFirst();
        for (var time : timeKeys) {
            var prediction = predictions.get(time.toString());
            var timestamp = recentGlucoseHistory.getDateTime() + time * 60_000L;

            predictGlucoseList.add(PredictGlucoseResponse.of(PREDICT_ID, timestamp, prediction.mean(), prediction.pi90()[0], prediction.pi90()[1]));
        }

        return predictGlucoseList;
    }

    public List<PredictGlucoseResponse> predictExerciseGlucose(List<GlucoseHistory> glucoseHistories, Integer duration) {
        var points = glucoseHistories.stream()
                                     .map(g -> Map.of("dateTime", g.getDateTime(), "sgv", g.getSgv()))
                                     .toList();

        var body = Map.of("points", points, "planned_duration_min", duration, "planned_mets", 4.0, "exercise_effect_scale", 1.0);

        var response = restClient.post()
                                 .uri(URI.create(PREDICT_SERVER_URL + "/predict/exercise"))
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .body(body)
                                 .retrieve()
                                 .body(new ParameterizedTypeReference<Map<String, Object>>() {
                                 });
        if (response == null || !response.containsKey("result")) {
            throw new ApplicationException(ErrorMessage.INTERNAL_SERVER_ERROR);
        }
        var dto = objectMapper.convertValue(response, PredictionResult.class);
        var predictions = dto.result()
                             .predictions();

        var timeKeys = List.of(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60);
        var predictGlucoseList = new ArrayList<PredictGlucoseResponse>();
        var recentGlucoseHistory = glucoseHistories.getFirst();
        for (var time : timeKeys) {
            var prediction = predictions.get(time.toString());
            var timestamp = recentGlucoseHistory.getDateTime() + time * 60_000L;
            predictGlucoseList.add(PredictGlucoseResponse.of(PREDICT_ID, timestamp, prediction.mean(), prediction.pi90()[0], prediction.pi90()[1]));
        }

        return predictGlucoseList;
    }
}

