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
            if(baseline.get(shortName).size()>0) {
                return baseline.get(shortName).poll();
            }
        }
        return null;
    }
    public void loadDataForStock(String shortName, Queue<EnvRecord> data) {
        this.baseline.put(shortName, data);
    }
    public void setBaseline(HashMap<String, Queue<EnvRecord>> baseline) {
        this.baseline = baseline;
    }
}
