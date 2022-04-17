package in.adarsh.mestfriend.payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import in.adarsh.mestfriend.R;
import in.adarsh.mestfriend.onboarding.User;

public class FinalPaymentActivity extends AppCompatActivity {

    TextView amoutView;
    EditText passEdit;
    Button payBtn;
    private int balance;
    private int amount;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_payment);
        setupView();
        dialog.setMessage("Loading Data...");
        dialog.setCancelable(false);
        Intent intent=getIntent();
        amount=intent.getIntExtra("balance",0);
        amoutView.setText(amount+"Rs");
        dialog.show();
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user=documentSnapshot.toObject(User.class);
                balance=user.getBalance();
                dialog.dismiss();
            }
        });
        payBtn.setOnClickListener(v->
        {
            String roll=passEdit.getText().toString().trim();
            if(roll!=null)
            {
                setupPayment(roll);
            }
        });
    }

    private void setupPayment(String roll) {
        if(balance<amount)
        {
            Toast.makeText(this, "Insufficient Balance!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            dialog.show();
            balance=balance-amount;
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                    .update("balance",balance).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
    }

    private void setupView() {
        dialog=new ProgressDialog(this);
        amoutView=findViewById(R.id.amount_final);
        passEdit=findViewById(R.id.payment_final);
        payBtn=findViewById(R.id.final_pay);
    }
}