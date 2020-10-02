package zm.co.hobbiton.mobicom.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import zm.co.hobbiton.mobicom.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FloatManagementFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_float_management, container, false);
    }
}