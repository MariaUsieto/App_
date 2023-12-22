package cat.tecnocampus.mobileapps.practicafinal.mariaperegrinausieto;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

public class EditUsernameFragment extends Fragment {
    EditText etUsername;
    Button btnConfirm;
    private static final String ARG_USERNAME = "username";
    private String username;
    public EditUsernameFragment() {
        // Required empty public constructor
    }

    public static EditUsernameFragment newInstance(String username) {
        EditUsernameFragment fragment = new EditUsernameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_username, container, false);

        etUsername = view.findViewById(R.id.etUsernameEdit);
        btnConfirm = view.findViewById(R.id.btnOk);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUsername = etUsername.getText().toString();
                if(validateUsername(newUsername))
                    ((ConfigurationActivity)getActivity()).updateUsername(newUsername);
            }
        });

        return view;
    }

    private boolean validateUsername(String newUsername) {
        Pattern regex = Pattern.compile("^[a-zA-Z0-9]+$");
        if(newUsername.equals(""))
            etUsername.setError(getString(R.string.err_empty_field));
        else if(!regex.matcher(newUsername).matches())
            etUsername.setError(getString(R.string.err_incorrect_value));
        else if(username.equals(newUsername))
            etUsername.setError(getString(R.string.err_same_value));
        else return true;

        return false;
    }
}