package com.example.orientationrace.model.participants;

import junit.framework.TestCase;

public class ParticipantsDatasetTest extends TestCase {

    public void testGetSize() {
        ParticipantsDataset dataset = new ParticipantsDataset();

        // Test getSize for an empty dataset
        assertEquals(0, dataset.getSize());

        // Add participants to the dataset
        Participant participant1 = new Participant("Quentin", (long) 1);
        Participant participant2 = new Participant("Hichem", (long) 2);
        dataset.addParticipant(participant1);
        dataset.addParticipant(participant2);

        // Test getSize for the dataset with added participants
        assertEquals(2, dataset.getSize());
    }

    public void testAddParticipant() {
        ParticipantsDataset dataset = new ParticipantsDataset();

        // Add a participant to the dataset
        Participant participant = new Participant("Quentin", (long) 1);
        dataset.addParticipant(participant);

        // Test getSize after adding a participant
        assertEquals(1, dataset.getSize());
    }

    public void testGetParticipantAtPosition() {
        ParticipantsDataset dataset = new ParticipantsDataset();

        // Add a participant to the dataset
        Participant participant = new Participant("Quentin", (long) 1);
        dataset.addParticipant(participant);

        // Test getParticipantAtPosition for the added participant
        assertEquals(participant, dataset.getParticipantAtPosition(0));
    }

    public void testGetKeyAtPosition() {
        ParticipantsDataset dataset = new ParticipantsDataset();

        // Add a participant to the dataset
        Participant participant = new Participant("Quentin", (long) 1);
        dataset.addParticipant(participant);

        // Test getKeyAtPosition for the added participant
        Long key = dataset.getKeyAtPosition(0);
        assertNotNull(key);
        assertEquals(participant.getKey(), key);
    }

    public void testGetPositionOfKey() {
        ParticipantsDataset dataset = new ParticipantsDataset();

        // Add a participant to the dataset
        Participant participant = new Participant("Quentin", (long) 1);
        dataset.addParticipant(participant);

        // Test getPositionOfKey for the added participant
        int position = dataset.getPositionOfKey(participant.getKey());
        assertEquals(0, position);
    }
}
