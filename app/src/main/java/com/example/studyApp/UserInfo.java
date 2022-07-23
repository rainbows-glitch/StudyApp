package com.example.studyApp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserInfo extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();  //to keep back button functional and avoid infinite loop
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private Map<String, Map> classInformation = new HashMap<>();
    private Map<String, ArrayList<String>> timetable = new HashMap<>();
    private int page = 1;
    private ArrayList<String> classNames = new ArrayList<>(); //empty string array that will be filled with values later

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        ((MainActivity)requireActivity()).hideNavigation();

        View glass = view.findViewById(R.id.glass);
        ConstraintLayout infoLayout = view.findViewById(R.id.infoLayout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

//        set glass height to be 85% of window height
        float vh = displayMetrics.heightPixels/100f;
        glass.getLayoutParams().height = (int) vh*85;

//        centre glass relative to window (not fragment)
        ConstraintSet mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(infoLayout);
        glass.post(()->{
            int verticalSpace = displayMetrics.heightPixels - glass.getHeight();
            mConstraintSet.setMargin(glass.getId(), ConstraintSet.BOTTOM, verticalSpace/2);
            mConstraintSet.applyTo(infoLayout);
        });

//       setting focus requests
        for(int i = 1; i <=5; i++){
            String id = "P" + i;
            int subjectLabelID = requireContext().getResources().getIdentifier(id +"SubjectLabel", "id", requireContext().getPackageName());
            int subjectInputID = requireContext().getResources().getIdentifier(id +"SubjectInput", "id", requireContext().getPackageName());
            int roomLabelID = requireContext().getResources().getIdentifier(id +"ClassLabel", "id", requireContext().getPackageName());
            int roomInputID = requireContext().getResources().getIdentifier(id +"ClassInput", "id", requireContext().getPackageName());
            int teacherLabelID = requireContext().getResources().getIdentifier(id +"TeacherLabel", "id", requireContext().getPackageName());
            int teacherInputID = requireContext().getResources().getIdentifier(id +"TeacherInput", "id", requireContext().getPackageName());

            TextView subjectLabel = view.findViewById(subjectLabelID);
            AutoCompleteTextView subjectInput = view.findViewById(subjectInputID);
            TextView roomLabel = view.findViewById(roomLabelID);
            EditText roomInput = view.findViewById(roomInputID);
            TextView teacherLabel = view.findViewById(teacherLabelID);
            EditText teacherInput = view.findViewById(teacherInputID);

            subjectLabel.setOnClickListener(view12 -> subjectInput.requestFocus());
            roomLabel.setOnClickListener(view12 -> roomInput.requestFocus());
            teacherLabel.setOnClickListener(view12 -> teacherInput.requestFocus());

            //next buttons
            subjectInput.setOnEditorActionListener((textView, actionID, keyEvent) -> {
                if(actionID == EditorInfo.IME_NULL || actionID == 100){
                    teacherInput.requestFocus();
                    return true;
                }else{return false;}
            });
            teacherInput.setOnEditorActionListener((textView, actionID, keyEvent) -> {
                if(actionID == EditorInfo.IME_NULL || actionID == 100){
                    roomInput.requestFocus();
                    return true;
                }else{return false;}
            });
        }
        EditText p1Next = view.findViewById(R.id.P1ClassInput);
        p1Next.setOnEditorActionListener((textView, actionID, keyEvent) ->{
            if (actionID== EditorInfo.IME_NULL || actionID == 100){
                view.findViewById(R.id.P2SubjectInput).requestFocus();
                return true;
            }else{return false;}
        });
        EditText p2Next = view.findViewById(R.id.P2ClassInput);
        p2Next.setOnEditorActionListener((textView, actionID, keyEvent) ->{
            if (actionID== EditorInfo.IME_NULL || actionID == 100){
                view.findViewById(R.id.P3SubjectInput).requestFocus();
                return true;
            }else{return false;}
        });
        EditText p3Next = view.findViewById(R.id.P3ClassInput);
        p3Next.setOnEditorActionListener((textView, actionID, keyEvent) ->{
            if (actionID== EditorInfo.IME_NULL || actionID == 100){
                view.findViewById(R.id.P4SubjectInput).requestFocus();
                return true;
            }else{return false;}
        });
        EditText p4Next = view.findViewById(R.id.P4ClassInput);
        p4Next.setOnEditorActionListener((textView, actionID, keyEvent) ->{
            if (actionID== EditorInfo.IME_NULL || actionID == 100){
                view.findViewById(R.id.P5SubjectInput).requestFocus();
                return true;
            }else{return false;}
        });
        EditText p5Next = view.findViewById(R.id.P5ClassInput);
        p5Next.setOnEditorActionListener((textView, actionID, keyEvent) ->{
            if (actionID== EditorInfo.IME_ACTION_DONE){
                buttonClicked(view, view.findViewById(R.id.infoDay));
                return true;
            }else{return false;}
        });

        view.findViewById(R.id.nextBtn).setOnClickListener(view1 -> buttonClicked(view, view.findViewById(R.id.infoDay)));

        return view;
    }

     public Map<String, String> createClasses(EditText subjectInput, EditText roomInput, EditText teacherInput){
        Map<String, String> returnedMap = new HashMap<>();
         returnedMap.put("subject", subjectInput.getText().toString().trim());
         returnedMap.put("room", roomInput.getText().toString().trim());
         returnedMap.put("teacher", teacherInput.getText().toString().trim());
         return returnedMap;
     }

     public void buttonClicked(View view, TextView day){
        ArrayList<String> classOrder = new ArrayList<>();
        ArrayAdapter<String> stringArrayAdapter;
        boolean ended = false;

         for (int i = 1; i <= 5; i++){
             String id = "P" + i;

             int subjectID = requireContext().getResources().getIdentifier(id +"SubjectInput", "id", requireContext().getPackageName());
             AutoCompleteTextView subjectInput = view.findViewById(subjectID);
             int roomID = requireContext().getResources().getIdentifier(id +"ClassInput", "id", requireContext().getPackageName());
             EditText roomInput = view.findViewById(roomID);
             int teacherID = requireContext().getResources().getIdentifier(id +"TeacherInput", "id", requireContext().getPackageName());
             EditText teacherInput = view.findViewById(teacherID);

             if(subjectInput.getText().toString().trim().isEmpty()){
                 subjectInput.setError("Please enter Subject name");
                 Toast.makeText(requireContext(), "Please fill in all empty fields.", Toast.LENGTH_LONG).show();
                 ended = true;
                 break;
             }
             if(roomInput.getText().toString().trim().isEmpty()){
                 roomInput.setError("Please enter subject class");
                 Toast.makeText(requireContext(), "Please fill in all empty fields.", Toast.LENGTH_LONG).show();
                 ended = true;
                 break;
             }
             if(teacherInput.getText().toString().trim().isEmpty()){
                 teacherInput.setError("Please enter subject teacher");
                 Toast.makeText(requireContext(), "Please fill in all empty fields.", Toast.LENGTH_LONG).show();
                 ended = true;
                 break;
             }

             Map<String, String> possibleNewClass = createClasses(subjectInput, roomInput, teacherInput);

             if (!classInformation.containsValue(possibleNewClass)){
                 if (!classInformation.containsKey(subjectInput.getText().toString().trim())){
                     classInformation.put(subjectInput.getText().toString().trim(), possibleNewClass);
                     classNames.add(subjectInput.getText().toString().trim()); //For ArrayAdapter
                 }else{
                     int offset =1;
                     while(true){ //loop until new key is not found
                         String key = subjectInput.getText().toString().trim() + offset;
                         if (!classInformation.containsKey(key)){
                             classInformation.put(key, possibleNewClass);
                             classNames.add(key); //For ArrayAdapter
                             break;
                         }else{offset++;}
                     }
                 }
             }

             stringArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, classNames);
             subjectInput.setOnItemClickListener((adapterView, view1, i1, l) -> {
//             subject input will be the classInformation key that will be replaced with correct value
               Map<String, String> data = classInformation.get(subjectInput.getText().toString());
               subjectInput.setText(data.get("subject"));
               roomInput.setText(data.get("room"));
               teacherInput.setText(data.get("teacher"));
           });

             for(Map.Entry<String, Map> entry: classInformation.entrySet()){
                if(entry.getValue().equals(possibleNewClass)){
                    classOrder.add(entry.getKey());
                }
             }
             if (i == 5){ //once all data has been added into classOrder for that day
                 timetable.put(DayOfWeek.of(page).toString(), classOrder);
             }

             if(page < 5){
                 subjectInput.setText("");
                 roomInput.setText("");
                 teacherInput.setText(""); //TODO: fix bug
                 subjectInput.setAdapter(stringArrayAdapter);
                 String dayText = DayOfWeek.of(page+1).toString();
                 dayText = dayText.charAt(0) + dayText.substring(1).toLowerCase();
                 day.setText(dayText);
                 ScrollView scrollView = view.findViewById(R.id.infoScroll);
                 scrollView.post(() -> scrollView.scrollTo(0, 0));
                 view.findViewById(R.id.P1SubjectInput).requestFocus();
                 if(page == 4){
                     Button nextBtn = view.findViewById(R.id.nextBtn);
                     nextBtn.setText("Finish");
                 }
             }else{
                 DocumentReference docRef = FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("userInformation")
                .document("classes");
                 docRef.set(classInformation).addOnSuccessListener(unused -> FirebaseFirestore.getInstance().collection("users")
                         .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                         .collection("userInformation")
                         .document("timetable").set(timetable).addOnSuccessListener(unused1 -> {
                             NavHostFragment.findNavController(this).navigate(R.id.globalNavHome);
                             Log.d("FIREBASE_FIRESTORE", "Success");
                         }).addOnFailureListener(err -> Log.d("FIREBASE_FIRESTORE", err.getMessage()))).addOnFailureListener(e -> Log.d("FIREBASE_FIRESTORE", e.getMessage()));
             }
         }
         if(!ended){page++;}
     }
}