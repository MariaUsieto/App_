package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConfigurationFragment extends Fragment {

    private static final String ARG_EMAIL = "email";
    private static final String ARG_USERNAME = "username";
    private String username;
    private String email;

    TextView tvEmail, tvUsername, tvLogout, tvMaps, tvApi;
    public ConfigurationFragment() {
        // Required empty public constructor
    }
    public static ConfigurationFragment newInstance(String username, String email) {
        ConfigurationFragment fragment = new ConfigurationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(ARG_EMAIL);
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_configuration, container, false);

        ((ConfigurationActivity)getActivity()).getWindow().setBackgroundDrawableResource(R.color.blue);

        tvEmail = view.findViewById(R.id.tvEmail);
        tvUsername = view.findViewById(R.id.tvUsernameEdit);
        tvLogout = view.findViewById(R.id.tvLogout);
        tvMaps = view.findViewById(R.id.tvGoogleMaps);
        tvApi = view.findViewById(R.id.tvApi);

        tvEmail.setText(email);
        tvUsername.setText(username+"\t");
        tvLogout.setText(getString(R.string.tv_logout)+"\t\t");
        tvMaps.setText(getString(R.string.tv_open_google_maps)+"\t");
        tvApi.setText(getString(R.string.tv_bored_api)+"\t");

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmLogout();
            }
        });

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              ((ConfigurationActivity)getActivity()).showEditUsernameFragment(username);
            }
        });

        tvMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        tvApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.boredapi.com/documentation"));
                startActivity(intent);
            }
        });

        return view;
    }

    private void confirmLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage(getString(R.string.dialog_question_cancel))
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((ConfigurationActivity)getActivity()).logout();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}