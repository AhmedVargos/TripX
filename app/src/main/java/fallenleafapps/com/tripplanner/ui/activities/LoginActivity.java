package fallenleafapps.com.tripplanner.ui.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import fallenleafapps.com.tripplanner.R;
import fallenleafapps.com.tripplanner.database.DatabaseAdapter;
import fallenleafapps.com.tripplanner.database.DatabaseContract;
import fallenleafapps.com.tripplanner.database.DatabaseHelper;
import fallenleafapps.com.tripplanner.models.UserClassModel;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.btn_login)
    AppCompatButton btnLogin;

    @BindView(R.id.input_email)
    TextInputEditText inputEmail;

    @BindView(R.id.input_password)
    TextInputEditText inputPassword;

    @BindView(R.id.link_signup)
    TextView linkSignup;

    FirebaseAuth firebaseAuth;

    LinearLayout layout;
    TextView googleText;

    private final int RC_SIGN_IN=101;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        layout=findViewById(R.id.mainlayout);
        googleText=findViewById(R.id.link_signup_G);

        if(!isNetworkAvailable())
        {
            Snackbar snackbar = Snackbar.make(layout, "No Internet Connection !", Snackbar.LENGTH_LONG);
            snackbar.getView().getBackground().setColorFilter(Color.RED, PorterDuff.Mode.ADD);
            snackbar.show();
        }

        firebaseAuth=FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=inputEmail.getText().toString();
                String password=inputPassword.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }
                        else{
                            if(!isNetworkAvailable())
                            {
                                Snackbar snackbar = Snackbar.make(layout, "No Internet Connection !", Snackbar.LENGTH_LONG);
                                snackbar.getView().getBackground().setColorFilter(Color.RED, PorterDuff.Mode.ADD);
                                snackbar.show();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


            }
        });

        linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupIntent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(signupIntent);
            }
        });


        configureSignIn();
        googleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void configureSignIn(){
        // Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, null /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult task = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                if(task.isSuccess()) {
                    GoogleSignInAccount account = task.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }
            } catch (Exception e) {
                // Google Sign In failed, update UI appropriately
                Log.w("hello", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "signed in using g+", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "failed to sign in using g+", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
