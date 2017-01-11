package cloud.orbit.actors.runtime.test;

import cloud.orbit.actors.runtime.AbstractPersistentStateManager;
import cloud.orbit.actors.runtime.PersistedStateActor;
import cloud.orbit.actors.runtime.RemoteReference;
import cloud.orbit.concurrent.Task;
import cloud.orbit.exception.UncheckedException;

public class TestActorImpl extends PersistedStateActor<State, TestActorImpl.ManagerPersistent> implements TestActor
{

    @Override
    public Task<?> activateAsync()
    {
        return super.activateAsync().thenReturn(() -> {
            readState().join();
            return new Object();
        });
    }

    public Task<State> getState()
    {
        return Task.fromValue(state());
    }

    public Task<Void> setFirstName(String firstName)
    {
        state.setFirstName(firstName);
        return writeState();
    }

    public Task<Void> setLastName(String lastName)
    {
        state.setLastName(lastName);
        return writeState();
    }

    public Task<Void> setAge(int age)
    {
        state.setAge(age);
        return writeState();
    }

    public Task<Void> delete()
    {
        return clearState();
    }

    static class ManagerPersistent extends AbstractPersistentStateManager<State, TestStorage>
    {
        public ManagerPersistent() {}

        @Override
        public Task<Void> write(RemoteReference<?> reference, State state)
        {
            persistentStore.writeState((String) RemoteReference.getId(reference), state);
            return Task.done();
        }

        @Override
        public Task<Void> clearState(RemoteReference<?> reference, State state)
        {
            persistentStore.clearState((String) RemoteReference.getId(reference));
            return Task.done();
        }

        @Override
        public Task<Boolean> read(RemoteReference<?> reference, State state)
        {
            Object readState = persistentStore.readState((String) RemoteReference.getId(reference));

            if (null == readState)
            {
                return Task.fromValue(true);
            }

            if (readState instanceof State)
            {
                State restoredState = (State) readState;
                state.setFirstName(restoredState.getFirstName());
                state.setLastName(restoredState.getLastName());
                state.setAge(restoredState.getAge());
            }
            else
            {
                throw new UncheckedException("Actor state class mismatch");
            }

            return Task.fromValue(true);
        }

        @Override
        public String toString()
        {
            return "ManagerPersistent{}";
        }
    }

}
