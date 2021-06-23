package com.example.digitalwellness;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirebaseHelper {
    private final FirebaseAuth auth;
    private final String KEY_SCREEN = "Screen";
    private final String KEY_STEP = "Steps";
    private final String PATTERN = "MM-dd-yyyy";
    private final int RANGE = 7;

    private static String[] axis = null;
    private static ArrayList<BarEntry> steps;
    private static ArrayList<BarEntry> screen;

    private static Date currentDate;
    private String Uid;

    /**
     * Public constructor.
     */
    public FirebaseHelper() {
        this.auth = FirebaseAuth.getInstance();
        currentDate = Calendar.getInstance().getTime();
        this.Uid = this.auth.getUid();
    }

    /**
     * Create user in Firebase.
     *
     * @param email    User's email.
     * @param password User's password.
     * @param activity  Current activity.
     */
    private void createAccount(String email, String password, String name, Activity activity) {
        // [START create_user_with_email]
        this.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // ref.push().child(date.toString()).setValue("Account created under: " + email);
                            updateProfile(name, "Account created!", activity);
                            createBasicData(activity);
                            activity.startActivity(new Intent(activity, Login.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w("Failure", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Create basic data when user first creates an account.
     */
    public void createBasicData(Context context) {
        FirebaseHelper firebase = new FirebaseHelper();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(firebase.getUid())
                .child(firebase.getCurrentDate());
        reference.child("Steps").setValue(0);
        reference.child("Screen").setValue(0);

        // Reset streak when it is a Sunday
        if (firebase.getCurrentDay() == 1) {
            MyPreference myPreference = new MyPreference(context, "Streak");
            for (int i = 1; i < 8; i++) {
                myPreference.setMilestone(String.valueOf(i), false);
            }
        }
    }

    /**
     * Returns an array of string which corresponds to the current day of the week.
     *
     * @return An array of string which corresponds to the current day of the week.
     */
    public String[] getAxis() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                axis = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                break;
            case Calendar.MONDAY:
                axis = new String[]{"Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon"};
                break;
            case Calendar.TUESDAY:
                axis = new String[]{"Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue"};
                break;
            case Calendar.WEDNESDAY:
                axis = new String[]{"Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed"};
                break;
            case Calendar.THURSDAY:
                axis = new String[]{"Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu"};
                break;
            case Calendar.FRIDAY:
                axis = new String[]{"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"};
                break;
            case Calendar.SATURDAY:
                axis = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                break;
        }
        return axis;
    }

    /**
     * Returns current date in dd-MM-yyyy format.
     *
     * @return current date.
     */
    public String getCurrentDate() {
        return new SimpleDateFormat(PATTERN, Locale.getDefault()).format(currentDate);
    }

    public int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Returns firebase auth.
     *
     * @return Firebase auth.
     */
    public FirebaseAuth getFirebaseAuth() {
        return this.auth;
    }

    /**
     * Returns yesterday's date in dd-MM-yyyy format.
     *
     * @return Yesterday's date.
     */
    public String getPreviousDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date date = cal.getTime();
        return new SimpleDateFormat(PATTERN, Locale.getDefault()).format(date);
    }

    /**
     * Get data of the past seven days and store it in an array.
     *
     * @param activity Calling activity.
     * @param intent The intent to be called after.
     */
    public void getData(Activity activity, Intent intent) {
        steps = new ArrayList<>();
        screen = new ArrayList<>();

        if (Uid == null) {
            Uid = new FirebaseHelper().getUid();
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(Uid);

        ref.limitToLast(7).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();

                    long entriesToAdd = RANGE - snapshot.getChildrenCount();
                    int counter = 0;
                    int i = 0;
                    int j = 0;

                    while (entriesToAdd >= 0) {
                        steps.add(new BarEntry(i, 0));
                        screen.add(new BarEntry(j, 0));
                        i++;
                        j++;
                        counter += 2;
                        entriesToAdd--;
                    }

                    for (DataSnapshot snap: snapshot.getChildren()) {
                        for (DataSnapshot s: snap.getChildren()) {
                            if (counter % 2 == 0) {
                                screen.add(new BarEntry(i, (Long) s.getValue()));
                                i++;
                            } else {
                                steps.add(new BarEntry(j, (Long) s.getValue()));
                                j++;
                            }
                            counter++;
                        }
                    }
                    activity.startActivity(intent);
                    System.out.println("data loaded");
                }
            }
        });
    }

    public ArrayList<BarEntry> getSteps() {
        return steps;
    }

    public ArrayList<BarEntry> getScreen() {
        return screen;
    }

    /**
     * Returns a database reference corresponding to current user's step of the day.
     *
     * @return Firebase database reference.
     */
    public DatabaseReference getStepsRef(String date) {
        return FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(Uid)
                .child(date)
                .child(KEY_STEP);
    }

    /**
     * Returns a database reference corresponding to the current screen time of the day.
     */
    public DatabaseReference getScreenRef(String date) {
        return FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(Uid)
                .child(date)
                .child(KEY_SCREEN);
    }

    /**
     * Returns user id.
     *
     * @return User id.
     */
    public String getUid() {
        return this.auth.getUid();
    }

    /**
     * Method called to get user data
     *
     * @return Firebase User, containing users email etc
     */
    public FirebaseUser getUser() {
        return this.auth.getCurrentUser();
    }

    /**
     * Method called to check status of user logged in
     *
     * @return boolean to check if user is logged in
     */
    public boolean isLoggedIn() {
        return this.getUser() != null;
    }

    /**
     * Method to sign out this instance of firebase.
     */
    public void logoutUser() {
        this.auth.signOut();
    }

    /**
     * Sign in using email and password.
     *
     * @param username Username of user.
     * @param password Password of user.
     * @param activity  Current activity.
     */
    public void login(String username, String password, Activity activity) {
        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Login Successful!", Toast.LENGTH_LONG).show();
                            // ref.push().child(date.toString()).setValue(username + " has logged in.");
                            MyAlarms myAlarms = new MyAlarms(activity);
                            myAlarms.startUpdateToFirebase();
                            myAlarms.startDailyUpdates();
                            getData(activity, new Intent(activity, MainActivity.class));
                        } else {
                            Toast.makeText(activity, "Username and password does not match!", Toast.LENGTH_LONG).show();
                            // ref.push().child(date.toString()).setValue(username + " entered a wrong password ");
                        }
                    }
                });
    }

    /**
     * Register user through valid email and password.
     *
     * @param username Username of user.
     * @param password Password of user.
     * @param activity  Current activity.
     */
    public void registerEmailAndPassword(String username, String password, String name, Activity activity) {
        String emailRegSample = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegSample);
        Matcher matcher = pattern.matcher(username);

        if (!matcher.find()) {
            Toast.makeText(activity, "Please enter a valid email!",
                    Toast.LENGTH_LONG).show();
        } else if (password.isEmpty() || password.length() < 8) {
            Toast.makeText(activity, "Password should contain at least 8 characters!",
                    Toast.LENGTH_LONG).show();
        } else {
            createAccount(username, password, name, activity);
        }
    }

    /**
     * Reset's user password
     *
     * @param email User's email.
     * @param context Current context.
     */
    public void resetPassword(String email, Context context) {
        this.auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Password reset link has been sent to this email.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "System error! \nPlease try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Update user's profile.
     *
     * @param name      Name to be updated.
     * @param toastText Text to be displayed.
     * @param context   Current context.
     */
    public void updateProfile(String name, String toastText, Context context) {
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        getUser().updateProfile(profile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Updating profile failed! Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Update steps from phone's shared preference to database.
     *
     * @param value The amount of steps to be updated.
     */
    public void updateSteps(String date, long value) {
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(Uid)
                .child(date)
                .child(KEY_STEP)
                .setValue(value);
    }

    /**
     * Update screen time from the shared preference to database
     *
     * @param value the amount of time spent on screen
     */
    public void updateScreen(String date, long value) {
        this.getScreenRef(date).setValue(value);
    }
}
