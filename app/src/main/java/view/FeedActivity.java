package view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.muhammedbuga.instagramclonejava.R;
import com.muhammedbuga.instagramclonejava.databinding.ActivityFeedBinding;

import java.util.ArrayList;
import java.util.Map;

import adapter.PostAdapter;
import model.Post;

public class FeedActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post>postArrayList;
    private ActivityFeedBinding binding;
    PostAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getDataFromFirestore();
        postArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_post){
            Intent intent = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intent);
        }else if (item.getItemId() == R.id.signout){
            auth.signOut();
            Intent intenttoMain = new Intent(FeedActivity.this, MainActivity.class);
            startActivity(intenttoMain);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void getDataFromFirestore() {

        CollectionReference collectionReference = firebaseFirestore.collection("Posts");

        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(FeedActivity.this,e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null) {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                        Map<String,Object> data = snapshot.getData();

                        //Casting
                        String comment = (String) data.get("comment");
                        String userEmail = (String) data.get("useremail");
                        String downloadUrl = (String) data.get("downloadurl");

                        Post post = new Post(userEmail,comment,downloadUrl);

                        postArrayList.add(post);

                    }
                    postAdapter.notifyDataSetChanged();

                }

            }
        });


    }
}