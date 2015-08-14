package biz.paluch.redis.extensibility;

import java.util.*;
import com.lambdaworks.redis.*;

public class LettuceGeoDemo {

    public static void main(String[] args) {

        RedisClient redisClient = new RedisClient("localhost", 6379);
        RedisConnection<String, String> redis = (RedisConnection<String, String>) redisClient.connect();
        String key = "my-geo-set";

        redis.geoadd(key, 8.6638775, 49.5282537, "Weinheim", 8.3796281, 48.9978127, "Office tower", 8.665351, 49.553302,
                "Train station");

        Set<String> georadius = redis.georadius(key, 8.6582861, 49.5285695, 5, GeoArgs.Unit.km);
        System.out.println("Geo Radius: " + georadius);

        // georadius contains "Weinheim" and "Train station"

        Double distance = redis.geodist(key, "Weinheim", "Train station", GeoArgs.Unit.km);
        System.out.println("Distance: " + distance + " km");

        // distance ≈ 2.78km

        GeoArgs geoArgs = new GeoArgs().withHash().withCoordinates().withDistance().withCount(2).asc();

        List<GeoWithin<String>> georadiusWithArgs = redis.georadius(key, 8.665351, 49.5285695, 5, GeoArgs.Unit.km, geoArgs);

        // georadiusWithArgs contains "Weinheim" and "Train station"
        // ordered descending by distance and containing distance/coordinates
        GeoWithin<String> weinheim = georadiusWithArgs.get(0);

        System.out.println("Member: " + weinheim.member);
        System.out.println("Geo hash: " + weinheim.geohash);
        System.out.println("Distance: " + weinheim.distance);
        System.out.println("Coordinates: " + weinheim.coordinates.x + "/" + weinheim.coordinates.y);

        List<GeoCoordinates> geopos = redis.geopos(key, "Weinheim", "Train station");
        GeoCoordinates weinheimGeopos = geopos.get(0);
        System.out.println("Coordinates: " + weinheimGeopos.x + "/" + weinheimGeopos.y);

        redis.close();
        redisClient.shutdown();
    }
}
