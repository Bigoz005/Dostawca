package com.example.dostawca.service;

import com.example.dostawca.dto.Point;
import com.example.dostawca.dto.Route;

public class CurrentRouteService {
    private static Route currentRoute = new Route();

    public static void setCurrentRoute(Route currentRoute) {
        CurrentRouteService.currentRoute = currentRoute;
    }

    public static void addPointToCurrentRoute(Point point) {
        currentRoute.getPoints().add(point);
    }

    public static Route getCurrentRoute() {
        return currentRoute;
    }
}
