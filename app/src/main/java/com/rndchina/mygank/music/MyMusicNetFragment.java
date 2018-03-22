package com.rndchina.mygank.music;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.rndchina.mygank.R;
import com.rndchina.mygank.base.BaseView;
import com.rndchina.mygank.base.Result;
import com.rndchina.mygank.music.model.VideoCategoryInfo;
import com.rndchina.mygank.music.model.VideoData;
import com.rndchina.mygank.music.util.Util;
import com.rndchina.mygank.net.Api;
import com.rndchina.mygank.net.HttpManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PC on 2018/1/30.
 */
public class MyMusicNetFragment extends BaseMucicFragment implements BaseView {

    @BindView(R.id.rv_video)
    RecyclerView mRvcyclerView;
    @BindView(R.id.srl)
    SwipeRefreshLayout mSrl;
    @BindView(R.id.tv_nonetwork)
    TextView mTvNonetwork;
    @BindView(R.id.avi)
    AVLoadingIndicatorView mAvi;
    private CategoriesAdapter mCategoriesAdapter;
    private VideoAdapter mVideoAdapter;
    List<VideoCategoryInfo> mCategoryList = new ArrayList<>();
    List<VideoData> mVideoDataList = new ArrayList<>();
    private String deviceId;
    private int mPage = 1;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        deviceId = Util.getDeviceId(mContext);
        initAdapter();
        showLoading();
        initListener();
        getDataFromNet();
    }

    private void initListener() {
        mVideoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VideoData videoData = (VideoData) adapter.getItem(position);
                Intent intent = new Intent(mContext, VideoDetailActivity.class);
                intent.putExtra("videoData",videoData);
                startActivity(intent);
            }
        });
    }

    private void getDataFromNet() {

        Api api = HttpManager.getInstance().getVideoApiService();
        api.getVideoList("api","getList",mPage,2,"0","0","android","1.3.0", Util.getCurrentSeconds(), deviceId,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Result.Data<List<VideoData>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Result.Data<List<VideoData>> listData) {
                        mVideoDataList.clear();
                        mVideoDataList = listData.getDatas();
                        mVideoAdapter.setNewData(mVideoDataList);
                        mVideoAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        hideLoading();

                    }

                    @Override
                    public void onComplete() {

                        hideLoading();

                    }
                });

    }

    private void initAdapter() {
        mRvcyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        mVideoAdapter = new VideoAdapter(R.layout.item_videopage,mVideoDataList);
        mRvcyclerView.setAdapter(mVideoAdapter);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_net, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void showLoading() {

        mAvi.smoothToShow();

    }

    @Override
    public void hideLoading() {

        mAvi.smoothToHide();

    }

    @Override
    public void stopRefush() {

    }

    @Override
    public void startLodingMore() {

    }

    @Override
    public void stopLodingMore() {

    }

    @Override
    public void loadMoreErr() {

    }

    @Override
    public void loadComplete() {

    }
}
