package algorithm;

import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer{

    private DataSet       dataset;
    private final int           maxIterations;
    private final int           updateInterval;
    private final AtomicBoolean tocontinue;

    public RandomClusterer(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters) {
        super(numberOfClusters);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(false);
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    @Override
    public void run() {

    }
}
