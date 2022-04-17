package in.adarsh.mestfriend.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import in.adarsh.mestfriend.R;
import in.adarsh.mestfriend.complaint.Complaint;
import in.adarsh.mestfriend.complaint.ComplaintActivity;

public class ComplaintFragment extends Fragment {

    EditText issue;
    EditText des;
    ImageView imgView;
    private static final int IMAGE_CODE=1;
    Button upload,submit;
    Uri downloadURL;
    StorageReference mstorageReference;
    FirebaseFirestore dbs = FirebaseFirestore.getInstance();
    private final CollectionReference complaintRef = dbs.collection("ComplaintSection");

    Button b1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComplaintFragment() {
        // Required empty public constructor
    }

    public static ComplaintFragment newInstance(String param1, String param2) {
        ComplaintFragment fragment = new ComplaintFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView=inflater.inflate(R.layout.fragment_complaint, container, false);
        issue=rootView.findViewById(R.id.line_1);
        des=rootView.findViewById(R.id.line_2);
        upload=rootView.findViewById(R.id.upload_button);

        imgView=rootView.findViewById(R.id.rectangle_4);
        submit=rootView.findViewById(R.id.file_complaint_button);
        b1=rootView.findViewById(R.id.file_complaint_button);

        mstorageReference= FirebaseStorage.getInstance().getReference().child("images");
        setup();
        return rootView;
    }

    public void setup() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                f(v);

            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent().setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Complete action using"), IMAGE_CODE);


            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            assert data != null;
            Uri selected = data.getData();
            try {
                Bitmap currentImage = MediaStore.Images.Media.getBitmap
                        (getActivity().getContentResolver(), selected);


                imgView.setImageBitmap(currentImage);
                Toast.makeText(getActivity(), "img uploaded", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            StorageReference photoRef = mstorageReference.child(selected.getLastPathSegment());
            Toast.makeText(getActivity(), "Loading", Toast.LENGTH_SHORT).show();

            photoRef.putFile(selected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    downloadURL = urlTask.getResult();
                }
            });
        }
    }
    public void f(View v) {
        String s = String.valueOf(downloadURL);
        String s1 = issue.getText().toString();
        String s2 = des.getText().toString();
        Complaint complaint = new Complaint(s1, s2, s);
        complaintRef.add(complaint);

        Toast.makeText(getActivity(), "complaint added", Toast.LENGTH_SHORT).show();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileComplaint(v);
            }
        });

    }
    public void fileComplaint(View v)
    {
        Intent intent = new Intent(getActivity(), ComplaintActivity.class);
        startActivity(intent);
    }

}