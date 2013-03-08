package org.bejug.tictactoe.server;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author mike
 */
public class InMemoryGameRegistryTest {
    private InMemoryGameRegistry registry;


    @Before
    public void setUp() throws Exception {
        registry = new InMemoryGameRegistry();
    }

    @Test
    public void testGetAvailableGame() throws Exception {
        Game game = registry.getAvailableGame();
        Game sameGame = registry.getAvailableGame();
        Game andAgainSameGame = registry.getAvailableGame();

        assertThat(game, is(sameInstance(sameGame)));
        assertThat(sameGame, is(sameInstance(andAgainSameGame)));

        game.setPlayer1(new Player("dummy", null));

        Game stillTheSame = registry.getAvailableGame();
        assertThat(stillTheSame, is(sameInstance(game)));
        game.setPlayer2(new Player("dummy", null));
        Game newGame = registry.getAvailableGame();
        assertThat(newGame, is(not(sameInstance(game))));
    }

    @Test
    public void testGameHasFinishedNotInList() throws Exception {
        Game game = new Game();
        registry.gameHasFinished(game);
    }

    @Test
    public void testGameHasFinishedOnlyOnGameLeftThenAskNewOne() throws Exception {
        Game game = registry.getAvailableGame();

        registry.gameHasFinished(game);

        Game newGame = registry.getAvailableGame();

        assertThat(newGame, is(not(sameInstance(game))));
    }
}
