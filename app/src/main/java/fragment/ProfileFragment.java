package fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;

import db.management.DataFlow;
import de.hdodenhof.circleimageview.CircleImageView;
import login.LoginManagement;
import login.SecureData;
import polito.lab.anes.eatnow.R;
import user.UserClass;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private View myProfileView;
    private Button saveBtnProfile;
    private Button changePasswordBtnProfile;
    private Button logoutBtnProfileFrag;
    private EditText nameProfileTxV ;
    private EditText fnameProfileTxV ;
    private EditText emailProfileTxV ;
    private EditText phoneProfileTxV ;
    private EditText bdayProfileTxV ;
    private TextView accountTypeProfileTxV ;
    private CircleImageView civ;

    private final int SELECT_PHOTO = 1 ;
    private final int RESULT_OK = -1 ;
    final private SecureData sd = new SecureData();
    final private LoginManagement lm = new LoginManagement();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myProfileView =  inflater.inflate(R.layout.fragment_profile, container, false);
        saveBtnProfile = (Button) myProfileView.findViewById(R.id.saveBtnProfile);
        changePasswordBtnProfile = (Button) myProfileView.findViewById(R.id.changePasswordBtnProfile);
        logoutBtnProfileFrag = (Button) myProfileView.findViewById(R.id.logoutBtnProfileFrag);
        nameProfileTxV = (EditText) myProfileView.findViewById(R.id.nameProfileTxV);
        fnameProfileTxV = (EditText) myProfileView.findViewById(R.id.fnameProfileTxV);
        emailProfileTxV = (EditText) myProfileView.findViewById(R.id.emailProfileTxV);
        phoneProfileTxV = (EditText) myProfileView.findViewById(R.id.phoneProfileTxV);
        bdayProfileTxV = (EditText) myProfileView.findViewById(R.id.bdayProfileTxV);
        accountTypeProfileTxV = (TextView) myProfileView.findViewById(R.id.accountTypeProfileTxV);
        civ = (CircleImageView) myProfileView.findViewById(R.id.profile_image);
        civ.setOnClickListener(this);
        saveBtnProfile.setOnClickListener(this);
        changePasswordBtnProfile.setOnClickListener(this);
        logoutBtnProfileFrag.setOnClickListener(this);
        fillProfileView();
        return myProfileView;
    }


    private void setImageFromProfile(String imagefilename){

        DataFlow df = new DataFlow();
        Bitmap bitmap = df.loadImageFromStorage(imagefilename);
        if(bitmap != null){
            civ.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == SELECT_PHOTO){
            if(resultCode == RESULT_OK){
                Uri profilePicUri = data.getData();
                DataFlow df= new DataFlow();
                LoginManagement ml = new LoginManagement();
                try {
                    Bitmap bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), profilePicUri);
                    BaasUser bu = BaasUser.current();
                    String pictureFilename = "picture." + (String) bu.getScope(BaasUser.Scope.PRIVATE).getString("accountType") + bu.getScope(BaasUser.Scope.PRIVATE).getString("fname") + bu.getScope(BaasUser.Scope.PRIVATE).getString("name");
                    String tmp = (String) bu.getScope(BaasUser.Scope.PRIVATE).getString("id");
                    String pictureFullPath = df.saveImageToInternalStorage(bitmap,pictureFilename,getContext());
                    bu.getScope(BaasUser.Scope.PRIVATE).put("profile_pic",pictureFullPath);
                    lm.saveBaasUser();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                civ.setImageURI(profilePicUri);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveBtnProfile :
                saveChanges();
                break;
            case R.id.changePasswordBtnProfile :
                createDialog();
                break;
            case R.id.logoutBtnProfileFrag :
                lm.logout(getContext());
                break;
            case R.id.profile_image :
                Intent intentNewPic = new Intent(Intent.ACTION_PICK);
                intentNewPic.setType("image/*");
                startActivityForResult(intentNewPic,SELECT_PHOTO);
                break;
        }
    }

    private void createDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Please enter the last and new Password");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertDialogBuilder
                .setView(inflater.inflate(R.layout.dialog_change_password, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        if(validForm(dialog)){
                            UserClass uc = new UserClass();
                            EditText newTxV = (EditText) ((AlertDialog) dialog).findViewById(R.id.newPassword);
                            try {
                                uc.changPassword(sd.getMySha256(newTxV.getText().toString()));
                                Toast.makeText(getContext(), "Password Changed !", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private boolean validForm(DialogInterface dialog){

        EditText prevTxV = (EditText) ((AlertDialog) dialog).findViewById(R.id.previousPassword);
        EditText newTxV = (EditText) ((AlertDialog) dialog).findViewById(R.id.newPassword);
        EditText retypeTxv = (EditText) ((AlertDialog) dialog).findViewById(R.id.retypingPassword);
        if(prevTxV != null && newTxV != null && prevTxV != null){
        String pPass = prevTxV.getText().toString().trim();
        String nPass = newTxV.getText().toString().trim();
        String rPass = retypeTxv.getText().toString().trim();
        if(TextUtils.isEmpty(pPass)){
            prevTxV.setError("Cannot Be Empty");
            return false;
        }else if(TextUtils.isEmpty(nPass)){
            newTxV.setError("Cannot Be Empty");
            return false;
        }else if(TextUtils.isEmpty(rPass)){
            retypeTxv.setError("Cannot Be Empty");
            return false;
        }else if(!rPass.equals(nPass)){
                retypeTxv.setError("Value differs");
            return false;
        }else{
            try {
                BaasUser current = BaasUser.current();
                if(checkPass(prevTxV,current)){
                    return true;
                }else{
                    prevTxV.setError("Incorrect Pasword");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        }
        return false;
    }
    private boolean checkPass(TextView prevTxV, BaasUser current) throws Exception {
        String tmp =sd.getMySha256(prevTxV.getText().toString());
        return tmp.equals(current.getPassword());
    }

    private void fillProfileView(){
        // TODO : get the info of from the profile
        BaasUser bu = BaasUser.current();

        nameProfileTxV.setText(bu.getScope(BaasUser.Scope.PRIVATE).getString("name"));
        fnameProfileTxV.setText(bu.getScope(BaasUser.Scope.PRIVATE).getString("fname"));
        emailProfileTxV.setText(bu.getScope(BaasUser.Scope.PRIVATE).getString("email"));
        phoneProfileTxV.setText(bu.getScope(BaasUser.Scope.PRIVATE).getString("phone"));
        bdayProfileTxV.setText(bu.getScope(BaasUser.Scope.PRIVATE).getString("bday"));
        accountTypeProfileTxV.setText("Account Type : " + bu.getScope(BaasUser.Scope.PRIVATE).getString("accountType"));
        String imagefilename = bu.getScope(BaasUser.Scope.PRIVATE).getString("profile_pic");
        if( imagefilename != null){
            setImageFromProfile(imagefilename);
        }


    }

    private void saveChanges(){
        lm.createProfileFromView(myProfileView);
        lm.uploadProfile();
        Toast.makeText(getContext(), "saved", Toast.LENGTH_LONG).show();

    }
}