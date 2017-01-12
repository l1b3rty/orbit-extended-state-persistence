package cloud.orbit.actors.runtime;

/**
 * Abstract implementation that contains reference to specified persistent store.
 * @param <T> - actor state type
 * @param <S> - persistent store extension type
 */
public abstract class AbstractPersistentStateManager<T, S extends PersistentStore> implements PersistentStateManager<T>
{

    protected S persistentStore;

    void setPersistentStore(S store)
    {
        persistentStore = store;
    }

}
