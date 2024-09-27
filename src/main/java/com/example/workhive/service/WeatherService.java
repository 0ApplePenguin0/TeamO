package com.example.workhive.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String weatherApiKey;

    // 좌표를 받아서 날씨 정보를 가져오는 메서드
    public String getWeatherByCoordinates(double latitude, double longitude) {
        return getWeatherFromKMA(latitude, longitude);
    }

    // 기상청 API를 사용하여 JSON으로 날씨 정보를 가져오는 메서드
    public String getWeatherFromKMA(double lat, double lon) {
        // 위도, 경도를 기상청 API에서 요구하는 격자 좌표로 변환하는 메서드
        int[] grid = convertToGrid(lat, lon);
        int nx = grid[0];
        int ny = grid[1];

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String currentTime = LocalTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("HH00"));

        try {
            // URL 생성
            StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst");
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(weatherApiKey, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(currentDate, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(currentTime, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(nx), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(ny), "UTF-8"));

            // API 호출
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            conn.disconnect();

            // JSON 파싱
            return parseWeatherResponse(response.toString());

        } catch (Exception e) {
            log.error("날씨 정보를 가져오는 중 오류 발생: ", e);
            return "날씨 정보를 가져오는 데 실패했습니다.";
        }
    }

    // JSON 응답에서 기온(T1H)과 강수 형태(PTY)를 추출하는 메서드
    private String parseWeatherResponse(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject response = jsonObject.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        JSONObject items = body.getJSONObject("items");
        JSONArray itemArray = items.getJSONArray("item");

        String temperature = "";
        String weatherCondition = "";

        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject item = itemArray.getJSONObject(i);
            String category = item.getString("category");
            String obsrValue = item.getString("obsrValue");

            if (category.equals("T1H")) {
                temperature = obsrValue;  // 기온
            } else if (category.equals("PTY")) {
                weatherCondition = getWeatherCondition(obsrValue);  // 강수 형태
            }
        }

        return String.format("기온: %s°C, 상태: %s", temperature, weatherCondition);
    }

    // 강수 형태 코드에 따른 상태를 반환하는 메서드
    private String getWeatherCondition(String obsrValue) {
        switch (obsrValue) {
            case "0":
                return "맑음";
            case "1":
                return "비";
            case "2":
                return "비/눈";
            case "3":
                return "눈";
            case "4":
                return "소나기";
            default:
                return "알 수 없음";
        }
    }

    // 위도, 경도를 기상청 격자 좌표로 변환하는 메서드
    private int[] convertToGrid(double lat, double lon) {
        // 격자 좌표 계산 로직
        double RE = 6371.00877;
        double GRID = 5.0;
        double SLAT1 = 30.0;
        double SLAT2 = 60.0;
        double OLON = 126.0;
        double OLAT = 38.0;
        double XO = 43;
        double YO = 136;

        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int x = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
        int y = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        return new int[]{x, y};
    }
}
