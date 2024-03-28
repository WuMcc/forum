package com.example.service;

import com.example.entity.vo.response.WeatherVo;

public interface WeatherService {
    WeatherVo fetchWeather(double longitude, double latitude);
}
