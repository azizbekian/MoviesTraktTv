package com.azizbekian.movies.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class SearchItem {

    public Movie movie;

    public static class Movie implements Parcelable {

        public String title;
        public String year;
        public String overview;
        public Ids ids;
        public String trailer;
        public String rating;
        public Images images;

        public String getImdb() {
            return null != ids ? ids.imdb : null;
        }

        public String getThumb() {
            return images != null && images.poster != null ? images.poster.thumb : "";
        }

        public static class Ids {
            String imdb;

            @Override
            public String toString() {
                return "Ids{" +
                        "imdb='" + imdb + '\'' +
                        '}';
            }
        }

        public static class Images {
            Poster poster;

            public static class Poster {
                String thumb;

                @Override
                public String toString() {
                    return "Poster{" +
                            "thumb='" + thumb + '\'' +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "Images{" +
                        "poster=" + poster +
                        '}';
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(year);
            dest.writeString(overview);
            dest.writeString(getImdb());
            dest.writeString(trailer);
            dest.writeString(rating);
            dest.writeString(getThumb());
        }

        public static final Creator CREATOR = new Creator() {
            public Movie createFromParcel(Parcel in) {
                return new Movie(in);
            }

            public Movie[] newArray(int size) {
                return new Movie[size];
            }
        };

        public Movie(Parcel in) {
            title = in.readString();
            year = in.readString();
            overview = in.readString();
            Ids ids = new Ids();
            ids.imdb = in.readString();
            this.ids = ids;
            trailer = in.readString();
            rating = in.readString();
            Images images = new Images();
            Images.Poster poster = new Images.Poster();
            poster.thumb = in.readString();
            images.poster = poster;
            this.images = images;
        }

        @Override
        public String toString() {
            return "Movie{" +
                    "title='" + title + '\'' +
                    ", year='" + year + '\'' +
                    ", overview='" + overview + '\'' +
                    ", ids=" + ids +
                    ", trailer='" + trailer + '\'' +
                    ", rating='" + rating + '\'' +
                    ", images=" + images +
                    '}';
        }

    }

    @Override
    public String toString() {
        return movie.toString();
    }
}
