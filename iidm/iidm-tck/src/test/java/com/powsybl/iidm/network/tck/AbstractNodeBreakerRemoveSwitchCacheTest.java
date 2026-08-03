/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.iidm.network.tck;

import com.powsybl.iidm.network.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests that removing switches or internal connections in node/breaker topology
 * invalidates calculated bus views when they were previously queried.
 *
 * @see <a href="https://github.com/powsybl/powsybl-core/issues/4021">#4021</a>
 */
public abstract class AbstractNodeBreakerRemoveSwitchCacheTest {

    private static Network createTwoBusNetwork(boolean retained) {
        Network network = Network.create("test", "test");
        VoltageLevel vl = network.newVoltageLevel()
                .setId("VL1")
                .setNominalV(400)
                .setTopologyKind(TopologyKind.NODE_BREAKER)
                .add();
        vl.getNodeBreakerView().newSwitch()
                .setNode1(0)
                .setNode2(1)
                .setId("SW1")
                .setKind(SwitchKind.BREAKER)
                .setOpen(false)
                .setRetained(retained)
                .add();
        vl.getNodeBreakerView().newBusbarSection().setNode(0).setId("BBS1").setName("BBS1").add();
        vl.getNodeBreakerView().newBusbarSection().setNode(1).setId("BBS2").setName("BBS2").add();
        vl.newLoad().setNode(3).setId("LD1").setP0(100).setQ0(100).add();
        vl.newLoad().setNode(4).setId("LD2").setP0(100).setQ0(100).add();
        vl.getNodeBreakerView().newInternalConnection().setNode1(0).setNode2(3).add();
        vl.getNodeBreakerView().newInternalConnection().setNode1(1).setNode2(4).add();
        return network;
    }

    @Test
    public void removeSwitchInvalidatesBusView() {
        Network network = createTwoBusNetwork(false);
        VoltageLevel vl = network.getVoltageLevel("VL1");

        List<Bus> busesBeforeRemoval = vl.getBusView().getBusStream().toList();
        assertEquals(1, busesBeforeRemoval.size());

        vl.getNodeBreakerView().removeSwitch("SW1");

        List<Bus> busesAfterRemoval = vl.getBusView().getBusStream().toList();
        assertEquals(2, busesAfterRemoval.size());
    }

    @Test
    public void removeSwitchInvalidatesBusBreakerView() {
        Network network = createTwoBusNetwork(false);
        VoltageLevel vl = network.getVoltageLevel("VL1");

        List<Bus> busesBeforeRemoval = vl.getBusBreakerView().getBusStream().toList();
        assertEquals(1, busesBeforeRemoval.size());

        vl.getNodeBreakerView().removeSwitch("SW1");

        List<Bus> busesAfterRemoval = vl.getBusBreakerView().getBusStream().toList();
        assertEquals(2, busesAfterRemoval.size());
    }

    @Test
    public void removeSwitchInvalidatesTerminalBusView() {
        Network network = createTwoBusNetwork(false);
        VoltageLevel vl = network.getVoltageLevel("VL1");
        Load load1 = network.getLoad("LD1");
        Load load2 = network.getLoad("LD2");

        assertNotNull(load1.getTerminal().getBusView().getBus());
        assertNotNull(load2.getTerminal().getBusView().getBus());
        assertEquals(load1.getTerminal().getBusView().getBus(), load2.getTerminal().getBusView().getBus());

        vl.getNodeBreakerView().removeSwitch("SW1");

        Bus load1Bus = load1.getTerminal().getBusView().getBus();
        Bus load2Bus = load2.getTerminal().getBusView().getBus();
        assertNotNull(load1Bus);
        assertNotNull(load2Bus);
        assertEquals("VL1_0", load1Bus.getId());
        assertEquals("VL1_1", load2Bus.getId());
    }

    @Test
    public void removeInternalConnectionInvalidatesBusView() {
        Network network = Network.create("test", "test");
        VoltageLevel vl = network.newVoltageLevel()
                .setId("VL1")
                .setNominalV(400)
                .setTopologyKind(TopologyKind.NODE_BREAKER)
                .add();
        vl.getNodeBreakerView().newBusbarSection().setNode(0).setId("BBS1").add();
        vl.getNodeBreakerView().newBusbarSection().setNode(1).setId("BBS2").add();
        vl.newLoad().setNode(2).setId("LD1").setP0(100).setQ0(100).add();
        vl.newLoad().setNode(3).setId("LD2").setP0(100).setQ0(100).add();
        vl.getNodeBreakerView().newInternalConnection().setNode1(0).setNode2(2).add();
        vl.getNodeBreakerView().newInternalConnection().setNode1(1).setNode2(3).add();

        List<Bus> busesBeforeRemoval = vl.getBusView().getBusStream().toList();
        assertEquals(2, busesBeforeRemoval.size());

        vl.getNodeBreakerView().removeInternalConnections(0, 2);

        List<Bus> busesAfterRemoval = vl.getBusView().getBusStream().toList();
        assertEquals(1, busesAfterRemoval.size());
        assertNull(network.getLoad("LD1").getTerminal().getBusView().getBus());
        assertNotNull(network.getLoad("LD2").getTerminal().getBusView().getBus());
    }

    @Test
    public void removeAllEdgesInvalidatesBusView() {
        Network network = createTwoBusNetwork(false);
        VoltageLevel vl = network.getVoltageLevel("VL1");

        List<Bus> busesBeforeRemoval = vl.getBusView().getBusStream().toList();
        assertEquals(1, busesBeforeRemoval.size());

        vl.remove();

        assertEquals(0, network.getBusView().getBusStream().count());
    }
}
