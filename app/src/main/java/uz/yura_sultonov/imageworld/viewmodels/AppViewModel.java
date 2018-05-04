package uz.yura_sultonov.imageworld.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import uz.yura_sultonov.imageworld.entities.ImageHits;
import uz.yura_sultonov.imageworld.entities.SortTypes;

public class AppViewModel extends AndroidViewModel {

    private ArrayList<ImageHits> images;
    private SortTypes sortType;
    private String searchKey;

    public AppViewModel(@NonNull Application application) {
        super(application);

        this.images = new ArrayList<>();
        this.searchKey = "";
        this.sortType = SortTypes.SORT_LATEST;
    }

    public void addNextPartData(List<ImageHits> hits) {
        images.addAll(hits);
    }

    public ArrayList<ImageHits> getImages() {
        return images;
    }

    public void clearAllData() {
        images.clear();
    }

    public SortTypes getSortType() {
        return sortType;
    }

    public void setSortType(SortTypes sortType) {
        this.sortType = sortType;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }
}
