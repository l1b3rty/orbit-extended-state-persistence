package cloud.orbit.actors.runtime;

import cloud.orbit.concurrent.Task;

/**
 * Provides state persistence mechanism for actors through usage of @see {PersistentStateManager}
 * @param <ST> - actor's state type
 * @param <SP> - persistent state manager type
 */
public abstract class PersistedStateActor<ST, SP extends PersistentStateManager<ST>> extends AbstractActor<ST>
{

    protected SP persistentStateManager;

    @Override
    protected Task<Void> writeState() {
        return persistentStateManager.write(reference, state());
    }

    @Override
    protected Task<Boolean> readState() {
        return persistentStateManager.read(reference, state());
    }

    @Override
    protected Task<Void> clearState() {
        return persistentStateManager.clearState(reference, state());
    }

}