package com.azizbekian.example.misc;

import com.azizbekian.example.entity.SearchItem;
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

    private List<Class> classsesToRemain;

    public SearchExclusionStrategy(List<Class> classesToRemain) {
        this.classsesToRemain = classesToRemain;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getDeclaringClass() == SearchItem.Movie.Images.Poster.class && !f.getName().equals("thumb"))
                || (f.getDeclaredClass() == SearchItem.Movie.Ids.class)
                || (f.getDeclaringClass() == SearchItem.Movie.Images.class && !f.getName().equals("poster"))
                || (f.getDeclaringClass() == SearchItem.Movie.class && !(f.getName().equals("title") || f.getName().equals("year") || f.getName().equals("overview") || f.getName().equals("images")))
                || (f.getDeclaringClass() == SearchItem.class && !(f.getName().equals("movie")));

    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return !classsesToRemain.contains(clazz);
    }
}
