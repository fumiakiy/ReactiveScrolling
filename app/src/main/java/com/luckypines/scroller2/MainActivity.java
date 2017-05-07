package com.luckypines.scroller2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static Integer[][] data = {
            {1,2,3,4,5,6,7,8,9,10}        ,
            {11,12,13,14,15,16,17,18,19,20},
            {21,22,23,24,25,26,27,28,29,30}        ,
            {31,32,33,34,35,36,37,38,39,40}
    };

    private RecyclerView list;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter<IntegerViewHolder> adapter;
    private ArrayList<Integer> values;

    private Observable<Boolean> observable;
    private CompositeDisposable disposables;

    private Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disposables = new CompositeDisposable();
        values = new ArrayList<>();

        list = (RecyclerView)findViewById(R.id.list);

        layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        adapter = new RecyclerView.Adapter<IntegerViewHolder>() {
            @Override
            public IntegerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());

                return new IntegerViewHolder(inflater.inflate(R.layout.list_item_1, parent, false));
            }

            @Override
            public void onBindViewHolder(IntegerViewHolder holder, int position) {
                int val = values.get(position);
                holder.setInteger(val);
            }

            @Override
            public int getItemCount() {
                return values.size();
            }
        };
        list.setAdapter(adapter);

        observable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> e) throws Exception {
                list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    private int lastPos;
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy <= 0)
                            return;
                        if (lastPos != layoutManager.findLastVisibleItemPosition()
                            && layoutManager.findLastVisibleItemPosition() == values.size() - 1) {
                            lastPos = layoutManager.findLastVisibleItemPosition();
                            e.onNext(true);
                        }
                    }
                });
            }
        });
        observable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(observable.subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean)
                    manager.next();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }));

        manager = new Manager();
        disposables.add(
                manager.start()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer[]>() {
                    Disposable d;
                               @Override
                               public void accept(Integer[] integers) throws Exception {
                                   for (Integer i : integers)
                                       values.add(i);
                                   adapter.notifyDataSetChanged();

                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {

                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {
                                   disposables.dispose();
                               }
                           }
                )
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.next();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    public class Manager {
        ObservableEmitter<Integer[]> emitter;
        int pos;
        boolean isWorking;

        public Manager() {
            pos = -1;
            isWorking = false;
        }

        public Observable<Integer[]> start() {
            return Observable.create(new ObservableOnSubscribe<Integer[]>() {
                @Override
                public void subscribe(ObservableEmitter<Integer[]> e) throws Exception {
                    emitter = e;
                }
            });
        }


        public void next() {
            if (isWorking)
                return;
            isWorking = true;
            emitNext();
        }

        private void emitNext() {
            pos++;
            if (pos < data.length)
                emitter.onNext(data[pos]);
            else
                emitter.onComplete();
            isWorking = false;
        }
    }
}
