package cloud.orbit.actors.runtime.test;

import cloud.orbit.actors.Actor;
import cloud.orbit.actors.Stage;
import cloud.orbit.actors.runtime.ActorPersistenceSupport;
import cloud.orbit.actors.test.ActorBaseTest;
import cloud.orbit.actors.test.TestInvocationLog;
import cloud.orbit.actors.test.TestLifecycleLog;
import cloud.orbit.actors.test.TestLogger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ActorPersistenceTest extends ActorBaseTest
{
    private TestStorage storage = new TestStorage();
    private Stage stage;

    @Before
    public void setup()
    {
        stage = new Stage.Builder()
            .clusterName("test-cluster")
            .extensions(
                new TestLogger(this.loggerExtension, "s" + this.stages.size()),
                new TestInvocationLog(this.loggerExtension, "s" + this.stages.size()),
                new TestLifecycleLog(this.loggerExtension, "s" + this.stages.size()),
                new ActorPersistenceSupport(),
                storage)
            .build();

        stage.start().join();
        stage.bind();
    }

    @After
    public void tearDown()
    {
        stage.stop().join();
    }


    @Test
    public void actorShouldBeInDefaultState()
    {
        TestActor actor = Actor.getReference(TestActor.class, "1");

        State actorState = actor.getState().join();

        Assert.assertEquals("", actorState.getFirstName());
        Assert.assertEquals("", actorState.getLastName());
        Assert.assertEquals(0, actorState.getAge());
    }

    @Test
    public void actorStateShouldBeReadFromStorage()
    {
        State savedState = new State();
        savedState.setFirstName("John");
        savedState.setLastName("Smith");
        savedState.setAge(34);

        storage.writeState("1", savedState);

        TestActor actor = Actor.getReference(TestActor.class, "1");

        State actorState = actor.getState().join();

        Assert.assertEquals("John", actorState.getFirstName());
        Assert.assertEquals("Smith", actorState.getLastName());
        Assert.assertEquals(34, actorState.getAge());
    }

    @Test
    public void actorStateAndStorageShouldBeUpdated()
    {
        State savedState = new State();
        savedState.setFirstName("John");
        savedState.setLastName("Smith");
        savedState.setAge(34);

        storage.writeState("1", savedState);

        TestActor actor = Actor.getReference(TestActor.class, "1");

        State actorState = actor.setFirstName("James")
            .thenApply(aVoid -> actor.getState().join())
            .join();

        Assert.assertEquals("James", actorState.getFirstName());
        Assert.assertEquals("Smith", actorState.getLastName());
        Assert.assertEquals(34, actorState.getAge());

        State stateFromStorage = (State) storage.readState("1");

        Assert.assertEquals("James", stateFromStorage.getFirstName());
        Assert.assertEquals("Smith", stateFromStorage.getLastName());
        Assert.assertEquals(34, stateFromStorage.getAge());

    }

    @Test
    public void actorStateShouldBeRemovedFromStorage()
    {
        State savedState = new State();
        savedState.setFirstName("John");
        savedState.setLastName("Smith");
        savedState.setAge(34);

        storage.writeState("1", savedState);

        TestActor actor = Actor.getReference(TestActor.class, "1");

        actor.delete().join();

        State stateFromStorage = (State) storage.readState("1");

        Assert.assertEquals(null, stateFromStorage);
    }

}


