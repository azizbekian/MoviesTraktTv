package com.azizbekian.movies.misc;

import com.azizbekian.movies.entity.SearchItem;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.List;

/**
 * Cuts off data we're not interested in.
 * <p>
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class SearchExclusionStrategy implements ExclusionStrategy {

    private List<Class> classesToRemain;

    public SearchExclusionStrategy(List<Class> classesToRemain) {
        this.classesToRemain = classesToRemain;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        String name = f.getName();
        return ((f.getDeclaringClass() == SearchItem.Movie.Images.class && !name.equals("poster"))
                || f.getDeclaringClass() == SearchItem.Movie.Images.Poster.class && !name.equals("thumb"))
                || (f.getDeclaringClass() == SearchItem.Movie.Ids.class && !name.equals("imdb"))
                || (f.getDeclaringClass() == SearchItem.Movie.class && !(name.equals("title")
                || name.equals("year") || name.equals("overview") || name.equals("images")
                || name.equals("ids")))
                || (f.getDeclaringClass() == SearchItem.class && !(name.equals("movie")));

    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return !classesToRemain.contains(clazz);
    }
}
