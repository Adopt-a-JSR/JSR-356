package org.bejug.tictactoe.client;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

import javax.inject.Singleton;

/**
 *
 */
public class TicTacToeApplication extends Application {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(new TicTacToeModule(this));
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }

    public ObjectGraph objectGraph() {
        return objectGraph;
    }

    @Module(entryPoints = {TicTacToeActivity.class})
    static class TicTacToeModule {
        private final Context appContext;

        public TicTacToeModule(Context applicationContext) {
            this.appContext = applicationContext;
        }

        @Provides @Singleton TicTacToeGame provideTicTacToeGame() {
            return new TicTacToeGame();
        }
    }
}
