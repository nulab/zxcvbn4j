package com.nulabinc.zxcvbn.guesses;

import com.nulabinc.zxcvbn.Context;
import com.nulabinc.zxcvbn.matchers.Keyboard;
import com.nulabinc.zxcvbn.matchers.Match;

public class SpatialGuess extends BaseGuess {

  public SpatialGuess(final Context context) {
    super(context);
  }

  @Override
  public double exec(Match match) {
    Keyboard keyboard = this.getContext().getKeyboardMap().get(match.graph);
    int startingPositions = keyboard.getStartingPositions();
    double averageDegree = keyboard.getAverageDegree();

    double totalGuesses = calculateBaseGuesses(match, startingPositions, averageDegree);
    totalGuesses *= calculateShiftedVariations(match);

    return totalGuesses;
  }

  private double calculateBaseGuesses(Match match, int startingPositions, double averageDegree) {
    double guesses = 0;
    int tokenLength = match.tokenLength();
    int turns = match.turns;

    for (int i = 2; i <= tokenLength; i++) {
      int possibleTurns = Math.min(turns, i - 1);
      for (int j = 1; j <= possibleTurns; j++) {
        guesses += nCk(i - 1, j - 1) * startingPositions * Math.pow(averageDegree, j);
      }
    }
    return guesses;
  }

  private double calculateShiftedVariations(Match match) {
    int shiftedCount = match.shiftedCount;
    if (shiftedCount == 0) {
      return 1;
    }

    int unshiftedCount = match.tokenLength() - shiftedCount;
    if (unshiftedCount == 0) {
      return 2;
    }

    int shiftedVariations = 0;
    int minCount = Math.min(shiftedCount, unshiftedCount);
    for (int i = 1; i <= minCount; i++) {
      shiftedVariations += nCk(shiftedCount + unshiftedCount, i);
    }
    return shiftedVariations;
  }
}
