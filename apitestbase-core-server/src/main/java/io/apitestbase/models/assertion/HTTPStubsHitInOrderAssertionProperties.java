package io.apitestbase.models.assertion;

import io.apitestbase.models.Properties;

import java.util.List;

public class HTTPStubsHitInOrderAssertionProperties extends Properties {
    private List<Short> expectedHitOrder;

    public HTTPStubsHitInOrderAssertionProperties() {}

    public HTTPStubsHitInOrderAssertionProperties(List<Short> expectedHitOrder) {
        this.expectedHitOrder = expectedHitOrder;
    }

    public List<Short> getExpectedHitOrder() {
        return expectedHitOrder;
    }

    public void setExpectedHitOrder(List<Short> expectedHitOrder) {
        this.expectedHitOrder = expectedHitOrder;
    }
}
