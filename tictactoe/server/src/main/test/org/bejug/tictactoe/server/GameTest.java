package org.bejug.tictactoe.server;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author mike
 */
public class GameTest {
    @Test
    public void testRegisterMove() {
        Game game = new Game();
        Player player1 = new Player("Mike Seghers", null);
        Player player2 = new Player("Johan Vos", null);
        game.setPlayer1(player1);
        game.setPlayer2(player2);

        assertThat(game.registerMove(0, 0, player1), is(true));
        assertThat(game.registerMove(0, 0, player1), is(false));
        assertThat(game.registerMove(0, 0, player2), is(false));
        assertThat(game.registerMove(0, 1, player2), is(true));
        assertThat(game.registerMove(0, 1, player1), is(false));
        assertThat(game.registerMove(0, 1, player2), is(false));
    }
    
    @Test
    public void testCheckForWinner() {
        Game game = new Game();
        assertThat(game.checkForWinner(), is(nullValue()));
        Player player1 = new Player("Mike Seghers", null);
        assertThat(game.checkForWinner(), is(nullValue()));
        Player player2 = new Player("Johan Vos", null);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(0, 0, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(0, 1, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(0, 2, player1);
        assertThat(game.checkForWinner(), is(player1));

        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(1, 0, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 1, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 2, player2);
        assertThat(game.checkForWinner(), is(player2));


        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(2, 0, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 1, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 2, player1);
        assertThat(game.checkForWinner(), is(player1));

        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(1, 0, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 1, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 2, player2);
        assertThat(game.checkForWinner(), is(player2));


        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(2, 0, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 1, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 2, player1);
        assertThat(game.checkForWinner(), is(player1));

        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(1, 0, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 1, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 2, player2);
        assertThat(game.checkForWinner(), is(player2));


        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(2, 0, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 1, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 2, player1);
        assertThat(game.checkForWinner(), is(player1));

        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(0, 0, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 0, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 0, player2);
        assertThat(game.checkForWinner(), is(player2));

        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(0, 1, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 1, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 1, player1);
        assertThat(game.checkForWinner(), is(player1));

        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(0, 2, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 2, player2);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 2, player2);
        assertThat(game.checkForWinner(), is(player2));

        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.registerMove(0, 0, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(1, 1, player1);
        assertThat(game.checkForWinner(), is(nullValue()));
        game.registerMove(2, 2, player1);
        assertThat(game.checkForWinner(), is(player1));
    }
} 
