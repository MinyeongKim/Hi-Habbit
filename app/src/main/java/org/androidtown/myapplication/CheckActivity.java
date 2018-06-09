package org.androidtown.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.util.Calendar;

public class CheckActivity extends BaseActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference, databaseReference1;

    Button button;
    int year, month, day;
    LinearLayout rating_layout;
    String UserID;
    int habitIdx;
    int totalHistoryNum;

    RatingBar ratingbar1;
    TextView rating_result1;
    EditText comment_value;

    String comment;
    private float rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        Intent check_type = getIntent();
        Bundle get_type = check_type.getExtras();

        final String type = get_type.getString("Check_type");
        UserID = get_type.getString("ID");
        habitIdx = get_type.getInt("INDEX");
        totalHistoryNum = get_type.getInt("HISTORYNUM");



        Toast.makeText(getApplicationContext(), type + "/" + UserID + "/" + habitIdx, Toast.LENGTH_SHORT).show();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users/" + UserID + "/habits/current/" + habitIdx + "/history");
<<<<<<< HEAD

        databaseReference1 = database.getReference("users/" + UserID + "/habits/current/" + habitIdx);
=======
>>>>>>> 2f576bc50dc45572bd9e06d2572929b47d15ed9c

        rating_result1 = (TextView) findViewById(R.id.rating_result);
        ratingbar1 = (RatingBar) findViewById(R.id.ratingbar);
        button = (Button) findViewById(R.id.button);
        comment_value = (EditText) findViewById(R.id.editTExt);

        rating_layout = (LinearLayout) findViewById(R.id.rating);

        //rating 검사
        ratingbar1.setStepSize((float) 0.5);        //별 색깔이 1칸씩줄어들고 늘어남 0.5로하면 반칸씩 들어감
        ratingbar1.setRating((float) 2.5);      // 처음보여줄때(색깔이 한개도없음) default 값이 0  이다
        ratingbar1.setIsIndicator(false);           //true - 별점만 표시 사용자가 변경 불가 , false - 사용자가 변경가능

        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int y, int m, int d) {
                //Toast.makeText(getApplicationContext(), y + "년 " + (m + 1) + "월 " + d + "일", Toast.LENGTH_SHORT).show();
                year = y;
                month = m + 1;
                day = d;

                rating_layout.setVisibility(View.VISIBLE);

            }
        });

        ratingbar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                rate = rating;
                rating_result1.setText("" + rating);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("ID", UserID);
                bundle.putInt("INDEX", habitIdx);

                //디비 history 라인에 값 저장하기 ->별점이랑 코멘트 저장
                comment = comment_value.getText().toString(); //사용자가 입력한 comment

                //rate라는 변수가 사용자가 입력한 별점

                //;날짜 -> year, month, day에 저장되어 있음

                //이제 디비에 넣어주면 됨
                //등록한 날짜 정보
                String checkedDate = year + "년 ";
                if (month < 10) checkedDate = checkedDate + "0" + month + "월 ";
                else checkedDate = checkedDate + "" + month + "월 ";

                if (day < 10) checkedDate = checkedDate + "0" + day + "일 ";
                else checkedDate = checkedDate + "" + day + "일 ";

                //시간도 읽어서 넣어줘야함
                /*

                 */

                String inputRate = String.valueOf(rate);
                String historyIdx = String.valueOf(totalHistoryNum + 1);
                databaseReference.child(historyIdx).child("DATE").setValue(checkedDate);
                databaseReference.child(historyIdx).child("COMMENT").setValue(comment);
                databaseReference.child(historyIdx).child("RATING").setValue(inputRate);

                //습관 실천 횟수 증가시켜주는 부분
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String didString = (String)dataSnapshot.child("DID").getValue();
                        int didNum = Integer.parseInt(didString);
                        didNum++;

                        databaseReference1.child("DID").setValue(String.valueOf(didNum));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //값 제대로 읽히는지 테스팅
                Toast.makeText(getApplicationContext(), "comment: " + comment + " rating: " + inputRate, Toast.LENGTH_SHORT).show();


                switch (type) {
                    //혼자 하는 경우에는 값을 저장한 후 액티비티 종료
                    case "alone":
                        finish();
                        break;

                    //친구랑 하는 경우에는 값을 저장한 후 이미지 로드하는 페이지로 연결
                    case "friend":
                        Intent intent = new Intent(getApplicationContext(), LoadImageActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;

                    //제 3자랑 하는 경우에는 값을 저장한 후 이미지 로드하는 페이지로 연결
                    case "otherPerson":
                        Intent intent2 = new Intent(getApplicationContext(), LoadImageActivity.class);
                        intent2.putExtras(bundle);
                        startActivity(intent2);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    //뒤로가는 버튼 생성
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //뒤로가기 버튼이 눌렀을 경우
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}