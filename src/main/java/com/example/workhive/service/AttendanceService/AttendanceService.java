package com.example.workhive.service.AttendanceService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class AttendanceService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    // 주소를 Kakao Map API를 사용하여 좌표로 변환
    public double[] getCoordinatesFromAddress(String address) {
        String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + address;
        RestTemplate restTemplate = new RestTemplate();

        // Kakao API 호출에 필요한 인증 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // 응답 파싱
        JSONObject jsonObject = new JSONObject(response.getBody());
        JSONObject document = jsonObject.getJSONArray("documents").getJSONObject(0);
        double latitude = document.getDouble("y");
        double longitude = document.getDouble("x");

        return new double[]{latitude, longitude}; // [위도, 경도] 배열 반환
    }
}