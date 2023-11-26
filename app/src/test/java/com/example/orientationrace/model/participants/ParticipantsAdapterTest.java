package com.example.orientationrace.model.participants;

import junit.framework.TestCase;

/********** Comment out Log.d row in ParticipantsAdapter constructor to run the test **********/
public class ParticipantsAdapterTest extends TestCase {

    public void testGetKeyAtPosition() {
        ParticipantsDataset dataset = new ParticipantsDataset();
        ParticipantsAdapter adapter = new ParticipantsAdapter(dataset);

        // Add a participant to the dataset
        Participant participant = new Participant("Quentin", (long) 1);
        dataset.addParticipant(participant);

        // Test getKeyAtPosition for the added participant
        Long key = adapter.getKeyAtPosition(0);
        assertNotNull(key);
        assertEquals(participant.getKey(), key);
    }

    public void testGetPositionOfKey() {
        ParticipantsDataset dataset = new ParticipantsDataset();
        ParticipantsAdapter adapter = new ParticipantsAdapter(dataset);

        // Add a participant to the dataset
        Participant participant = new Participant("Quentin", (long) 1);
        dataset.addParticipant(participant);

        // Test getPositionOfKey for the added participant
        int position = adapter.getPositionOfKey(participant.getKey());
        assertEquals(0, position);
    }

    public void testGetItemCount() {
        ParticipantsDataset dataset = new ParticipantsDataset();
        ParticipantsAdapter adapter = new ParticipantsAdapter(dataset);

        // Add participants to the dataset
        Participant participant1 = new Participant("Quentin", (long) 1);
        Participant participant2 = new Participant("Hichem", (long) 2);
        dataset.addParticipant(participant1);
        dataset.addParticipant(participant2);

        // Test getItemCount for the added participants
        assertEquals(2, adapter.getItemCount());
    }
}
