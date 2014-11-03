package com.practice.mydouban;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyang on 10/20/14.
 */
public class BooksData {
    private JSONObject jsonData;

    public BooksData(JSONObject jsonData) {

        this.jsonData = jsonData;
    }

    public List<Book> getBooks() {
        JSONArray array = jsonData.optJSONArray("books");
        List<Book> books = new ArrayList<Book>(array.length());

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = (JSONObject) array.opt(i);
            books.add(new Book(object.optString("title"), object.optString("image"), object.optJSONArray("author").toString(), object.optString("publisher"), object.optString("pubdate"), object.optString("summary"), object.optJSONObject("rating").optDouble("average")));
        }

        return books;
    }

    public int getTotal() {
        return jsonData.optInt("total");
    }
}
