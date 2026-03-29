package com.transport.system.domain;

import static com.transport.system.domain.TripTestSamples.*;
import static com.transport.system.domain.WaybillTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.transport.system.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WaybillTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Waybill.class);
        Waybill waybill1 = getWaybillSample1();
        Waybill waybill2 = new Waybill();
        assertThat(waybill1).isNotEqualTo(waybill2);

        waybill2.setId(waybill1.getId());
        assertThat(waybill1).isEqualTo(waybill2);

        waybill2 = getWaybillSample2();
        assertThat(waybill1).isNotEqualTo(waybill2);
    }

    @Test
    void tripTest() {
        Waybill waybill = getWaybillRandomSampleGenerator();
        Trip tripBack = getTripRandomSampleGenerator();

        waybill.setTrip(tripBack);
        assertThat(waybill.getTrip()).isEqualTo(tripBack);
        assertThat(tripBack.getWaybill()).isEqualTo(waybill);

        waybill.trip(null);
        assertThat(waybill.getTrip()).isNull();
        assertThat(tripBack.getWaybill()).isNull();
    }
}
