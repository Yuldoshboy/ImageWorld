package uz.yura_sultonov.imageworld.entities;

import java.util.List;

public class ImageResponse {

    private String total;

    private List<ImageHits> hits;

    private int totalHits;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ImageHits> getHits() {
        return hits;
    }

    public void setHits(List<ImageHits> hits) {
        this.hits = hits;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    @Override
    public String toString() {
        return "ImageResponse [total = " + total + ", hits = " + hits + ", totalHits = " + totalHits + "]";
    }

}
