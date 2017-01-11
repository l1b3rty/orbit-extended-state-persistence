package cloud.orbit.actors.runtime;

import cloud.orbit.actors.extensions.LifetimeExtension;
import cloud.orbit.concurrent.Task;
import cloud.orbit.core.shaded.com.googlecode.gentyref.GenericTypeReflector;
import cloud.orbit.exception.UncheckedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ActorPersistenceSupport implements LifetimeExtension
{

    @Override
    public Task<?> preActivation(AbstractActor<?> actor)
    {
        if (PersistedStateActor.class.isInstance(actor))
        {
            PersistedStateActor persistedStateActor = (PersistedStateActor) actor;

            Class<?> persistentManagerClass = persistentManagerClass(persistedStateActor);

            if (AbstractPersistentStateManager.class.isAssignableFrom(persistentManagerClass))
            {
                AbstractPersistentStateManager<?, PersistentStore> persistentManager = instantiatePersistentManager(persistentManagerClass);
                Class<? extends PersistentStore> persistentStoreClass = persistenceStoreClass(persistentManager);
                PersistentStore persistentStore = persistedStateActor.runtime.getFirstExtension(persistentStoreClass);
                persistentManager.setPersistentStore(persistentStore);
                persistedStateActor.persistentStateManager = persistentManager;
            }
        }
        return Task.done();
    }

    @SuppressWarnings("unchecked")
    private Class<? extends PersistentStateManager> persistentManagerClass(PersistedStateActor actor)
    {
        final Class<? extends PersistedStateActor> aClass = actor.getClass();

        return (Class<? extends PersistentStateManager>)
            GenericTypeReflector.getTypeParameter(aClass, PersistedStateActor.class.getTypeParameters()[1]);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends PersistentStore> persistenceStoreClass(AbstractPersistentStateManager state)
    {
        final Class<? extends AbstractPersistentStateManager> aClass = state.getClass();

        return (Class<? extends PersistentStore>)
            GenericTypeReflector.getTypeParameter(aClass, AbstractPersistentStateManager.class.getTypeParameters()[1]);
    }

    @SuppressWarnings("unchecked")
    private AbstractPersistentStateManager<?, PersistentStore> instantiatePersistentManager(Class<?> persistentManagerClass)
    {
        Constructor constructor;
        try
        {
            constructor = persistentManagerClass.getConstructor();

            if (null == constructor) {
                constructor = persistentManagerClass.getDeclaredConstructor();
            }
            if (null != constructor) {
                constructor.setAccessible(true);
            } else {
                throw new UncheckedException("Default ctor not found");
            }
            return (AbstractPersistentStateManager<?, PersistentStore>) constructor.newInstance();
        }
        catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
        {
            throw new UncheckedException(e);
        }
    }

}
