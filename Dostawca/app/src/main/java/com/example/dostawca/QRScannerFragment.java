package com.example.dostawca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class QRScannerFragment extends Fragment {

    public QRScannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setActionBarTitle("QR Scanner");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qrscanner, container, false);
    }
}