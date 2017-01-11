package cloud.orbit.actors.runtime.test;


import cloud.orbit.actors.Actor;
import cloud.orbit.concurrent.Task;

public interface TestActor extends Actor
{

    Task<State> getState();

    Task<Void> setFirstName(String firstName);

    Task<Void> setLastName(String lastName);

    Task<Void> setAge(int age);

    Task<Void> delete();

}
