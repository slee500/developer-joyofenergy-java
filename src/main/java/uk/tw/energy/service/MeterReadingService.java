package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;

    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    public Optional<List<ElectricityReading>> getReadings(String smartMeterId, Instant start, Instant end) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId))
                .map(res -> getRelevantReadings(res, start, end));
    }

    public void storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        if (!meterAssociatedReadings.containsKey(smartMeterId)) {
            meterAssociatedReadings.put(smartMeterId, new ArrayList<>());
        }
        meterAssociatedReadings.get(smartMeterId).addAll(electricityReadings);
    }

    private List<ElectricityReading> getRelevantReadings(List<ElectricityReading> readings, Instant start, Instant end) {
        return readings.stream()
                .filter(reading -> reading.getTime().isAfter(start.minusSeconds(1)) && reading.getTime().isBefore(end.plusSeconds(1)))
                .collect(Collectors.toList());
    }
}
