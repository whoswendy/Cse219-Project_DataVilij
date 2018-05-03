package algorithm;

/**
 * @author Ritwik Banerjee
 */
public abstract class Clusterer implements Algorithm {

    protected final int numberOfClusters;
    protected boolean stop;

    public int getNumberOfClusters() { return numberOfClusters; }

    public boolean getStop(){return stop; }

    public abstract void resume();

    public abstract void guarded();

    public abstract DataSet getDataset();

    public Clusterer(int k) {
        if (k < 2)
            k = 2;
        else if (k > 4)
            k = 4;
        numberOfClusters = k;
    }
}
