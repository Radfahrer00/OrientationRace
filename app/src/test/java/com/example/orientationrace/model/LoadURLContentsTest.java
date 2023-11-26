package com.example.orientationrace.model;

import android.os.Handler;
import android.os.Message;
import com.example.orientationrace.model.gardens.Garden;
import junit.framework.TestCase;

public class LoadURLContentsTest extends TestCase {

    public void testParseJsonString() {
        // Create a mock JSON string for testing
        String jsonString = "{\"@graph\": [{\"title\": \"Garden1\", \"location\": {\"latitude\": 1.0, \"longitude\": 2.0}}, {\"title\": \"Garden2\", \"location\": {\"latitude\": 3.0, \"longitude\": 4.0}}]}";

        // Create a mock LoadURLContents instance
        LoadURLContents loadURLContents = new LoadURLContents(new Handler(), "application/json", "https://example.com");

        try {
            // Call the private parseJsonString method
            Garden[] gardens = loadURLContents.parseJsonString(jsonString);

            // Assert that the gardens array is not null and has the correct length
            assertNotNull(gardens);
            assertEquals(2, gardens.length);

            // Assert that the garden details are correctly parsed
            assertEquals("Garden1", gardens[0].getGardenName());
            assertEquals(1.0, gardens[0].getLatitude());
            assertEquals(2.0, gardens[0].getLongitude());

            assertEquals("Garden2", gardens[1].getGardenName());
            assertEquals(3.0, gardens[1].getLatitude());
            assertEquals(4.0, gardens[1].getLongitude());

        } catch (Exception e) {
            fail("Exception should not be thrown");
        }
    }
}
