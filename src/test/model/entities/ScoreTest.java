package model.entities;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ScoreTest
{
	@Test
	void defaultConstructorStartsEverythingAtZero() {
		Score score = new Score();

		assertEquals(0, score.getPermanentScore());
		assertEquals(0, score.getRoundScore());
		assertEquals(0, score.getScoreFromMultiples());
	}

	@Test
	void mutatorsUpdateEachScoreBucket() {
		Score score = new Score(1000, 200, 150);

		score.increasePermanentScore(500);
		score.setRoundScore(350);
		score.increaseRoundScore(50);
		score.setScoreFromMultiples(400);

		assertEquals(1500, score.getPermanentScore());
		assertEquals(400, score.getRoundScore());
		assertEquals(400, score.getScoreFromMultiples());
	}
}
