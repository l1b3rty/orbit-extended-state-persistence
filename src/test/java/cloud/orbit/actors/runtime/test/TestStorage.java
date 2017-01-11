package cloud.orbit.actors.runtime.test;

import cloud.orbit.actors.runtime.PersistentStore;
import infinispan.com.google.common.collect.Maps;

import java.util.Map;

public class TestStorage implements PersistentStore
{
    private Map<String, Object> internalInMemoryStorage = Maps.newHashMap();

    public void writeState(String id, Object state)
    {
        internalInMemoryStorage.put(id, state);
    }

    public Object readState(String id)
    {
        return internalInMemoryStorage.get(id);
    }

    public void clearState(String id)
    {
        internalInMemoryStorage.remove(id);
    }

    @Override
    public String toString()
    {
        return "TestInMemoryStorage";
    }

}
