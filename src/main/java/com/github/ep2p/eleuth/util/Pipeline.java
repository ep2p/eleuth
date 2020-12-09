package com.github.ep2p.eleuth.util;

import java.util.ArrayList;
import java.util.List;

public class Pipeline<I, O> {
    private List<Stage<I,O>> stages = new ArrayList<>();

    public Pipeline() {
    }

    public Pipeline<I,O> addStage(Stage<I,O> stage){
        stages.add(stage);
        return this;
    }

    public void run(I i, O o){
        for(Stage<I, O> stage: stages){
            if (!stage.process(i, o)) {
                break;
            }
        }
    }

    public interface Stage<I, O> {
        /**
         * @param i Input reference
         * @param o Output reference
         * @return boolean: determines if next stage should run
         */
        boolean process(I i, O o);
    }
}