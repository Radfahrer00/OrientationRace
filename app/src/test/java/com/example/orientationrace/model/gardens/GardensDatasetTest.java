package com.example.orientationrace.model.gardens;

import junit.framework.TestCase;

/********** Comment out Log.d row in GardensDataset constructor to run the test **********/
public class GardensDatasetTest extends TestCase {

    // GardensDataset instance for testing
    private GardensDataset gardensDataset;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Initialize GardensDataset
        gardensDataset = new GardensDataset();
    }

    public void testGetSize() {
        // Test if getSize returns the expected size
        assertEquals(0, gardensDataset.getSize());

        // Add gardens to the dataset
        gardensDataset.addGarden(new Garden("Garden 1", 0.0, 0.0, 1L));
        gardensDataset.addGarden(new Garden("Garden 2", 1.0, 1.0, 2L));

        // Test again after adding gardens
        assertEquals(2, gardensDataset.getSize());
    }

    public void testAddGarden() {
        // Add a garden to the dataset
        gardensDataset.addGarden(new Garden("Test Garden", 0.0, 0.0, 1L));

        // Test if the garden is added and the size is correct
        assertEquals(1, gardensDataset.getSize());
    }

    public void testGetGardenAtPosition() {
        // Add gardens to the dataset
        gardensDataset.addGarden(new Garden("Garden 1", 0.0, 0.0, 1L));
        gardensDataset.addGarden(new Garden("Garden 2", 1.0, 1.0, 2L));

        // Test if getGardenAtPosition returns the correct garden
        assertEquals("Garden 1", gardensDataset.getGardenAtPosition(0).getGardenName());
        assertEquals("Garden 2", gardensDataset.getGardenAtPosition(1).getGardenName());
    }

    public void testGetKeyAtPosition() {
        // Add gardens to the dataset
        gardensDataset.addGarden(new Garden("Garden 1", 0.0, 0.0, 1L));
        gardensDataset.addGarden(new Garden("Garden 2", 1.0, 1.0, 2L));

        // Test if getKeyAtPosition returns the correct key
        assertEquals(1L, (long) gardensDataset.getKeyAtPosition(0));
        assertEquals(2L, (long) gardensDataset.getKeyAtPosition(1));
    }

    public void testGetPositionOfKey() {
        // Add gardens to the dataset
        gardensDataset.addGarden(new Garden("Garden 1", 0.0, 0.0, 1L));
        gardensDataset.addGarden(new Garden("Garden 2", 1.0, 1.0, 2L));

        // Test if getPositionOfKey returns the correct position
        assertEquals(0, gardensDataset.getPositionOfKey(1L));
        assertEquals(1, gardensDataset.getPositionOfKey(2L));
    }

    public void testRemoveGardenAtPosition() {
        // Add gardens to the dataset
        gardensDataset.addGarden(new Garden("Garden 1", 0.0, 0.0, 1L));
        gardensDataset.addGarden(new Garden("Garden 2", 1.0, 1.0, 2L));

        // Remove a garden and test if the size is correct
        gardensDataset.removeGardenAtPosition(0);
        assertEquals(1, gardensDataset.getSize());
    }

    public void testRemoveGardenWithKey() {
        // Add gardens to the dataset
        gardensDataset.addGarden(new Garden("Garden 1", 0.0, 0.0, 1L));
        gardensDataset.addGarden(new Garden("Garden 2", 1.0, 1.0, 2L));

        // Remove a garden by key and test if the size is correct
        gardensDataset.removeGardenWithKey(1L);
        assertEquals(1, gardensDataset.getSize());
    }
}
