package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import polito.lab.anes.eatnow.R;
import polito.lab.anes.eatnow.activity.LoginActivity;


public class NotLoggedProfileFragment extends Fragment implements View.OnClickListener{
    private View myOwnFragView;
    private ImageButton logBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myOwnFragView = inflater.inflate(R.layout.fragment_not_logged_profile, container, false);
        logBtn = (ImageButton) myOwnFragView.findViewById(R.id.logBtn);
        logBtn.setOnClickListener(this);

        return myOwnFragView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logBtn :
                Intent launcherIntent = new Intent();
                launcherIntent = new Intent(getActivity(), LoginActivity.class);
                launcherIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(launcherIntent);
                break;
        }
    }
}
