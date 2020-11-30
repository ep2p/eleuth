package com.github.ep2p.eleuth.repository;

import com.github.ep2p.kademlia.node.KademliaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EleuthKademliaRepository implements KademliaRepository<Key, String> {
    private final Map<Key, String> data = new ConcurrentHashMap<>();

    @Override
    public void store(Key key, String value) {
        data.putIfAbsent(key, value);
    }

    @Override
    public String get(Key key) {
        return data.get(key);
    }

    @Override
    public void remove(Key key) {
        data.remove(key);
    }

    @Override
    public boolean contains(Key key) {
        return data.containsKey(key);
    }

    @Override
    public List<Key> getKeys() {
        return new ArrayList<>(data.keySet());
    }
}
