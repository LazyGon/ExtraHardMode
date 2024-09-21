package com.extrahardmode.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.extrahardmode.service.MockConfigNode;
import org.junit.jupiter.api.Test;

/**
 * @author Diemex
 */
public class TestConfigPlotter {
    @Test
    public void testLastPart() {
        assertEquals("test04", ConfigPlotter.getLastPart(MockConfigNode.BOOL_TRUE));
        assertEquals("test 01", ConfigPlotter.getLastPart(MockConfigNode.BOOL_FALSE));
    }
}
