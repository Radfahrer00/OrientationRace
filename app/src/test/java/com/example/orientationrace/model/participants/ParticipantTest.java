package com.example.orientationrace.model.participants;

import com.example.orientationrace.model.participants.Participant;

import junit.framework.TestCase;

public class ParticipantTest extends TestCase {

    public void testConstructorForQuentin() {
        Participant participant = new Participant("Quentin", 1L);
        assertEquals("Quentin", participant.getUsername());
        assertEquals(1L, participant.getKey().longValue());
    }

    public void testGetUsernameHichem() {
        Participant participant = new Participant("Hichem", 2L);

        assertEquals("Hichem", participant.getUsername());
    }

    public void testGetUsernameWithEmptyString() {
        Participant participant = new Participant("", 1L);
        assertEquals("", participant.getUsername());
    }

    public void testGetKey() {
        Participant participant = new Participant("QuentinTwo", 3L);

        assertEquals(3L, participant.getKey().longValue());
    }

    public void testEquals() {
        Participant participant1 = new Participant("David", 4L);
        Participant participant2 = new Participant("Eve", 4L);
        Participant participant3 = new Participant("David", 5L);

        assertTrue(participant1.equals(participant2));
        assertFalse(participant1.equals(participant3));
    }

}