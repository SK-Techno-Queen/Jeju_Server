package com.jeju_server;

import com.jeju_server.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    @Query(value = "SELECT b.* FROM bus_test_topic3 b INNER JOIN (SELECT b2.plate_No, MAX(b2.timestamp) AS latestTimestamp FROM bus_test_topic3 b2 GROUP BY b2.plate_No) latest ON b.plate_No = latest.plate_No AND b.timestamp = latest.latestTimestamp", nativeQuery = true)
    List<Bus> findLatestBusesByPlateNo();

    List<Bus> findTop50ByOrderByTimestampDesc();

    @Query(value = """
            SELECT new com.jeju_server.dto.BusDetailsDto(
                   A.PLATE_NO, 
                   MAX(A.TIMESTAMP), 
                   MAX(A.id),
                   (SELECT MAX(B.TIMESTAMP) 
                    FROM bus_search_topic B 
                    WHERE A.PLATE_NO = B.PLATE_NO),
                   (SELECT B.LOCAL_Y 
                    FROM bus_produce_topic B 
                    WHERE MAX(A.id) = B.id),
                   (SELECT B.LOCAL_X 
                    FROM bus_produce_topic B 
                    WHERE MAX(A.id) = B.id),
                   (SELECT B.id 
                    FROM bus_produce_topic B 
                    WHERE MAX(A.id) = B.id),
                   (SELECT B.CURR_STATION_NM 
                    FROM bus_produce_topic B 
                    WHERE MAX(A.id) = B.id),
                   (SELECT B.ROUTE_NUM 
                    FROM bus_produce_topic B 
                    WHERE MAX(A.id) = B.id))
            FROM bus_produce_topic A
            WHERE A.timestamp >= NOW() + INTERVAL 9 HOUR - INTERVAL 10 SECOND
            GROUP BY A.PLATE_NO
            """, nativeQuery = true)
    List<BusDetailsDto> findBusDetails();
}