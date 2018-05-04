package uz.yura_sultonov.imageworld.utils;

import okhttp3.MediaType;

public interface Constants {

    MediaType CONSUMER_JSON = MediaType.parse("application/json; charset=utf-8");

    String API_BASE_URL = "http://pixabay.com/api/?";

    String API_KEY = "8889090-dd244e7a3cf3447dd2a869ade";

    String SORT_LATEST = "latest";

    String SORT_POPULAR = "popular";
    CharSequence[] sortTypes = {Constants.SORT_LATEST, Constants.SORT_POPULAR};

    int PER_PAGE = 20;

}
