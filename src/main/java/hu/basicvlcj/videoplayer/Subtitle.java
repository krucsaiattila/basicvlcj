package hu.basicvlcj.videoplayer;

public class Subtitle {

    private int spu;
    private String description;

    public Subtitle(int spu, String description){
        this.spu = spu;
        this.description = description;
    }

    public int getSpu() {
        return spu;
    }

    public void setSpu(int spu) {
        this.spu = spu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
