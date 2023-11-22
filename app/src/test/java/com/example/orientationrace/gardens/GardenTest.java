package com.example.orientationrace.gardens;

import junit.framework.TestCase;

public class GardenTest extends TestCase {

    public void testConstructorRetiro() {
        Garden garden = new Garden("Retiro", 1L);

        assertEquals("Retiro", garden.getGardenName());
        assertEquals(1L, garden.getKey().longValue());
    }

    public void testGetGardenNameCasaDeCampo() {
        Garden garden = new Garden("Casa de Campo", 2L);

        assertEquals("Casa de Campo", garden.getGardenName());
    }

    public void testGetKey() {
        Garden garden = new Garden("Campo del Moro", 3L);

        assertEquals(3L, garden.getKey().longValue());
    }

    public void testEquals() {
        Garden garden1 = new Garden("Parque de Atenas", 4L);
        Garden garden2 = new Garden("Parque de la Cornisa", 4L);
        Garden garden3 = new Garden("Parque Casino de la Reina", 5L);

        assertTrue(garden1.equals(garden2));
        assertFalse(garden1.equals(garden3));
    }
}
