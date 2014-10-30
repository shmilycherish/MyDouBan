package com.practice.mydouban;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final String BOOU_URL = "http://api.douban.com/v2/book/search?tag=%s&count=%d&start=%d";
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyBookListAdapter adapter;
    private boolean isLoaing;
    private boolean hasMoreItem;
    private ListView bookList;

    public BookListFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        bookList = (ListView) rootView.findViewById(R.id.bookList);

        bookList.setOnScrollListener(new ListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0) {
                    int lastVisibleItem = firstVisibleItem + visibleItemCount;
                    if (!isLoaing && hasMoreItem && (lastVisibleItem == totalItemCount)) {
                        doLoadMore();
                    }
                }
            }
        });

        adapter = new MyBookListAdapter(getActivity());
        bookList.setAdapter(adapter);

        doRefresh();
        return rootView;
    }

    private void doLoadMore() {
        new AsyncTask<String, Void, Books>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isLoaing = true;
            }

            @Override
            protected Books doInBackground(String... params) {
                String urlStr = params[0];
                return new Books(DataFetcher.readDataFromFile(urlStr));
            }

            @Override
            protected void onPostExecute(Books books) {
                super.onPostExecute(books);
                adapter.addAll(books.getBooks());
                hasMoreItem = books.getTotal() - bookList.getCount() > 0;
                isLoaing = false;
            }
        }.execute(getUrl(bookList.getCount()));

    }

    private void doRefresh() {
        new AsyncTask<String, Void, Books>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Books doInBackground(String... params) {
                String urlStr = params[0];
                return new Books(DataFetcher.readDataFromFile(urlStr));
            }

            @Override
            protected void onPostExecute(Books books) {
                super.onPostExecute(books);
                adapter.clear();
                adapter.addAll(books.getBooks());
                hasMoreItem = books.getTotal() - bookList.getCount() > 0;
                swipeRefreshLayout.setRefreshing(false);
            }
        }.execute(getUrl(0));
    }

    private String getUrl(int start) {
        return String.format(BOOU_URL, Uri.encode("编程"), 20, start);
    }

    @Override
    public void onRefresh() {
        doRefresh();
    }

    static class MyBookListAdapter extends BaseAdapter {
        private List<Book> books;
        private Context context;

        MyBookListAdapter(Context context) {
            this.books = new ArrayList<Book>();

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

        public void addAll(List<Book> books) {
            this.books.addAll(books);
            notifyDataSetChanged();
        }

        public void clear() {
            this.books.clear();
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        ImageView bookCover;
        TextView bookName;
        RatingBar rating;
        TextView bookInfo;
    }

}

