package cloud.orbit.actors.runtime;

import cloud.orbit.concurrent.Task;

/**
 * Persistent State Manager is building block for providing more flexible way of actor's state persistence.
 * @param <T> - actor state type
 */
public interface PersistentStateManager<T>
{

    /**
     * Performs state write to storage
     * @param state - current actor state
     * @return
     */
    Task<Void> write(RemoteReference<?> reference, T state);

    /**
     * Performs removing current state from storage
     * @param state - current actor state
     * @return
     */
    Task<Void> clearState(RemoteReference<?> reference, T state);

    /**
     * Performs current state reading from storage
     * @param state - current actor state
     * @return
     */
    Task<Boolean> read(RemoteReference<?> reference, T state);

}
