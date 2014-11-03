package com.practice.mydouban;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Book implements Parcelable {
    public static final Creator CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            Book book = new Book();
            book.title = source.readString();
            book.image = source.readString();
            book.author = source.readString();
            book.publisher = source.readString();
            book.publishDate = source.readString();
            book.summary = source.readString();
            book.rating = source.readDouble();
            return book;
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
    public String title;
    public String image;
    public String author;
    public String publisher;
    public String publishDate;
    public String summary;
    public double rating;

    public Book(String title, String image, String author, String publisher, String publishDate, String summary, Double rating) {
        this.title = title;
        this.image = image;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.summary = summary;
        this.rating = rating;
    }

    public Book() {

    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getSummary() {
        return summary;
    }

    public double getRating() {
        return rating;
    }

    public String getInformation() {
        return TextUtils.join("/", new String[]{
                getAuthor(), getPublisher(), getPublishDate()
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(author);
        dest.writeString(publisher);
        dest.writeString(publishDate);
        dest.writeString(summary);
        dest.writeDouble(rating);
    }
}
