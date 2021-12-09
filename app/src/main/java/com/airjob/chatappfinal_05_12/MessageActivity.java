package com.airjob.chatappfinal_05_12;

import static com.airjob.chatappfinal_05_12.ConstantNode.NODE_CHATLIST;
import static com.airjob.chatappfinal_05_12.ConstantNode.NODE_CHATS;
import static com.airjob.chatappfinal_05_12.ConstantNode.NODE_USERS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airjob.chatappfinal_05_12.Adapter.MessageAdapter;
import com.airjob.chatappfinal_05_12.Model.ChatModel;
import com.airjob.chatappfinal_05_12.Model.ChatlistModel;
import com.airjob.chatappfinal_05_12.Model.UserModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import com.airjob.chatappfinal_05_12.ConstantNode;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "#######>>>>>";

    // Var des widgets
    private CircleImageView profile_image;
    private TextView username;
    private ImageButton btn_send;
    private EditText text_send;
    private RecyclerView recyclerView;
    private String idParticipantChat;

    // Var globales
    private MessageAdapter messageAdapter;
    private List<ChatModel> mchat;
    private Intent intent;
    private ValueEventListener seenListener;

    // Var Firebase
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private CollectionReference chatCollectionRef;
    private CollectionReference chatListCollectionRef;

    private DocumentReference userDocumentRef;
    private DocumentReference chatDocumentRef;
    private DocumentReference chatlistDocumentRef;


    // Initialisation des widgets
    private void init() {
        recyclerView = findViewById(R.id.recycler_view_message_activity);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
    }

    // Initialisation de FirebaseUser
    private void initFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        chatCollectionRef = db.collection(NODE_CHATS);
        chatListCollectionRef = db.collection(NODE_CHATLIST);

        userDocumentRef = db.collection(NODE_USERS).document(currentUser.getUid());
        chatDocumentRef = db.collection(NODE_CHATS).document(currentUser.getUid());
        chatlistDocumentRef = db.collection(NODE_CHATLIST).document(currentUser.getUid());
    }

    // Gestion des clics sur les boutons
    private void btnSend() {
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(currentUser.getUid(), idParticipantChat, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send an empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Initialisation des widgets
        init();
        // Initialisation de Firebase
        initFirebase();

        // Gestion de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Gestion de la navigation de la toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        // Récupération de l'id du participant au chat via l'intent
        intent = getIntent();
        idParticipantChat = intent.getStringExtra("idParticipantChat");
        Log.i("#####>>>>>", "onCreate: " + idParticipantChat);

        // Appel des clics sur les boutons
        btnSend();

        // Query pour le SnapshotListner
        Query query = db.collection("Users");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    UserModel user = documentSnapshot.toObject(UserModel.class);
                    username.setText(user.getUsername());
                    if (user.getImageURL().equals("default")) {
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                    }
                    readMessages(currentUser.getUid(), idParticipantChat, user.getImageURL());
                }
            }
        });
        seenMessage(idParticipantChat);
    }

    // Les messages on-ils été vu ?
    private void seenMessage(String userid) {
//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        seenListener = reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    ChatModel chat = snapshot.getValue(ChatModel.class);
//                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("isseen", true);
//                        snapshot.getRef().updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    // Envoi des messages
    private void sendMessage(String sender, final String receiver, String message) {

        // Upload du message dans la table Chats
        ChatModel newChat = new ChatModel(sender, receiver, message, false);
        long time= System.currentTimeMillis();
        String docId = String.valueOf(time);
        chatCollectionRef.document(docId).set(newChat)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });

        // Upload de la liaison des participants du chat en question dans Chatlist
//        chatlistDocumentRef.set(idParticipantChat, idParticipantChat);
        ChatlistModel newChatChannel = new ChatlistModel(idParticipantChat);
        newChatChannel.setId(idParticipantChat);
        chatListCollectionRef.document(currentUser.getUid()).set(idParticipantChat);

//        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//                .child(firebaseUser.getUid())
//                .child(userid);
//        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    chatRef.child("id").setValue(userid);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    // Affichage des messages
    private void readMessages(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();

        Query query = db.collection(NODE_CHATS);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                mchat.clear();
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    ChatModel chat = documentSnapshot.toObject(ChatModel.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }
        });

    }

    // Gestion du statut de l'utilisateur
    private void status(String status) {
        userDocumentRef.update("status", status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        status("offline");
    }
}