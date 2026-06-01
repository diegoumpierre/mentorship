package com.poc.logisticfreight.quote;

import com.poc.logisticfreight.mode.Boat;
import com.poc.logisticfreight.mode.Rail;
import com.poc.logisticfreight.mode.TransportMode;
import com.poc.logisticfreight.mode.Truck;
import com.poc.logisticfreight.model.TransportType;
import com.poc.logisticfreight.model.Weight;
import com.poc.logisticfreight.pricing.ComponentPricingStrategy;
import com.poc.logisticfreight.pricing.PriceComponent;
import com.poc.logisticfreight.pricing.PricingStrategy;
import com.poc.logisticfreight.pricing.components.BaseFareComponent;
import com.poc.logisticfreight.pricing.components.DistanceSurchargeComponent;
import com.poc.logisticfreight.pricing.components.FuelSurchargeComponent;
import com.poc.logisticfreight.pricing.components.HazardSurchargeComponent;
import com.poc.logisticfreight.pricing.components.VolumeComponent;
import com.poc.logisticfreight.pricing.components.WeightComponent;

import java.math.BigDecimal;
import java.util.List;

final class TestModes {

    private TestModes() {
    }

    static TransportMode truck() {
        return new Truck(Weight.ofKilograms("24000"), new BigDecimal("250"), new BigDecimal("70"), pricing(TransportType.TRUCK));
    }

    static TransportMode rail() {
        return new Rail(Weight.ofKilograms("90000"), new BigDecimal("60"), pricing(TransportType.RAIL));
    }

    static TransportMode boat() {
        return new Boat(Weight.ofKilograms("500000"), new BigDecimal("35"), pricing(TransportType.BOAT));
    }

    static PricingStrategy pricing(TransportType type) {
        return new ComponentPricingStrategy(type, chain());
    }

    private static List<PriceComponent> chain() {
        return List.of(
                new BaseFareComponent(),
                new VolumeComponent(),
                new WeightComponent(),
                new DistanceSurchargeComponent(),
                new FuelSurchargeComponent(),
                new HazardSurchargeComponent());
    }
}
