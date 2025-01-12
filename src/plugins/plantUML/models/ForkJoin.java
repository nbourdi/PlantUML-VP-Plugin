package plugins.plantUML.models;

public class ForkJoin extends StateData{

    private boolean isFork; // else Join

    public ForkJoin(String name) {
        super(name);
    }

    public boolean isFork() {
        return isFork;
    }

    public void setFork(boolean fork) {
        isFork = fork;
    }
}
