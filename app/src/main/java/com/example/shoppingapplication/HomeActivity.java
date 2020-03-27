package com.example.shoppingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingapplication.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    private TextView total_amount;
  private Toolbar toolbar;
  private FloatingActionButton fab_btn;
  private RecyclerView recyclerView;
  private String types;
  private int amount;
  private String note;
  private String post_key;
  private DatabaseReference mDatabase;
  private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar=findViewById(R.id.home_toolbar);
        total_amount=findViewById(R.id.total_amount);
        setSupportActionBar(toolbar);
        recyclerView=findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uId=mUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uId);
        mDatabase.keepSynced(true);
        getSupportActionBar().setTitle("Daily Shopping List");
        fab_btn=findViewById(R.id.fab);
        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog();

            }
        });

        //sum of total amount

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAmount=0;
                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    Data data=snap.getValue(Data.class);
                    totalAmount+=data.getAmount();
                    String total=String.valueOf(totalAmount+".00");
                    total_amount.setText(total);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void customDialog() {
        final AlertDialog.Builder mydialog=new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);
        View view=inflater.inflate(R.layout.input_data,null);
        final AlertDialog dialog=mydialog.create();
        dialog.setView(view);
        final EditText type=view.findViewById(R.id.edt_type);
        final EditText amount=view.findViewById(R.id.edit_amount);
        final EditText note=view.findViewById(R.id.edt_note);
        Button btnsave=view.findViewById(R.id.btn_save);
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mtype=type.getText().toString().trim();
                String mamount=amount.getText().toString().trim();
                String mnote=note.getText().toString().trim();


                if(!TextUtils.isEmpty(mtype) && !TextUtils.isEmpty(mamount) && !TextUtils.isEmpty(mnote)) {

                   try{
                       int ammint=Integer.parseInt(mamount);


                       String id = mDatabase.push().getKey();
                       String date = DateFormat.getDateInstance().format(new Date());

                       Data data = new Data(mtype, ammint, mnote, date, id);
                       mDatabase.child(id).setValue(data);
                       Toast.makeText(HomeActivity.this, "Data Add", Toast.LENGTH_SHORT).show();
                       dialog.dismiss();
                   }
                   catch (NumberFormatException e)
                   {

                   }
                }
                else{
                    Toast.makeText(HomeActivity.this, "please fill all the fields...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter=new FirebaseRecyclerAdapter<Data,MyViewHolder>(
                Data.class,R.layout.item_data,MyViewHolder.class,mDatabase) {

            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data data, final int i) {
                viewHolder.setDate(data.getDate());
                viewHolder.setType(data.getType());
                viewHolder.setNote(data.getNote());
                viewHolder.setAmount(data.getAmount());
                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(i).getKey();
                        types = data.getType();
                        note = data.getNote();
                        amount = data.getAmount();
                        updateData();
                    }
                });
            }


        };
        recyclerView.setAdapter(adapter);

    }
    public static  class MyViewHolder extends RecyclerView.ViewHolder{
        View myview;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
          myview=itemView;
        }
        public void setType(String type)
        {
            TextView mtype= myview.findViewById(R.id.type);
            mtype.setText(type);
        }
        public void setNote(String note)
        {
            TextView mnote= myview.findViewById(R.id.note);
            mnote.setText(note);
        }
        public void setDate(String date){
            TextView mDate= myview.findViewById(R.id.date);
            mDate.setText(date);
        }
        public void setAmount(int amount)
        {
            TextView mAmount= myview.findViewById(R.id.amount);
            String stam=String.valueOf(amount);
            mAmount.setText(stam);
        }
    }

    public void updateData()
    {
        AlertDialog.Builder mydialog=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);
        View view=inflater.inflate(R.layout.update_data,null);
        final AlertDialog dialog=mydialog.create();
        dialog.setView(view);
        final  EditText edt_type=view.findViewById(R.id.edt_type_upd);
        final  EditText edt_Ammount=view.findViewById(R.id.edit_amount_upd);
        final  EditText edt_note=view.findViewById(R.id.edt_note_upd);

        edt_type.setText(types);
        edt_type.setSelection(types.length());
        edt_Ammount.setText(String.valueOf(amount));
        edt_Ammount.setSelection(String.valueOf(amount).length());
        edt_note.setText(note);
        edt_note.setSelection(note.length());
        Button btnUpdate=view.findViewById(R.id.btn_save_upd);
        Button btnDelete=view.findViewById(R.id.btn_delete);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 types=edt_type.getText().toString().trim();
                String change_amt;
                change_amt=edt_Ammount.getText().toString().trim();
                note=edt_note.getText().toString().trim();

                int intammount=Integer.parseInt(change_amt);

                String date=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(types,intammount,note,date,post_key);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.log_out:
                mAuth.signOut();
                Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
