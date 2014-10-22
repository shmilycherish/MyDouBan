package com.practice.mydouban;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class BookListFragment extends Fragment {


    public BookListFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        final ListView bookList = (ListView) rootView.findViewById(R.id.bookList);
        new AsyncTask<String, Void, List<Book>>() {
            @Override
            protected List<Book> doInBackground(String... params) {
                String urlStr = params[0];
                return new Books().getBooks(DataFetcher.readDataFromFile(urlStr));
            }

            @Override
            protected void onPostExecute(List<Book> books) {
                super.onPostExecute(books);
                bookList.setAdapter(new MyBookListAdapter(books, getActivity()));
            }
        }.execute("http://api.douban.com/v2/book/search?tag=%E8%AE%A1%E7%AE%97%E6%9C%BA");
        return rootView;
    }

    static class MyBookListAdapter extends BaseAdapter {
        private List<Book> books;
        private Context context;

        MyBookListAdapter(List<Book> books, Context context) {
            this.books = books;

            this.context = context;
        }

        @Override
        public int getCount() {
            return books.size();

        }

        @Override
        public Book getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_book, parent, false);
                viewHolder.bookCover = (ImageView) convertView.findViewById(R.id.bookCover);
                viewHolder.bookName = (TextView) convertView.findViewById(R.id.bookName);
                viewHolder.rating = (RatingBar) convertView.findViewById(R.id.rating);
                viewHolder.bookInfo = (TextView) convertView.findViewById(R.id.bookInfo);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Book book = getItem(position);

            viewHolder.bookCover.setTag(book.getImage().hashCode());
            ImageLoader.loadImage(book.getImage(), new ImageLoader.ImageLoaderListener() {
                @Override
                public void onImageLoaded(Bitmap bitmap) {

                    if (bitmap != null && viewHolder.bookCover.getTag().equals(book.getImage().hashCode())) {
                        viewHolder.bookCover.setImageBitmap(bitmap);
                    }
                }
            });
            viewHolder.bookName.setText(book.getTitle());
            viewHolder.rating.setRating((float) (book.getRating() / 2));
            viewHolder.bookInfo.setText(book.getInformation());
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView bookCover;
        TextView bookName;
        RatingBar rating;
        TextView bookInfo;
    }

}

