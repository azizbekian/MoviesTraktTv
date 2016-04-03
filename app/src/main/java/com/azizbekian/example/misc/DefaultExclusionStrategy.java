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
public class DefaultExclusionStrategy implements ExclusionStrategy {

    private List<Class> classesToRemain;

    public DefaultExclusionStrategy(List<Class> classesToRemain) {
        this.classesToRemain = classesToRemain;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getDeclaringClass() == SearchItem.Movie.Images.Poster.class && !f.getName().equals("thumb"))
                || (f.getDeclaringClass() == SearchItem.Movie.Ids.class && !f.getName().equals("imdb"))
                || (f.getDeclaringClass() == SearchItem.Movie.Images.class && !f.getName().equals("poster"))
                || (f.getDeclaringClass() == SearchItem.Movie.class && !(f.getName().equals("title") || f.getName().equals("year") || f.getName().equals("overview") || f.getName().equals("trailer") || f.getName().equals("rating") || f.getName().equals("ids") || f.getName().equals("images")));
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return !classesToRemain.contains(clazz);
    }
}
