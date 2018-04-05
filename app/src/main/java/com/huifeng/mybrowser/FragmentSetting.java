package com.huifeng.mybrowser;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huifeng.mybrowser.Models.FavoriteModel;

import java.util.ArrayList;


public class FragmentSetting extends Fragment {
    ArrayList<FavoriteModel> list = new ArrayList<>();

    RecyclerView favor_recycler_view;
    FavoriteRecyclerAdapter adapter;

    //private OnFragmentInteractionListener mListener;

    public FragmentSetting() {
        // Required empty public constructor
    }


    public static FragmentSetting newInstance(ArrayList<FavoriteModel> list) {
        FragmentSetting fragment = new FragmentSetting();
        fragment.list = list;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        favor_recycler_view = rootView.findViewById(R.id.favor_recycler_view);

        adapter = new FavoriteRecyclerAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(favor_recycler_view.getContext(), LinearLayoutManager.VERTICAL, false);

        favor_recycler_view.setLayoutManager(linearLayoutManager);
        favor_recycler_view.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(favor_recycler_view.getContext(), DividerItemDecoration.VERTICAL);
        favor_recycler_view.addItemDecoration(itemDecoration);
        favor_recycler_view.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
