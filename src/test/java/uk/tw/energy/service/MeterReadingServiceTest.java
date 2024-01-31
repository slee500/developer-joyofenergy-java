package uk.tw.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MeterReadingServiceTest {

    private MeterReadingService meterReadingService;

    @BeforeEach
    public void setUp() {
        meterReadingService = new MeterReadingService(new HashMap<>());
    }

    @Test
    public void givenMeterIdThatDoesNotExistShouldReturnNull() {
        assertThat(meterReadingService.getReadings("unknown-id")).isEqualTo(Optional.empty());
    }

    @Test
    public void givenMeterReadingThatExistsShouldReturnMeterReadings() {
        meterReadingService.storeReadings("random-id", new ArrayList<>());
        assertThat(meterReadingService.getReadings("random-id")).isEqualTo(Optional.of(new ArrayList<>()));
    }

    @Test
    public void getMeterReadingsWithinTimeRange() {
        ElectricityReading reading1 = new ElectricityReading(Instant.now().minus(2, ChronoUnit.DAYS), new BigDecimal(1));
        ElectricityReading reading2 = new ElectricityReading(Instant.now().minus(1, ChronoUnit.DAYS), new BigDecimal(2));
        ElectricityReading reading3 = new ElectricityReading(Instant.now(), new BigDecimal(3));

        List<ElectricityReading> readings = new ArrayList<>();
        readings.add(reading1);
        readings.add(reading2);
        readings.add(reading3);

        meterReadingService.storeReadings("random-id", readings);
        assertThat(meterReadingService.getReadings("random-id")).isEqualTo(Optional.of(readings));

        List<ElectricityReading> expectedList = new ArrayList<>();
        expectedList.add(reading2);
        assertThat(meterReadingService.getReadings("random-id", Instant.now().minus(1, ChronoUnit.DAYS), Instant.now().minusSeconds(10))).isEqualTo(Optional.of(expectedList));
    }
}
