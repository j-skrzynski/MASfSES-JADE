package org.example.logic.stockexchange.utils;

import java.util.HashMap;
import java.util.Queue;

public class ArtificialBaseline  {
    private HashMap<String, Queue<EnvRecord>> baseline;

    public ArtificialBaseline() {
        baseline = new HashMap<>();
    }

    public EnvRecord getNextEnvRec(String shortName){
        if(baseline.containsKey(shortName)){
            if(!baseline.get(shortName).isEmpty()) {
                return baseline.get(shortName).poll();
            }
        }

        return null;
    }

    public void loadDataForStock(String shortName, Queue<EnvRecord> data) {
        this.baseline.put(shortName, data);
    }

    public HashMap<String, Queue<EnvRecord>> getBaseline() {
        return baseline;
    }

    public void setBaseline(HashMap<String, Queue<EnvRecord>> baseline) {
        this.baseline = baseline;
    }
}
