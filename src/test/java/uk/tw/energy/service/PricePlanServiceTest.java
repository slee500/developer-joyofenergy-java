package uk.tw.energy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PricePlanServiceTest {

    private MeterReadingService mockMeterReadingService;

    @BeforeEach
    public void setUp() {
        mockMeterReadingService = mock(MeterReadingService.class);
    }

    @Test
    public void testGetCostOfUsage() {
        PricePlan pricePlan = new PricePlan("basic-plan", null, BigDecimal.ONE, Collections.emptyList());

        ElectricityReading reading1 = new ElectricityReading(Instant.now().minus(2, ChronoUnit.HOURS), new BigDecimal(10));
        ElectricityReading reading2 = new ElectricityReading(Instant.now().minus(1, ChronoUnit.HOURS), new BigDecimal(20));
        ElectricityReading reading3 = new ElectricityReading(Instant.now(), new BigDecimal(30));

        List<ElectricityReading> readings = new ArrayList<>();
        readings.add(reading1);
        readings.add(reading2);
        readings.add(reading3);

        when(mockMeterReadingService.getReadings(eq("random-id"), any(), any()))
                .thenReturn(Optional.of(readings));

        PricePlanService target = new PricePlanService(Collections.singletonList(pricePlan), mockMeterReadingService);

        assertThat(target.getCostOfUsage("not-found-id", "basic-plan", Instant.now(), Instant.now())).isEqualTo(Optional.empty());
        assertThat(target.getCostOfUsage("random-id", "unknown-plan", Instant.now(), Instant.now())).isEqualTo(Optional.empty());
        assertThat(target.getCostOfUsage("random-id", "basic-plan", Instant.now(), Instant.now())).isEqualTo(Optional.of(BigDecimal.valueOf(40.0)));
    }
}
