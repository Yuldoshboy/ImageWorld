package uz.yura_sultonov.imageworld.entities;

public enum SortTypes {
    SORT_LATEST("latest"),
    SORT_POPULAR("popular");

    private final String sortType;

    SortTypes(String type) {
        this.sortType = type;
    }

    public String valueStr() {
        return sortType;
    }

    public static CharSequence[] ALL() {
        return new CharSequence[]{SORT_LATEST.sortType, SORT_POPULAR.sortType};
    }

    public static CharSequence[] getTitles() {
        return new CharSequence[]{"Популярные", "Популярные"};
    }
}
